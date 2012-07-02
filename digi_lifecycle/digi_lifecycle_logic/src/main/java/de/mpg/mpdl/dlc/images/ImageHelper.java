package de.mpg.mpdl.dlc.images;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.apache.log4j.Logger;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

import de.mpg.mpdl.dlc.util.PropertyReader;
  


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
	    
		public static File mergeImages(File inputFile, File footer) throws IOException {
		       //creating a bufferd image array from image files  
		        BufferedImage scanBI = ImageIO.read(inputFile);  
		        BufferedImage footerBI = ImageIO.read(footer);
		        
		        int scanWidth = scanBI.getWidth();
		        int footerWidth = footerBI.getWidth();
		        int scanHeight = scanBI.getHeight();
		        int footerHeight = footerBI.getHeight();
//		        if(scanWidth == footerWidth)
//		        {
		            BufferedImage finalImg  = new BufferedImage(scanWidth, scanHeight+ footerHeight, scanBI.getType());
		        	finalImg.createGraphics().drawImage(scanBI,0,0, null);
		        	finalImg.createGraphics().drawImage(footerBI,0, scanHeight, null);
		            ImageIO.write(finalImg, "jpg", inputFile);
//		        }
		        return inputFile;

		    }     
	    
		
		/** 
	     * converts tif to jpg 
	     * @param String Source 
	     * @throws IOException 
	     */  
	    public static File tiffToJpeg(File tiffFile, String name) throws Exception 
	    {  
	    	
	    	File tmpFile = File.createTempFile(name,"jpg.tmp");
	        SeekableStream s = new FileSeekableStream(tiffFile);
	        TIFFDecodeParam param = new TIFFDecodeParam();
	        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
	        RenderedImage ri = dec.decodeAsRenderedImage(0);
	        
	        FileOutputStream fos = new FileOutputStream(tmpFile);
	        JPEGEncodeParam jParam = new JPEGEncodeParam();
	        ImageEncoder imageEncoder = ImageCodec.createImageEncoder("JPEG", fos, jParam);
	        imageEncoder.encode(ri);
	        fos.close();
	        
	        return tmpFile;
	    	
	    }  
	    
		
		public static RenderedImage readAndTile(File f) throws Exception {
			   //create ImageInputStream
			   ImageInputStream iis = ImageIO.createImageInputStream(f);

			   ImageReader reader = null;

			   Iterator iter = ImageIO.getImageReaders(iis);
			   while (iter.hasNext()) {
			       reader = (ImageReader) iter.next();
			   }

			   if (reader != null) {
			       reader.setInput(iis);
			             //nothing special here
			       ImageReadParam param = new ImageReadParam();
			       RenderedImage renderedImage = reader.readAsRenderedImage(0, param);

			       //not sure if this line needed or not
			       renderedImage = PlanarImage.wrapRenderedImage(renderedImage);
			             ImageLayout layout = new ImageLayout(renderedImage);
			       layout.setTileWidth(500);
			       layout.setTileHeight(500);

			       RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
			layout);

			       ParameterBlock pb = new ParameterBlock();
			       pb.addSource(renderedImage);
			       RenderedImage renderedOp = JAI.create("format", pb, hints);
			       return renderedOp;

			   }
			   return null;
			} 
    
	    
    public static void main(String[] arg) throws Exception
    {
    	/*
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
		
		 */
 
		File tiffScan = new File("C://Users//yu//Desktop//test//dt9bg18q+bd1=d0001.tif");	
		File tiffFooter = new File("C://Users//yu//Desktop//test//footer_mpirg.tif");
		String dir= "C://Users//yu//Desktop//test//";


		try {
//			tiffToJpeg(tiffScan, "scan.jpg", dir);
//			tiffToJpeg(tiffFooter, "footer.jpg", dir);
			
			
			RenderedImage ri = readAndTile(tiffScan);
			ImageIO.write(ri, "jpg", new File("C://Users//yu//Desktop//test//scan.jpg"));
			
			RenderedImage ri2 = readAndTile(tiffFooter);
			ImageIO.write(ri2, "jpg", new File("C://Users//yu//Desktop//test//footer.jpg"));

			mergeImages(new File("C://Users//yu//Desktop//test//scan.jpg"), new File("C://Users//yu//Desktop//test//footer.jpg"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
    }
   
    
    /* display image metadata
		public static void displayMetadata(File file) throws Exception {
			
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                // pick the first available ImageReader
                ImageReader reader = readers.next();
                // attach source to the reader
                reader.setInput(iis, true);
                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);
                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                    System.out.println( "Format name: " + names[ i ] );
                    displayMetadata(metadata.getAsTree(names[i]), 0);
                }
            }

	    }
		public static void indent(int level) {
	        for (int i = 0; i < level; i++)
	            System.out.print("    ");
	    }
		static void displayMetadata(Node node, int level) {
	        // print open tag of element
	        indent(level);
	        System.out.print("<" + node.getNodeName());
	        NamedNodeMap map = node.getAttributes();
	        if (map != null) {

	            // print attribute values
	            int length = map.getLength();
	            for (int i = 0; i < length; i++) {
	                Node attr = map.item(i);
	                System.out.print(" " + attr.getNodeName() +
	                                 "=\"" + attr.getNodeValue() + "\"");
	            }
	        }

	        Node child = node.getFirstChild();
	        if (child == null) {
	            // no children, so close element and return
	            System.out.println("/>");
	            return;
	        }

	        // children, so close current tag
	        System.out.println(">");
	        while (child != null) {
	            // print children recursively
	            displayMetadata(child, level + 1);
	            child = child.getNextSibling();
	        }

	        // print close tag of element
	        indent(level);
	        System.out.println("</" + node.getNodeName() + ">");
	    }

     */
    
   



}
