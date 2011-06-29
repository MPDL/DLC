package de.mpg.mpdl.dlc.imageupload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
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
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import de.mpg.mpdl.dlc.imageupload.ImageHelper.Type;


public class ImageServlet extends HttpServlet {
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String destDir =(String)PropertyReader.getProperty("image-upload.destDir");
		
		File f = new File(destDir + req.getPathInfo());
		
		ServletContext sc = getServletContext();
		
		if(!f.exists())
		{
			resp.sendError(HttpStatus.SC_NOT_FOUND, "Image not found");
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
		    out.close();
			
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try 
		{
			String authHeader = req.getHeader("authorization");
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
		     
			
			try {
				/*
				 * Parse the request
				 */
				List<FileItem> items = upload.parseRequest(req);
				Iterator<FileItem> itr = items.iterator();
				String destDir = "";
				
				while(itr.hasNext()) {
					FileItem item = (FileItem) itr.next();
					/*
					 * Handle Form Fields.
					 */
					if(item.isFormField()) {
						
						if(item.getFieldName().equals("directory"))
						{
							destDir = item.getString();
							
							log("Directory retrieved: " + destDir);
							if(destDir.contains("../"))
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

						String filename = storeImages(destDir, item, "jpg");
						out.write(destDir + "/" + filename);
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

}
