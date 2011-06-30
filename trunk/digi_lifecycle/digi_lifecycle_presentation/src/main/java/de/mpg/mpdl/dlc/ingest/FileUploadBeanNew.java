package de.mpg.mpdl.dlc.ingest;


import gov.loc.mods.v3.ModsDocument;

import java.io.File;
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
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.richfaces.component.UIExtendedDataTable;
import org.richfaces.event.DropEvent;
import org.richfaces.event.DropListener;

import de.escidoc.core.client.Authentication;
import de.mpg.mpdl.dlc.beans.IngestServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.jsf.components.fileUpload.FileUploadEvent;

 
/**
 * @author Ilya Shaikovsky
 *
 */
@ManagedBean
@SessionScoped
public class FileUploadBeanNew implements Serializable, DropListener {
 
	private static Logger logger = Logger.getLogger(FileUploadBeanNew.class);
    private ArrayList<FileItem> files = new ArrayList<FileItem>();
	private Collection<Object> selection;
	private List<FileItem> selectionItems = new ArrayList<FileItem>();
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int moveTo;
 
	@EJB
	private IngestServiceBean ingestService;

    public void paint(OutputStream stream, Object object) throws Exception {
    	
    	
    	stream.write(getFiles().get((Integer) object).get());
        stream.close();
    }
    
    public String clearUploadData() {
        files.clear();
        return null;
    }
 
    public int getSize() {
        if (getFiles().size() > 0) {
            return getFiles().size();
        } else {
            return 0;
        }
    }
 
    public long getTimeStamp() {
        return System.currentTimeMillis();
    }
 
    public ArrayList<FileItem> getFiles() {
        return files; 
    }
 
    public void setFiles(ArrayList<FileItem> files) {
        this.files = files;
    }

	
	
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
	
	public void fileUploaded(FileUploadEvent evt)
	{
		
		logger.info("File uploaded" + evt.getFileItem().getName() +" (" + evt.getFileItem().getSize()+")");
		FileUploadEvent fue = (FileUploadEvent) evt;
		if(fue.getFileItem()!=null)
		{
			files.add(fue.getFileItem());
			
		}
		
		
	
	}
	
	public String indexOfFile(FileItem f)
	{
		return String.valueOf(getFiles().indexOf(f));
	}

	public void setMoveTo(int moveTo) {
		this.moveTo = moveTo;
	}

	public int getMoveTo() {
		return moveTo;
	}
	
	
	
	public void save() throws Exception
	{
		
		ModsDocument modsdoc = ModsDocument.Factory.newInstance();
    	modsdoc.addNewMods().addNewTitleInfo().addNewTitle().setStringValue("Test Title");
    	
    	ingestService.createNewVolume("escidoc:5002", getLoginBean().getUserHandle(), modsdoc, files);
    	
	
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}
	
	
	

	


}