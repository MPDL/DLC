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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;

import de.escidoc.core.client.exceptions.application.notfound.ResourceNotFoundException;
import de.mpg.mpdl.dlc.export.Export.ExportTypes;




/**
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class ExportServlet extends HttpServlet 
{
	private static Logger logger = Logger.getLogger(ExportServlet.class);
	private static final long serialVersionUID = 1L;
	private String contentType = "";
	private int status = 404;
	private String name = "";
	private byte[] content = null;
	private OutputStream outStream = null;

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
     * Http post method for export interface.
     * @param request
     * @param response
     */
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
    {
        String identifier = request.getParameter("id");
        String format = request.getParameter("format");
        Export export = new Export();
        boolean valid = false;

		try 
		{
	    	
	        if (identifier == null || identifier == "")
	        {
	        	throw new HttpException("Parameter id is missing.");
	        }
	        if (format == null || format == "")
	        {
	        	throw new HttpException("Parameter format is missing.");
	        }
	        if (identifier != null && format != null) {
	        	this.status = 200;
	        	valid = true;
	        }
			
	        if (valid && format.equalsIgnoreCase(ExportTypes.PDF.toString()))
	        {
	        	this.contentType ="application/pdf";
	        	this.content = export.pdfExport(identifier);
	        }	        
	        if (valid && format.equalsIgnoreCase(ExportTypes.MODS.toString()))
	        {
	        	this.contentType ="application/xml";
	        	this.content = export.metsModsExport(identifier, false);	        	
	        }
	        if (valid && format.equalsIgnoreCase(ExportTypes.TEI.toString()))
	        {
	        	System.out.println("in tei export");
	        	this.contentType ="application/xml";
	        	this.content = export.teiExport(identifier);	        	
	        }

	        if (!format.equalsIgnoreCase(ExportTypes.PDF.toString()) 
	        		&& !format.equalsIgnoreCase(ExportTypes.MODS.toString())
	        				&& !format.equalsIgnoreCase(ExportTypes.TEI.toString()))
	        {
	        	response.sendError(400, "Format " + format + " not supported (allowed formats: PDF, MODS, TEI)");
	        	this.status = 400;
	        }
	        
	        this.name = identifier;
	        
			response.setContentType(this.contentType);
			response.setCharacterEncoding("UTF-8");			
			OutputStream outStream = response.getOutputStream();
			response.setStatus(this.status);			
            outStream.write(this.content);
            outStream.flush();
            outStream.close();
		} 
		catch (ResourceNotFoundException re) 
		{
			try {
				response.sendError(400, "Resource with identifier " + identifier + " not found in the repository.");
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
}
