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
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.viewer;


import java.util.ArrayList;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.persistence.entities.CarouselItem;
import de.mpg.mpdl.dlc.search.AdvancedSearchResultBean;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;


@ManagedBean
@SessionScoped
@URLMapping(id = "ou", viewId = "/ou.xhtml", pattern = "/ou/#{viewOU.id}")
public class ViewOU {

	
	private static Logger logger = Logger.getLogger(ViewOU.class);
	
	private String id;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{applicationBean}")
	private ApplicationBean applicationBean;
	

	private OrganizationalUnitServiceBean ouServiceBean = new OrganizationalUnitServiceBean();
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private Organization orga;
	
	private List<Context> collections;
	
	private List<String> collectionSearchStrings;
	
	private List<Volume> vols_Carousel = new ArrayList<Volume>();
	
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	@ManagedProperty("#{advancedSearchResultBean}")
	private AdvancedSearchResultBean advancedSearchResultBean;
	
	private SearchBean searchBean = new SearchBean();
	
	@URLAction(onPostback=false)
	public void loadOu()
	{    
		System.out.println("test");
		try{
			
//			if(orga==null || !id.equals(orga.getId()))
//			{
				this.orga = ouServiceBean.retrieveOrganization(id);
				if(vols_Carousel !=null)
					{this.vols_Carousel.clear();}
				
				if(!"".equals(orga.getDlcMd().getFoafOrganization().getImgURL()))
				{
					sessionBean.setLogoLink(orga.getId());
					sessionBean.setLogoUrl(orga.getDlcMd().getFoafOrganization().getImgURL());
					sessionBean.setLogoTlt(InternationalizationHelper.getTooltip("main_home")
							.replace("$1", orga.getEscidocMd().getTitle()));
				}
				
				
				

				collections = contextServiceBean.retrieveOUContexts(id, false);
				collectionSearchStrings = new ArrayList<String>(collections.size());
				for(int i=0; i<collections.size(); i++)
				{
					collectionSearchStrings.add(null);
				}
				
				if (collections != null && collections.size()>0)
				{
					
					List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

					EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
					TypedQuery<CarouselItem> query = em.createNamedQuery(CarouselItem.ALL_ITEMS_BY_OU_ID, CarouselItem.class);
					query.setParameter(1, id);
					query.setMaxResults(10);
					List<CarouselItem> carouselItems= query.getResultList();
					em.close();
					if(carouselItems!=null && !carouselItems.isEmpty())
					{
						for(CarouselItem ci : carouselItems)
						{
							SearchCriterion sc= new SearchCriterion(Operator.OR, SearchType.OBJECT_ID, ci.getItemId());
							scList.add(sc);
						}
					}
					else
					{
						for(Context context : collections)
						{
							
							SearchCriterion sc = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, context.getObjid());
							scList.add(sc);
						
						}
					}
					

					List<SortCriterion> sortList = new ArrayList<SortCriterion>();
					sortList.add(new SortCriterion(SortIndices.NEWEST, SortOrders.DESCENDING));
					VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.VOLUME};
					SearchBean searchBean = new SearchBean();
					VolumeSearchResult res = searchBean.search(volTypes, scList, sortList, 7, 0);
					for(Volume vol : res.getVolumes())
					{ 
						if(vol.getPages() != null && vol.getPages().size()>0)
						{
							this.vols_Carousel.add(vol);
	
						}
					}
					
				}
			//}
			logger.info("load ou " + id);
		}catch(Exception e){
			logger.error("Problem with loading ou", e);
		}
		
	}
	
	public String save() throws Exception
	{  
		ouServiceBean.updateOU(orga, loginBean.getUserHandle());
		this.orga = ouServiceBean.retrieveOrganization(orga.getId());
		this.orga.setEditable(false);
		applicationBean.setOus(ouServiceBean.retrieveOUs());
		loginBean.getUser().setCreatedOrgas(ouServiceBean.retrieveOrgasCreatedBy(loginBean.getUserHandle(), loginBean.getUser().getId()));
		return "pretty:ou";
	}
  
	public String edit()
	{  
		this.orga.setEditable(true);
		return "pretty:ou";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}


	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public Organization getOrga() {
		return orga;
	}

	public void setOrga(Organization orga) {
		this.orga = orga;
	}

	public List<Volume> getVols_Carousel() {
		return vols_Carousel;
	}

	public void setVols_Carousel(List<Volume> vols_Carousel) {
		this.vols_Carousel = vols_Carousel;
	}

	public List<Context> getCollections() {
		return collections;
	}

	public void setCollections(List<Context> collections) {
		this.collections = collections;
	}

	public List<String> getCollectionSearchStrings() {
		return collectionSearchStrings;
	}

	public void setCollectionSearchStrings(List<String> collectionSearchStrings) {
		this.collectionSearchStrings = collectionSearchStrings;
	}
	
	public String searchInCollection()
	{
		try {

			String collNumber = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("searchParam");
			int pos = Integer.parseInt(collNumber);
			Context ctx = collections.get(pos);
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
			scList.add(new SearchCriterion(SearchType.CONTEXT_ID, ctx.getObjid()));
			scList.add(new SearchCriterion(SearchType.FREE, collectionSearchStrings.get(pos)));
			
			advancedSearchResultBean.reset();
			advancedSearchResultBean.setSearchCriterionList(scList);
			String cql = searchBean.getAdvancedSearchCQL(scList);
			advancedSearchResultBean.setCqlQuery(cql);
			return "pretty:searchResult";
		} catch (Exception e) {
			logger.error("Error while creating CQL query", e);
			return "";
		}
	}

	public AdvancedSearchResultBean getAdvancedSearchResultBean() {
		return advancedSearchResultBean;
	}

	public void setAdvancedSearchResultBean(AdvancedSearchResultBean advancedSearchResultBean) {
		this.advancedSearchResultBean = advancedSearchResultBean;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	
	
	
	
}
