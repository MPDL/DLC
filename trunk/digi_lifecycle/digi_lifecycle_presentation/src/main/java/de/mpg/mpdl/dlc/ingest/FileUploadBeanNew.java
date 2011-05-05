package de.mpg.mpdl.dlc.ingest;


import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.apache.commons.fileupload.FileItem;

import de.mpg.mpdl.jsf.components.fileUpload.FileUploadEvent;

 
/**
 * @author Ilya Shaikovsky
 *
 */
@ManagedBean
@SessionScoped
public class FileUploadBeanNew implements Serializable, ActionListener {
 
    private ArrayList<FileItem> files = new ArrayList<FileItem>();
 

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
			
		System.out.println("Uploaded" + fue.getFileItem().getFieldName() + " Now size: " + getSize());
		
	}

	


}