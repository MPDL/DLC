package de.mpg.mpdl.dlc.viewer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;

@ManagedBean
@SessionScoped
@URLMapping(id = "viewPages", pattern = "/view/#{viewPages.volumeId}/#{viewPages.selectedPageNumber}", viewId = "/viewPages.xhtml")
public class ViewPages {
	
	private static Logger logger = Logger.getLogger(ViewPages.class);
	@EJB
	private VolumeServiceBean volServiceBean;
	
	private String volumeId;
	
	private Volume volume;
	
	private int selectedPageNumber;
	
	private Page selectedPage;
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		try { 
			ModsMetadata md = new ModsMetadata();
			md.printExample();
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{
				logger.info("Load new book" + volume);
				this.volume = volServiceBean.retrieveVolume(volumeId, null);
				
				
			}
			
			this.setSelectedPage(volume.getPages().get(getSelectedPageNumber()-1));
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
	
	public String getDigilibScalerUrlForPage(Page p)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.scaler.url");
			String url = digilibUrl + "?fn=" + URLEncoder.encode(p.getFile().getHref(), "UTF-8") + "&dh=1000&dw=500";
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for image", e);
			return null;
		}
	}
	
	public String getDigilibJQueryUrlForPage(Page p)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.jquery.url");
			String url = digilibUrl + "?fn=" + URLEncoder.encode(p.getFile().getHref(), "UTF-8");
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for image", e);
			return null;
		}
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
		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        if (volume.getPages().size()>= selectedPageNumber + 1)
        {
           selectedPageNumber ++;
           loadVolume();
        }
        return "";
	}
	
	public String goToPreviousPage()
	{
		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        if (selectedPageNumber -1 > 0)
        {
           selectedPageNumber --;
           loadVolume();
        }
        return null;
	}
	
	public String goToLastPage()
	{
		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = volume.getPages().size();
        loadVolume();
        return null;
	}
	
	public String goToFirstPage()
	{
		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = 1; 
        loadVolume();
        return null;
	}
	
	
	

}
