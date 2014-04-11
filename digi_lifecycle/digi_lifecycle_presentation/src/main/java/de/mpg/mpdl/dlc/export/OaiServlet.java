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
package de.mpg.mpdl.dlc.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;

import de.escidoc.core.client.exceptions.application.notfound.ResourceNotFoundException;
import de.mpg.mpdl.dlc.util.PropertyReader;




/**
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class OaiServlet extends HttpServlet 
{
	private static Logger logger = Logger.getLogger(OaiServlet.class);
	private static final long serialVersionUID = 1L;
	private int status = 404;
	//private byte[] content = null;
	//private OutputStream outStream = null;
	private String OAI_URL= null; 

	/**
     * Http get method for export interface.
     * @param request
     * @param response
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        this.doPost(request, response);
    }

    /**
     * Http post method for oai export interface.
     * @param request
     * @param response
     */
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
    {
        String query = request.getQueryString();
        String format = request.getParameter("metadataPrefix");
        String id = request.getParameter("identifier");
        String verb = request.getParameter("verb");   
        String resp = "";
        byte[] content = null;
		
		try 
		{	    	
	        if (format != null && format.equalsIgnoreCase("zvdd"))
	        {
	        	//retrieve result from escidoc oai provider and transform to mets/mods records
	        	if (verb != null && verb.equals("ListRecords"))
	        	{
	        		OaiParser parser = new OaiParser();
	        		query = query.replace("zvdd", "oai_dc");
	        		resp = getFromEscidocOaiProvider(query);
	        		//Check if oai response from escidoc oai provider contains error message
	        		if (!resp.contains("</error>"))
	        		{
		        		content = parser.parseListRecords(resp).getBytes("UTF-8");
	        		}
	        		else
	        		{	
	        			content = (resp).getBytes("UTF-8");	        			
	        		}
	        	}
	        	//Create mets/mods record from item
	        	else
	        	{
	        		Export export = new Export();
	        		content = export.metsModsExport(id.replace("oai:escidoc.org:", ""), true);
	        	}
	        }
	        //Take response from escidoc oai provider as is
	        else
	        {
		        content = getFromEscidocOaiProvider(query).getBytes("UTF-8");
	        }

			response.setCharacterEncoding("UTF-8");			
			OutputStream outStream = response.getOutputStream();
			response.setStatus(this.status);	
			response.setContentType("application/xml");
            outStream.write(content);
            outStream.flush();
            outStream.close();
		} 
		catch (ResourceNotFoundException re) 
		{
			try {
				response.sendError(400, "Resource with identifier " + id + " not found in the repository.");
			} catch (IOException ie) {
				System.err.println(ie.getStackTrace());
			}
		}
		catch (HttpException he) 
		{
			try {
				response.sendError(400, he.getMessage());
			} catch (IOException ie) {
				System.err.println(ie.getStackTrace());
			}
		}
		catch (Exception e) 
		{
			try {
				response.sendError(404, "Internal server error during export: " + e.getMessage());
				System.out.println(e.getLocalizedMessage());
			} catch (IOException ie) {
				System.err.println(ie.getStackTrace());
			}
		}
    }
    
    /**
     * This methods calls the escidoc oai provider and forwards the response.
     * @param query
     * @return the response from the escidoc oai provider
     * @throws MalformedURLException
     * @throws IOException
     * @throws URISyntaxException
     */
	private String getFromEscidocOaiProvider(String query) throws MalformedURLException, IOException, URISyntaxException 
	{
		StringBuffer resultXml = new StringBuffer();
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;
        
        //Set url of escidoc oai provider
		String context = "oai/?";
		String base ="";
		try {
			base = PropertyReader.getProperty("escidoc.common.login.url");
			//base = "http://dlc.mpdl.mpg.de:8080/";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
		this.OAI_URL = base + context;

    	URL url = new URL(OAI_URL + query);    	
    	conn = url.openConnection();

        HttpURLConnection httpConn = (HttpURLConnection) conn;
        this.status = httpConn.getResponseCode();
		
        switch (status)
        {
        	case 200:
			logger.info("Source responded with 200.");			
            isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
            bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
            	resultXml.append(line + "\n");
            }
            httpConn.disconnect(); 			
            break;
        }    
		return resultXml.toString();
	}
}
