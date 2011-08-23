package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
@URLMapping(id = "volumes", viewId = "/volumes.xhtml", pattern = "/volumes")

public class AllVolumesBean extends BasePaginatorBean<Volume> {
	
	@EJB
	private VolumeServiceBean volServiceBean;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	public AllVolumesBean()
	{
		super();
		//TODO
 
	}
  
	//TODO
	public List<Volume> retrieveList(int offset, int limit)throws Exception {
		List<Volume> volList = volServiceBean.retrieveVolumes(limit, offset, loginBean.getUserHandle());
		return volList;
 
		
	}

	public int getTotalNumberOfRecords() 
	{
		return volServiceBean.getNumberOfVolumes();
	}

	@Override
	public String getNavigationString() {
		return "pretty:volumes";
	}
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	
}
