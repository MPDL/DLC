package de.mpg.mpdl.dlc.ingest;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;
import org.richfaces.component.UIExtendedDataTable;
import org.richfaces.event.DropEvent;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
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
	private ModsMetadata modsMetadata = new ModsMetadata();
	private FileItem teiFile;
	private int numberOfTeiPbs;
	
    private Collection<Object> selection;
	private List<FileItem> selectionItems = new ArrayList<FileItem>();
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int moveTo;
	
	private String selectedContextId;
	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
	
 
	@EJB
	private VolumeServiceBean volumeService;
	
	public IngestBean()
	{
		this.modsMetadata.getTitles().add(new ModsTitle());
		this.modsMetadata.getNames().add(new ModsName());
		
	}

    public void paint(OutputStream stream, Object object) throws Exception {
    	
    	
    	stream.write(getImageFiles().get((Integer) object).get());
        stream.close();
    }
    
    public String clearUploadData() {
        imageFiles.clear();
        return null;
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
	
	
	
	public void save()
	{
		
		//ModsMetadata md = new ModsMetadata();
		//ModsTitle title = new ModsTitle();
		//title.setTitle("Test title");
		//md.getTitles().add(title);
    	
    	try {
    		
    		
			
    		if(getImageFiles().size()==0)
    		{
    			MessageHelper.errorMessage("You have to upload at least Image");
    			return;
    		}
    		if (getNumberOfTeiPbs()!=getImageFiles().size())
    		{
    			MessageHelper.errorMessage("You have to upload " + getNumberOfTeiPbs() + " Images, which are referenced in the TEI");
    			return;
    		}
    		
    		volumeService.createNewVolume(getSelectedContextId(), getLoginBean().getUserHandle(), modsMetadata, imageFiles, teiFile);
		} catch (Exception e) {
			MessageHelper.errorMessage("An error occured during creation. " + e.toString() + " " + e.getMessage());
		}
    	
	
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public LoginBean getLoginBean() {
		return loginBean;
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
		contextSelectItems.clear();
		for(Context c : loginBean.getDepositorContexts())
		{
			contextSelectItems.add(new SelectItem(c.getObjid(), c.getProperties().getName()));
		}
		
		return contextSelectItems;
	}

	public void setContextSelectItems(List<SelectItem> contextSelectItems) {
		this.contextSelectItems = contextSelectItems;
	}
	
	
	

	


}