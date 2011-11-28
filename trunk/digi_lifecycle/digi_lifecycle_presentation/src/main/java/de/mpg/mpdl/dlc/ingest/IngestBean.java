package de.mpg.mpdl.dlc.ingest;


import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.search.SearchBean;
import de.mpg.mpdl.dlc.search.SearchCriterion;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.mods.ModsIdentifier;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.mods.ModsNote;
import de.mpg.mpdl.dlc.vo.mods.ModsPublisher;
import de.mpg.mpdl.dlc.vo.mods.ModsTitle;
import de.mpg.mpdl.jsf.components.fileUpload.FileUploadEvent;

 
/**
 * @author Ilya Shaikovsky
 *
 */
@ManagedBean
@SessionScoped
public class IngestBean implements Serializable {
 
	private static Logger logger = Logger.getLogger(IngestBean.class);
   
	private ArrayList<FileItem> imageFiles = new ArrayList<FileItem>();
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


	@EJB
	private VolumeServiceBean volumeService;
	
	@EJB 
	private ContextServiceBean contextServiceBean;

	@EJB
	private SearchBean searchBean;
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
					for(Context c: loginBean.getUser().getCreatedContexts())
						this.contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
				}
				else {
					for(Context c : loginBean.getUser().getDepositorContexts())
					{  
						if(!ids.contains(c.getObjid()))
						{
							ids.add(c.getObjid());
							item = new SelectItem(c.getObjid(),c.getProperties().getName());
							this.contextSelectItems.add(item);
						}
					}
					for(Context c : loginBean.getUser().getModeratorContexts())
					{  
						if(!ids.contains(c.getObjid()))
						{
							ids.add(c.getObjid());
							item = new SelectItem(c.getObjid(),c.getProperties().getName());
							this.contextSelectItems.add(item);
						}
					}
				}
			}catch(Exception e)
			{
					
			}
		}
		this.selectedContextId = (String) contextSelectItems.get(0).getValue();	
	}


	
	public void addModsMetadata()
	{
		this.modsMetadata.getTitles().add(new ModsTitle());
		this.modsMetadata.getNames().add(new ModsName());
		this.modsMetadata.getNotes().add(new ModsNote());
		this.modsMetadata.getIdentifiers().add(new ModsIdentifier());
		this.modsMetadata.getPublishers().add(new ModsPublisher());
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
 
    public ArrayList<FileItem> getImageFiles() {
        return imageFiles; 
    }
 
    public void setImageFiles(ArrayList<FileItem> files) {
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
				imageFiles.add(fue.getFileItem());
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
	
	
	
	public String save() 
	{ 
		logger.info("SAVE!!");
		try {
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
		scList.add(new SearchCriterion(SearchType.CONTEXT_ID, getSelectedContextId()));
		
		VolumeSearchResult vsr = searchBean.search(new VolumeTypes[]{VolumeTypes.MULTIVOLUME}, scList, 1000, 0);
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