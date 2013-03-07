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
package de.mpg.mpdl.dlc.vo;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;


public class BatchIngestItem {
	private Long dbID;
	private String contentModel;
	private String name;
	private String dlcDirectory;
	private ModsMetadata modsMetadata;
	private IngestImage teiFile;
	private IngestImage codicologicalFile;
	private int teiPbs;
	private List<IngestImage> imageFiles = new ArrayList<IngestImage>();
	private int imageNr;
	private String imagesDirectory;
	private IngestImage footer;
	private int footerNr;
	private String parentId;
	private List<BatchIngestItem> volumes = new ArrayList<BatchIngestItem>();
	private List<String> logs = new ArrayList<String>();
	
	public BatchIngestItem()
	{

	}
	
	public BatchIngestItem(String contentModel, String name, ModsMetadata modsMetadata, IngestImage teiFile, IngestImage codicologicalFile, ArrayList<IngestImage> imageFiles, IngestImage footer, String parentId, List<BatchIngestItem> volumes, ArrayList<String> logs)
	{
		
		this.contentModel = contentModel;
		this.name = name;
		this.modsMetadata = modsMetadata;
		this.teiFile = teiFile;
		this.codicologicalFile = codicologicalFile;
		this.imageFiles = imageFiles;
		this.footer = footer;
		this.parentId = parentId;
		this.volumes = volumes;
		this.logs = logs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentModel() {
		return contentModel;
	}

	public void setContentModel(String contentModel) {
		this.contentModel = contentModel;
	}

	public ModsMetadata getModsMetadata() {
		return modsMetadata;
	}

	public void setModsMetadata(ModsMetadata modsMetadata) {
		this.modsMetadata = modsMetadata;
	}

	public IngestImage getTeiFile() {
		return teiFile;
	}

	public void setTeiFile(IngestImage teiFile) {
		this.teiFile = teiFile;
	}

	public List<IngestImage> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<IngestImage> imageFiles) {
		this.imageFiles = imageFiles;
	}
	
	

	public IngestImage getFooter() {
		return footer;
	}

	public void setFooter(IngestImage footer) {
		this.footer = footer;
	}

	public List<BatchIngestItem> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<BatchIngestItem> volumes) {
		this.volumes = volumes;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public List<String> getLogs() {
		return logs;
	}


	public void setLogs(List<String> logs) {
		this.logs = logs;
	}

	public String getDlcDirectory() {
		return dlcDirectory;
	}


	public void setDlcDirectory(String dlcDirectory) {
		this.dlcDirectory = dlcDirectory;
	}

	public int getTeiPbs() {
		return teiPbs;
	}

	public void setTeiPbs(int teiPbs) {
		this.teiPbs = teiPbs;
	}

	public int getImageNr() {
		return imageNr;
	}

	public void setImageNr(int imageNr) {
		this.imageNr = imageNr;
	}


	public String getImagesDirectory() {
		return imagesDirectory;
	}


	public void setImagesDirectory(String imagesDirectory) {
		this.imagesDirectory = imagesDirectory;
	}


	public int getFooterNr() {
		return footerNr;
	}


	public void setFooterNr(int footerNr) {
		this.footerNr = footerNr;
	}


	public Long getDbID() {
		return dbID;
	}


	public void setDbID(Long dbID) {
		this.dbID = dbID;
	}

	public IngestImage getCodicologicalFile() {
		return codicologicalFile;
	}

	public void setCodicologicalFile(IngestImage codicologicalFile) {
		this.codicologicalFile = codicologicalFile;
	}






	
	
	
	
	

}
