package de.mpg.mpdl.dlc.imageupload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.imageupload.ImageHelper.Type;


public class ImageServlet extends HttpServlet {
	
	public static Logger logger = Logger.getLogger(ImageServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String mainDir = null;
		try {
			mainDir = (String)PropertyReader.getProperty("image-upload.destDir");
		} catch (Exception e) {
			logger.error("Could not read properties", e);
			resp.sendError(HttpStatus.SC_NOT_FOUND, "Properties not found!");
		}
		
		File f = new File(mainDir + req.getPathInfo());
		
		ServletContext sc = getServletContext();
		
		if(!f.exists())
		{
			resp.sendError(HttpStatus.SC_NOT_FOUND, "Image " + req.getPathInfo() + " not found!");
			return;
		}
		else
		{
			
			String mimetype = sc.getMimeType(f.getPath());
			resp.setContentType(mimetype);
			resp.setContentLength((int)f.length());
			
			FileInputStream fis = new FileInputStream(f);
			OutputStream out = resp.getOutputStream();
			
			byte[] buf = new byte[1024];
		    int count = 0;
		    while ((count = fis.read(buf)) >= 0) {
		        out.write(buf, 0, count);
		    }
		    fis.close();
		    out.flush();
		    out.close();
			
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try 
		{
			String authHeader = req.getHeader("Authorization");
			String encodedValue = authHeader.split(" ")[1];
			
			String decodedValue = new String((Base64.decodeBase64(encodedValue.getBytes())));
			String username = decodedValue.split(":")[0];
			String password = decodedValue.split(":")[1];
			
			if(!(username.equals(PropertyReader.getProperty("image-upload.username"))) || !(password.equals(PropertyReader.getProperty("image-upload.password"))))
			{
				resp.sendError(HttpStatus.SC_FORBIDDEN);
				return;
			}
		} catch (Exception e)
		{
			resp.sendError(HttpStatus.SC_UNAUTHORIZED);
			return;
		}
		
		
		if(ServletFileUpload.isMultipartContent(req))
		{
			try {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Set factory constraints
			factory.setSizeThreshold(100);
			factory.setRepository(new File((String)PropertyReader.getProperty("image-upload.tmpDir")));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Set overall request size constraint
			long sizeMax = Long.parseLong((String)PropertyReader.getProperty("image-upload.sizeMax"));
			upload.setSizeMax(sizeMax);
		    PrintWriter out = resp.getWriter();
			resp.setContentType("text/plain");
		     
			
			
				/*
				 * Parse the request
				 */
				List<FileItem> items = upload.parseRequest(req);
				Iterator<FileItem> itr = items.iterator();
				String subDir = "";
				
				while(itr.hasNext()) {
					FileItem item = (FileItem) itr.next();
					/*
					 * Handle Form Fields.
					 */
					if(item.isFormField()) {
						
						if(item.getFieldName().equals("directory"))
						{
							subDir = item.getString();
							
							log("Directory retrieved: " + subDir);
							if(subDir.contains("../"))
							{
								throw new Exception("Filepath not allowed");
							}
						}
						
					} 
					else 
					{
						log("File retrived: Field Name = "+item.getFieldName()+
							", File Name = "+item.getName()+
							", Content type = "+item.getContentType()+
							", File Size = "+item.getSize());

						
						String mainDir = PropertyReader.getProperty("image-upload.destDir");
						
						
						//Write file to directory
						if(!mainDir.endsWith("/") || subDir.startsWith("/"))
						{
							mainDir=mainDir + "/";
						}
						if(!subDir.endsWith("/"))
						{
							subDir = subDir + "/";
						}
						
						subDir = subDir.replaceAll(":", "_");
						
						File dirFile = new File(mainDir + subDir);
						if(!dirFile.exists())
						{
							dirFile.mkdirs();
						}
						
						File f = new File(dirFile, item.getName());
						if(!f.exists())
						{
							f.createNewFile();
						}
					    FileOutputStream output = new FileOutputStream(f);
					    InputStream tempFileInput = ((DiskFileItem)item).getInputStream();
					    
					    byte[] buf = new byte[2048];
					    int count = 0;
					    while((count = tempFileInput.read(buf)) != -1)
					    {
					    	output.write(buf, 0, count);
					    }
					    output.flush();
					    output.close();
					    
					    tempFileInput.close();

						//String filename = storeImages(destDir, item, "jpg");
						out.write(subDir + f.getName());
					}
					
				}
				resp.setStatus(HttpStatus.SC_CREATED);
				out.close();
				
			}catch(FileUploadException ex) {
				log("Error encountered while parsing the request",ex);
				resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
			} catch(Exception ex) {
				log("Error encountered while uploading file",ex);
				resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
			}
		}
		
	}
	
	
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		
		try 
		{
			String authHeader = req.getHeader("Authorization");
			String encodedValue = authHeader.split(" ")[1];
			
			String decodedValue = new String((Base64.decodeBase64(encodedValue.getBytes())));
			String username = decodedValue.split(":")[0];
			String password = decodedValue.split(":")[1];
			
			if(!(username.equals(PropertyReader.getProperty("image-upload.username"))) || !(password.equals(PropertyReader.getProperty("image-upload.password"))))
			{
				resp.sendError(HttpStatus.SC_FORBIDDEN);
				return;
			}
		} catch (Exception e)
		{
			resp.sendError(HttpStatus.SC_UNAUTHORIZED);
			return;
		}
		
		
		String mainDir  =null;
		try {
			mainDir = (String)PropertyReader.getProperty("image-upload.destDir");
		} catch (URISyntaxException e) {
			logger.error("Could not read properties", e);
			resp.sendError(HttpStatus.SC_NOT_FOUND, "Properties not found!");
		}
		
		if(req.getPathInfo()==null || req.getPathInfo().trim().isEmpty() || req.getPathInfo().contains("..") || !req.getPathInfo().substring(1).contains("/"))
		{
			resp.sendError(HttpStatus.SC_FORBIDDEN);
			return;
		}
		
		
		File f = new File(mainDir + req.getPathInfo());
		
		if(!f.exists())
		{
			resp.sendError(HttpStatus.SC_NOT_FOUND, "Image or directory " + req.getPathInfo() + " not found! " + f.getAbsolutePath());
			return;
		}
		else
		{
			
			
			try {
				boolean deleteSuccess = deleteDir(f);
				if(!deleteSuccess)
				{
					resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Could not delete all files");
					return;
					
				}
				else
				{
					resp.setStatus(HttpStatus.SC_OK);

				}
				
			} catch (Exception e) {
				resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				return;
			}
			
			
		}
	}
	
	
	
