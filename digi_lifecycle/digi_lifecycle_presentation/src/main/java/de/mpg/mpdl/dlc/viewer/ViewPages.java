package de.mpg.mpdl.dlc.viewer;

import java.net.URLEncoder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;

@ManagedBean
@SessionScoped
@URLMapping(id = "viewPages", pattern = "/view/#{viewPages.volumeId}/#{viewPages.selectedPageNumber}", viewId = "/viewPages.xhtml")
public class ViewPages {
	
	private static Logger logger = Logger.getLogger(ViewPages.class);
	@EJB
	private VolumeServiceBean volServiceBean;
	
	private String volumeId;
	
	private static Volume volume;
	
	private int selectedPageNumber;
	
	private Page selectedPage;
	private int i = 0;

	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		try { 
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{    
				this.volume = volServiceBean.retrieveVolume(volumeId, null);
				logger.info("Load new book" + volumeId);
				
			}  
			this.setSelectedPage(volume.getPages().get(getSelectedPageNumber()));
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage("Problem while loading volume");
		}
		
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setSelectedPage(Page selectedPage) {
		this.selectedPage = selectedPage;
	}

	public Page getSelectedPage() {
		return selectedPage;
	}
	
	

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public String getVolumeId() {
		return volumeId;
	}

	public void setSelectedPageNumber(int selectedPageNumber) {
		this.selectedPageNumber = selectedPageNumber;
	}

	public int getSelectedPageNumber() {
		return selectedPageNumber;
	}
	
	public String goToNextPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        if (volume.getPages().size()>= selectedPageNumber + 1)
        {
           selectedPageNumber ++;
           loadVolume();
        }
        return "";
	}
	
	public String goToPreviousPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) - 1;
        if (selectedPageNumber -1 >= 0)
        {
           selectedPageNumber --;
           loadVolume();
        }
        return null;
	} 
	
	public String goToLastPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = volume.getPages().size()-1;
        loadVolume();
        return null;
	}
	
	public String goToFirstPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = 0; 
        loadVolume();
        return null;
	}
	

	
	
	

}
