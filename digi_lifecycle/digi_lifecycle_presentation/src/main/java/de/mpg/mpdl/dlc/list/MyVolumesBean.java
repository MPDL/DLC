/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.CreateVolumeServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.SortableVolumePaginatorBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.editor.StructuralEditorBean;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem.IngestStatus;
import de.mpg.mpdl.dlc.searchLogic.FilterBean;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
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
@URLMapping(id = "myVolumes", viewId = "/myvolumes.xhtml", pattern = "/myvolumes")
public class MyVolumesBean extends SortableVolumePaginatorBean {
	private static Logger logger = Logger.getLogger(MyVolumesBean.class);
	

	private SearchBean searchBean = new SearchBean();
	
	private FilterBean filterBean = new FilterBean();
	

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	private CreateVolumeServiceBean createVolServiceBean = new CreateVolumeServiceBean();
	

	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	//private List<SortCriterion> sortCriterionList = new ArrayList<SortCriterion>();	
	
	private CombinedSortCriterion selectedSortCriterion; 
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int totalNumberOfRecords;

	private String filterString;
	
	private String filterItemState = "all";
	
	private OrganizationalUnitServiceBean ouServiceBean = new OrganizationalUnitServiceBean();
	private Organization orga;
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	private List<DatabaseItem> uploadedItems = new ArrayList<DatabaseItem>();
	
	public MyVolumesBean()
	{		
		super();
		filterString = null;
		setCurrentPageNumber(1);
		selectedSortCriterion = CombinedSortCriterion.LAST_MODIFIED_DESC;
	}

	
	@URLAction(onPostback=false)
	public void init()
	{
		
		
	} 
	
	

	@Override
	public List<SortCriterion> getSortCriterionList() 
	{
		return selectedSortCriterion.getScList();
	}


