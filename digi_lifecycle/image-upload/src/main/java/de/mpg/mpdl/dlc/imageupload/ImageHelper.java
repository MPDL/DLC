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
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.imageupload;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;


public class ImageHelper{
	
	
	
	public enum Type
	{
		THUMBNAIL, WEB, ORIGINAL;
	}
	
	
	
	
    public static File scaleAndStoreImage(String destDir, int res, BufferedImage input, String fileName, String imgType, Type type) throws Exception{
	         	BufferedImage scaledImage;

		        scaledImage = scaleImage(input, res, type);
		        
		        File destDirFile = new File(destDir);
		        if(!destDirFile.exists())
				{
					destDirFile.mkdirs();
				}
		        
		       
		        File file = new File(destDir,fileName);
		        if(!file.exists())
		        {
		        	file.createNewFile();
		        }
		        
		        
		        FileOutputStream output = new FileOutputStream(file);
	        	ImageIO.write(scaledImage, imgType, output);
	        	output.flush();
	        	output.close();
	        	return file;
		        
		       
		        // use imageIO.write to encode the image back into a byte[]
		        
        	
		        /*
        	catch(Exception e){
        		Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        		String test = navigation.getApplicationUrl() + "resources/icon/defaultThumb.gif";
        		URL noThumbUrl = new URL(test);
        		int contentLength = noThumbUrl.openConnection().getContentLength();
        		InputStream openStream =noThumbUrl.openStream();
        		byte[] data = new byte[contentLength];
        		openStream.read(data);
        		openStream.close();

        		url = ImageHelper.uploadFile(data, mimetype, userHandle);
        		*/
        
    	} 
    

    
    public static BufferedImage scaleImage(BufferedImage image, int size, Type type) throws Exception{
    	int width = image.getWidth(null);
    	int height = image.getHeight(null);
    	BufferedImage newImg = null;
    	Image rescaledImage;
    	

    	if(Type.ORIGINAL.equals(type))
    	{
    		 rescaledImage = image;
    	}
    	
    	else{
    		
    	
    		/*
	    	if(width > height)
	    	{
	    		if(Type.THUMBNAIL.equals(type))
	    		{
		    		newImg= new BufferedImage(height, height,BufferedImage.TYPE_INT_RGB);
		        	Graphics g1 = newImg.createGraphics();
		        	g1.drawImage(image, (height-width)/2, 0, null);
		        	if(height>size)
		        		rescaledImage = newImg.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
		        	else
		        		rescaledImage = newImg;
	    		}
	    		else
	    			rescaledImage = image.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
	    	}
	    	else  
	    	{
	    	
	    		if(Type.THUMBNAIL.equals(type))
	    		{
		    		newImg= new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
		        	Graphics g1 = newImg.createGraphics();
		        	g1.drawImage(image, 0, (width-height)/2, null);
		        	if(width>size)
		        		rescaledImage = newImg.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
		        	else
		        		rescaledImage = newImg;
	    		}
	    		else
	    		*/
	            	rescaledImage = image.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
	
	    	//}
	    	
	        
    	}
    	
    	BufferedImage rescaledBufferedImage = new BufferedImage(rescaledImage.getWidth(null), rescaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g2 = rescaledBufferedImage.getGraphics();
        g2.drawImage(rescaledImage, 0, 0, null);

        return rescaledBufferedImage;
    }
    
    public static void main(String[] arg) throws Exception
    {
    	File f = new File("C:\\Users\\haarlae1\\Pictures\\TestBilder\\1.jpg");
    	BufferedImage bufferedImage = ImageIO.read(f);
    	String destDir="test";
    	String imgType="jpg";
    	
		String dir = PropertyReader.getProperty("image-upload.destDir") + "/original/" + destDir;
		ImageHelper.scaleAndStoreImage(dir, 0, bufferedImage, f.getName(), imgType, Type.ORIGINAL);
		
		dir = PropertyReader.getProperty("image-upload.destDir") + "/web/" + destDir;
		ImageHelper.scaleAndStoreImage(dir, Integer.parseInt(PropertyReader.getProperty("image-upload.resolution.web")), bufferedImage, f.getName(), imgType, Type.WEB);
		
		dir = PropertyReader.getProperty("image-upload.destDir") + "/thumbnails/" + destDir;
		ImageHelper.scaleAndStoreImage(dir, Integer.parseInt(PropertyReader.getProperty("image-upload.resolution.thumbnail")), bufferedImage,f.getName(), imgType, Type.THUMBNAIL);
 		
    }
    
   



}
