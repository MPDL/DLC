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
package de.mpg.mpdl.dlc.ingest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class UploadedFile implements FileItem{
	
	private static Logger logger = Logger.getLogger(UploadedFile.class);
	private InputStream inputStream;
	private String name;
	private String contentType;
	private byte[] imageStream;
	
	public UploadedFile(String name, String contentType, byte[] imageStream)
	{
		this.name = name;
		this.contentType = contentType;
		this.imageStream = imageStream;
	}

	public  void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType; 
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public boolean isInMemory() {
		return false;
	}

	public long getSize() {
		return imageStream.length;
	}

	public byte[] get() 
	{ 
		return imageStream;

	}

	public String getString(String encoding)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(File file) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}

	public String getFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFieldName(String name) {
		// TODO Auto-generated method stub
		
	}

	public boolean isFormField() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFormField(boolean state) {
		// TODO Auto-generated method stub
		
	}

	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileItemHeaders getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders arg0) {
		// TODO Auto-generated method stub
		
	}
	


}
