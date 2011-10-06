package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ApplicationServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
@URLMapping(id = "volumes", viewId = "/volumes.xhtml", pattern = "/volumes/#{allVolumesBean.contextId}")
public class AllVolumesBean extends BasePaginatorBean<Volume> {
	private static Logger logger = Logger.getLogger(AllVolumesBean.class);
	
	@EJB
	private VolumeServiceBean volServiceBean;
	
	@EJB
	private ApplicationServiceBean appServiceBean;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int totalNumberOfRecords;
	private Context context;
	private String contextId;
	private List<Volume> relatedVolumes;
	
	public AllVolumesBean()
	{
		super();
		//TODO
	}
	public List<Volume> getRelatedVolumes(Volume vol)
	{
		List<Volume> vols = new ArrayList<Volume>();
		for(Relation rel : vol.getItem().getRelations())
		{
			System.err.println(rel.getObjid());
			Volume relatedVolume = null;
			try {
				relatedVolume= volServiceBean.retrieveVolume(rel.getObjid(), loginBean.getUserHandle());
			} catch (Exception e) {
			}
			if(relatedVolume != null)
				vols.add(relatedVolume);
		}
			return vols;
	}

	@URLAction(onPostback=false)
	public void loadContext()
	{ 
		if(contextId != null && !contextId.equalsIgnoreCase("all"))
		{
			try {
				this.context = appServiceBean.retrieveContext(contextId, null);
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