	public static boolean deleteDir(File dir) throws Exception{
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}
	
	
	/*
	 public String storeImages(String destDir, FileItem input, String imgType) throws Exception{
			BufferedImage bufferedImage = ImageIO.read(input.getInputStream());
			String dir = PropertyReader.getProperty("image-upload.destDir") + "/original/" + destDir;
			String filename = input.getName();
			if(!filename.endsWith(".jpg") && ! filename.endsWith(".JPG"))
			{
				filename=filename + ".jpg";
			}
			
			File orig = ImageHelper.scaleAndStoreImage(dir, 0, bufferedImage, filename, imgType, Type.ORIGINAL);
			
			dir = PropertyReader.getProperty("image-upload.destDir") + "/web/" + destDir;
			ImageHelper.scaleAndStoreImage(dir, Integer.parseInt(PropertyReader.getProperty("image-upload.resolution.web")), bufferedImage, filename, imgType, Type.WEB);
			
			dir = PropertyReader.getProperty("image-upload.destDir") + "/thumbnails/" + destDir;
			ImageHelper.scaleAndStoreImage(dir, Integer.parseInt(PropertyReader.getProperty("image-upload.resolution.thumbnail")), bufferedImage, filename, imgType, Type.THUMBNAIL);
	 		
			return orig.getName();
		 }
		 */

}
