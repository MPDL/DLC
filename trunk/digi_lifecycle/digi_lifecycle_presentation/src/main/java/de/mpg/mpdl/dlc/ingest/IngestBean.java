package de.mpg.mpdl.dlc.ingest;


import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;
import org.richfaces.event.DropEvent;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.search.SearchBean;
import de.mpg.mpdl.dlc.search.SearchCriterion;
import de.mpg.mpdl.dlc.search.SearchCriterion.Operator;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.search.SortCriterion;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.mods.ModsDate;
import de.mpg.mpdl.dlc.vo.mods.ModsIdentifier;
import de.mpg.mpdl.dlc.vo.mods.ModsLanguage;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.mods.ModsNote;
import de.mpg.mpdl.dlc.vo.mods.ModsPublisher;
import de.mpg.mpdl.dlc.vo.mods.ModsSeries;
import de.mpg.mpdl.dlc.vo.mods.ModsTitle;
import de.mpg.mpdl.jsf.components.fileUpload.FileUploadEvent;

 
/**
 * @author Ilya Shaikovsky
 *
 */

@ManagedBean
@SessionScoped
@URLMapping(id="upload", pattern = "/upload", viewId = "/ingest.xhtml", onPostback=true)

public class IngestBean implements Serializable {
 
	private static Logger logger = Logger.getLogger(IngestBean.class);
   
	private ArrayList<DiskFileItem> imageFiles = new ArrayList<DiskFileItem>();
	private FileItem mabFile;
	private ModsMetadata modsMetadata  = new ModsMetadata();
	private FileItem teiFile;
	private int numberOfTeiPbs;

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
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();

	
	private SearchBean searchBean = new SearchBean();
	
	public IngestBean() throws Exception
	{
		MabXmlTransformation test = new MabXmlTransformation();
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
		this.modsMetadata.getSeries().add(new ModsSeries());
		this.modsMetadata.getExtents().add("");
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
        return "";
    }
    
    public String clearUploadedMAB() 
    {    
		this.mabFile = null;
		modsMetadata = new ModsMetadata();
		addModsMetadata();
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
    	numberOfTeiPbs = 0;
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
 
    public ArrayList<DiskFileItem> getImageFiles() {
        return imageFiles; 
    }
 
    public void setImageFiles(ArrayList<DiskFileItem> files) {
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
				this.setMabFile(fue.getFileItem());
				processMabFile(fue.getFileItem());
			}
			else if(fue.getFileItem().getName().endsWith(".xml"))
			{
				this.setTeiFile(fue.getFileItem());
				DiskFileItem diskTeiFile = (DiskFileItem)teiFile;
				try {
					numberOfTeiPbs = volumeService.validateTei(diskTeiFile.getInputStream());
				} catch (Exception e) {
					logger.error("error while validating TEI", e);
					MessageHelper.errorMessage("Error while validating TEI"); 
				}
			}
			else
			{
				imageFiles.add((DiskFileItem)fue.getFileItem());
			}
		}
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


		if(md.getExtents().get(0) == "" )
			md.setExtents(null);

		this.modsMetadata.getSeries().add(new ModsSeries());
		
		if(md.getSeries().get(0).getSeriesTitle() =="")
			md.setSeries(null);
		else
		{
			md.getSeries().get(0).setDisplayLabel("series1");
			md.getSeries().get(0).setType("series");
		}
		
