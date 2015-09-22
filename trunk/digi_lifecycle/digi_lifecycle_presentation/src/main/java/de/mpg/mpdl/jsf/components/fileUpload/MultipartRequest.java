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
 * net/balusc/http/multipart/MultipartRequest.java
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
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

/**
 * This class represents a multipart request. It not only abstracts the <code>{@link Part}</code>
 * away, but it also provides direct access to the <code>{@link MultipartMap}</code>, so that one 
 * can get the uploaded files out of it.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2009/12/uploading-files-in-servlet-30.html
 */
public class MultipartRequest extends HttpServletRequestWrapper {

    // Vars ---------------------------------------------------------------------------------------

    private MultipartMap multipartMap;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct MultipartRequest based on the given HttpServletRequest.
     * @param request HttpServletRequest to be wrapped into a MultipartRequest.
     * @param location The location to save uploaded files in.
     * @throws IOException If something fails at I/O level.
     * @throws ServletException If something fails at Servlet level.
     */
    public MultipartRequest(HttpServletRequest request, String location)
        throws ServletException, IOException
    {
        super(request);
        this.multipartMap = new MultipartMap(request, location);
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public String getParameter(String name) {
        return multipartMap.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return multipartMap.getParameterValues(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return multipartMap.getParameterNames();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return multipartMap.getParameterMap();
    }

    /**
     * @see MultipartMap#getFile(String)
     */
    public FileItem getFile(String name) {
        return multipartMap.getFile(name);
    }

}
