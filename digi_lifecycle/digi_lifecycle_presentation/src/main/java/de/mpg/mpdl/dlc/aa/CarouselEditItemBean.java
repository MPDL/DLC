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
package de.mpg.mpdl.dlc.aa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.persistence.entities.CarouselItem;
import de.mpg.mpdl.dlc.searchLogic.FilterBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.mets.Page;


@ManagedBean
@SessionScoped
@URLMapping(id="carouselEdit", viewId="/carouselEdit.xhtml", pattern="/admin/carouseledit")
public class CarouselEditItemBean{
	
	private static Logger logger = Logger.getLogger(CarouselEditItemBean.class); 

	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	

	private int totalNumberOfRecords;
	
	private String selectedContextId;
	
	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
	private List<Context> contextList = new ArrayList<Context>();
	

	private List<Volume> volumeList = new ArrayList<Volume>();
	
	private List<CarouselItem> carouselItemList = new ArrayList<CarouselItem>();
	
	private Map<String, Boolean> checkedMap = new HashMap<String, Boolean>();
	
	
	private Map<String, Volume> volumeMap = new HashMap<String, Volume>();
	
	private Map<String, CarouselItem> carouselItemMap = new HashMap<String, CarouselItem>();
	
    
	public CarouselEditItemBean()
	{
		super();		
	}
	
	@PostConstruct
	public void init()
	{      
		if(loginBean == null || loginBean.getUser() == null)
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			try {
				String dlc_URL = PropertyReader.getProperty("dlc.instance.url") + "/" + PropertyReader.getProperty("dlc.context.path") ;
				fc.getExternalContext().redirect(dlc_URL);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		else
		{
			
			this.contextSelectItems.clear();
			this.contextSelectItems.add(new SelectItem("", "Bitte hier auswählen..."));
			
			for(Grant grant: loginBean.getUser().getGrants())
			{ 
				try
				{   
					if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
					{  
						for(Context context : contextServiceBean.retrieveOUContexts(loginBean.getUser().getOu().getObjid(), true))
						{
							this.contextSelectItems.add(new SelectItem(context.getObjid(), context.getProperties().getName()));
							this.contextList.add(context);
						}
							
					}
				}catch(Exception e)
				{
					logger.error("Error init seleteItem ", e);
				}
			}   
		}

	}
	
	
	public void updateList() 
	{
		try {
			
			if(selectedContextId==null || selectedContextId.isEmpty())
			{
				checkedMap.clear();
				carouselItemMap.clear();
				volumeMap.clear();
				volumeList.clear();
				carouselItemList.clear();
			}
			
			else
			{
				FilterBean fb = new FilterBean();
				SearchCriterion contextSc = new SearchCriterion(SearchType.CONTEXT_ID, this.selectedContextId);
				List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
				scList.add(contextSc);
				
				
				VolumeSearchResult res = fb.itemFilter(new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.VOLUME}, new VolumeStatus[]{VolumeStatus.released},  new VolumeStatus[]{VolumeStatus.released}, 
						scList, CombinedSortCriterion.AUTHOR_TITLE_ASC.getScList(), 1000, 1, loginBean.getUserHandle());
				 
				this.volumeList = res.getVolumes();
				 
				EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
				TypedQuery<CarouselItem> query = em.createNamedQuery(CarouselItem.ALL_ITEMS_BY_CONTEXT_ID, CarouselItem.class);
				query.setParameter("contextId", selectedContextId);
				this.carouselItemList = query.getResultList();
				em.close();
				
				
				carouselItemMap.clear();
				
				
				for(CarouselItem ci : carouselItemList)
				{
					carouselItemMap.put(ci.getItemId(), ci);
				}
				
				
				getCheckedMap().clear();
				volumeMap.clear();
				for(Volume volume : volumeList)
				{
					
					if(carouselItemMap.containsKey(volume.getItem().getOriginObjid()))
					{
						getCheckedMap().put(volume.getItem().getOriginObjid(), true);
					}
					else
					{
						getCheckedMap().put(volume.getItem().getOriginObjid(), false);
					}
					
					volumeMap.put(volume.getItem().getOriginObjid(), volume);
				}
			}
			
		} catch (Exception e) {
			logger.error("Error while creating list for carousel selection", e);
		}
	}
	
	
	public void save()
	{
		try {
			EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
			em.getTransaction().begin();
			for(Entry<String, Boolean> entry : getCheckedMap().entrySet())
			{
				//CarouselItem dummy = new CarouselItem();
				//dummy.setItemId(entry.getKey());
				boolean exists = carouselItemMap.containsKey(entry.getKey());
				if(entry.getValue() && !exists)
				{
					CarouselItem ci = new CarouselItem();
					ci.setContextId(selectedContextId);
					ci.setItemId(entry.getKey());
					Volume vol = volumeMap.get(entry.getKey());
					Page titlePage = VolumeUtilBean.getTitlePage(vol);
					ci.setImageUrl(titlePage.getContentIds());
					
					for(Context c : contextList)
					{
						if(selectedContextId.equals(c.getObjid()))
						{
							ci.setOuId(c.getProperties().getOrganizationalUnitRefs().get(0).getObjid());
						}
					}
					
					em.persist(ci);
					
					
				}
				else if(!entry.getValue() && exists)
				{
					CarouselItem ci =em.find(CarouselItem.class, entry.getKey());
					//em.merge(ci);
					//em.flush();
					em.remove(ci);
				}
				
				
			}
			em.getTransaction().commit();
			em.close();
			updateList();
			MessageHelper.infoMessage("Successfully updated carousel items");
		} catch (Exception e) {
			logger.error("Error while saving carousel items to database", e);
			MessageHelper.errorMessage("Error while saving carousel items", e.getMessage());
		}
		
	}
	
	
	
	
	
	public void changeContext()
	{
		updateList();
	}


	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	

	

	public String getSelectedContextId() {
		return selectedContextId;
	}

	public void setSelectedContextId(String selectedContextId) {
		this.selectedContextId = selectedContextId;
	}

	public List<Volume> getVolumeList() {
		return volumeList;
	}

	public void setVolumeList(List<Volume> volumeList) {
		this.volumeList = volumeList;
	}

	public List<SelectItem> getContextSelectItems() {
		return contextSelectItems;
	}

	public void setContextSelectItems(List<SelectItem> contextSelectItems) {
		this.contextSelectItems = contextSelectItems;
	}

	public Map<String, Boolean> getCheckedMap() {
		return checkedMap;
	}

	public void setCheckedMap(Map<String, Boolean> checkedMap) {
		this.checkedMap = checkedMap;
	}

}
