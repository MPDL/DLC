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
 
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
 
/**
 * @author Ilya Shaikovsky
 *
 */
@ManagedBean
@SessionScoped
public class FileUploadBean implements Serializable {
 
    private ArrayList<File> files = new ArrayList<File>();
 
    public void paint(OutputStream stream, Object object) throws IOException {
        stream.write(getFiles().get((Integer) object).getData());
        stream.close();
    }
 
    public void listener(FileUploadEvent event) throws Exception { 
        UploadedFile item = event.getUploadedFile();
        File file = new File();
        file.setLength(item.getData().length);
        file.setName(item.getName());
        file.setData(item.getData());
        files.add(file);
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
 
    public ArrayList<File> getFiles() {
        return files; 
    }
 
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

	


}