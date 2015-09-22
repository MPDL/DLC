/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
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

import java.io.File;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class Test {
	
	 private URL serverAddress;
     private String multiPart;
     private String result;
     private String originalURL;
   
     /**
      * Main constructor
      *
      * @param urlString
      * @param queryString
      * @param mainReferenceIn
      */
     public Test(String urlString) throws Exception
     {
    	 HttpClient client = new HttpClient( );

    	// Create POST method
    	
    	 
    	 
    	 String weblintURL = "http://localhost:8080/image-upload/";
    	
    	
    	PostMethod method = new PostMethod(weblintURL);
    	Part[] parts = new Part[2];
    	parts[0] = new StringPart("directory", "test");
    	File file = new File( "C:/Users/haarlae1/Documents/Digi Lifecycle/images/2539210_09+1738_0003.max.jpg");
    	parts[1] = new FilePart( "test.tif", file );
    	HttpMethodParams params = new HttpMethodParams();
    	method.setRequestEntity(new MultipartRequestEntity(parts, params));
    	String handle = "Basic " + new String(Base64.encodeBase64("image-uploader:dlc-uploader".getBytes()));
    	method.addRequestHeader("authorization", handle);
    	// Execute and print response
    	client.executeMethod( method );
    	String response = method.getResponseBodyAsString( );
    	System.out.println(method.getStatusCode());
    	System.out.println( response );
    	method.releaseConnection( );
     }
     
     public static void main(String[] arg) throws Exception
     {
    	 //new Test("bla");
    	 

 		HttpClient client = new HttpClient();
 		//String weblinkURL = PropertyReader.getProperty("image-upload.url.upload");
 		//URL url = new URL(weblinkURL);
 		DeleteMethod delete = new DeleteMethod("http://dlc.mpdl.mpg.de/image-upload/thumbnails/escidoc_8054/F9_001_1921_0014.jpg.jpg");
 		client.executeMethod(delete);
//         delete.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
     	
//     	String username = PropertyReader.getProperty("image-upload.username");
//     	String password = PropertyReader.getProperty("image-upload.password");




     	
     	
 		String response = delete.getResponseBodyAsString();
 		System.out.println(response);
     }


}
