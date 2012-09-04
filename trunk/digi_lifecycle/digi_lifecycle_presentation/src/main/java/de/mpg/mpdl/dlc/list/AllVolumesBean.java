package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

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
import de.mpg.mpdl.dlc.searchLogic.FilterBean;
import de.mpg.mpdl.dlc.searchLogic.FilterCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.FilterCriterion.FilterParam;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;

@ManagedBean
@SessionScoped
@URLMapping(id = "volumes", viewId = "/volumes.xhtml", pattern = "/volumes/#{allVolumesBean.colId}")
public class AllVolumesBean extends SortableVolumePaginatorBean {
	private static Logger logger = Logger.getLogger(AllVolumesBean.class);
	

	private SearchBean searchBean = new SearchBean();
	
	private FilterBean filterBean = new FilterBean();
	

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	

	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private List<SortCriterion> sortCriterionList = SortCriterion.getStandardFilterSortCriteria();	
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int totalNumberOfRecords;
	private Collection collection;
	private String colId;
	

	public AllVolumesBean()
	{		
		super();
		
	}


	@URLAction(onPostback=false)
	public void loadContext()
	{ 
		//update();
		if(colId != null  && !colId.equalsIgnoreCase("all") && !colId.equalsIgnoreCase("my"))
		{
			try {				
				this.collection = contextServiceBean.retrieveCollection(colId, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else
		{
			collection = null;
		}
	}
	

	@Override
	public List<SortCriterion> getSortCriterionList() {
		return sortCriterionList;
	}


	@Override
	public List<SelectItem> getSortIndicesMenu() {
		List<SelectItem> scMenuList = new ArrayList<SelectItem>();
		scMenuList.add(new SelectItem(SortIndices.NEWEST_FILTER.name(), internationalizationHelper.getLabel("sort_criterion_newest")));
		scMenuList.add(new SelectItem(SortIndices.TITLE_FILTER.name(), internationalizationHelper.getLabel("sort_criterion_title")));
		scMenuList.add(new SelectItem(SortIndices.AUTHOR_FILTER.name(), internationalizationHelper.getLabel("sort_criterion_author")));
		scMenuList.add(new SelectItem(SortIndices.YEAR_FILTER.name(), internationalizationHelper.getLabel("sort_criterion_year")));
		return scMenuList;
	}
  

	//TODO
	public List<Volume> retrieveList(int offset, int limit)throws Exception 
	{  
		VolumeSearchResult res = null;
		List<FilterCriterion> fcList = new ArrayList<FilterCriterion>();
		if(colId.equalsIgnoreCase("my") )
		{
			
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
			res = filterBean.itemFilter(new VolumeTypes[]{VolumeTypes.MULTIVOLUME, VolumeTypes.MONOGRAPH}, new VolumeStatus[]{VolumeStatus.pending, VolumeStatus.released}, fcList, getSortCriterionList(), limit, offset, loginBean.getUserHandle());
			
			volServiceBean.loadVolumesForMultivolume(res.getVolumes(), loginBean.getUserHandle(), true);
			
			
		}
		else{
			//List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
			if(collection != null)
			{
				fcList.add(new FilterCriterion(FilterParam.CONTEXT_ID, colId));
				//SearchCriterion sc = new SearchCriterion(SearchType.CONTEXT_ID, colId);
				//scList.add(sc);
			}
			
			
			res = filterBean.itemFilter(new VolumeTypes[]{VolumeTypes.MULTIVOLUME, VolumeTypes.MONOGRAPH}, new VolumeStatus[]{VolumeStatus.released}, fcList, getSortCriterionList(), limit, offset, loginBean.getUserHandle());
			volServiceBean.loadVolumesForMultivolume(res.getVolumes(), loginBean.getUserHandle(), true);
			//res = searchBean.advancedSearchVolumes(scList, getSortCriterionList(), limit, offset);
		}
		this.totalNumberOfRecords = res.getNumberOfRecords();
		
		
		return res.getVolumes();
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
	
	
	
	public Collection getCollection() {
		return collection;
	}


	public void setCollection(Collection collection) {
		this.collection = collection;
	}


	public String getColId() {
		return colId;
	}


	public void setColId(String colId) {
		this.colId = colId;
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
			vol = volServiceBean.releaseVolume(vol.getItem().getObjid(), userHandle);
			MessageHelper.infoMessage(ApplicationBean.getResource("Messages", "releasedSuccessfully"));
		} catch (Exception e) {
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_saveAndReleaseStruct"));
			logger.error("Error while releasing volume " + vol.getObjidAndVersion(), e);
		}

	}
	
	/**
	 * Retrieve the 5 last works of all collections
	 * TODO: check multivolume, what can be displayed here?
	 * @return
	 */
	public  List<Volume> getStartPageVolumes ()
	{ 
		List<Volume> volumes = new ArrayList<Volume>();
		
		try
		{
//			VolumeSearchResult res = searchBean.advancedSearchVolumes(new ArrayList<SearchCriterion>(), getSortCriterionList(), 6, 0);	
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
			

			List<SortCriterion> sortList = new ArrayList<SortCriterion>();
			sortList.add(new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING));
			VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.VOLUME};
			SearchBean searchBean = new SearchBean();
			VolumeSearchResult res = searchBean.search(volTypes, scList, sortList, 10, 0);		
			
			for(Volume vol : res.getVolumes())
			{ 		
					if(vol != null && vol.getPages() != null && vol.getPages().size()>0)
					{					
						volumes.add(vol);
					}
			}
		logger.info("volumes for startpage carusel loaded");
		}
		catch(Exception e)
		{
			logger.error("error loading volumes for startpage carusel", e);
			return null;
		}
		
		return volumes;
	}


	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}


	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}



	
}
