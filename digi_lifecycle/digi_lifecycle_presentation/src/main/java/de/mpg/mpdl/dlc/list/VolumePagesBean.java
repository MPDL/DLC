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
import de.mpg.mpdl.dlc.viewer.ViewVolume;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;


@ManagedBean
@SessionScoped
public class VolumePagesBean extends BasePaginatorBean<Page>{
	
	private static Logger logger = Logger.getLogger(VolumePagesBean.class); 


	@EJB 
	private VolumeServiceBean volServiceBean;
	
	@ManagedProperty("#{viewVolume}")
	private ViewVolume	viewVolume;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	

	private int totalNumberOfRecords;
	private List<Page> pageList = new ArrayList<Page>();
    
	public VolumePagesBean()
	{
		super();

		

	}
	
	public List<Page> retrieveList(int offset, int limit) throws Exception 
	{
		pageList = volServiceBean.retrieveVolume(viewVolume.getVolumeId(), loginBean.getUserHandle()).getPages();
		List<Page> subList = pageList.subList(offset, offset+limit);
		totalNumberOfRecords = pageList.size();
		return subList;
	}

	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public String getNavigationString() {
		return "pretty:viewVolume";
	}
	
	public ViewVolume getViewVolume() {
		return viewVolume;
	}

	public void setViewVolume(ViewVolume viewVolume) {
		this.viewVolume = viewVolume;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

}
