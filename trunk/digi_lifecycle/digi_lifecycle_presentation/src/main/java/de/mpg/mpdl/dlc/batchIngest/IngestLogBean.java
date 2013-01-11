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
