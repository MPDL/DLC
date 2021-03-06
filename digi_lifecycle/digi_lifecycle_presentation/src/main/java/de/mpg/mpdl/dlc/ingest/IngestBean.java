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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.richfaces.event.DropEvent;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.component.Component;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.CreateVolumeServiceBean;
import de.mpg.mpdl.dlc.beans.CreateVolumeThread;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.list.AllVolumesBean;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.searchLogic.FilterBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.viewer.ViewPages;
import de.mpg.mpdl.dlc.vo.IngestImage;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsDate;
import de.mpg.mpdl.dlc.vo.mods.ModsIdentifier;
import de.mpg.mpdl.dlc.vo.mods.ModsLanguage;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.mods.ModsNote;
import de.mpg.mpdl.dlc.vo.mods.ModsPart;
import de.mpg.mpdl.dlc.vo.mods.ModsPhysicalDescription;
import de.mpg.mpdl.dlc.vo.mods.ModsPublisher;
import de.mpg.mpdl.dlc.vo.mods.ModsRelatedItem;
import de.mpg.mpdl.dlc.vo.mods.ModsTitle;
import de.mpg.mpdl.jsf.components.fileUpload.FileUploadEvent;



@ManagedBean
@ViewScoped
@URLMapping(id="upload", viewId = "/ingest.xhtml", pattern = "/upload/#{ingestBean.volumeId}")
public class IngestBean{
 
	private static Logger logger = Logger.getLogger(IngestBean.class);
   
	private List<IngestImage> imageFiles = new ArrayList<IngestImage>();
	private DiskFileItem footer;
	
	private DiskFileItem mabFile;
	private ModsMetadata modsMetadata  = new ModsMetadata();
	
	private DiskFileItem teiFile;
	private List<XdmNode> teiPbFacsValues;
	
	private DiskFileItem codicologicalFile;

	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int moveTo;
	
	private String selectedContextId;
	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();

	private String selectedContentModel;
	private List<SelectItem> contentModelItems = new ArrayList<SelectItem>();
	
	private String selectedMultiVolumeId;
	private List<SelectItem> multiVolItems = new ArrayList<SelectItem>();
	
	private boolean hasMab = true;

	private VolumeServiceBean volumeService = new VolumeServiceBean();
	private CreateVolumeServiceBean createVolumeService = new CreateVolumeServiceBean();
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private Context context;
	

//	private SearchBean searchBeans = new SearchBean();
	
	private FilterBean filterBean = new FilterBean();
	
	private String volumeId;
	
	private Volume volume;
	
	private List<String> pagesOfVolume = new ArrayList<String>();
	
	private String sortImagesAlgorithm;
	
	private boolean sortableByTei = false;
	
	private Tika tika = new Tika();

	private DiskFileItem recentlyUpladedMabFile;

	private DiskFileItem recentlyUpladedTeiFile;

	private DiskFileItem recentlyUpladedFooterFile;
	
	private DiskFileItem recentlyUpladedCodicologigalFile;

	private List<DiskFileItem> recentlyUploadedImageFiles = new ArrayList<DiskFileItem>();
	
