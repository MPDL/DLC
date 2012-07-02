package de.mpg.mpdl.dlc.viewer;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.list.ThumbnailsBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.Volume;


@ManagedBean
@SessionScoped
@URLMapping(id="viewVolume", pattern = "/volume/#{viewVolume.volumeId}/#{viewVolume.currentPageNumber}", viewId = "/viewVolume.xhtml")
public class ViewVolume {
	private static Logger logger = Logger.getLogger(ViewVolume.class);
	

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	
	private String volumeId;
	private Volume volume;
	private int currentPageNumber;
	private int position;
	

	@URLAction(onPostback=false)
	public void loadVolume()
	{  
		try { 
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{
				this.volume = volServiceBean.loadCompleteVolume(volumeId, null);
				logger.info("Load new book" + volumeId);
			}
			
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_loadVolume"));
		}
	}
	
	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}
	
	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public String getVolumeId() {
		return volumeId;
	}
	
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber= currentPageNumber;
	}
	 
	public int getPagePosition(Page p)
	{ 
		position = getVolume().getPages().indexOf(p) + 1;
		return position;
	}



}