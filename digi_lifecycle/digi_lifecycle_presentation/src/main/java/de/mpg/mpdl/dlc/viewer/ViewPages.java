package de.mpg.mpdl.dlc.viewer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;

@ManagedBean
@RequestScoped
@URLMapping(id = "viewPage", pattern = "/view/#{viewPages.volumeId}/#{viewPages.selectedPageNumber}", viewId = "/viewPages.xhtml")
public class ViewPages {
	
	private static Logger logger = Logger.getLogger(ViewPages.class);
	@EJB
	private VolumeServiceBean volServiceBean;
	
	private String volumeId;
	
	private Volume volume;
	
	private int selectedPageNumber;
	
	private Page selectedPage;
	
	@URLAction
	public void loadVolume() throws Exception
	{
		if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
		{
			this.volume = volServiceBean.retrieveVolume(volumeId, null);
			
		}
		
		this.setSelectedPage(volume.getPages().get(getSelectedPageNumber()-1));
		
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
	
	public String getDigilibUrlForPage(Page p)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.scaler.url");
			String url = digilibUrl + "?fn=" + URLEncoder.encode(p.getPath(), "UTF-8") + "&mo=file";
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
	
	
	

}