	private String uploadedFilesByPlupload;
	
	 
	@URLAction(onPostback=false)
	public void loadContext()
	{ 
		if(!volumeId.equals("new") && volume==null)
		{ 
			try {
				//this.volume = volumeService.retrieveVolume(volumeId, loginBean.getUserHandle());
				clearAllData();
				this.volume = volumeService.loadCompleteVolume(volumeId,  loginBean.getUserHandle());
				this.context = contextServiceBean.retrieveContext(volume.getItem().getProperties().getContext().getObjid(), null);
				//if(mabFile == null)
				
				if("".equalsIgnoreCase(volume.getModsMetadata().getCatalogueId_001()) || volume.getModsMetadata().getCatalogueId_001() == null)
					this.modsMetadata = retrieveModsMetadata(volume.getModsMetadata());
				else
					this.modsMetadata = volume.getModsMetadata();
				if(volume.getMets()!=null)
				{
					for(Page p : volume.getMets().getPages())
					{	
						imageFiles.add(new IngestImage(p));
						/*
						int beginIndex = p.getContentIds().indexOf("/");
						String name = p.getContentIds().substring(beginIndex+1);
						this.pagesOfVolume.add(name);
						*/
					}
				}
				if(volume.getTei()!=null)
				{
					teiPbFacsValues = VolumeServiceBean.getAllPbs(new StreamSource(new StringReader(volume.getTei())));
				}
				else if(volume.getTeiSdXml()!=null)
				{
					teiPbFacsValues = VolumeServiceBean.getAllPbs(new DOMSource(volume.getTeiSdXml()));
					
				}
				this.selectedContentModel = volume.getItem().getProperties().getContentModel().getObjid();
				checkSortImagesByTei();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else if(volumeId.equals("new"))
		{	
			/*
				volume=null;
				clearAllData();
				*/
		}
	}
	
	public ModsMetadata retrieveModsMetadata(ModsMetadata md)
	{
		
		if(md.getNames().size() == 0)
		{
			ModsName modsName = new ModsName();
			modsName.setDisplayLabel("author");
			md.getNames().add(modsName);
		}
		if(md.getNotes().size() == 0)
			md.getNotes().add(new ModsNote());
		
		if(md.getIdentifiers().size() == 0)
		{
			md.getIdentifiers().add(new ModsIdentifier());
			md.getIdentifiers().add(new ModsIdentifier());
		}
		
		if(md.getIdentifiers().size() == 1)
		{
			md.getIdentifiers().add(new ModsIdentifier());
		}
		
		if(md.getPublishers().size() == 0 )
		{
			ModsPublisher publisher = new ModsPublisher();
			publisher.setDisplayLabel("publisher");
			publisher.setDateIssued_425(new ModsDate());
	    	md.getPublishers().add(publisher);
		}
		if(md.getKeywords().size()==0)
			md.getKeywords().add("");
		if(md.getLanguage_037() == null)
			md.setLanguage_037(new ModsLanguage());
		

		
		if(md.getRelatedItems().size()== 0)
			md.getRelatedItems().add(new ModsRelatedItem());
		
		if(md.getPhysicalDescriptions().size()==0)
			md.getPhysicalDescriptions().add(new ModsPhysicalDescription());
		
		if(md.getParts().size() == 0)
		{
			md.getParts().add(new ModsPart());
			md.getParts().add(new ModsPart());
		}
		if(md.getParts().size() == 1)
		{
			md.getParts().add(new ModsPart());
		}
		
		return md;
	}
	
	
	public IngestBean() throws Exception
	{
		addModsMetadata();
		//init contentModel
		this.contentModelItems.add(new SelectItem(VolumeServiceBean.monographContentModelId, "Monograph"));
		this.contentModelItems.add(new SelectItem(VolumeServiceBean.multivolumeContentModelId, "Multivolume"));
		this.contentModelItems.add(new SelectItem(VolumeServiceBean.volumeContentModelId, "Volume"));
		this.selectedContentModel = (String) contentModelItems.get(0).getValue();
	}
	  
	@PostConstruct
	public void init()
	{  
		this.contextSelectItems.clear();
		this.contextSelectItems.add(new SelectItem(null, "---"));
		
		SelectItem item;
		List<String> ids = new ArrayList();
		//init contexts 
		for(Grant grant: loginBean.getUser().getGrants())
		{ 
			try  
			{
				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")) || grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
				{
					for(Collection c : loginBean.getUser().getCreatedCollections())
						this.contextSelectItems.add(new SelectItem(c.getId(),c.getName()));
				}
				else 
				{
					for(Collection c : loginBean.getUser().getDepositorCollections())
					{  
						if(!ids.contains(c.getId()))
						{
							ids.add(c.getId());
							item = new SelectItem(c.getId(),c.getName());
							this.contextSelectItems.add(item);
						}
					}
					/*
					for(Collection c : loginBean.getUser().getModeratorCollections())
					{  
						if(!ids.contains(c.getId()))
						{
							ids.add(c.getId());
							item = new SelectItem(c.getId(),c.getName());
							this.contextSelectItems.add(item);
						}
					}
					*/
				}
				this.selectedContextId = (String)contextSelectItems.get(0).getValue();
			}
			catch(Exception e)
			{
					logger.error("Error while retrieving contexts", e);
			}
		}
		
		this.selectedContextId = null;
		
	}


	
	public void addModsMetadata()
	{

		this.modsMetadata.getTitles().add(new ModsTitle());
		ModsName modsName = new ModsName();
		modsName.setDisplayLabel("author");
		this.modsMetadata.getNames().add(modsName);
		this.modsMetadata.getNotes().add(new ModsNote());
		this.modsMetadata.getIdentifiers().add(new ModsIdentifier());
		this.modsMetadata.getIdentifiers().add(new ModsIdentifier());
		ModsPublisher publisher = new ModsPublisher();
		publisher.setDisplayLabel("publisher");
		publisher.setDateIssued_425(new ModsDate());
    	this.modsMetadata.getPublishers().add(publisher);
		this.modsMetadata.getKeywords().add("");
		this.modsMetadata.setLanguage_037(new ModsLanguage());
		this.modsMetadata.getRelatedItems().add(new ModsRelatedItem());
		this.modsMetadata.getPhysicalDescriptions().add(new ModsPhysicalDescription());
		this.modsMetadata.getParts().add(new ModsPart());
		this.modsMetadata.getParts().add(new ModsPart());
	}

   
    
    public String clearUploadedImages() 
    {    
        imageFiles.clear();
        return "";
    }
    public String clearUploadedTEI() 
    {     
    	this.teiFile = null;
    	this.teiPbFacsValues = null;
    	checkSortImagesByTei();
        return "";
    }
    
    public String clearUploadedCodicological() 
    {     
    	this.codicologicalFile = null;
        return "";
    }
    
    
    

    
    public String clearUploadedMAB() 
    {    
		this.mabFile = null;
		modsMetadata = new ModsMetadata();
		addModsMetadata();
		return "";
    }
    
    public String resetMAB() 
    {    
		this.mabFile = null;
		modsMetadata = volume.getModsMetadata();
		return "";
    }
    
    public String clearAllData()
    {   
    	if(imageFiles.size()>0)
    		imageFiles.clear();
    	if(mabFile != null)
    		this.mabFile = null;
    	modsMetadata = new ModsMetadata();
    	addModsMetadata();
    	if(teiFile != null)
    		this.teiFile = null;
    	this.teiPbFacsValues = null;
    	if(footer != null)
    		this.footer = null;
    	return "";
    }
 
    public int getSize() {
        if (getImageFiles().size() > 0) {
            return getImageFiles().size();
        } else {
            return 0;
        }
    }
  
    public long getTimeStamp() {
        return System.currentTimeMillis();
    }
 
    public List<IngestImage> getImageFiles() {
        return imageFiles; 
    }
 
    public void setImageFiles(List<IngestImage> files) {
        this.imageFiles = files;
    }
    
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
    
	/*
	
	public Collection<Object> getSelection() {
        return selection;
    }
 
    public void setSelection(Collection<Object> selection) {
        this.selection = selection;
    }
    
    public void selectionListener(AjaxBehaviorEvent event) {
        UIExtendedDataTable dataTable = (UIExtendedDataTable) event.getComponent();
        Object originalKey = dataTable.getRowKey();
        selectionItems.clear();
        for (Object selectionKey : selection) {
            dataTable.setRowKey(selectionKey);
            if (dataTable.isRowAvailable()) {
                selectionItems.add((FileItem) dataTable.getRowData());
            }
        }
        dataTable.setRowKey(originalKey);
    }

    private void move(FileItem from, FileItem to)
    {
    	int indexOfTo = 0;
    	if(to!=null)
    	{
    		indexOfTo = getFiles().indexOf(to);
    	}
    	
    	int indexOfFrom = getFiles().indexOf(from);
    	
    	
		if(indexOfTo > indexOfFrom)
		{
			getFiles().add(indexOfTo, from);
			getFiles().remove(indexOfFrom);
		}
		else
		{
			getFiles().add(indexOfTo, from);
			getFiles().remove(indexOfFrom+1);
		}
		

    }
    
	public void processDrop(DropEvent evt) {
		System.out.println("DROPPED!!!");
		
		FileItem dragged = (FileItem)evt.getDragValue();
		int indexOfDragged = getFiles().indexOf(dragged);
		int indexOfDropped = 0; 
		 
		if(!evt.getDropValue().equals("pos0"))
		{
			indexOfDropped = getFiles().indexOf(evt.getDropValue());
		
			
			if(indexOfDropped > indexOfDragged)
			{
				getFiles().add(indexOfDropped+1, dragged);
				getFiles().remove(indexOfDragged);
			}
			else
			{
				getFiles().add(indexOfDropped+1, dragged);
				getFiles().remove(indexOfDragged+1);
			}
		}
		else
		{
			getFiles().add(0, dragged);
			getFiles().remove(indexOfDragged+1);
		}
			
		
	}
	
	public void changeOrder(FileItem f)
	{
		
		try {
			move(f, getFiles().get(getMoveTo()-1));
		} catch (Exception e) {
			System.out.println("Error" + e);
		}
		setMoveTo(0);
		System.out.println("ORDER changed!!" + getMoveTo());
	}
	         
	*/
	public void fileUploaded(FileUploadEvent evt)
	{
		logger.info("File uploaded" + evt.getFileItem().getName() +" (" + evt.getFileItem().getSize()+")");
		FileUploadEvent fue = (FileUploadEvent) evt;
		
		if(fue.getFileItem()!=null)
		{
			if (fue.getFileItem().getName().endsWith(".mab.xml"))
			{
				recentlyUpladedMabFile = (DiskFileItem)fue.getFileItem();
				
			}
			else if(fue.getFileItem().getName().endsWith(".cdc.xml"))
			{
				
				recentlyUpladedCodicologigalFile = (DiskFileItem)fue.getFileItem();
				
				
			}
			
			else if(fue.getFileItem().getName().endsWith(".xml"))
			{
				
				recentlyUpladedTeiFile = (DiskFileItem)fue.getFileItem();
				
				
			}
			else if(fue.getFileItem().getName().startsWith("footer"))
			{
				recentlyUpladedFooterFile = (DiskFileItem)fue.getFileItem();
				
			}
			else
			{
				
				
				recentlyUploadedImageFiles .add((DiskFileItem)fue.getFileItem());
				
				
			
			}
		}
	}
	
	public String uploadComplete()
	{
		MessageHelper.infoMessage(InternationalizationHelper.getMessage("ingest_uploadComplete"));
		
		List<String> uploadedFileNamesByPluploader = new LinkedList<String>();
		for(String plFileName : uploadedFilesByPlupload.split("\\|\\|"))
		{
			if (!plFileName.isEmpty())
			{
				uploadedFileNamesByPluploader.add(plFileName);
			}
		}
		
		
		
				
		
		//process mab
		if(recentlyUpladedMabFile!=null)
		{
			try {
				this.modsMetadata = processMabFile(recentlyUpladedMabFile);
				this.mabFile = recentlyUpladedMabFile;
			} catch (Exception e) {
				logger.error("error while processing mab xml", e);
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_invalidMab"));
				//this.modsMetadata = new ModsMetadata();
			}
			uploadedFileNamesByPluploader.remove(recentlyUpladedMabFile.getName());
			recentlyUpladedMabFile = null;
			
			
		}
		
		
		
		//validate tei
		if(recentlyUpladedTeiFile!=null)
		{
			try {
				
				CreateVolumeServiceBean.validateTeiAndPb(new StreamSource(recentlyUpladedTeiFile.getStoreLocation()));
				teiPbFacsValues = VolumeServiceBean.getAllPbs(new StreamSource(recentlyUpladedTeiFile.getStoreLocation()));
				this.teiFile = recentlyUpladedTeiFile;
			} catch (Exception e) {
				logger.error("Error while validating TEI", e);
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_invalidTei") + "\n " + e.getMessage()); 
			}
			
			uploadedFileNamesByPluploader.remove(recentlyUpladedTeiFile.getName());
			recentlyUpladedTeiFile = null;
			
		}
		
		//validate codicological md
		if(recentlyUpladedCodicologigalFile!=null)
		{
			try {
				
				CreateVolumeServiceBean.validateCodicologicalMd(new StreamSource(recentlyUpladedCodicologigalFile.getStoreLocation()));
				this.codicologicalFile = recentlyUpladedCodicologigalFile;
			} catch (Exception e) {
				logger.error("Error while validating TEI", e);
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_invalidCodicological") + "\n " + e.getMessage()); 
			}
			
			uploadedFileNamesByPluploader.remove(recentlyUpladedCodicologigalFile.getName());
			recentlyUpladedCodicologigalFile = null;
		}
		
		
		
		if(recentlyUpladedFooterFile!=null)
		{
			try {
				String footerMimetype = tika.detect(recentlyUpladedFooterFile.getStoreLocation());
				
				if(!footerMimetype.startsWith("image/"))
				{	
					MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_invalidImage") + ": " + recentlyUpladedFooterFile.getName());
				}
				else
				{
					this.footer = recentlyUpladedFooterFile;
				}
			} catch (Exception e) {
				logger.error("Error while reading footer", e);
			}
			
			uploadedFileNamesByPluploader.remove(recentlyUpladedFooterFile.getName());
			this.recentlyUpladedFooterFile = null;
		}
		

		//validate Images
		for(DiskFileItem img : recentlyUploadedImageFiles)
		{
			IngestImage ingestImage = new IngestImage(img);
			
			try {

				String mimetype = tika.detect(ingestImage.getFile());
				
				if(!mimetype.startsWith("image/"))
				{	
					MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_invalidImage") + ": " + ingestImage.getName());
				}
				else
				{
					if(imageFiles.contains(ingestImage))
					{
						int pos = imageFiles.indexOf(ingestImage);
						imageFiles.remove(pos);
						imageFiles.add(pos, ingestImage);
					}
					else
					{
						imageFiles.add(ingestImage);
					}
				}
			} catch (IOException e) {
				
				logger.error("Could not detect mimetype for " +ingestImage.getName());	
			}
			uploadedFileNamesByPluploader.remove(img.getName());
		}
		
		recentlyUploadedImageFiles.clear();
		
		if(uploadedFileNamesByPluploader.size()>0)
		{
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_uploadFollowing Files") + ": " + uploadedFileNamesByPluploader);
		}
	
		this.uploadedFilesByPlupload = "";
		checkSortImagesByTei();
		
		return "";
	}
	
	public void checkSortImagesByTei()
	{
		sortableByTei = sortImagesByTeiFile();
		if(sortableByTei)
		{
			this.sortImagesAlgorithm = "tei";
		}
	}
		
	
	public boolean sortImagesByTeiFile()
	{
		//Sort images using pb facs attribute in tei file
				
			
			
			List<IngestImage> imageFilesSorted = new ArrayList<IngestImage>();
			if(teiPbFacsValues != null && imageFiles.size() == teiPbFacsValues.size())
			{
				
				for(XdmNode node : teiPbFacsValues)
				{
					String facs = node.getAttributeValue(new QName("facs"));
					boolean found = false;
					if(facs!=null)
					{
						
						for(IngestImage imgFile : imageFiles)
						{
							if(facs.equals(imgFile.getName()))
							{
								imageFilesSorted.add(imgFile);
								found=true;
								break;
							}
						}
					}
					if(!found)
					{
						
						//imageFilesSorted = imageFiles;
						return false;
					}
					
				}
				imageFiles = imageFilesSorted;
				return true;
				
			}
			else
			{
				
			}
			
			
			return false;
	
	}
	

	
	public List<SelectItem> getSortImageSelectItem()
	{
		List<SelectItem> sortImageSelectItems = new ArrayList<SelectItem>();
		if(sortableByTei)
		{
			sortImageSelectItems.add(new SelectItem("tei", InternationalizationHelper.getLabel("ingest_sortTei")));
		}
		sortImageSelectItems.add(new SelectItem("intelligent", InternationalizationHelper.getLabel("ingest_sortIntelligent")));
		sortImageSelectItems.add(new SelectItem("alphanumeric", InternationalizationHelper.getLabel("ingest_sortAlphabetical")));
		
		return sortImageSelectItems;
	}
	
	
    
	private ModsMetadata processMabFile(FileItem fileItem) throws Exception{
		
	  
		if(fileItem instanceof DiskFileItem)
		{
			DiskFileItem item = (DiskFileItem) fileItem;
			MabXmlTransformation transform = new MabXmlTransformation();
			File modsFile = transform.mabToMods(null, item.getStoreLocation());
			return VolumeServiceBean.createModsMetadataFromXml(new FileInputStream(modsFile));
		}
		return null;
		
		
		
		
	}

	public String indexOfFile(FileItem f)
	{
		return String.valueOf(getImageFiles().indexOf(f));
	}

	public void setMoveTo(int moveTo) {
		this.moveTo = moveTo;
	}

	public int getMoveTo() {
		return moveTo;
	}
	  
	public void processDrop(DropEvent e) {
		IngestImage draggedFile = (IngestImage)e.getDragValue();
		//FileItem droppedFile = (FileItem)e.getDropValue();
		int draggedFileIndex = getImageFiles().indexOf(draggedFile);
		//int droppedFileIndex = getImageFiles().indexOf(droppedFile);
		int droppedFileIndex = (Integer)e.getDropValue();
		
		
		
		if(draggedFileIndex < droppedFileIndex)
		{
			getImageFiles().remove(draggedFileIndex);
			getImageFiles().add(droppedFileIndex-1, (IngestImage) draggedFile);
		}
		else
		{
			getImageFiles().remove(draggedFileIndex);
			getImageFiles().add(droppedFileIndex, (IngestImage)draggedFile);
		}
		
	}
	
	public void deleteImage(int i, IngestImage file)
	{
		getImageFiles().remove(i);
		checkSortImagesByTei();
	}
	
	
	
	public ModsMetadata updateModsMetadata(ModsMetadata md)
	{      
		
		//update ModsNames displayLabel to
		List<ModsName> names =  new ArrayList<ModsName>();
		int i = 1,j =1 , k = 1;
		for(ModsName name : md.getNames())
		{
			if(name.getName() != null && name.getName() !="")
			{
				if(name.getDisplayLabel().equals("author"))
				{
					name.setType("personal");
					name.setAuthority("pnd");
					name.setDisplayLabel("author"+String.valueOf(i));
					name.setRole("aut");
					i++;
				}
				else if(name.getDisplayLabel().equals("editor"))
				{
					name.setType("personal");
					name.setAuthority("pnd");
					name.setDisplayLabel("editor"+String.valueOf(j));
					name.setRole("asn");
					j++;
				}
				else if(name.getDisplayLabel().equals("body"))
				{
					name.setType("corporate");
					name.setAuthority("gkd");
					name.setDisplayLabel("body"+String.valueOf(k));
					name.setRole("asn");
					k++;
				}
				names.add(name);
			}
		}
		md.setNames(names);

		//add displayLabel to ModsTitle
		ModsTitle title = md.getTitles().get(0);
		title.setDisplayLabel("mainTitle");
		
		//update ModsPublisher displayLabel
		ModsPublisher publisher = md.getPublishers().get(0);
		if((publisher.getPublisher() == null || publisher.getPublisher()=="") && (publisher.getPlace() == null || publisher.getPlace() =="") && (publisher.getEdition() == null || publisher.getEdition() =="") && (publisher.getDateIssued_425().getDate() == null || publisher.getDateIssued_425().getDate() ==""))
			md.setPublishers(null);		
		else
		{
			if(publisher.getDisplayLabel().equals("publisher"))
			{
				publisher.setDisplayLabel("publisher1");
				md.getPublishers().remove(0);
				md.getPublishers().add(publisher);
			}
			if(publisher.getDisplayLabel().equals("printer"))
			{
				publisher.setDisplayLabel("printer1");
				md.getPublishers().remove(0);
				md.getPublishers().add(publisher);
			}
		}
			
		if(md.getIdentifiers().get(1).getValue() != null && md.getIdentifiers().get(1).getValue() !="")
		{
			md.getIdentifiers().get(1).setType("issn");
			md.getIdentifiers().get(1).setInvalid("yes");
		}
		else
			md.getIdentifiers().remove(1);

		if(md.getIdentifiers().get(0).getValue() != null && md.getIdentifiers().get(0).getValue() !="")
		{
			md.getIdentifiers().get(0).setType("isbn");
		}
		else 
			md.getIdentifiers().remove(0);
			
		
		List<String> keywords = new ArrayList<String>();
		for(String keyword : md.getKeywords())
		{
			if(keyword != "" && keyword != null)
				keywords.add(keyword);
		}
		md.setKeywords(keywords);
		
		if(md.getLanguage_037().getLanguage()=="")
			md.setLanguage_037(null);
		
		if(md.getNotes().get(0).getNote() == "")
			md.setNotes(null);
		else
		{
			md.getNotes().get(0).setType("statement of responsibility");
		}


		if(md.getPhysicalDescriptions().get(0).getExtent() == "" )
			md.setPhysicalDescriptions(null);

		this.modsMetadata.getRelatedItems().add(new ModsRelatedItem());
		
		if(md.getRelatedItems().get(0).getTitle() =="")
			md.setRelatedItems(null);
		else
		{
//			md.getRelatedItems().get(0).setDisplayLabel("series1");
//			md.getRelatedItems().get(0).setType("series");
			md.getRelatedItems().get(0).setType("host");
		}
		if(md.getParts().get(1).getValue() !="" && md.getParts().get(1).getValue() != null)
			md.getParts().get(1).setType("host");
		else
			md.getParts().remove(1);
		if(md.getParts().get(0).getVolumeDescriptive_089() !="" && md.getParts().get(0).getVolumeDescriptive_089() != null)
			md.getParts().get(0).setType("host");
		else
			md.getParts().remove(0);

			
		
		return md;
	}
	

	
	public String save(String operation) 
	{            
		logger.info("Reingest/update!!");
		
		if(volume==null && (getSelectedContextId()==null || getSelectedContextId().isEmpty()))
		{
			MessageHelper.infoMessage(InternationalizationHelper.getMessage("error_ingest_nullCollection"));
			return "";
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		AllVolumesBean allVolBean = (AllVolumesBean) context.getApplication().evaluateExpressionGet(context, "#{allVolumesBean}", AllVolumesBean.class);
		ManualIngestLogBean ingestLogBean = (ManualIngestLogBean) context.getApplication().evaluateExpressionGet(context, "#{manualIngestLogBean}", ManualIngestLogBean.class);
		
		/*
		if(mabFile == null && modsMetadata.getCatalogueId_001() == null)
			modsMetadata = updateModsMetadata(modsMetadata);
		*/
		DatabaseItem imp = new DatabaseItem();
		imp.setContentModelId(selectedContentModel);
		imp.setContextId(selectedContextId);
		imp.setUserId(loginBean.getUser().getId());
		imp.setUserName(loginBean.getUser().getName());
		Volume dummy = new Volume();
		dummy.setModsMetadata(modsMetadata);
		
		imp.setNumberOfImages(imageFiles.size());
		
		try {
			if(volumeId.equalsIgnoreCase("new"))
			{
				
				if(getSelectedContentModel().equals(VolumeServiceBean.multivolumeContentModelId))
				{
					
	     			if(mabFile == null && modsMetadata.getTitles().get(0).getTitle().equals(""))
	     			{
	     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_nullTitle"));	
	     				return "";
	     			}
					if(mabFile == null  && modsMetadata.getCatalogueId_001() == null)
						modsMetadata = updateModsMetadata(modsMetadata);
					imp.setShortTitle(VolumeUtilBean.getShortTitleView(dummy));
					imp.setSubTitle(VolumeUtilBean.getSubTitleView(dummy));
		    		volume = createVolumeService.createNewMultiVolume(operation,PropertyReader.getProperty("dlc.content-model.multivolume.id"),getSelectedContextId(), loginBean.getUserHandle(), modsMetadata);
		    		clearAllData();
		    		MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_newMultivolume") + "[" + volume.getItem().getObjid()+"]");
		    		this.setVolumeId("new");
		    		this.volume = null;
		    		allVolBean.setColId("my");
		    		allVolBean.setCurrentPageNumber(1);
		    		return "pretty:myVolumes";
				}
				
				else
				{
		     				     			
	     			if(getImageFiles().size()==0 )
	     			{
	     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_imageUpload"));
	     				return "";
	     			}
	     			if(teiFile!=null && teiPbFacsValues.size()!=getImageFiles().size())
	     			{
	     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_wrongNumberOfImages")); //getNumberOfTeiPbs()
	     				return "";
	     			}
	     			if(!getSelectedContentModel().equals(VolumeServiceBean.volumeContentModelId) && mabFile == null && modsMetadata.getTitles().get(0).getTitle().equals(""))
	     			{
	     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_nullTitle"));
	     				return "";
	     			}

	     			
					if(mabFile == null && modsMetadata.getCatalogueId_001() == null)
						modsMetadata = updateModsMetadata(modsMetadata);
					
					if(getSelectedContentModel().equals(VolumeServiceBean.volumeContentModelId))
					{
						imp.setShortTitle(VolumeUtilBean.getVolumeShortTitleView(dummy));
						imp.setSubTitle(VolumeUtilBean.getVolumeSubTitleView(dummy));
					}
					else
					{
						imp.setShortTitle(VolumeUtilBean.getShortTitleView(dummy));
						imp.setSubTitle(VolumeUtilBean.getSubTitleView(dummy));
					}
					
					//Volume volume = null;
					
					/*
					List<DiskFileItem> diskFileItems = new ArrayList<DiskFileItem>();
					for(IngestImage img : imageFiles)
					{
						diskFileItems.add(img.getDiskFileItem());
					}
					
*/					
					
					if(getSelectedContentModel().equals(VolumeServiceBean.monographContentModelId))
					{
						CreateVolumeThread cvt = new CreateVolumeThread(operation, PropertyReader.getProperty("dlc.content-model.monograph.id"),getSelectedContextId(),null,loginBean.getUserHandle(), modsMetadata, imageFiles, footer, teiFile, codicologicalFile, imp);
						cvt.start();
						//volume = volumeService.createNewVolume(operation, PropertyReader.getProperty("dlc.content-model.monograph.id"),getSelectedContextId(),null,loginBean.getUserHandle(), modsMetadata, imageFiles, footer, teiFile, codicologicalFile);
					}
		    			
					else
					{
						CreateVolumeThread cvt = new CreateVolumeThread (operation,PropertyReader.getProperty("dlc.content-model.volume.id"),getSelectedContextId(), getSelectedMultiVolumeId(), loginBean.getUserHandle(), modsMetadata, imageFiles, footer, teiFile, codicologicalFile, imp);
						cvt.start();
						//volume = volumeService.createNewVolume(operation,PropertyReader.getProperty("dlc.content-model.volume.id"),getSelectedContextId(), getSelectedMultiVolumeId(), loginBean.getUserHandle(), modsMetadata, imageFiles, footer, teiFile, codicologicalFile);
					}
			    		
					/*	
					clearAllData();
		    		String title = VolumeUtilBean.getMainTitle(volume.getModsMetadata()).getTitle();
		    		if(getSelectedContentModel().equals(VolumeServiceBean.monographContentModelId))
		    			MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_newMonograph")+"[" + volume.getItem().getObjid()+"]");
		    		else
			    		MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_newVolume")+ title + "[" + volume.getItem().getObjid()+"]");
		    		
		    		*/
					
					//Wait 3 seconds until the empty item was created
					//Thread.sleep(3000);
					
					MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_uploadStarted"));
		    		
		    		/*
		    		FacesContext context = FacesContext.getCurrentInstance();
					ViewPages viewPagesBean = (ViewPages) context.getApplication().evaluateExpressionGet(context, "#{viewPages}", ViewPages.class);
					viewPagesBean.setVolumeId(volume.getObjidAndVersion());
		    		
		    		return "pretty:viewPagesWithoutNumber";
		    		*/
		    		
		    		
		    		//allVolBean.setColId("my");
		    		//allVolBean.setCurrentPageNumber(1);
		    		return "pretty:ingestLog";
		    		
				}
			}
			else{
				/*
				if(imageFiles != null)
				{
					
					for(IngestImage file: imageFiles)
						if(!pagesOfVolume.contains(file.getName()))
						{
			    			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_wrongImages")); //getNumberOfTeiPbs()
			    			return "";
						}
				}
				*/
				
				
	     		
				
				
				
					if(!getSelectedContentModel().equals(VolumeServiceBean.multivolumeContentModelId) && getImageFiles().size()==0 )
					{
						MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_imageUpload"));
						return "";
					}
					
					if(teiPbFacsValues != null && teiPbFacsValues.size()!=getImageFiles().size())
					{
						MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_wrongNumberOfImages")); //getNumberOfTeiPbs()
						return "";
					}
						
					if(!getSelectedContentModel().equals(VolumeServiceBean.volumeContentModelId) && (modsMetadata.getTitles()==null || modsMetadata.getTitles().size() == 0 || modsMetadata.getTitles().get(0).getTitle().equals("")))
					{
						MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_nullTitle"));
						return "";
					}
		     			
		    		
					
					if(mabFile == null && modsMetadata.getCatalogueId_001() == null)
						modsMetadata = updateModsMetadata(modsMetadata);
						
					IngestImage ingestImageFooter = null;
					if(footer!=null)
					{
						ingestImageFooter = new IngestImage(footer);
					}
					this.volume = createVolumeService.update(volume, loginBean.getUserHandle(),operation, new IngestImage(teiFile), modsMetadata, imageFiles, new IngestImage(codicologicalFile), ingestImageFooter);
					
				
				Thread.sleep(3000);
				allVolBean.setColId("my");
	    		allVolBean.setCurrentPageNumber(1);
	    		return "pretty:myVolumes";
				
			}
			/*
			FacesContext context = FacesContext.getCurrentInstance();
			
			ViewPages viewPagesBean = (ViewPages) context.getApplication().evaluateExpressionGet(context, "#{viewPages}", ViewPages.class);
			viewPagesBean.setVolumeId(volume.getObjidAndVersion());
    		
    		return "pretty:viewPagesWithoutNumber";
    		*/
			
			
    		
    		
			
		} catch (Exception e) {
			logger.error("Error while creating/updating volume", e);
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_internal")+ ":" + e.getMessage());
		}
//		ModsMetadata md = new ModsMetadata();
//		ModsTitle title = new ModsTitle();
//		title.setTitle("Test title");
//		md.getTitles().add(title);
//    	try {
//     		if(getImageFiles().size()==0)
//    		{
//    			MessageHelper.errorMessage("You have to upload at least Image");
//    			return "";
//    		}
//    		if (teiFile!=null && getNumberOfTeiPbs()!=getImageFiles().size())
//    		{
//    			MessageHelper.errorMessage("You have to upload " + getNumberOfTeiPbs() + " Images, which are referenced in the TEI");
//    			return "";
//    		}
//    		volumeService.createNewVolume(getSelectedContentModel(),getSelectedContextId(), getLoginBean().getUserHandle(), modsMetadata, imageFiles, teiFile);
//    	} catch (Exception e) {
//			MessageHelper.errorMessage("An error occured during creation. " + e.toString() + " " + e.getMessage());
//		}
    	return "";
    	
	
	}
	
	



	public FileItem getMabFile() {
		return mabFile;
	}

	public void setMabFile(DiskFileItem mabFile) {
		this.mabFile = mabFile;
	}

	public FileItem getTeiFile() {
		return teiFile;
	}

	public void setTeiFile(DiskFileItem teiFile) {
		this.teiFile = teiFile;
	}

	public ModsMetadata getModsMetadata() {
		return modsMetadata;
	}

	public void setModsMetadata(ModsMetadata modsMetadata) {
		this.modsMetadata = modsMetadata;
	}


	public String getSelectedContextId() {
		return selectedContextId;
	}

	public void setSelectedContextId(String selectedContextId) {
		this.selectedContextId = selectedContextId;
	}

	public List<SelectItem> getContextSelectItems() {
		return contextSelectItems;
	}
	
	public void setContextSelectItems(List<SelectItem> contextSelectItems) {
		this.contextSelectItems = contextSelectItems;
	}
	
	public List<SelectItem> getContentModelItems() throws Exception {


		return contentModelItems;
	}

	public void setContentModelItems(List<SelectItem> contentModelItems) {
		this.contentModelItems = contentModelItems;
	}
 
	public String getSelectedContentModel() {
		return selectedContentModel;
	}

	public void setSelectedContentModel(String selectedContentModel) {
		this.selectedContentModel = selectedContentModel;
	}

	public String getSelectedMultiVolumeId() {
		return selectedMultiVolumeId;
	}

	public void setSelectedMultiVolumeId(String selectedMultiVolId) {
		this.selectedMultiVolumeId = selectedMultiVolId;
	} 

	public List<SelectItem> getMultiVolItems() throws Exception{
		multiVolItems.clear(); 
		if(getSelectedContextId()!=null && !getSelectedContextId().isEmpty())
		{
			List<SearchCriterion> fcList = new ArrayList<SearchCriterion>();
			SearchCriterion sc = null ;
			if(loginBean.getUser().getModeratorCollections()!=null && loginBean.getUser().getModeratorCollections().size() > 0)
			{
				for(Collection c : loginBean.getUser().getModeratorCollections())
				{
					if(c.getId().equalsIgnoreCase(getSelectedContextId()))
					{
						sc = new SearchCriterion(SearchType.CONTEXT_ID, getSelectedContextId());
						fcList.add(sc);
					}
				}

			}
			else if(loginBean.getUser().getDepositorCollections()!=null && loginBean.getUser().getDepositorCollections().size() > 0)
			{
				for(Collection c : loginBean.getUser().getDepositorCollections())
				{
					if(c.getId().equalsIgnoreCase(getSelectedContextId()))
					{
						sc = new SearchCriterion( SearchType.CREATED_BY, loginBean.getUser().getId());
						fcList.add(sc);
					}
					
				}
			}


			//		VolumeSearchResult vsr = searchBean.search(new VolumeTypes[]{VolumeTypes.MULTIVOLUME}, scList, SortCriterion.getStandardSortCriteria(), 1000, 0);
//			VolumeSearchResult vsr = volumeService.filterSearch(query, scList, limit, offset, index, userHandle);
			VolumeSearchResult vsr = filterBean.itemFilter(new VolumeTypes[]{VolumeTypes.MULTIVOLUME}, 
					new VolumeStatus[]{VolumeStatus.pending, VolumeStatus.submitted, VolumeStatus.released}, 
					new VolumeStatus[]{VolumeStatus.pending, VolumeStatus.submitted, VolumeStatus.released}, 
					fcList, CombinedSortCriterion.AUTHOR_TITLE_ASC.getScList(), 1000, 0, loginBean.getUserHandle());
			for(Volume vol : vsr.getVolumes())
			{
				multiVolItems.add(new SelectItem(vol.getItem().getObjid(), VolumeUtilBean.getMainTitle(vol.getModsMetadata()).getTitle()));
			}
		}

		return multiVolItems;
	}

	public List<String> getPagesOfVolume() {
		return pagesOfVolume;
	}


	public void setPagesOfVolume(List<String> pagesOfVolume) {
		this.pagesOfVolume = pagesOfVolume;
	}


	public void setMultiVolItems(List<SelectItem> multiVolItems) {
		this.multiVolItems = multiVolItems;
	}
	
	public boolean isHasMab() {
		return hasMab;
	}

	public void setHasMab(boolean hasMab) {
		this.hasMab = hasMab;
	}


	public String getVolumeId() {
		return volumeId;
	}


	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

 
	public Volume getVolume() {
		return volume;
	}


	public void setVolume(Volume volume) {
		this.volume = volume;
	}


	public DiskFileItem getFooter() {
		return footer;
	}


	public void setFooter(DiskFileItem footer) {
		this.footer = footer;
	}


	public List<XdmNode> getTeiPbFacsValues() {
		return teiPbFacsValues;
	}


	public void setTeiPbFacsValues(List<XdmNode> teiPbFacsValues) {
		this.teiPbFacsValues = teiPbFacsValues;
	}
	
	

	public String sortImages()
	{
		if(sortImagesAlgorithm.equals("alphanumeric"))
		{
			Collections.sort(imageFiles, new AlphanumericDiskFileComparator());
		}
		else if (sortImagesAlgorithm.equals("intelligent"))
		{
			Collections.sort(imageFiles, new AlphanumComparator());
		}
		
		else if (sortImagesAlgorithm.equals("tei"))
		{
			sortImagesByTeiFile();
		}
		
		return "";
	}


	public String getSortImagesAlgorithm() {
		return sortImagesAlgorithm;
	}


	public void setSortImagesAlgorithm(String sortImagesAlgorithm) {
		this.sortImagesAlgorithm = sortImagesAlgorithm;
	}
	
	
	public class AlphanumericDiskFileComparator implements Comparator<IngestImage>
	{

		@Override
		public int compare(IngestImage o1, IngestImage o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
	/*
	 * The Alphanum Algorithm is an improved sorting algorithm for strings
	 * containing numbers.  Instead of sorting numbers in ASCII order like
	 * a standard sort, this algorithm sorts numbers in numeric order.
	 *
	 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
	 *
	 *
	 * This library is free software; you can redistribute it and/or
	 * modify it under the terms of the GNU Lesser General Public
	 * License as published by the Free Software Foundation; either
	 * version 2.1 of the License, or any later version.
	 *
	 * This library is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	 * Lesser General Public License for more details.
	 *
	 * You should have received a copy of the GNU Lesser General Public
	 * License along with this library; if not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
	 *
	 */

	

	/**
	 * This is an updated version with enhancements made by Daniel Migowski,
	 * Andre Bogus, and David Koelle
	 *
	 * To convert to use Templates (Java 1.5+):
	 *   - Change "implements Comparator" to "implements Comparator<String>"
	 *   - Change "compare(Object o1, Object o2)" to "compare(String s1, String s2)"
	 *   - Remove the type checking and casting in compare().
	 *
	 * To use this class:
	 *   Use the static "sort" method from the java.util.Collections class:
	 *   Collections.sort(your list, new AlphanumComparator());
	 */
	public class AlphanumComparator implements Comparator<IngestImage>
	{
	    private final boolean isDigit(char ch)
	    {
	        return ch >= 48 && ch <= 57;
	    }

	    /** Length of string is passed in for improved efficiency (only need to calculate it once) **/
	    private final String getChunk(String s, int slength, int marker)
	    {
	        StringBuilder chunk = new StringBuilder();
	        char c = s.charAt(marker);
	        chunk.append(c);
	        marker++;
	        if (isDigit(c))
	        {
	            while (marker < slength)
	            {
	                c = s.charAt(marker);
	                if (!isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        } else
	        {
	            while (marker < slength)
	            {
	                c = s.charAt(marker);
	                if (isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        }
	        return chunk.toString();
	    }

	    public int compare(IngestImage o1, IngestImage o2)
	    {
	    	/*
	        if (!(o1 instanceof String) || !(o2 instanceof String))
	        {
	            return 0;
	        }
	        */
	        String s1 = o1.getName();
	        String s2 = o2.getName();

	        int thisMarker = 0;
	        int thatMarker = 0;
	        int s1Length = s1.length();
	        int s2Length = s2.length();

	        while (thisMarker < s1Length && thatMarker < s2Length)
	        {
	            String thisChunk = getChunk(s1, s1Length, thisMarker);
	            thisMarker += thisChunk.length();

	            String thatChunk = getChunk(s2, s2Length, thatMarker);
	            thatMarker += thatChunk.length();

	            // If both chunks contain numeric characters, sort them numerically
	            int result = 0;
	            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0)))
	            {
	                // Simple chunk comparison by length.
	                int thisChunkLength = thisChunk.length();
	                result = thisChunkLength - thatChunk.length();
	                // If equal, the first different number counts
	                if (result == 0)
	                {
	                    for (int i = 0; i < thisChunkLength; i++)
	                    {
	                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
	                        if (result != 0)
	                        {
	                            return result;
	                        }
	                    }
	                }
	            } else
	            {
	                result = thisChunk.compareTo(thatChunk);
	            }

	            if (result != 0)
	                return result;
	        }

	        return s1Length - s2Length;
	    }
	}
	
	
	public Component getTeiComponent()
	{
		if(volume!=null && volume.getItem()!=null)
		{
			for(Component c : volume.getItem().getComponents())
			{
				if(c.getProperties().getContentCategory().equals("tei"))
				{
					return c;
				}
			}
		}
		return null;
	}

	public String getAttributeValue(XdmNode node, String attributeName)
	{
		return node.getAttributeValue(new QName(attributeName));
	}


	public DiskFileItem getCodicologicalFile() {
		return codicologicalFile;
	}


	public void setCodicologicalFile(DiskFileItem codicologicalFile) {
		this.codicologicalFile = codicologicalFile;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * A List of filenames, seperated by "||", which is set by pluploader before a file is uploaded. Thus, errors can be detected if an
	 * file was added to pluploader, but does not appear in the backing bean.
	 * @return
	 */
	public String getUploadedFilesByPlupload() {
		return uploadedFilesByPlupload;
	}

	public void setUploadedFilesByPlupload(String uploadedFilesByPlupload) {
		this.uploadedFilesByPlupload = uploadedFilesByPlupload;
	}
	


}
