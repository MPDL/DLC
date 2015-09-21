/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
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
@URLMapping(id = "volumes", viewId = "/volumes.xhtml", pattern = "/volumes/#{allVolumesBean.colId}")
public class AllVolumesBean extends SortableVolumePaginatorBean {
	private static Logger logger = Logger.getLogger(AllVolumesBean.class);
	

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
	private Collection collection;
	private String colId;
	private String oldColId;
	
	
	
	private OrganizationalUnitServiceBean ouServiceBean = new OrganizationalUnitServiceBean();
	private Organization orga;
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	
	public AllVolumesBean()
	{		
		super();
	}

	
	@URLAction(onPostback=false)
	public void init()
	{
		//update();
		if(oldColId==null || (!oldColId.equals(colId)))
		{
			this.loadContext(); 
			if (collection != null) 
			{
				this.orga = ouServiceBean.retrieveOrganization(collection.getOuId());
			}
			else if(loginBean.isLogin())
			{  
				this.orga = ouServiceBean.retrieveOrganization(loginBean.getUser().getOuId());
			}
			if(orga != null)
			{
				sessionBean.setLogoLink(orga.getId());
				sessionBean.setLogoUrl(orga.getDlcMd().getFoafOrganization().getImgURL());
				sessionBean.setLogoTlt(InternationalizationHelper.getTooltip("main_home").replace("$1", orga.getEscidocMd().getTitle())); 
			}
				
			oldColId = colId;
			setCurrentPageNumber(1);
			selectedSortCriterion = CombinedSortCriterion.AUTHOR_TITLE_ASC;
			
		}
		
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
		return scMenuList;
	}
  

	//TODO
	public List<Volume> retrieveList(int offset, int limit)throws Exception 
	{  

		VolumeSearchResult res = null;
		List<SearchCriterion> fcList = new ArrayList<SearchCriterion>();

	
		//List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		if(collection != null)
		{
			fcList.add(new SearchCriterion(SearchType.CONTEXT_ID, colId));
			//SearchCriterion sc = new SearchCriterion(SearchType.CONTEXT_ID, colId);
			//scList.add(sc);
		}
		
		
		res = filterBean.itemFilter(new VolumeTypes[]{VolumeTypes.MULTIVOLUME, VolumeTypes.MONOGRAPH}, new VolumeStatus[]{VolumeStatus.released}, new VolumeStatus[]{VolumeStatus.released}, fcList, getSortCriterionList(), limit, offset, null);
		//volServiceBean.loadVolumesForMultivolume(res.getVolumes(), loginBean.getUserHandle(), true, new VolumeStatus[]{VolumeStatus.released}, new VolumeStatus[]{VolumeStatus.released});
		//res = searchBean.advancedSearchVolumes(scList, getSortCriterionList(), limit, offset);
		
		this.totalNumberOfRecords = res.getNumberOfRecords();
		
		
		return res.getVolumes();
	}
	
	
	@Override
	public void loadSubvolumes(Volume v) {
		
		try {
			List<Volume> volList = new ArrayList<Volume>();
			volList.add(v);
			volServiceBean.loadVolumesForMultivolume(volList, loginBean.getUserHandle(), true, new VolumeStatus[]{VolumeStatus.released}, new VolumeStatus[]{VolumeStatus.released}, false);
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
		return "pretty:volumes";
	}
	public LoginBean getLoginBean() 
	{
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) 
	{
		this.loginBean = loginBean;
	}
	
	
	
	public Collection getCollection() 
	{
		return collection;
	}


	public void setCollection(Collection collection) 
	{
		this.collection = collection;
	}


	public String getColId() 
	{
		return colId;
	}


	public void setColId(String colId) 
	{
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
	
	
	
	/**
	 * Retrieve the 5 last works of all collections
	 * TODO: check multivolume, what can be displayed here?
	 * @return
	 */
	/*
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
	*/


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


	
	public SessionBean getSessionBean() 
	{
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
	}




	
}
