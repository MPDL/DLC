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
package de.mpg.mpdl.jsf.components.fileUpload;
/*
 * net/balusc/http/multipart/MultipartMap.java
 *
 * Copyright (C) 2009 BalusC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;

/**
 * The MultipartMap. It simulates the <code>HttpServletRequest#getParameterXXX()</code> methods to
 * ease the processing in <code>@MultipartConfig</code> servlets. You can access the normal request
 * parameters by <code>{@link #getParameter(String)}</code> and you can access multiple request
 * parameter values by <code>{@link #getParameterValues(String)}</code>.
 * <p>
 * On creation, the <code>MultipartMap</code> will put itself in the request scope, identified by
 * the attribute name <code>parts</code>, so that you can access the parameters in EL by for example
 * <code>${parts.fieldname}</code> where you would have used <code>${param.fieldname}</code>. In
 * case of file fields, the <code>${parts.filefieldname}</code> returns a <code>{@link File}</code>.
 * <p>
 * It was a design decision to extend <code>HashMap&lt;String, Object&gt;</code> instead of having
 * just <code>Map&lt;String, String[]&gt;</code> and <code>Map&lt;String, File&gt;</code>
 * properties, because of the accessibility in Expression Language. Also, when the value is obtained
 * by <code>{@link #get(Object)}</code>, as will happen in EL, then multiple parameter values will
 * be converted from <code>String[]</code> to <code>List&lt;String&gt;</code>, so that you can use
 * it in the JSTL <code>fn:contains</code> function.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2009/12/uploading-files-in-servlet-30.html
 */
public class MultipartMap extends HashMap<String, Object> {

	private static Logger logger = Logger.getLogger(MultipartMap.class);
	
    // Constants ----------------------------------------------------------------------------------

    private static final String ATTRIBUTE_NAME = "parts";
    private static final String CONTENT_DISPOSITION = "content-disposition";
    private static final String CONTENT_DISPOSITION_FILENAME = "filename";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    // Vars ---------------------------------------------------------------------------------------

    private String encoding;
    private String location;
    private boolean multipartConfigured;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct multipart map based on the given multipart request and the servlet associated with
     * the request. The file upload location will be extracted from <code>@MultipartConfig</code>
     * of the servlet. When the encoding is not specified in the given request, then it will default
     * to <tt>UTF-8</tt>.
     * @param multipartRequest The multipart request to construct the multipart map for.
     * @param servlet The servlet which is responsible for the given request.
     * @throws ServletException If something fails at Servlet level.
     * @throws IOException If something fails at I/O level.
     */
    public MultipartMap(HttpServletRequest multipartRequest, Servlet servlet)
        throws ServletException, IOException
    {
        this(multipartRequest, new MultipartConfigElement(
            servlet.getClass().getAnnotation(MultipartConfig.class)).getLocation(), true);
    }

    /**
     * Construct multipart map based on the given multipart request and file upload location. When
     * the encoding is not specified in the given request, then it will default to <tt>UTF-8</tt>.
     * @param multipartRequest The multipart request to construct the multipart map for.
     * @param location The location to save uploaded files in.
     * @throws ServletException If something fails at Servlet level.
     * @throws IOException If something fails at I/O level.
     */
    public MultipartMap(HttpServletRequest multipartRequest, String location)
        throws ServletException, IOException
    {
        this(multipartRequest, location, false);
    }

    /**
     * Global constructor.
     */
    private MultipartMap
        (HttpServletRequest multipartRequest, String location, boolean multipartConfigured)
            throws ServletException, IOException
    {
        //multipartRequest.setAttribute(ATTRIBUTE_NAME, this);

        this.encoding = multipartRequest.getCharacterEncoding();
        if (this.encoding == null) {
            multipartRequest.setCharacterEncoding(this.encoding = DEFAULT_ENCODING);
        }
        this.location = location;
        this.multipartConfigured = multipartConfigured; 
  
        
        /** NEW */
        try {
			DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
			diskFileItemFactory.setRepository(new File(VolumeServiceBean.tmpDir));
			diskFileItemFactory.setSizeThreshold(0);
			
			//System.out.println("Detected Multipart Request. Saving temp files to " +System.getProperty("java.io.tmpdir"));
			
			ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
			
			List<FileItem> items = servletFileUpload.parseRequest(multipartRequest);
			
			for(FileItem item : items) { 
				if(item.isFormField())
				{
					
			        String[] values = (String[]) super.get(item.getFieldName());
					if (values == null) {
			            // Not in parameter map yet, so add as new value.
			            put(item.getFieldName(), new String[] { item.getString() });
			        } else {
			            // Multiple field values, so add new value to existing array.
			            int length = values.length;
			            String[] newValues = new String[length + 1];
			            System.arraycopy(values, 0, newValues, 0, length);
			            newValues[length] = item.getString();
			            put(item.getFieldName(), newValues);
			        }
				}
				else {
					put(item.getFieldName(), item);
					logger.info("File found: " + item.getName());
				}
			}
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}
        
		/** NEW END **/
		
        /*
        for (Part part : multipartRequest.getParts()) { 
            String filename = getFilename(part);
            if (filename == null) {
                processTextPart(part);
            } else if (!filename.isEmpty()) {
                processFilePart(part, filename);
            }
        }
        */
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        if (value instanceof String[]) {
            String[] values = (String[]) value;
            return values.length == 1 ? values[0] : Arrays.asList(values);
        } else {
            return value; // Can be File or null.
        }
    }

    /**
     * @see ServletRequest#getParameter(String)
     * @throws IllegalArgumentException If this field is actually a File field.
     */
    public String getParameter(String name) {
        Object value = super.get(name);
        if (value instanceof File) {
            throw new IllegalArgumentException("This is a File field. Use #getFile() instead.");
        }
        String[] values = (String[]) value;
        return values != null ? values[0] : null;
    }

    /**
     * @see ServletRequest#getParameterValues(String)
     * @throws IllegalArgumentException If this field is actually a File field.
     */
    public String[] getParameterValues(String name) {
        Object value = super.get(name);
        if (value instanceof File) {
            throw new IllegalArgumentException("This is a File field. Use #getFile() instead.");
        }
        return (String[]) value;
    }

    /**
     * @see ServletRequest#getParameterNames()
     */
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(keySet());
    }

    /**
     * @see ServletRequest#getParameterMap()
     */
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<String, String[]>();
        for (Entry<String, Object> entry : entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String[]) {
                map.put(entry.getKey(), (String[]) value);
            } else {
                map.put(entry.getKey(), new String[] { ((File) value).getName() });
            }
        }
        return map;
    }

    /**
     * Returns uploaded file associated with given request parameter name.
     * @param name Request parameter name to return the associated uploaded file for.
     * @return Uploaded file associated with given request parameter name.
     * @throws IllegalArgumentException If this field is actually a Text field.
     */
    public FileItem getFile(String name) {
        Object value = super.get(name);
        if (value instanceof String[]) {
            throw new IllegalArgumentException("This is a Text field. Use #getParameter() instead.");
        }
        return (FileItem) value;
    }

    // Helpers ------------------------------------------------------------------------------------


}
