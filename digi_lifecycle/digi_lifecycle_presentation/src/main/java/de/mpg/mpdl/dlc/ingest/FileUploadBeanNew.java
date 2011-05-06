package de.mpg.mpdl.dlc.ingest;


import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.apache.commons.fileupload.FileItem;
import org.richfaces.component.UIExtendedDataTable;
import org.richfaces.event.DropEvent;
import org.richfaces.event.DropListener;

import de.mpg.mpdl.jsf.components.fileUpload.FileUploadEvent;

 
/**
 * @author Ilya Shaikovsky
 *
 */
@ManagedBean
@SessionScoped
public class FileUploadBeanNew implements Serializable, ActionListener, DropListener {
 
    private ArrayList<FileItem> files = new ArrayList<FileItem>();
	private Collection<Object> selection;
	private List<FileItem> selectionItems = new ArrayList<FileItem>();
 

    public void paint(OutputStream stream, Object object) throws IOException {
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

	public void processAction(ActionEvent evt) throws AbortProcessingException {
		FileUploadEvent fue = (FileUploadEvent) evt;
		if(fue.getFileItem()!=null)
		{
			files.add(fue.getFileItem());
		}
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

	


}