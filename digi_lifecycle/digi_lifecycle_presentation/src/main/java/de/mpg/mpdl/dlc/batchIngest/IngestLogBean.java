package de.mpg.mpdl.dlc.batchIngest;

import java.beans.PropertyVetoException;
import java.io.IOException;

import java.net.URISyntaxException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.batchIngest.IngestLog.ErrorLevel;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Status;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Step;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;



@ManagedBean
@SessionScoped
@URLMapping(id="myItemsBatch", pattern="/myItemsBatch", viewId="/myItemsBatch.xhtml", onPostback = false)
public class IngestLogBean extends BasePaginatorBean<IngestLog>{
	
	private static Logger logger = Logger.getLogger(IngestLogBean.class);
	
	private String userId;
	
	private List<IngestLog> logs = new ArrayList<IngestLog>();
	
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
	
	public List<IngestLog> retrieveList(int offset, int limit) throws Exception 
	{
		logs.clear();   
		this.conn = IngestLog.getConnection();
		List<IngestLog> allLogs = new ArrayList<IngestLog>();
		ResultSet rset = null;
		try
		{
			String q = "select * from dlc_batch_ingest_log where user_id = ? ORDER BY id DESC";
			stmt = conn.prepareStatement(q);
			stmt.setString(1, userId);
			rset = stmt.executeQuery();

			
//			String query = "select * from dlc_batch_ingest_log where user_id = ? and id between ? and ? ";
//			statement = conn.prepareStatement(query);
//			statement.setString(1, userId);
//			statement.setInt(2, offset);
//			statement.setInt(3, limit);
//			resultSet = statement.executeQuery();

            while (rset.next())
            {
            	int id = rset.getInt("id");
            	IngestLog log = getIngestLog(id);
            	allLogs.add(log);
            }
            this.totalNumberOfRecords = allLogs.size();

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
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
		}

		return logs;
	}
	
	


	public int getTotalNumberOfRecords() {

		return totalNumberOfRecords;
	}


	@Override
	public String getNavigationString() {
		return "pretty:myItemsBatch";
	}
	

	
	public IngestLog getIngestLog(Integer id)
	{
		IngestLog log = null;
		String query = null;
		ResultSet rset = null;
		

		try
		{
			query = "select * from dlc_batch_ingest_log where id = " + id;
			stmt = conn.prepareStatement(query);
			rset = stmt.executeQuery();
			if(rset.next())
			{
				log = fillLog(rset);
			}
			
		}catch(Exception e)
		{
  			logger.error("Error while retrieving one ingest log: " + e.getMessage());
		}

		
		return log;
	}
	
    public IngestLog fillLog(ResultSet resultSet) throws SQLException
    {
    	IngestLog log = null;
        log = new IngestLog();
        log.setId(resultSet.getInt("id"));
        log.setName(resultSet.getString("name"));
        log.setStep(Step.valueOf(resultSet.getString("step").toUpperCase()));
        log.setStatus(Status.valueOf(resultSet.getString("status").toUpperCase()));
        log.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    	Array a = resultSet.getArray("logs");
    	if(a != null)
    	{
	    	String[] logs = (String[]) a.getArray();
	    	if(logs.length > 0)
	    	{
		    	for(int i = 1; i<=logs.length; i++)
		    		log.getLogs().add(logs[i-1]);
	    	}
    	}
        log.setStartDate(resultSet.getTimestamp("startdate"));
        log.setEndDate(resultSet.getTimestamp("enddate"));
        log.setContextId(resultSet.getString("context_id"));
        log.setFinishedItems(resultSet.getInt("finished_items"));
        log.setTotalItems(resultSet.getInt("total_items"));
        
        return log;
    }
	
