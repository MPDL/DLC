package de.mpg.mpdl.dlc.vo;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;


public class BatchIngestItem {
	private int dbID;
	private String contentModel;
	private String name;
	private String dlcDirectory;
	private ModsMetadata modsMetadata;
	private File teiFile;
	private int teiPbs;
	private List<File> imageFiles = new ArrayList<File>();
	private int imageNr;
	private String imagesDirectory;
	private File footer;
	private int footerNr;
	private String parentId;
	private List<BatchIngestItem> volumes = new ArrayList<BatchIngestItem>();
	private List<String> logs = new ArrayList<String>();
	
	public BatchIngestItem()
	{
		
	}
	
	
	public BatchIngestItem(String contentModel, String name, ModsMetadata modsMetadata, File teiFile, ArrayList<File> imageFiles, File footer, String parentId, List<BatchIngestItem> volumes, ArrayList<String> logs)
	{
		
		this.contentModel = contentModel;
		this.name = name;
		this.modsMetadata = modsMetadata;
		this.teiFile = teiFile;
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

	public File getTeiFile() {
		return teiFile;
	}

	public void setTeiFile(File teiFile) {
		this.teiFile = teiFile;
	}

	public List<File> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<File> imageFiles) {
		this.imageFiles = imageFiles;
	}
	
	

	public File getFooter() {
		return footer;
	}

	public void setFooter(File footer) {
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


	public int getDbID() {
		return dbID;
	}


	public void setDbID(int dbID) {
		this.dbID = dbID;
	}






	
	
	
	
	

}
