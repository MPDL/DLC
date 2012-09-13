package de.mpg.mpdl.dlc.vo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.disk.DiskFileItem;

import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;


public class BatchIngestItem {
	private String contentModel;
	
	private String name;
	
	private ModsMetadata modsMetadata;
	
	private File teiFile;
	
	private List<File> imageFiles = new ArrayList<File>();
	
	private File footer;
	
	private String parentId;
	
	private List<BatchIngestItem> volumes = new ArrayList<BatchIngestItem>();
	
	private List<String> errorMessage = new ArrayList<String>();
	
	public BatchIngestItem()
	{
		
	}
	
	
	public BatchIngestItem(String contentModel, String name, ModsMetadata modsMetadata, File teiFile, ArrayList<File> imagesFiles, File footer, String parentId, List<BatchIngestItem> volumes, ArrayList<String> errorMessage)
	{
		super();
		this.contentModel = contentModel;
		this.name = name;
		this.modsMetadata = modsMetadata;
		this.teiFile = teiFile;
		this.imageFiles = imagesFiles;
		this.footer = footer;
		this.parentId = parentId;
		this.volumes = volumes;
		this.errorMessage  = errorMessage;
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

	public void setErrorMessage(ArrayList<String> errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<String> getErrorMessage() {
		return errorMessage;
	}
	
	
	
	
	
	
	
	
	

}