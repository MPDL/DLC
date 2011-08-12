package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.viewer.ViewPages;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;


@ManagedBean
@SessionScoped
public class VolumeBean extends BasePaginatorBean<Page>{
	
	private static Logger logger = Logger.getLogger(VolumeBean.class); 

	@EJB 
	private VolumeServiceBean volServiceBean;
	
	@ManagedProperty("#{viewPages}")
	private ViewPages viewPages;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int totalNumberOfRecords;

	private List<Page> pageList = new ArrayList<Page>();
    
	public VolumeBean()
	{
		super();
	}
	
	public List<Page> retrieveList(int offset, int limit) throws Exception 
	{
		System.err.println("offset= " + offset);
		pageList = volServiceBean.retrieveVolume(viewPages.getVolumeId(), loginBean.getUserHandle()).getPages();
		totalNumberOfRecords = pageList.size();
		List<Page> subList = pageList.subList(offset, (totalNumberOfRecords > (offset+limit))?(offset+limit): totalNumberOfRecords);
		return subList;
	}
	
	public int getCurrentPageNumber() 
	{
	    int a=viewPages.getSelectedPageNumber();
	    int b=getElementsPerPage();
	    int currentPageNr = (double)a/(double)b > (a/b) ? a/b+1 : a/b ;
	    setCurrentPageNumber(currentPageNr);
		return currentPageNr ;
	}

	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public String getNavigationString() {
		return "pretty:viewVolume";
	}
	
	public ViewPages getViewPages() {
		return viewPages;
	}

	public void setViewPages(ViewPages viewPages) {
		this.viewPages = viewPages;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	



}
