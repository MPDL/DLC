package de.mpg.mpdl.dlc.list;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
@URLMapping(id = "volumes", viewId = "/volumes.xhtml", pattern = "/volumes/#{allVolumesBean.contextId}")
public class AllVolumesBean extends BasePaginatorBean<Volume> {
	
	@EJB
	private VolumeServiceBean volServiceBean;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	


	private int totalNumberOfRecords;
	private Context context;
	private String contextId;
	
	public AllVolumesBean()
	{
		super();
		//TODO
	}
	
	@URLAction(onPostback=false)
	public void loadContext()
	{ 
		if(contextId != null && !contextId.equalsIgnoreCase("all"))
		{
			try {
				this.context = volServiceBean.retrieveContext(contextId, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else
		{
			context = null;
		}
	}
	
	
  
	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	//TODO
	public List<Volume> retrieveList(int offset, int limit)throws Exception 
	{
		VolumeSearchResult res = null;
		if(context != null)
		{
			 res = volServiceBean.retrieveContextVolumes(contextId, limit, offset, loginBean.getUserHandle());
		}
		else
			res = volServiceBean.retrieveVolumes(limit, offset, loginBean.getUserHandle());
		this.totalNumberOfRecords = res.getNumberOfRecords();
		return res.getVolumes();
 
		
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getTotalNumberOfRecords() 
	{
		return totalNumberOfRecords;
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
