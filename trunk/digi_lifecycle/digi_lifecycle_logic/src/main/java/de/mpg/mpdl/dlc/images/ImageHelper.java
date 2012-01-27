package de.mpg.mpdl.dlc.images;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.mets.Page;


public class ImageHelper{
	
	static Logger logger = Logger.getLogger(ImageHelper.class);
	
	public enum Type
	{
		THUMBNAIL, WEB, ORIGINAL;
	}
	
	
	public static String THUMBNAILS_DIR;
	public static String WEB_DIR;
	public static String ORIGINAL_DIR;
	
	static 
	{
		try {
			THUMBNAILS_DIR = PropertyReader.getProperty("image-upload.thumbnailsDir") + "/";
			WEB_DIR =  PropertyReader.getProperty("image-upload.webDir") + "/";
			ORIGINAL_DIR = PropertyReader.getProperty("image-upload.originalDir")  + "/";
		} catch (Exception e) 
		{
			logger.error("Error while reading properties for image-upload", e);
		}
	}
	
	public static String getFullImageUrl(String subUrl, Type type)
	{
		try {
			String mainUrl = PropertyReader.getProperty("image-upload.url.download");
			String typeUrl = "";
			switch(type) 
	    	{
	    		case THUMBNAIL :
		    	{
		    		typeUrl = THUMBNAILS_DIR;
		    		break;
		    	}
	    		case WEB :
		    	{
		    		typeUrl = WEB_DIR;
		    		break;
		    	}
	    		case ORIGINAL :
		    	{
		    		typeUrl = ORIGINAL_DIR;
		    		break;
		    	}
	    	}
			
			String url = mainUrl + typeUrl + subUrl;
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for sub url page " + subUrl, e);
			return null;
		}
	}
	
	
	
	
	
    public static File scaleImage(File inputFile, String fileName, Type type) throws Exception{
	         	
    	
    	File tmpFile = File.createTempFile(fileName + type.name(), "jpg.tmp");
    	BufferedImage inputImage = ImageIO.read(inputFile);
    	
    	
    	String widthString = "";
    	String heightString = "";
    	switch(type) 
    	{
    		case THUMBNAIL :
	    	{
	    		widthString = PropertyReader.getProperty("image.thumbnail.maxWidth");
	    		heightString = PropertyReader.getProperty("image.thumbnail.maxHeight");
	    		break;
	    	}
    		case WEB :
	    	{
	    		widthString = PropertyReader.getProperty("image.web.maxWidth");
	    		heightString = PropertyReader.getProperty("image.web.maxHeight");
	    		break;
	    	}
    		case ORIGINAL :
	    	{
	    		widthString = PropertyReader.getProperty("image.original.maxWidth");
	    		heightString = PropertyReader.getProperty("image.original.maxHeight");
	    		break;
	    	}
    	}
    	

    	int width = Integer.parseInt(widthString);
    	int height = Integer.parseInt(heightString);

    	BufferedImage scaledImage = scaleImage(inputImage, width, height);

        FileOutputStream output = new FileOutputStream(tmpFile);
    	ImageIO.write(scaledImage, "jpg", output);
    	output.flush();
    	output.close();
    	return tmpFile;
		             
        
    	} 
    

    
    public static BufferedImage scaleImage(BufferedImage image, int width, int height) throws Exception{
    	
    	float originalWidth = image.getWidth();
    	float originalHeight = image.getHeight();
    	
    	//if()
    	
    	
    	float widthRatio = originalWidth/width;  
    	float heightRatio = originalHeight/height;  
    	
    	//If new image is larger than old, do nothing
    	if(widthRatio < 1 || heightRatio < 1 )
    	{
    		return image;
    	}
    	
    	BufferedImage resizeImageJpg = null;  
    	  
    	if (widthRatio > heightRatio)  
    	{  
    	  
    	float newHeight = (float)originalHeight/(float)originalWidth * width;  
    	  
    	resizeImageJpg = resizeImage(image, width, (int) newHeight);  
    	}  
    	else  
    	{  
    	  
    	float newWidth = (float)originalWidth/(float)originalHeight * height;  
    	  
    	resizeImageJpg = resizeImage(image, (int) newWidth, height);  
    	}  

        return resizeImageJpg;
    }
    
    
    
	    private static BufferedImage resizeImage(BufferedImage image, int width, int height)  
	    {  
		    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
		    Graphics2D g = resizedImage.createGraphics();  
		    g.drawImage(image, 0, 0, width, height, null);  
		    g.dispose();  
		    return resizedImage;  
	    }  
    
	    /*
    public static void main(String[] arg) throws Exception
    {
    	File f = new File("C:\\Users\\haarlae1\\Pictures\\TestBilder\\1.jpg");
    	BufferedImage bufferedImage = ImageIO.read(f);
    	String destDir="test";
    	String imgType="jpg";
    	
		String dir = PropertyReader.getProperty("image-upload.destDir") + "/original/" + destDir;
		ImageHelper.scaleAndStoreImage(f, f.getName(), bufferedImage, f.getName(), imgType, Type.ORIGINAL);
		
		dir = PropertyReader.getProperty("image-upload.destDir") + "/web/" + destDir;
		ImageHelper.scaleAndStoreImage(dir, Integer.parseInt(PropertyReader.getProperty("image-upload.resolution.web")), bufferedImage, f.getName(), imgType, Type.WEB);
		
		dir = PropertyReader.getProperty("image-upload.destDir") + "/thumbnails/" + destDir;
		ImageHelper.scaleAndStoreImage(dir, Integer.parseInt(PropertyReader.getProperty("image-upload.resolution.thumbnail")), bufferedImage,f.getName(), imgType, Type.THUMBNAIL);
 		
    }
    */
    
   



}