    public  List<IngestLogItem> getIngestLogItems(Integer logId)
    {
    	
		List<IngestLogItem> logItems = new ArrayList<IngestLogItem>();
		ResultSet rset = null;
		String query = null;
		try
		{
			query = "select * from dlc_batch_ingest_log_item where log_id = " + logId;
			conn = IngestLog.getConnection();
	        stmt = conn.prepareStatement(query);
	        rset = stmt.executeQuery();
	        
	        while (rset.next())
	        {
	        	int id = rset.getInt("id");
	        	IngestLogItem logItem = getIngestLogItem(id);
	            logItems.add(logItem);
	        }
		
		
		}catch(Exception e)
		{
  			logger.error("Error while retrieving batch ingest log Items: " + e.getMessage());
  			System.err.println("Error while retrieving batch ingest log Items: " + e.getMessage());
		}
		finally{
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
    	return logItems;
    	
    }
    
	public IngestLogItem getIngestLogItem(Integer id)
	{
		IngestLogItem logItem = null;


		String query = null;
		ResultSet rset = null;
		try
		{
			query = "select * from dlc_batch_ingest_log_item where id = ?";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			rset = stmt.executeQuery();
			if(rset.next())
			{
				logItem = fillLogItem(rset);
			}
			
		}catch(Exception e)
		{
  			logger.error("Error while retrieving one ingestlogitem: " + e.getMessage());
		}
		return logItem;
	}
	
    public IngestLogItem fillLogItem(ResultSet resultSet) throws SQLException, IOException, URISyntaxException
    {
    	IngestLogItem logItem = null;
    	logItem = new IngestLogItem();
    	logItem.setId(resultSet.getInt("id"));
    	logItem.setName(resultSet.getString("name"));
//    	logItem.setStatus(Status.valueOf(resultSet.getString("status").toUpperCase()));
//    	try {
////    		logItem.setStatus(Status.valueOf(volumeService.releaseVolume(resultSet.getString("item_id"), loginBean.getUserHandle()).getItem().getProperties().getPublicStatus().toString()));
//    		if(resultSet.getString("item_id") != null)
//    			logItem.setEsciDocStatus(volumeService.retrieveVolume(resultSet.getString("item_id"), loginBean.getUserHandle()).getItem().getProperties().getPublicStatus().toString());
//
//    	} catch (Exception e) {
//			e.printStackTrace();
//		}
    	logItem.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    	logItem.setStartDate(resultSet.getTimestamp("startdate"));
    	logItem.setEndDate(resultSet.getTimestamp("enddate"));
    	logItem.setLogId(resultSet.getInt("log_id"));
    	Array a = resultSet.getArray("logs");
    	String[] logs = (String[]) a.getArray();
    	for(int i = 1; i<=logs.length; i++)
    	{
    		logItem.getLogs().add(logs[i-1]);
    	}
    	logItem.setEscidocId(resultSet.getString("item_id"));
    	logItem.setContentModel(resultSet.getString("content_model"));
    	logItem.setImagesNr(resultSet.getInt("images_nr"));
    	logItem.setHasFooter(resultSet.getBoolean("footer"));
    	logItem.setHasTEI(resultSet.getBoolean("tei"));
        
        return logItem;
    }
    
    
   public List<IngestLogItemVolume> getIngestLogItemVolumes(Integer logItemId)
    {
    	
		List<IngestLogItemVolume> logItemVolumes = new ArrayList<IngestLogItemVolume>();

		String query = null;
		ResultSet rset = null;
		try
		{
			query = "select * from dlc_batch_ingest_log_item_volume where log_item_id = " + logItemId;
			conn = IngestLog.getConnection();
	       
	        stmt = conn.prepareStatement(query);
	        rset = stmt.executeQuery();
	        
	        while (rset.next())
	        {
	        	int id = rset.getInt("id");
	        	IngestLogItemVolume logItemVolume = getIngestLogItemVolume(id);
	        	logItemVolumes.add(logItemVolume);
	        }
		
		
		}catch(Exception e)
		{
  			logger.error("Error while retrieving logItemVolumes " + e.getMessage());
  			System.err.print("Error while retrieving logItemVolumes " + e.getMessage());
		}
		finally{
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
    	return logItemVolumes;
    	
    }
    
	public IngestLogItemVolume getIngestLogItemVolume(Integer id)
	{
		IngestLogItemVolume logItemVolume = null;
		String query = null;
		ResultSet rset = null;
		try
		{
			query = "select * from dlc_batch_ingest_log_item_volume where id = ?";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			rset = stmt.executeQuery();
			if(rset.next())
			{
				logItemVolume = fillLogItemVolume(rset);
			}
			
		}catch(Exception e)
		{
  			logger.error("Error while retrieving one ingestLogItemVolume: " + e.getMessage());
		}
		return logItemVolume;
	}
	
    public IngestLogItemVolume fillLogItemVolume(ResultSet resultSet) throws SQLException, IOException, URISyntaxException
    {
    	IngestLogItemVolume logItemVolume = null;
    	logItemVolume = new IngestLogItemVolume();
    	logItemVolume.setId(resultSet.getInt("id"));
    	logItemVolume.setName(resultSet.getString("name"));
//    	logItemVolume.setStatus(Status.valueOf(resultSet.getString("status").toUpperCase()));
//    	try {
//    		if(resultSet.getString("item_id") != null && !"null".equals(resultSet.getString("item_id")))
//    			logItemVolume.setEsciDocStatus(volumeService.retrieveVolume(resultSet.getString("item_id"), loginBean.getUserHandle()).getItem().getProperties().getPublicStatus().toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    	logItemVolume.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    	logItemVolume.setStartDate(resultSet.getTimestamp("startdate"));
    	logItemVolume.setEndDate(resultSet.getTimestamp("enddate"));
    	logItemVolume.setLogItemId(resultSet.getInt("log_item_id"));
    	
    	Array a = resultSet.getArray("logs");
    	String[] logs = (String[]) a.getArray();
    	for(int i = 1; i<=logs.length; i++)
    		logItemVolume.getLogs().add(logs[i-1]);
    	logItemVolume.setEscidocId(resultSet.getString("item_id"));
    	logItemVolume.setContentModel(resultSet.getString("content_model"));
    	
    	logItemVolume.setImagesNr(resultSet.getInt("images_nr"));
    	logItemVolume.setHasFooter(resultSet.getBoolean("footer"));
    	logItemVolume.setHasTEI(resultSet.getBoolean("tei"));
        
        return logItemVolume;
    }



	public List<IngestLog> getLogs() {
		return logs;
	}


	public void setLogs(List<IngestLog> logs) {
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
