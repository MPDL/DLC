package de.mpg.mpdl.dlc.ingest;


import java.io.File;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;
import org.richfaces.event.DropEvent;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.searchLogic.FilterBean;
import de.mpg.mpdl.dlc.searchLogic.FilterCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.FilterCriterion.FilterParam;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
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

 
/**
 * @author Ilya Shaikovsky
 *
 */

@ManagedBean
@SessionScoped
@URLMapping(id="upload", viewId = "/ingest.xhtml", pattern = "/upload/#{ingestBean.volumeId}")
public class IngestBean{
 
	private static Logger logger = Logger.getLogger(IngestBean.class);
   
	private List<DiskFileItem> imageFiles = new ArrayList<DiskFileItem>();
	private DiskFileItem footer;
	
	private FileItem mabFile;
	private ModsMetadata modsMetadata  = new ModsMetadata();
	
	private FileItem teiFile;
	private List<XdmNode> teiPbFacsValues;

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
	

//	private SearchBean searchBeans = new SearchBean();
	
	private FilterBean filterBean = new FilterBean();
	
	private String volumeId;
	
	private Volume volume;
	
	private List<String> pagesOfVolume = new ArrayList<String>();
	
	 
	@URLAction(onPostback=false)
	public void loadContext()
	{ 
		if(volumeId != null  && !volumeId.equalsIgnoreCase("new"))
		{ 
			try {
				this.volume = volumeService.retrieveVolume(volumeId, loginBean.getUserHandle());
				if(mabFile == null)
					this.modsMetadata = volume.getModsMetadata();
				if(volume.getMets()!=null)
				{
					for(Page p : volume.getMets().getPages())
					{	
						int beginIndex = p.getContentIds().indexOf("/");
						String name = p.getContentIds().substring(beginIndex+1);
						this.pagesOfVolume.add(name);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else
		{
			if(volume != null)
				clearAllData();
			volume = null;
		}
	}
	
	
	public IngestBean() throws Exception
	{
		addModsMetadata();
		//init contentModel
		this.contentModelItems.add(new SelectItem("Monograph", "Monograph"));
		this.contentModelItems.add(new SelectItem("Multivolume", "Multivolume"));
		this.contentModelItems.add(new SelectItem("Volume", "Volume"));
		this.selectedContentModel = (String) contentModelItems.get(0).getValue();
	}
	  
	@PostConstruct
	public void init()
	{  
		this.contextSelectItems.clear();
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
				else {
					for(Collection c : loginBean.getUser().getDepositorCollections())
					{  
						if(!ids.contains(c.getId()))
						{
							ids.add(c.getId());
							item = new SelectItem(c.getId(),c.getName());
							this.contextSelectItems.add(item);
						}
					}
					for(Collection c : loginBean.getUser().getModeratorCollections())
					{  
						if(!ids.contains(c.getId()))
						{
							ids.add(c.getId());
							item = new SelectItem(c.getId(),c.getName());
							this.contextSelectItems.add(item);
						}
					}
				}
			}catch(Exception e)
			{
					
			}
		}
		if(contextSelectItems.size()>0)
			this.selectedContextId = (String) contextSelectItems.get(0).getValue();	
	}


	
	public void addModsMetadata()
	{

		this.modsMetadata.getTitles().add(new ModsTitle());
		this.modsMetadata.getNames().add(new ModsName());
		this.modsMetadata.getNotes().add(new ModsNote());
		this.modsMetadata.getIdentifiers().add(new ModsIdentifier());
		this.modsMetadata.getIdentifiers().add(new ModsIdentifier());
		ModsPublisher publisher = new ModsPublisher();
		publisher.setDateIssued_425(new ModsDate());
    	this.modsMetadata.getPublishers().add(publisher);
		this.modsMetadata.getKeywords().add("");
		this.modsMetadata.setLanguage_037(new ModsLanguage());
		this.modsMetadata.getRelatedItems().add(new ModsRelatedItem());
		this.modsMetadata.getPhysicalDescriptions().add(new ModsPhysicalDescription());
		this.modsMetadata.getParts().add(new ModsPart());
		this.modsMetadata.getParts().add(new ModsPart());
	}

    public void paint(OutputStream stream, Object object) throws Exception {
    	stream.write(getImageFiles().get((Integer) object).get());
        stream.close();
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
 
    public List<DiskFileItem> getImageFiles() {
        return imageFiles; 
    }
 
    public void setImageFiles(List<DiskFileItem> files) {
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
			if (fue.getFileItem().getName().endsWith(".mab"))
			{
				if(this.volumeId != null)
					this.modsMetadata = new ModsMetadata(); 
				this.setMabFile(fue.getFileItem());
				processMabFile(fue.getFileItem());
			}
			else if(fue.getFileItem().getName().endsWith(".xml"))
			{
				this.setTeiFile(fue.getFileItem());
				DiskFileItem diskTeiFile = (DiskFileItem)teiFile;
				try {
					
					teiPbFacsValues = VolumeServiceBean.getAllPbs(diskTeiFile.getInputStream());
				} catch (Exception e) {
					logger.error("error while validating TEI", e);
					MessageHelper.errorMessage("Error while validating TEI"); 
				}
			}
			else if(fue.getFileItem().getName().startsWith("footer"))
			{
				this.setFooter((DiskFileItem)fue.getFileItem());
			}
			else
			{
				imageFiles.add((DiskFileItem)fue.getFileItem());

			}
		}
	}
	
	public String uploadComplete()
	{
		imageFiles = sortImagesByTeiFile(imageFiles, teiPbFacsValues);
		return null;
	}
		
	
	public static List<DiskFileItem> sortImagesByTeiFile(List<DiskFileItem> imageFiles, List<XdmNode> teiPbFacsValues)
	{
		//Sort images using pb facs attribute in tei file
				
			List<DiskFileItem> imageFilesSorted = new ArrayList<DiskFileItem>();
			if(teiPbFacsValues != null && imageFiles.size() == teiPbFacsValues.size())
			{
				
				for(XdmNode node : teiPbFacsValues)
				{
					String facs = node.getAttributeValue(new QName("facs"));
					boolean found = false;
					if(facs!=null)
					{
						
						for(DiskFileItem imgFile : imageFiles)
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
						
						imageFilesSorted = imageFiles;
						break;
					}
					
				}
				
			}
			else
			{
				imageFilesSorted = imageFiles;
			}
			
			return imageFilesSorted;
	
	}
	

	
	
	
	
    
	private void processMabFile(FileItem fileItem) {
		
		try { 
			if(fileItem instanceof DiskFileItem)
			{
				DiskFileItem item = (DiskFileItem) fileItem;
				MabXmlTransformation transform = new MabXmlTransformation();
				File modsFile = transform.mabToMods(null, item.getStoreLocation());
				this.modsMetadata = VolumeServiceBean.createModsMetadataFromXml(new FileInputStream(modsFile));
				System.out.println("");
			}
		} catch (Exception e) {
			logger.error("Error while transforming MAB", e);
		}
		
		
		
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
		FileItem draggedFile = (FileItem)e.getDragValue();
		FileItem droppedFile = (FileItem)e.getDropValue();
		int draggedFileIndex = getImageFiles().indexOf(draggedFile);
		int droppedFileIndex = getImageFiles().indexOf(droppedFile);
		
		if(draggedFileIndex < droppedFileIndex)
		{
			getImageFiles().remove(draggedFileIndex);
			getImageFiles().add(droppedFileIndex, (DiskFileItem) draggedFile);
		}
		else
		{
			getImageFiles().remove(draggedFileIndex);
			getImageFiles().add(droppedFileIndex, (DiskFileItem)draggedFile);
		}
		
	}
	
	public void deleteImage(int i, FileItem file)
	{
		getImageFiles().remove(i);
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
				publisher.setDisplayLabel("printer1");
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
			md.getRelatedItems().get(0).setDisplayLabel("series1");
			md.getRelatedItems().get(0).setType("series");
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
		logger.info("SAVE!!");
		try {
			if(volumeId.equalsIgnoreCase("new"))
			{

				if(getSelectedContentModel().equals("Multivolume"))
				{
					
	     			if(mabFile == null && modsMetadata.getTitles().get(0).getTitle().equals(""))
	     			{
	     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_nullTitle"));	
	     				return "";
	     			}
					if(mabFile == null)
						modsMetadata = updateModsMetadata(modsMetadata);
		    		Volume volume = volumeService.createNewMultiVolume(operation,PropertyReader.getProperty("dlc.content-model.multivolume.id"),getSelectedContextId(), loginBean.getUserHandle(), modsMetadata);
		    		clearAllData();
		    		String title = VolumeUtilBean.getMainTitle(volume.getModsMetadata()).getTitle();
		    		MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_newMultivolume") + "[" + volume.getItem().getObjid()+"]");
				}
				
				else
				{
		     		if(getImageFiles().size()==0 || (teiFile!=null && teiPbFacsValues.size()!=getImageFiles().size()) || (mabFile == null && modsMetadata.getTitles().get(0).getTitle().equals("")))
		    		{
		     			
		     			if(getImageFiles().size()==0 )
		     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_imageUpload"));
		     			if(teiFile!=null && teiPbFacsValues.size()!=getImageFiles().size())
		     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_wrongNumberOfImages")); //getNumberOfTeiPbs()
		     			if(mabFile == null && modsMetadata.getTitles().get(0).getTitle().equals(""))
		     				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_nullTitle"));
		     			return "";
		    		}
					if(mabFile == null)
						modsMetadata = updateModsMetadata(modsMetadata);
					Volume volume = null;
					if(getSelectedContentModel().equals("Monograph"))
		    			volume = volumeService.createNewVolume(operation, PropertyReader.getProperty("dlc.content-model.monograph.id"),getSelectedContextId(),null,loginBean.getUserHandle(), modsMetadata, imageFiles, footer, teiFile);
					else
			    		volume = volumeService.createNewVolume(operation,PropertyReader.getProperty("dlc.content-model.volume.id"),getSelectedContextId(), getSelectedMultiVolumeId(), loginBean.getUserHandle(), modsMetadata, imageFiles, footer, teiFile);
						
					clearAllData();
		    		String title = VolumeUtilBean.getMainTitle(volume.getModsMetadata()).getTitle();
		    		if(getSelectedContentModel().equals("Monograph"))
		    			MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_newMonograph")+"[" + volume.getItem().getObjid()+"]");
		    		else
			    		MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_newVolume")+ title + "[" + volume.getItem().getObjid()+"]");
				}
			}
			else{
				if(imageFiles != null)
				{
					for(DiskFileItem file: imageFiles)
						if(!pagesOfVolume.contains(file.getName()))
						{
			    			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_wrongImages")); //getNumberOfTeiPbs()
			    			return "";
						}
				}
				if(mabFile != null)
					this.volume = volumeService.update(volume, loginBean.getUserHandle(),operation, teiFile, modsMetadata, imageFiles);
				else
					this.volume = volumeService.update(volume, loginBean.getUserHandle(),operation, teiFile, null, imageFiles);
				
			}
		} catch (Exception e) {
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

	public void setMabFile(FileItem mabFile) {
		this.mabFile = mabFile;
	}

	public FileItem getTeiFile() {
		return teiFile;
	}

	public void setTeiFile(FileItem teiFile) {
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
		List<FilterCriterion> fcList = new ArrayList<FilterCriterion>();
		FilterCriterion sc = null ;
		if(loginBean.getUser().getModeratorCollections()!=null && loginBean.getUser().getModeratorCollections().size() > 0)
		{
			for(Collection c : loginBean.getUser().getModeratorCollections())
			{
				if(c.getId().equalsIgnoreCase(getSelectedContextId()))
				{
					sc = new FilterCriterion(FilterParam.CONTEXT_ID, getSelectedContextId());
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
					sc = new FilterCriterion( FilterParam.CREATED_BY, loginBean.getUser().getId());
					fcList.add(sc);
				}
				
			}
		}


		//		VolumeSearchResult vsr = searchBean.search(new VolumeTypes[]{VolumeTypes.MULTIVOLUME}, scList, SortCriterion.getStandardSortCriteria(), 1000, 0);
//		VolumeSearchResult vsr = volumeService.filterSearch(query, scList, limit, offset, index, userHandle);
		VolumeSearchResult vsr = filterBean.itemFilter(new VolumeTypes[]{VolumeTypes.MULTIVOLUME}, new VolumeStatus[]{VolumeStatus.pending, VolumeStatus.released}, fcList, SortCriterion.getStandardFilterSortCriteria(), 1000, 0, loginBean.getUserHandle());
		for(Volume vol : vsr.getVolumes())
		{
			multiVolItems.add(new SelectItem(vol.getItem().getObjid(), VolumeUtilBean.getMainTitle(vol.getModsMetadata()).getTitle()));
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
	
	






}