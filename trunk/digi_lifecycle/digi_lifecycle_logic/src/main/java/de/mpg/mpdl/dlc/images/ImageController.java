package de.mpg.mpdl.dlc.images;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
/*
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
*/
import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.mets.Mets;

public class ImageController {
	
	private static Logger logger = Logger.getLogger(ImageController.class);
	
	
	public static String uploadFileToImageServer(File item, String directory, String filename) throws Exception
	{
		/*
		File tmpFile = File.createTempFile(item.getName(), "tmp");
		item.write(tmpFile);
		*/
		HttpClient client = new HttpClient( );

		String weblintURL = PropertyReader.getProperty("image-upload.url.upload");
    	PostMethod method = new PostMethod(weblintURL);
    	Part[] parts = new Part[2];
    	parts[0] = new StringPart("directory", directory);
    	parts[1] = new FilePart(filename, new FilePartSource(filename, item));
    	
    	//parts[1] = new FilePart( item.getName(), tmpFile );
    	HttpMethodParams params = new HttpMethodParams();
    	method.setRequestEntity(new MultipartRequestEntity(parts, params));
    	String username = PropertyReader.getProperty("image-upload.username");
    	String password = PropertyReader.getProperty("image-upload.password");
    	String handle = "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    	method.addRequestHeader("Authorization", handle);
    	// Execute and print response
    	client.executeMethod( method );
    	String response = method.getResponseBodyAsString( );
    	logger.info("Image Upload servlet responded with: " + method.getStatusCode() + "\n" + response);
    	method.releaseConnection( );
    	if(response == null || !(method.getStatusCode()==201 || method.getStatusCode()==200))
    	{
    		throw new RuntimeException("File Upload Servlet responded with: " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
    	}

    	return response;
	}
	
	/*
	public static List<String> uploadFilesToImageServer(List<File> items, String directory) throws Exception
	{
		String[] dirs = new String[items.size()];
		List<FileItem> retry = new ArrayList<FileItem>();
		
		for(File item : items)
		{
			if(item != null)
			{
				try 
				{
					long start = System.currentTimeMillis();
					String dir = uploadFileToImageServer(item, directory);
					long time = System.currentTimeMillis()-start;
					System.out.println("Time Upload one image: " + time);

					dirs[items.indexOf(item)] =  dir;
				} catch (Exception e) {
					logger.warn("Failed upload for file " + item.getName() + "\nWill retry later...", e);
					retry.add(item);
				}
			}
		}
		
		for(FileItem item : retry)
		{
			if(item != null)
			{
					String dir = uploadFileToImageServer(item, directory);
					dirs[items.indexOf(item)] =  dir;
			}
		}
		
		return Arrays.asList(dirs);
	}
	
	*/
	/*
	
	public static void uploadFilesViaFtp(String targetDir, List<File> fileList, List<String> filenames)
	{
		
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect("ftp://blaaa");
		int reply = ftpClient.getReply();
		if(!FTPReply.isPositiveCompletion(reply)) {
	        ftpClient.disconnect();
	        logger.error("Could not connect to FTP server. Reply code is " +reply );
	        return;
	      }
		
		
		
		ftpClient.login("test", "test");
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		
		boolean createDir = ftpClient.makeDirectory(targetDir);
		ftpClient.changeWorkingDirectory(targetDir);
		
		
		for(int i = 0; i<fileList.size(); i++)
		{
			ftpClient.storeFile(filenames.get(i), new FileInputStream(fileList.get(i)));
		}
		
		
	}
	*/
	

}
