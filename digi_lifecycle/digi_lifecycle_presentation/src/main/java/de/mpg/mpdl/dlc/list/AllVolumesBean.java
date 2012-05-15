package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.SortableVolumePaginatorBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.search.Criterion.Operator;
import de.mpg.mpdl.dlc.search.FilterBean;
import de.mpg.mpdl.dlc.search.FilterCriterion;
import de.mpg.mpdl.dlc.search.SortCriterion;
import de.mpg.mpdl.dlc.search.FilterCriterion.FilterParam;
import de.mpg.mpdl.dlc.search.SearchBean;
import de.mpg.mpdl.dlc.search.SearchCriterion;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;

@ManagedBean
@SessionScoped
@URLMapping(id = "volumes", viewId = "/volumes.xhtml", pattern = "/volumes/#{allVolumesBean.contextId}")
public class AllVolumesBean extends SortableVolumePaginatorBean {
	private static Logger logger = Logger.getLogger(AllVolumesBean.class);
	

	private SearchBean searchBean = new SearchBean();
	
	private FilterBean filterBean = new FilterBean();
	

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	

	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
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
		//update();
		if(contextId != null  && !contextId.equalsIgnoreCase("all") && !contextId.equalsIgnoreCase("my"))
		{
			try {				
				this.context = contextServiceBean.retrieveContext(contextId, null);
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

		if(contextId.equalsIgnoreCase("my") )
		{
			List<FilterCriterion> fcList = new ArrayList<FilterCriterion>();
			FilterCriterion fc;
//			for(Grant grant: loginBean.getUser().getGrants())
//			{
//				
//				
//				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.moderator")))
//				{
//					for(Collection c : loginBean.getUser().getModeratorCollections())
//					{
//						sc = new SearchCriterion(SearchType.CONTEXT_ID, c.getId());
//						scList.add(sc);
//					}
//				}
//				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.depositor")))
//				{
//					sc = new SearchCriterion(SearchType.CREATEDBY,loginBean.getUser().getName());
//					scList.add(sc);
//				}
//			}
			
			if(loginBean.getUser().getModeratorCollections()!=null && loginBean.getUser().getModeratorCollections().size() > 0)
			{
				for(int i=0; i< loginBean.getUser().getModeratorCollections().size(); i++)
				{
					Collection c = loginBean.getUser().getModeratorCollections().get(i);
					if(i == 0)
						fc = new FilterCriterion(FilterParam.CONTEXT_ID, c.getId());
					else
						fc = new FilterCriterion(Operator.OR,FilterParam.CONTEXT_ID, c.getId());
					fcList.add(fc);
				}
			}
			if(loginBean.getUser().getDepositorCollections()!=null && loginBean.getUser().getDepositorCollections().size() > 0)
			{
				for(int i=0; i<loginBean.getUser().getDepositorCollections().size(); i++)
				{
					Collection c = loginBean.getUser().getDepositorCollections().get(i);
					if(fcList.size()==0)
					{
						fc = new FilterCriterion(FilterParam.CREATED_BY,loginBean.getUser().getId());
						fcList.add(fc);
					}
					else
					{
						fc = new FilterCriterion(Operator.OR, FilterParam.CREATED_BY,loginBean.getUser().getId());
						fcList.add(fc);
					}
				}
			}
			res = filterBean.itemFilter(new VolumeTypes[]{VolumeTypes.MULTIVOLUME, VolumeTypes.MONOGRAPH}, new VolumeStatus[]{VolumeStatus.pending, VolumeStatus.released}, fcList, SortCriterion.getStandardFilterSortCriteria(), limit, offset, loginBean.getUserHandle());
			
			volServiceBean.loadVolumesForMultivolume(res.getVolumes(), loginBean.getUserHandle(), true);
			
			
		}
		else{
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
			if(context != null)
			{
				SearchCriterion sc = new SearchCriterion(SearchType.CONTEXT_ID, contextId);
				scList.add(sc);
			}
			res = searchBean.advancedSearchVolumes(scList, getSortCriterionList(), limit, offset);
		}
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
	
	public List<Volume> getRelatedVolumes(Volume vol)
	{
		List<Volume> vols = new ArrayList<Volume>();
		for(Relation rel : vol.getItem().getRelations())
		{
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
	
	public void release(Volume vol)
	{
		String userHandle = loginBean.getUserHandle();
		try {
			vol = volServiceBean.releaseVolume(vol, userHandle);;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
}
