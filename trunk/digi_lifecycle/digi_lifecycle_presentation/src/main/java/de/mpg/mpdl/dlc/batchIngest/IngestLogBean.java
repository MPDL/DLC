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
		logs.clear();   
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		TypedQuery<BatchLog> query = em.createNamedQuery(BatchLog.ITEMS_BY_USER_ID, BatchLog.class);
		query.setParameter("userId", userId);
		
		List<BatchLog> allLogs =query.getResultList(); 
		
		this.totalNumberOfRecords = allLogs.size();

		try
		{
            for(int i = offset-1; i< ((totalNumberOfRecords > offset + limit)?(offset+limit):totalNumberOfRecords); i++)
            {
            	logs.add(allLogs.get(i));
            }
            
  		}catch(Exception e)
		{
  			logger.error("Error while retrieving batch ingest logs: " + e.getMessage());
  			System.err.println("Error while retrieving batch ingest logs: " + e.getMessage());
		}
		finally{
			em.close();
		}

		return logs;
	}
	
    public  List<BatchLogItem> getLogItems(BatchLog logId)
    {  	
		List<BatchLogItem> logItems = new ArrayList<BatchLogItem>();
		
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		TypedQuery<BatchLogItem> query = em.createNamedQuery(BatchLogItem.ITEMS_BY_LOG_ID, BatchLogItem.class);
		query.setParameter("logId", logId);
		try
		{

			logItems = query.getResultList();
		
		}catch(Exception e)
		{
  			logger.error("Error while retrieving batch ingest log Items: " + e.getMessage());
  			System.err.println("Error while retrieving batch ingest log Items: " + e.getMessage());
		}
		finally{
			em.close();
		}
    	return logItems;
    	
    }
    
   public List<BatchLogItemVolume> getLogItemVolumes(BatchLogItem logItemId)
    {
    	
		List<BatchLogItemVolume> logItemVolumes = new ArrayList<BatchLogItemVolume>();
		EntityManager em = VolumeServiceBean.getEmf().createEntityManager();
		TypedQuery<BatchLogItemVolume> query = em.createNamedQuery(BatchLogItemVolume.ITEMS_BY_LOG_ITME_ID, BatchLogItemVolume.class);
		query.setParameter("logItemId", logItemId);
		try
		{
			logItemVolumes = query.getResultList();
		
		}catch(Exception e)
		{
  			logger.error("Error while retrieving logItemVolumes " + e.getMessage());
  			System.err.print("Error while retrieving logItemVolumes " + e.getMessage());
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


}
