package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.sun.tools.javac.comp.Todo;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.valueobjects.DlcBook;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
public class AllVolumesBean extends BasePaginatorBean<Volume> {
	
	@EJB
	private VolumeServiceBean volServiceBean;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

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

	@Override
	public int getTotalNumberOfRecords() {
		return 100;
	}

	@Override
	public String getNavigationString() {
		return "pretty:";
	}
	
}
