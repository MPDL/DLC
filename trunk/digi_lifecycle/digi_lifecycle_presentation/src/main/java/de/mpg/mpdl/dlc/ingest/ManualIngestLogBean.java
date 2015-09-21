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
package de.mpg.mpdl.dlc.ingest;



import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.CreateVolumeServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem.IngestStatus;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
@URLMapping(id = "ingestLog", viewId = "/ingestLog.xhtml", pattern = "/ingestlog")
public class ManualIngestLogBean extends BasePaginatorBean<DatabaseItem> {
	private static Logger logger = Logger.getLogger(ManualIngestLogBean.class);
	


	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	private CreateVolumeServiceBean createVolServiceBean = new CreateVolumeServiceBean();
	


	
	//private List<SortCriterion> sortCriterionList = new ArrayList<SortCriterion>();	
	
	private CombinedSortCriterion selectedSortCriterion; 
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private int totalNumberOfRecords;



	private List<DatabaseItem> uploadedItems;
	
	
	public ManualIngestLogBean()
	{		
		super();
	}

	
	
	
  

	



	public int getTotalNumberOfRecords() 
	{
		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() 
	{
		return "pretty:ingestLog";
	}
	public LoginBean getLoginBean() 
	{
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) 
	{
		this.loginBean = loginBean;
	}
	
	
	
	
	
	
	
	
	
	public boolean checkUploadComplete()
	{ 
		for(DatabaseItem item : uploadedItems)
		{
			if(IngestStatus.RUNNING.equals(item.getIngestStatus()))
			{
				return true;
			}
		}
		
		return false;

	}
	
	public synchronized void checkUploadComplete(DatabaseItem dbItem, int rowKey)
	{ 
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		DatabaseItem dbItemInDb = em.find(DatabaseItem.class, dbItem.getId());	
		dbItem = dbItemInDb;
		getCurrentPartList().set(rowKey, dbItemInDb);
		em.close();

	}











	@Override
	public List<DatabaseItem> retrieveList(int offset, int limit)
	{
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		
		Query countQuery =  em.createNamedQuery(DatabaseItem.ALL_ITEMS_BY_USER_ID_COUNT);
		countQuery.setParameter("userId", loginBean.getUser().getId());
		this.totalNumberOfRecords = ((Number)countQuery.getSingleResult()).intValue();
		
		TypedQuery<DatabaseItem> query = em.createNamedQuery(DatabaseItem.ALL_ITEMS_BY_USER_ID, DatabaseItem.class);
		query.setParameter("userId", loginBean.getUser().getId());
		query.setFirstResult(offset-1);
		query.setMaxResults(limit);
		List<DatabaseItem> resList = query.getResultList();
		this.uploadedItems = resList;
		em.close();
		//this.totalNumberOfRecords = uploadedItems.size();
		return uploadedItems;
	}










	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}










	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}



	public void deleteDbItem(DatabaseItem dbItem)
	{
		
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		DatabaseItem dbItemInDb = em.find(DatabaseItem.class, dbItem.getId());
		em.getTransaction().begin();
		em.remove(dbItemInDb);
		em.getTransaction().commit();
		em.close();
	}
	
	
	
	
	
}
