/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.images;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.apache.log4j.Logger;
import org.imgscalr.Scalr;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codec.PNGEncodeParam;
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
	
	public static String appendPaths(String path1, String path2)
	{

		String appendedPath = "";
		if(path1.endsWith("/") && path2.startsWith("/"))
		{
			appendedPath = path1 + path2.substring(1); 
		}
		else if(!path1.endsWith("/") && !path2.startsWith("/"))
		{
			appendedPath = path1 + "/" + path2; 
		}
		else
		{
			appendedPath = path1 + path2; 
		}
		return appendedPath;
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
			
			String url = appendPaths(mainUrl, typeUrl);
			url = appendPaths(url, subUrl);
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

    	Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
    	ImageWriter writer = (ImageWriter)iter.next();
    	ImageWriteParam param = writer.getDefaultWriteParam();
    	param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    	param.setCompressionQuality(0.9f);
    	
        FileOutputStream output = new FileOutputStream(tmpFile);
    	ImageIO.write(scaledImage, "JPG", output);
    	output.flush();
    	output.close();
    	scaledImage.flush();
    	inputImage.flush();
    	
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
    	
    	BufferedImage resizeImage = null;  
    	  
    	if (widthRatio > heightRatio)  
    	{  
    	  
    	float newHeight = (float)originalHeight/(float)originalWidth * width;  
    	  
    	resizeImage = resizeImage(image, width, (int) newHeight);  
    	}  
    	else  
    	{  
    	  
    	float newWidth = (float)originalWidth/(float)originalHeight * height;  
    	  
    	resizeImage = resizeImage(image, (int) newWidth, height);  
    	}  

        return resizeImage;
        
    }
    
    
    
	    private static BufferedImage resizeImage(BufferedImage image, int width, int height)  
	    { long time = System.currentTimeMillis();
	    	BufferedImage scaledImage = Scalr.resize(image, Scalr.Method.QUALITY, width, height);
	    	
	    	
	    	long neededTime = System.currentTimeMillis() - time;
	    	System.err.println("Conversion time: " + neededTime);
	    	return scaledImage;
	    	/*
		    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
		    Graphics2D g = resizedImage.createGraphics();  
		    g.drawImage(image, 0, 0, width, height, null);  
		    g.dispose();  
		    return resizedImage;
		    */  
	    }
	    
	    
		public static File mergeImages(File inputFile, File footer){
				try {
			       //creating a bufferd image array from image files  
					BufferedImage scanBI = ImageIO.read(inputFile);
			        BufferedImage footerBI = ImageIO.read(footer);
			        
			        int scanWidth = scanBI.getWidth();
			        int footerWidth = footerBI.getWidth();
			        int scanHeight = scanBI.getHeight();
			        int footerHeight = footerBI.getHeight();
			        
			        if(scanWidth < footerWidth)
			        {
			        	footerBI = scaleImage(footerBI, scanWidth, scanHeight);
			        	footerHeight = footerBI.getHeight();
			        }
		            BufferedImage finalImg  = new BufferedImage(scanWidth, scanHeight+ footerHeight, scanBI.getType());
		        	finalImg.createGraphics().drawImage(scanBI,0,0, null);
		        	finalImg.createGraphics().drawImage(footerBI,0, scanHeight, null);
		            ImageIO.write(finalImg, "JPG", inputFile);
		            scanBI.flush();
		            footerBI.flush();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  

		        return inputFile;

		    }     
	    
		
		/** 
	     * converts tif to jpg 
	     * @param String Source 
	     * @throws IOException 
	     */  
	    public static File tiffToJpeg(File tiffFile, String name) 
	    {  
	    	
	    	File tmpFile = null;
	    	try
	    	{
	    		tmpFile =File.createTempFile(name,"jpg.tmp");
	    		logger.info("tmpFile Name = " + tmpFile.getName() + " | Path = " + tmpFile.getAbsolutePath());
	    		logger.info("Name = " + tiffFile.getName() + " | Path = " + tiffFile.getAbsolutePath() + " | Size = " + tiffFile.length());
		        SeekableStream s = new FileSeekableStream(tiffFile);
		        TIFFDecodeParam param = new TIFFDecodeParam();
		        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
		        RenderedImage ri = dec.decodeAsRenderedImage(0);
		        
		        FileOutputStream fos = new FileOutputStream(tmpFile);
		        JPEGEncodeParam jParam = new JPEGEncodeParam();
		        jParam.setQuality(1.0f);
		        ImageEncoder imageEncoder = ImageCodec.createImageEncoder("JPEG", fos, jParam);
		        imageEncoder.encode(ri);
		        
		        fos.flush();
		        fos.close();
	    	}catch(Exception e)
	    	{
	    		logger.error("cann not convert tiff to jpeg. Error: " + e.getMessage() + " | Size = " + tmpFile.length(), e);
	    	}
	        return tmpFile;
	    } 
	    
	    
	    public static File tiffToPng(File tiffFile, String name) 
	    {  
	    	long startTime = System.currentTimeMillis();
	    	File tmpFile = null;
	    	try
	    	{
	    		tmpFile =File.createTempFile(name,"png.tmp");
	    		logger.info("tmpFile Name = " + tmpFile.getName() + " | Path = " + tmpFile.getAbsolutePath());
	    		logger.info("Name = " + tiffFile.getName() + " | Path = " + tiffFile.getAbsolutePath() + " | Size = " + tiffFile.length());
		        SeekableStream s = new FileSeekableStream(tiffFile);
		        TIFFDecodeParam param = new TIFFDecodeParam();
		        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
		        RenderedImage ri = dec.decodeAsRenderedImage(0);
		        
		        FileOutputStream fos = new FileOutputStream(tmpFile);
		      
		        //ImageEncoder imageEncoder = ImageCodec.createImageEncoder(name, dst, param)("PNG", fos);
		        //imageEncoder.encode(ri);
		        ImageIO.write(ri, "png", fos);
		        
		        fos.flush();
		        fos.close();
	    	}catch(Exception e)
	    	{
	    		logger.error("cann not convert tiff to png. Error: " + e.getMessage() + " | Size = " + tmpFile.length());
	    	}
	    	long time = System.currentTimeMillis() - startTime;
	    	System.err.println("Conversion time tiffToPng: "  + time);
	        return tmpFile;
	    } 
	    
	    public static File jpegToPng(File jpegFile, String name) 
	    {  
	    	long startTime = System.currentTimeMillis();
	    	File tmpFile = null;
	    	try
	    	{
	    		tmpFile =File.createTempFile(name,"png.tmp");
	    		BufferedImage bufferedImage = ImageIO.read(jpegFile);
		      
		        //ImageEncoder imageEncoder = ImageCodec.createImageEncoder(name, dst, param)("PNG", fos);
		        //imageEncoder.encode(ri);
		        ImageIO.write(bufferedImage, "png", tmpFile);
		        
		        
	    	}catch(Exception e)
	    	{
	    		logger.error("cann not convert tiff to jpeg. Error: " + e.getMessage() + " | Size = " + tmpFile.length());
	    	}
	    	
	    	long time = System.currentTimeMillis() - startTime;
	    	System.err.println("Conversion time jpegToPng: "  + time);
	        return tmpFile;
	    }    
	    
	    public static File pngToJpeg(File pngFile, String name) 
	    {	
	    	File tmpFile = null;
	    	try{
  
	    		tmpFile = File.createTempFile(name, "jpg.tmp");
	    		BufferedImage image = ImageIO.read(pngFile);
	    		BufferedImage bufferedImage = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics2D g = bufferedImage.createGraphics();
			    //Color.WHITE estes the background to white. You can use any other color
			    g.drawImage(image, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), Color.WHITE, null);
			    ImageIO.write(bufferedImage,"JPEG", tmpFile); 

			    
/*		    	
 * Problem with transparent png Files	
	    		tmpFile = File.createTempFile(name, "jpg.tmp");
		    	SeekableStream s = new FileSeekableStream(pngFile);
		    	PNGDecodeParam param = new PNGDecodeParam();
		        ImageDecoder dec = ImageCodec.createImageDecoder("png", s, param);		        
		        RenderedImage ri = dec.decodeAsRenderedImage(0);
		        
		        FileOutputStream fos = new FileOutputStream(tmpFile);
		        JPEGEncodeParam jParam = new JPEGEncodeParam();
		        jParam.setQuality(1.0f);
		        ImageEncoder imageEncoder = ImageCodec.createImageEncoder("JPEG", fos, jParam);
		        imageEncoder.encode(ri);
		        fos.flush();
		        fos.close();
*/		        
		        
	    	}catch(Exception e)
	    	{
	    		logger.error("cann not convert png to jpeg. Error: " + e.getMessage());
	    	}
	    	
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
    
		public static String getJPEGFilename(String filename)
		{
			
//			if(filename.matches("\\.(jpg|JPEG|jpeg|JPG|Jpeg)$"))
			// if(filename.matches(".+?(\\.jpg|\\.JPEG|\\.jpeg|\\.JPG|\\.Jpeg)"))
			if(filename.matches("^.*?(jpg|jpeg|JPG|JPEG)$"))
			{
				return filename;
			}
			else
			{
				return filename + ".jpg";
			}
		}
		
		public static String getPNGFilename(String filename)
		{
			
//			if(filename.matches("\\.(jpg|JPEG|jpeg|JPG|Jpeg)$"))
			// if(filename.matches(".+?(\\.jpg|\\.JPEG|\\.jpeg|\\.JPG|\\.Jpeg)"))
			if(filename.matches("^.*?(png|PNG)$"))
			{
				return filename;
			}
			else
			{
				return filename + ".png";
			}
		}

		private static String splitSuffix(String name)
		{
			int dot = name.lastIndexOf('.');
//			String extension = (dot == -1) ? "" : name.substring(dot+1);
			String base = (dot == -1) ? name : name.substring(0, dot);
			return base;
		}
		
		
	public static void convertTiffToJpeg(String inputDic, String outputDic) throws IOException
	{
    	File images = new File(inputDic);
    	File[] list = images.listFiles();
    	if(list.length >0)
    	{
    		for(File i : list)
    		{
    			String name = splitSuffix(i.getName());
    			String jpegName = name + ".jpg";
    			File jpegImage = new File(outputDic + "//"+jpegName);


    	        SeekableStream s = new FileSeekableStream(i);
    	        TIFFDecodeParam param = new TIFFDecodeParam();
    	        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
    	        RenderedImage ri = dec.decodeAsRenderedImage(0);
    	        
    	        FileOutputStream fos = new FileOutputStream(jpegImage);
    	        JPEGEncodeParam jParam = new JPEGEncodeParam();
    	        ImageEncoder imageEncoder = ImageCodec.createImageEncoder("JPEG", fos, jParam);
    	        imageEncoder.encode(ri);
    	        fos.flush();
    	        fos.close();
    			
    		}
    	}
	}
	
	 public static File ToJpeg(File pngFile) throws IOException
	 {
		 File jpgFile = new File("C:\\Users\\yu\\Desktop\\test.jpeg");
		 BufferedImage image = ImageIO.read(pngFile);
		 
	     BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	     Graphics2D g = bufferedImage.createGraphics();
	     //Color.WHITE estes the background to white. You can use any other color
	     g.drawImage(image, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), Color.WHITE, null);
	     ImageIO.write(bufferedImage,"JPEG", jpgFile);      

	     return jpgFile;
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
 
//    	String inputDic = "C://Doku//dlc//TEST_MPDL//ROM//batch//images//Va6400-1990a";
//    	String outputDic = "C://Doku//dlc//TEST_MPDL//ROM//batch//jpegimages//Va6400-1990a"; 
//    	convertTiffToJpeg(inputDic, outputDic);   
    	
    	File pngFile = new File("C:\\Users\\yu\\Desktop\\minilouge3_300dpi_16bit_adobergb.tif");
    	File jpeg = ToJpeg(pngFile);
    			
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
