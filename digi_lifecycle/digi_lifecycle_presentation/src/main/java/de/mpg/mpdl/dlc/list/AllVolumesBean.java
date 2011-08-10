package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.sun.tools.javac.comp.Todo;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.valueobjects.DlcBook;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped

public class AllVolumesBean extends BasePaginatorBean<Volume> {
	
	@EJB
	private VolumeServiceBean volServiceBean;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int totalNumberOfRecords;
	private List<Volume> volList= new ArrayList<Volume>();


	public AllVolumesBean()
	{
		super();
		//TODO

	}
  
	//TODO
	public List<Volume> retrieveList(int offset, int limit)throws Exception {
		
		volList = volServiceBean.retrieveVolumes(limit, offset, loginBean.getUserHandle());
		totalNumberOfRecords = volList.size();
		List<Volume> subList = volList.subList(offset, (totalNumberOfRecords > (offset+limit))?(offset+limit): totalNumberOfRecords);
		return subList;
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() {
		return "pretty:viewPage";
	}
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	@Override
	public int getCurrentPageNumber() {
		return 1;
	}
	
}
