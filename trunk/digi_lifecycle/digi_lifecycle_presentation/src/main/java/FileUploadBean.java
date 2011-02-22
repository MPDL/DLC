
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;


import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;



/**
 * @author Ilya Shaikovsky
 *
 */
@ManagedBean
@SessionScoped 
public class FileUploadBean{
	    
	    private ArrayList<java.io.File> files = new ArrayList<java.io.File>();
	    private int uploadsAvailable = 5;
	    private boolean autoUpload = false;
	    private boolean useFlash = false;
	    public int getSize() {
	        if (getFiles().size()>0){
	            return getFiles().size();
	        }else 
	        {
	            return 0;
	        }
	    }

	    public FileUploadBean() {
	    }

	    public void paint(OutputStream stream, Object object) throws IOException {
	        BufferedImage img = ImageIO.read(getFiles().get((Integer)object));
	    	ImageIO.write(img, "jpeg", stream);
	    }
	    public void listener(UploadEvent event) throws Exception{
	        UploadItem item = event.getUploadItem();
	       
	        files.add(item.getFile());
	        uploadsAvailable--;
	    }  
	      
	    public String clearUploadData() {
	        files.clear();
	        setUploadsAvailable(5);
	        return null;
	    }
	    
	    public long getTimeStamp(){
	        return System.currentTimeMillis();
	    }
	    
	    public ArrayList<java.io.File> getFiles() {
	        return files;
	    }

	    public void setFiles(ArrayList<java.io.File> files) { 
	        this.files = files;
	    }

	    public int getUploadsAvailable() {
	        return uploadsAvailable;
	    }

	    public void setUploadsAvailable(int uploadsAvailable) {
	        this.uploadsAvailable = uploadsAvailable;
	    }

	    public boolean isAutoUpload() {
	        return autoUpload;
	    }

	    public void setAutoUpload(boolean autoUpload) {
	        this.autoUpload = autoUpload;
	    }

	    public boolean isUseFlash() {
	        return useFlash;
	    }

	    public void setUseFlash(boolean useFlash) {
	        this.useFlash = useFlash;
	    }
	    
	    public String getTest()
	    {
	    	return "test2";
	    }

}
