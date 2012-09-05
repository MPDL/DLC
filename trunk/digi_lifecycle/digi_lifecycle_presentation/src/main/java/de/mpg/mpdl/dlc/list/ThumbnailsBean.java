package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
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
//			System.out.println("ID: " + volume.getProperties().getLatestVersion().getObjid());
			if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
			{
				this.volume = volServiceBean.retrieveVolume(volumeId, loginBean.getUserHandle());
				logger.info("Load new book" + volumeId);
				setElementsPerPage(24);
				setCurrentPageNumber(1);
			}

			
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_loadVolume"));
		}
	}
	
	public List<Page> retrieveList(int offset, int limit) throws Exception 
	{
		pageList = volServiceBean.retrieveVolume(volumeId, loginBean.getUserHandle()).getPages();
		totalNumberOfRecords = pageList.size();
		List<Page> subList = pageList.subList(offset-1, (totalNumberOfRecords > (offset-1+limit))?(offset-1+limit): totalNumberOfRecords);

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