		return md;
	}
	
	
	public String save() 
	{         
		logger.info("SAVE!!");
		try {
			if(mabFile == null)
				modsMetadata = updateModsMetadata(modsMetadata);
			
			if(getSelectedContentModel().equals("Monograph"))
			{
	     		if(getImageFiles().size()==0)
	    		{
	    			MessageHelper.errorMessage("You have to upload at least Image");
	    			return "";
	    		}
	    		if (teiFile!=null && getNumberOfTeiPbs()!=getImageFiles().size())
	    		{
	    			MessageHelper.errorMessage("You have to upload " + getNumberOfTeiPbs() + " Images, which are referenced in the TEI");
	    			return "";
	    		}
	    		Volume volume = volumeService.createNewVolume(PropertyReader.getProperty("dlc.content-model.monograph.id"),getSelectedContextId(),null,loginBean.getUserHandle(), modsMetadata, imageFiles, teiFile);
	    		clearAllData();
	    		String title = VolumeUtilBean.getMainTitle(volume.getModsMetadata()).getTitle();
	    		MessageHelper.infoMessage("Neues MonoVolume erstellt! title= " + title + " id= " + volume.getItem().getObjid());
			}
			else if(getSelectedContentModel().equals("Multivolume"))
			{

			
	    		Volume volume = volumeService.createNewMultiVolume(PropertyReader.getProperty("dlc.content-model.multivolume.id"),getSelectedContextId(), loginBean.getUserHandle(), modsMetadata);
	    		clearAllData();
	    		String title = VolumeUtilBean.getMainTitle(volume.getModsMetadata()).getTitle();
	    		MessageHelper.infoMessage("Neues MultiVolume erstellt! title= " + title + " id= " + volume.getItem().getObjid());
			}
			else
			{
	     		if(getImageFiles().size()==0)
	    		{
	    			MessageHelper.errorMessage("You have to upload at least Image");
	    			return "";
	    		}
	    		if (teiFile!=null && getNumberOfTeiPbs()!=getImageFiles().size())
	    		{
	    			MessageHelper.errorMessage("You have to upload " + getNumberOfTeiPbs() + " Images, which are referenced in the TEI");
	    			return "";
	    		}
	    		Volume volume = volumeService.createNewVolume(PropertyReader.getProperty("dlc.content-model.volume.id"),getSelectedContextId(), getSelectedMultiVolumeId(), loginBean.getUserHandle(), modsMetadata, imageFiles, teiFile);
	    		clearAllData();
	    		String title = VolumeUtilBean.getMainTitle(volume.getModsMetadata()).getTitle();
	    		MessageHelper.infoMessage("Neues Volume erstellt! title= " + title + " id= " + volume.getItem().getObjid());
			}
		} catch (Exception e) {
			MessageHelper.errorMessage("An error occured during creation. " + e.toString() + " " + e.getMessage());
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

	public int getNumberOfTeiPbs() {
		return numberOfTeiPbs;
	}

	public void setNumberOfTeiPbs(int numberOfTeiPbs) {
		this.numberOfTeiPbs = numberOfTeiPbs;
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
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		SearchCriterion sc = null ;
		if(loginBean.getUser().getModeratorCollections()!=null && loginBean.getUser().getModeratorCollections().size() > 0)
		{
			for(Collection c : loginBean.getUser().getModeratorCollections())
			{
				if(c.getId().equalsIgnoreCase(getSelectedContextId()))
				{
					sc = new SearchCriterion(SearchType.CONTEXT_ID, getSelectedContextId());
					scList.add(sc);
				}
			}

		}
		else if(scList.size()!=0 && loginBean.getUser().getDepositorCollections()!=null && loginBean.getUser().getDepositorCollections().size() > 0)
		{
			for(Collection c : loginBean.getUser().getDepositorCollections())
			{
				if(c.getId().equalsIgnoreCase(getSelectedContextId()))
				{
					sc = new SearchCriterion( SearchType.CREATEDBY,loginBean.getUser().getName());
					scList.add(sc);
				}
				
			}
		}
		VolumeSearchResult vsr = searchBean.search(new VolumeTypes[]{VolumeTypes.MULTIVOLUME}, scList, SortCriterion.getStandardSortCriteria(), 1000, 0);
		for(Volume vol : vsr.getVolumes())
		{
			multiVolItems.add(new SelectItem(vol.getItem().getObjid(), VolumeUtilBean.getMainTitle(vol.getModsMetadata()).getTitle()));
		}
		return multiVolItems;
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






}