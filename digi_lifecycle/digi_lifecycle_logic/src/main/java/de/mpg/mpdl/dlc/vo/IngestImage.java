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
package de.mpg.mpdl.dlc.vo;

import java.io.File;
import org.apache.commons.fileupload.disk.DiskFileItem;

import de.mpg.mpdl.dlc.vo.mets.Page;

public class IngestImage implements Comparable<IngestImage> {

	
	public enum Type {
		DISK, ONLINE;
	}
	
	private Type type;
	
	//keep disk file item, otherwise it is deleted from temp
	private DiskFileItem diskFileItem;
	private File file;
	
	
	
	private Page page;
	private String url;
	
	private String name;
	

	public IngestImage(DiskFileItem diskFileItem)
	{
		this.diskFileItem = diskFileItem;
		if(diskFileItem!=null)
		{
			this.file =diskFileItem.getStoreLocation();
			//this.diskFileItem = diskFileItem;
			this.name = diskFileItem.getName();
		}
		
		this.type = Type.DISK;
	}
	  
	public IngestImage(File f)
	{    
		this.name = f.getName();
		this.file = f;
		this.type = Type.DISK;
	}
	
	public IngestImage(String url, String name)
	{
		this.url = url;
		if(name==null)
		{
			int beginIndex = url.lastIndexOf("/");
			this.name = url.substring(beginIndex+1);
			
			if(this.name.matches(".*.\\.*jpg$"))
			{
				this.name = this.name.substring(0, this.name.length()-4);
			}
		}
		else
		{
			this.name = name;
		}
		this.type = Type.ONLINE;
	}
	
	
	public IngestImage(Page p)
	{
		this(p.getContentIds(), p.getLabel());
	}
	
	

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	/*
	public DiskFileItem getDiskFileItem() {
		return diskFileItem;
	}

	public void setDiskFileItem(DiskFileItem diskFileItem) {
		this.diskFileItem = diskFileItem;
	}
	*/
	
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	

	@Override
	public int compareTo(IngestImage o) {
		
		return this.getName().compareTo(o.getName());
	}
	
	@Override
	public boolean equals(Object o)
	{
		return getName().equals(((IngestImage)o).getName());
	}


	
	public static void main(String[] args) {
		File f = new File("C:/Users/yu/Desktop/Neuer Ordner/Dg450-2004-0000a~D3GV7612.jpg");
		DiskFileItem diskFileItem = new DiskFileItem(f.getName(), "image", true, f.getName(), 0, f);
		File newFile = diskFileItem.getStoreLocation();
		System.out.println(newFile);
		
	}
}