	@Override
	public List<SelectItem> getSortIndicesMenu() 
	{
		List<SelectItem> scMenuList = new ArrayList<SelectItem>();
		
		scMenuList.add(new SelectItem(CombinedSortCriterion.AUTHOR_TITLE_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_author")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.AUTHOR_TITLE_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_author_desc")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.TITLE_YEAR_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_title")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.TITLE_YEAR_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_title_desc")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.YEAR_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_year")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.YEAR_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_year_desc")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.SIGNATURE_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_signature")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.NEWEST_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_newest")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.LAST_MODIFIED_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_lastModified")));
		
		
		return scMenuList;
	}
  

	//TODO
	public List<Volume> retrieveList(int offset, int limit)throws Exception 
	{  

		VolumeSearchResult res = null;
		List<SearchCriterion> fcList = new ArrayList<SearchCriterion>();
		
		if(loginBean.getUser() == null)
		{
			return null;
		}
		SearchCriterion fc;
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
					fc = new SearchCriterion(SearchType.CONTEXT_ID, c.getId());
				else
					fc = new SearchCriterion(Operator.OR,SearchType.CONTEXT_ID, c.getId());
				fcList.add(fc);
			}
		}
		if(loginBean.getUser().getDepositorCollections()!=null && loginBean.getUser().getDepositorCollections().size() > 0)
		{
			/*
			for(int i=0; i<loginBean.getUser().getDepositorCollections().size(); i++)
			{
				Collection c = loginBean.getUser().getDepositorCollections().get(i);
				*/
				if(fcList.size()==0)
				{
					fc = new SearchCriterion(SearchType.CREATED_BY,loginBean.getUser().getId());
					fcList.add(fc);
				}
				else
				{
					fc = new SearchCriterion(Operator.OR, SearchType.CREATED_BY,loginBean.getUser().getId());
					fcList.add(fc);
				}
				/*
			}*/
		}
		
		if(loginBean.getUser().getMdEditorCollections() != null && loginBean.getUser().getMdEditorCollections().size() >0)
		{
			for(int i = 0; i < loginBean.getUser().getMdEditorCollections().size(); i++)
			{
				Collection c = loginBean.getUser().getMdEditorCollections().get(i);
				if(fcList.size() == 0)
					fc = new SearchCriterion(SearchType.CONTEXT_ID, c.getId());
				else
					fc = new SearchCriterion(Operator.OR,SearchType.CONTEXT_ID, c.getId());
				fcList.add(fc);
			}
		}
		
		if(filterString!=null && !filterString.trim().isEmpty())
		{
			fc = new SearchCriterion(Operator.AND, SearchType.TITLE, filterString, 1, 0);
			fcList.add(fc);
			fc = new SearchCriterion(Operator.OR, SearchType.AUTHOR, filterString, 0, 0);
			fcList.add(fc);
			fc = new SearchCriterion(Operator.OR, SearchType.SHELFMARK, filterString, 0, 1);
			fcList.add(fc);
		}
		
		VolumeStatus[] volVersionStatus = new VolumeStatus[]{};
		VolumeStatus[] volPublicStatus = new VolumeStatus[]{};
		VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MULTIVOLUME, VolumeTypes.MONOGRAPH};
		if("all".equals(filterItemState))
		{
			volVersionStatus = new VolumeStatus[]{VolumeStatus.submitted, VolumeStatus.released};
			volPublicStatus = new VolumeStatus[]{VolumeStatus.submitted, VolumeStatus.released};
		}
		else if("submitted".equals(filterItemState))
		{
			volVersionStatus = new VolumeStatus[]{VolumeStatus.submitted,};
			volPublicStatus = new VolumeStatus[]{VolumeStatus.submitted, VolumeStatus.released};
			volTypes = new VolumeTypes[]{VolumeTypes.VOLUME, VolumeTypes.MULTIVOLUME, VolumeTypes.MONOGRAPH};
		}
		else if("released".equals(filterItemState))
		{
			volVersionStatus = new VolumeStatus[]{VolumeStatus.released};
			volPublicStatus = new VolumeStatus[]{VolumeStatus.released};
		}
		else if("withdrawn".equals(filterItemState))
		{
			volPublicStatus = new VolumeStatus[]{VolumeStatus.withdrawn};
			volTypes = new VolumeTypes[]{VolumeTypes.VOLUME, VolumeTypes.MULTIVOLUME, VolumeTypes.MONOGRAPH};
		}
		if(fcList.size()>0)
		{
			res = filterBean.itemFilter(volTypes, volVersionStatus, volPublicStatus, fcList, getSortCriterionList(), limit, offset, loginBean.getUserHandle());
			/*
			//Add items which are currently uploading
			VolumeSearchResult resultVolumesInProgress = filterBean.itemFilter(new VolumeTypes[]{VolumeTypes.VOLUME, VolumeTypes.MONOGRAPH}, new VolumeStatus[]{VolumeStatus.pending}, new VolumeStatus[]{VolumeStatus.pending, VolumeStatus.submitted, VolumeStatus.released}, fcList, getSortCriterionList(), limit, offset, loginBean.getUserHandle());
			res.getVolumes().addAll(0, resultVolumesInProgress.getVolumes());
			res.setNumberOfRecords(res.getNumberOfRecords() + resultVolumesInProgress.getNumberOfRecords());
			*/
			
			/*
			EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
			TypedQuery<DatabaseItem> query = em.createNamedQuery(DatabaseItem.ALL_ITEMS_BY_USER_ID, DatabaseItem.class);
			query.setParameter("userId", loginBean.getUser().getId());
			List<DatabaseItem> resList = query.getResultList();
			this.uploadedItems = resList;
			em.close();
			*/
			volServiceBean.loadVolumesForMultivolume(res.getVolumes(), loginBean.getUserHandle(), true, null, null, true);
			this.totalNumberOfRecords = res.getNumberOfRecords();
			return res.getVolumes();
		}
		else
			return null;
	}

	@Override
	public void loadSubvolumes(Volume v) {
		VolumeStatus[] volVersionStatus = new VolumeStatus[]{};
		VolumeStatus[] volPublicStatus = new VolumeStatus[]{};
		if("all".equals(filterItemState))
		{
			volVersionStatus = new VolumeStatus[]{VolumeStatus.submitted, VolumeStatus.released};
			volPublicStatus = new VolumeStatus[]{VolumeStatus.submitted, VolumeStatus.released};
		}
		else if("submitted".equals(filterItemState))
		{
			volVersionStatus = new VolumeStatus[]{VolumeStatus.submitted,};
			volPublicStatus = new VolumeStatus[]{VolumeStatus.submitted, VolumeStatus.released};
		}
		else if("released".equals(filterItemState))
		{
			volVersionStatus = new VolumeStatus[]{VolumeStatus.released};
			volPublicStatus = new VolumeStatus[]{VolumeStatus.released};
		}
		else if("withdrawn".equals(filterItemState))
		{
			volPublicStatus = new VolumeStatus[]{VolumeStatus.withdrawn};
		}
		try {
			List<Volume> volList = new ArrayList<Volume>();
			volList.add(v);
			volServiceBean.loadVolumesForMultivolume(volList, loginBean.getUserHandle(), true, volVersionStatus, volPublicStatus, false);
		} catch (Exception e) {
			logger.error("could not load volumes for multivolume", e);
		}
	}

	public int getTotalNumberOfRecords() 
	{
		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() 
	{
		return "pretty:myVolumes";
	}
	public LoginBean getLoginBean() 
	{
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) 
	{
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
			Volume newVol = createVolServiceBean.releaseVolume(vol.getItem().getObjid(), userHandle);
			VolumeServiceBean.importVolumeIntoVolume(newVol, vol, userHandle);

			
			MessageHelper.infoMessage(InternationalizationHelper.getMessage("success_releaseVolume"));
		} catch (Exception e) {
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_releaseVolume"));
			logger.error("Error while releasing volume " + vol.getObjidAndVersion(), e);
		}

	}
	
	public String delete(Volume vol)
	{ 
		String userHandle = loginBean.getUserHandle();
		try {
			createVolServiceBean.deleteVolume(vol, userHandle);
		

			
			MessageHelper.infoMessage(InternationalizationHelper.getMessage("success_deleteVolume"));
		} catch (Exception e) {
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_deleteVolume"));
			logger.error("Error while deleting volume " + vol.getObjidAndVersion(), e);
		}
		return "";
	}
	
	public String withdraw(Volume vol)
	{ 
		String userHandle = loginBean.getUserHandle();
		try {
			createVolServiceBean.withdrawVolume(vol, userHandle);
		

			
			MessageHelper.infoMessage(InternationalizationHelper.getMessage("success_withdrawVolume"));
		} catch (Exception e) {
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_withdrawVolume"));
			logger.error("Error while withdrawing volume " + vol.getObjidAndVersion(), e);
		}
		return "";

	}
	
	
	public void checkUploadComplete(Volume vol)
	{ 
		String userHandle = loginBean.getUserHandle();
	
			try {
				Volume newVol = volServiceBean.retrieveVolume(vol.getItem().getOriginObjid(), userHandle);
				VolumeServiceBean.importVolumeIntoVolume(newVol, vol, userHandle);
			} catch (Exception e) {
				logger.error("Error while updating volume in myItems " + vol.getObjidAndVersion(), e);
			}

		

	}
	
	
	public boolean checkUploadComplete()
	{ 
		
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		TypedQuery<DatabaseItem> query = em.createNamedQuery(DatabaseItem.ALL_ITEMS_BY_USER_ID, DatabaseItem.class);
		query.setParameter("userId", loginBean.getUser().getId());
		List<DatabaseItem> resList = query.getResultList();
		this.uploadedItems = resList;
		em.close();
		
		for(DatabaseItem item : uploadedItems)
		{
			if(IngestStatus.RUNNING.equals(item.getIngestStatus()))
			{
				return true;
			}
		}
		
		return false;

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


	public InternationalizationHelper getInternationalizationHelper() 
	{
		return internationalizationHelper;
	}


	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) 
	{
		this.internationalizationHelper = internationalizationHelper;
	}



	public String editStructure(Volume vol)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		StructuralEditorBean editBean = (StructuralEditorBean) context.getApplication().evaluateExpressionGet(context, "#{structuralEditorBean}", StructuralEditorBean.class);
		editBean.setVolumeId(vol.getItem().getOriginObjid());
		editBean.setVolume(null);
		
		return "pretty:structuralEditor";
		
	}


	public CombinedSortCriterion getSelectedSortCriterion() 
	{
		return selectedSortCriterion;
	}


	public void setSelectedSortCriterion(CombinedSortCriterion selectedSortCriterion) 
	{
		this.selectedSortCriterion = selectedSortCriterion;
	}


	public String getFilterString() 
	{
		//System.out.println("SET FILTER:" + filterString);
		return filterString;
	}


	public void setFilterString(String filterString) 
	{
		//System.out.println("GET FILTER:" + filterString);
		this.filterString = filterString;
	}


	public String filterResults()
	{
		setCurrentPageNumber(1);
		return getNavigationString();
	}
	
	public String resetFilter()
	{
		this.filterString=null;
		setCurrentPageNumber(1);
		return getNavigationString();
	}


	public String getFilterItemState() 
	{
		return filterItemState;
	}


	public void setFilterItemState(String filterItemState) 
	{
		this.filterItemState = filterItemState;
	}
	
	public SessionBean getSessionBean() 
	{
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
	}


	public List<DatabaseItem> getUploadedItems() {
		return uploadedItems;
	}


	public void setUploadedItems(List<DatabaseItem> uploadedItems) {
		this.uploadedItems = uploadedItems;
	}
	
}
