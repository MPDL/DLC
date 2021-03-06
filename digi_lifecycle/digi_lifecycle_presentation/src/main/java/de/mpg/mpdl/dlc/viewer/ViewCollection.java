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
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;

@ManagedBean
@SessionScoped
@URLMapping(id="collection", viewId="/collection.xhtml", pattern="/collection/#{viewCollection.id}")
public class ViewCollection {
	
	private static Logger logger = Logger.getLogger(ViewCollection.class);
	
	private String id;
	protected LoginBean loginBean;
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	private OrganizationalUnitServiceBean orgaServiceBean = new OrganizationalUnitServiceBean();
		
	private Collection col;
	
	private String searchString;
	
	@ManagedProperty("#{advancedSearchResultBean}")
	private AdvancedSearchResultBean advancedSearchResultBean;
	
	private SearchBean searchBean = new SearchBean();
	
	
	//volumes which schown in carousel
	private List<Volume> volumes = new ArrayList<Volume>();	

	
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	@URLAction(onPostback=false)
	public void loadCollection()
	{
		try
		{
			if(col==null || !col.getId().equals(id))
			{
				this.col = contextServiceBean.retrieveCollection(id, null);
				
				Organization orga = orgaServiceBean.retrieveOrganization(col.getOuId());
				
				if(!"".equals(orga.getDlcMd().getFoafOrganization().getImgURL()))
				{
					sessionBean.setLogoLink(orga.getId());
					sessionBean.setLogoUrl(orga.getDlcMd().getFoafOrganization().getImgURL());
					sessionBean.setLogoTlt(InternationalizationHelper.getTooltip("main_home")
							.replace("$1", orga.getEscidocMd().getTitle()));
				}
				
				if(volumes!=null)
					this.volumes.clear();

				
				
				//search volumes for carousel
				List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

				EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
				TypedQuery<CarouselItem> query = em.createNamedQuery(CarouselItem.ALL_ITEMS_BY_CONTEXT_ID, CarouselItem.class);
				query.setParameter("contextId", id);
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
					//search all in context
					SearchCriterion sc= new SearchCriterion(SearchType.CONTEXT_ID, id);
					scList.add(sc);
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
//						String pageUrl = vol.getPages().get(0).getContentIds();
//						this.pages.add(VolumeUtilBean.getImageServerUrl(pageUrl, "THUMBNAIL"));
						this.volumes.add(vol);

					}
				}
			}
			logger.info("load collection" + id);
		}catch(Exception e){
			logger.error("load collection", e);
		}
	}
	


	public List<Volume> getVolumes() {
		return volumes;
	}



	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
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



	public Collection getCol() {
		return col;
	}

	public void setCol(Collection col) {
		this.col = col;
	}
	
	
	public String searchInCollection()
	{
		try {

			
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
			scList.add(new SearchCriterion(SearchType.CONTEXT_ID, col.getId()));
			scList.add(new SearchCriterion(SearchType.FREE, searchString));
			
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



	public String getSearchString() {
		return searchString;
	}



	public void setSearchString(String searchString) {
		this.searchString = searchString;
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
