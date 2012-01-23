package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.viewer.ViewPages;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;


@ManagedBean
@SessionScoped
@URLMapping(id="thumbnails", viewId="/thumbnails.xhtml", pattern="/thumbnails/#{thumbnailsBean.volumeId}")
public class ThumbnailsBean extends BasePaginatorBean<Page>{
	
	private static Logger logger = Logger.getLogger(ThumbnailsBean.class); 

	
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private String volumeId;
	private Volume volume;
	private int totalNumberOfRecords;

	private List<Page> pageList = new ArrayList<Page>();
    
	public ThumbnailsBean()
	{
		super();
	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{  
		try { 
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{
				this.volume = volServiceBean.retrieveVolume(volumeId, null);
				logger.info("Load new book" + volumeId);
			}
			setElementsPerPage(24);
			
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage("Problem while loading volume");
		}
	}
	
	public List<Page> retrieveList(int offset, int limit) throws Exception 
	{
		pageList = volServiceBean.retrieveVolume(volumeId, loginBean.getUserHandle()).getPages();
		totalNumberOfRecords = pageList.size();
		List<Page> subList = pageList.subList(offset, (totalNumberOfRecords > (offset+limit))?(offset+limit): totalNumberOfRecords);
		return subList;
	}
	
//	public int getCurrentPageNumber() 
//	{
//	    int a=viewPages.getSelectedPageNumber();
//	    int b=getElementsPerPage();
//	    int currentPageNr = (double)a/(double)b > (a/b) ? a/b+1 : a/b ;
//	    setCurrentPageNumber(currentPageNr);
//		return currentPageNr ;
//	}
	
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

	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public String getNavigationString() {
		return "pretty:thumbnails";
	}
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	



}
