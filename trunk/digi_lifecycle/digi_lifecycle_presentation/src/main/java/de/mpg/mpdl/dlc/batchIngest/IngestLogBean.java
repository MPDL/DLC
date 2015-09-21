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
package de.mpg.mpdl.dlc.batchIngest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.ErrorLevel;
import de.mpg.mpdl.dlc.persistence.entities.BatchLogItem;
import de.mpg.mpdl.dlc.persistence.entities.BatchLogItemVolume;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;



@ManagedBean
@SessionScoped
@URLMapping(id="myItemsBatch", pattern="/myItemsBatch", viewId="/myItemsBatch.xhtml", onPostback = false)
public class IngestLogBean extends BasePaginatorBean<BatchLog>{
	
	private static Logger logger = Logger.getLogger(IngestLogBean.class);
	
	private String userId;
	
	private List<BatchLog> logs = new ArrayList<BatchLog>();
	
	private int totalNumberOfRecords;
	
	private Connection conn = null; 
    private PreparedStatement stmt = null;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;


	
	public IngestLogBean()
	{
		super();
		setElementsPerPage(6);
	}
	
	@URLAction(onPostback=false)	
	public void init()
	{
		if(loginBean == null || loginBean.getUser() == null)
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			try {
				String dlc_URL = PropertyReader.getProperty("dlc.instance.url") + "/" + PropertyReader.getProperty("dlc.context.path") + "/";
				fc.getExternalContext().redirect(dlc_URL);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		else
		{
			this.userId = loginBean.getUser().getId();
		}
	}
	
	public List<BatchLog> retrieveList(int offset, int limit)
	{  
		long start = System.currentTimeMillis();
		logs.clear();   

		List<BatchLog> allLogs = new ArrayList<BatchLog>();
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		
		List<String> conIds_mo = loginBean.getUser().getModeratorContextIds();
		List<String> conIds_de = loginBean.getUser().getDepositorContextIds();
 
		if(conIds_mo != null && conIds_mo.size() > 0 )
		{
			for(int i= 0; i < conIds_mo.size(); i++)
			{
				String conId_mo = conIds_mo.get(i);
				TypedQuery<BatchLog> query = em.createNamedQuery(BatchLog.ITEMS_BY_CONTEXT_ID, BatchLog.class);
				query.setParameter("contextId", conId_mo);
				allLogs.addAll(query.getResultList());
				if(conIds_de != null && conIds_de.contains(conId_mo))
					conIds_de.remove(conId_mo);
			}
		}
		if(conIds_de != null && conIds_de.size() > 0)
		{
			TypedQuery<BatchLog> query = em.createNamedQuery(BatchLog.ITEMS_BY_USER_ID, BatchLog.class);
			query.setParameter("userId", userId);
			allLogs.addAll(query.getResultList()); 
		}
		
		long time1 = System.currentTimeMillis()-start;
		
		this.totalNumberOfRecords = allLogs.size();

		try
		{
            for(int i = offset-1; i < ((totalNumberOfRecords > limit)?(limit):totalNumberOfRecords); i++)
            {
            	logs.add(allLogs.get(i));
            }
            logger.info("List Nr. = " + logs.size());
    		long time = System.currentTimeMillis()-start;
    		logger.info("RetrieveList Time = " + time);
            
  		}catch(Exception e)
		{
  			logger.error("Error while retrieving batch ingest logs: " + e.getMessage());
		}
		finally{
			em.close();
		}

		return logs;
	}
	
    public  List<BatchLogItem> getLogItems(BatchLog logId)
    {  	
		long start = System.currentTimeMillis();
		List<BatchLogItem> logItems = new ArrayList<BatchLogItem>();
		
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		TypedQuery<BatchLogItem> query = em.createNamedQuery(BatchLogItem.ITEMS_BY_LOG_ID, BatchLogItem.class);
		query.setParameter("logId", logId);
		try
		{
			logItems = query.getResultList();
    		long time = System.currentTimeMillis()-start;
		}catch(Exception e)
		{
  			logger.error("Error while retrieving batch ingest log Items: " + e.getMessage());
		}
		finally{
			em.close();
		}
    	return logItems;
    	
    }
    
   public List<BatchLogItemVolume> getLogItemVolumes(BatchLogItem logItemId)
    {
		long start = System.currentTimeMillis();
		
		List<BatchLogItemVolume> logItemVolumes = new ArrayList<BatchLogItemVolume>();
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		TypedQuery<BatchLogItemVolume> query = em.createNamedQuery(BatchLogItemVolume.ITEMS_BY_LOG_ITME_ID, BatchLogItemVolume.class);
		query.setParameter("logItemId", logItemId);
		try
		{
			logItemVolumes = query.getResultList();
    		long time = System.currentTimeMillis()-start;
    		//System.err.println("getLogItemVolumes Time = " + time +  " | size = " + logItemVolumes.size() + " | logItemId = " + logItemId.getId());
		
		}catch(Exception e)
		{
  			logger.error("Error while retrieving logItemVolumes " + e.getMessage());
  			//System.err.print("Error while retrieving logItemVolumes " + e.getMessage());
		}
		finally{
			em.close();
		}
    	return logItemVolumes;
    	
    }
    
	public int getTotalNumberOfRecords() {

		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() {
		return "pretty:myItemsBatch";
	}

	public List<BatchLog> getLogs() {
		return logs;
	}

	public void setLogs(List<BatchLog> logs) {
		this.logs = logs;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public PreparedStatement getStmt() {
		return stmt;
	}

	public void setStmt(PreparedStatement stmt) {
		this.stmt = stmt;
	}

	
	public String deleteBatchLog(BatchLog batchLog)
	{
		logger.info("Trying to delete batch log " + batchLog.getId());
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		BatchLog batchLogInDb = em.find(BatchLog.class, batchLog.getId());
		em.getTransaction().begin();
		em.remove(batchLogInDb);
		em.getTransaction().commit();
		em.close();
		return "";
	}
	
	

}
