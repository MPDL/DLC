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
package de.mpg.mpdl.jsf.components.fileUpload;
/*
 * net/balusc/http/multipart/MultipartFilter.java
 * 
 * Copyright (C) 2009 BalusC
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * This filter detects <tt>multipart/form-data</tt> and <tt>multipart/mixed</tt> POST requests and
 * will then replace the <code>HttpServletRequest</code> by a <code>{@link MultipartRequest}</code>.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2009/12/uploading-files-in-servlet-30.html
 */

@WebFilter(filterName = "FileUploadFilter", urlPatterns = { "/*" })
public class MultipartRequestFilter implements Filter {

	private static Logger logger = Logger.getLogger(MultipartRequestFilter.class);
    // Constants ----------------------------------------------------------------------------------

    private static final String INIT_PARAM_LOCATION = "location";
    private static final String REQUEST_METHOD_POST = "POST";
    private static final String CONTENT_TYPE_MULTIPART = "multipart/";

    // Vars --------------------------------------------------------------------------------------

    private String location;

    // Actions ------------------------------------------------------------------------------------

    
    public void init(FilterConfig config) throws ServletException {
        this.location = config.getInitParameter(INIT_PARAM_LOCATION);
    }

   
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        if (ServletFileUpload.isMultipartContent(httpRequest)) {
            request = new MultipartRequest(httpRequest, location);
          
            
            //System.out.println(request);
            //System.out.println(httpRequest.getHeader("Cookie"));
        }
       
       
        //System.out.println(request.getParameter("form1"));
        chain.doFilter(request, response);
        

    }

    public void destroy() {
        // NOOP.
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Returns true if the given request is a multipart request.
     * @param request The request to be checked.
     * @return True if the given request is a multipart request.
     */
    public static final boolean isMultipartRequest(HttpServletRequest request) {
        return REQUEST_METHOD_POST.equalsIgnoreCase(request.getMethod())
            && request.getContentType() != null
            && request.getContentType().toLowerCase().startsWith(CONTENT_TYPE_MULTIPART);
    }

}
