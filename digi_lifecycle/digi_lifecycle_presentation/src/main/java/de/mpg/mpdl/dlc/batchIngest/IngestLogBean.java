package de.mpg.mpdl.dlc.batchIngest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.batchIngest.IngestLog.ErrorLevel;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Status;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Step;
import de.mpg.mpdl.dlc.beans.LoginBean;

import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;



@ManagedBean
@SessionScoped
@URLMapping(id="myItemsBatch", pattern="/myItemsBatch/#{ingestLogBean.userId}", viewId="/myItemsBatch.xhtml", onPostback = false)
public class IngestLogBean extends BasePaginatorBean<IngestLog>{
	
	private static Logger logger = Logger.getLogger(IngestLogBean.class);
	
	private String userId;
	
	private List<IngestLog> logs = new ArrayList<IngestLog>();
	
	private int totalNumberOfRecords;
	
	private Connection conn; 

	
	public IngestLogBean()
	{
		super();
	}
	
	public List<IngestLog> retrieveList(int offset, int limit) throws Exception {
		logs.clear();
		this.conn = IngestLog.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = null;
		
		try
		{
			query = "select * from dlc_batch_ingest_log where user_id = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, userId);
			resultSet = statement.executeQuery();
			
            while (resultSet.next())
            {
            	int id = resultSet.getInt("id");
            	IngestLog log = getIngestLog(id);
                logs.add(log);
            }
            
            this.totalNumberOfRecords = logs.size();
            
  		}catch(Exception e)
		{
			
		}
		return logs;
	}

	public int getTotalNumberOfRecords() {

		return totalNumberOfRecords;
	}


	public String getNavigationString() {

		return "";
	}
	

	
	public IngestLog getIngestLog(Integer id)
	{
		IngestLog log = null;
		if(conn == null)
			conn = IngestLog.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = null;
		
		try
		{
			query = "select * from dlc_batch_ingest_log where id = ?";
			statement = conn.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				log = fillLog(resultSet);
			}
			
		}catch(Exception e)
		{
			
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
		if(conn == null)
			conn = IngestLog.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = null;
		
		try
		{
			query = "select * from dlc_batch_ingest_log_item where log_id = " + logId;
	        if(conn == null)
	        	conn = IngestLog.getConnection();
	        statement = conn.prepareStatement(query);
	        resultSet = statement.executeQuery();
	        
	        while (resultSet.next())
	        {
	        	int id = resultSet.getInt("id");
	        	IngestLogItem logItem = getIngestLogItem(id);
	            logItems.add(logItem);
	        }
		
		
		}catch(Exception e)
		{
			
		}
    	
    	return logItems;
    	
    }
    
	public IngestLogItem getIngestLogItem(Integer id)
	{
		IngestLogItem logItem = null;
		if(conn == null)
			conn = IngestLog.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = null;
		
		try
		{
			query = "select * from dlc_batch_ingest_log_item where id = ?";
			statement = conn.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				logItem = fillLogItem(resultSet);
			}
			
		}catch(Exception e)
		{
			
		}
		return logItem;
	}
	
    public IngestLogItem fillLogItem(ResultSet resultSet) throws SQLException
    {
    	IngestLogItem logItem = null;
    	logItem = new IngestLogItem();
    	logItem.setId(resultSet.getInt("id"));
    	logItem.setName(resultSet.getString("name"));
    	logItem.setStatus(Status.valueOf(resultSet.getString("status").toUpperCase()));
    	logItem.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    	logItem.setStartDate(resultSet.getTimestamp("startdate"));
    	logItem.setEndDate(resultSet.getTimestamp("enddate"));
    	logItem.setLogId(resultSet.getInt("log_id"));
    	logItem.setMessage(resultSet.getString("message"));
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
		if(conn == null)
			conn = IngestLog.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = null;
		
		try
		{
			query = "select * from dlc_batch_ingest_log_item_volume where log_item_id = " + logItemId;
	        if(conn == null)
	        	conn = IngestLog.getConnection();
	        statement = conn.prepareStatement(query);
	        resultSet = statement.executeQuery();
	        
	        while (resultSet.next())
	        {
	        	int id = resultSet.getInt("id");
	        	IngestLogItemVolume logItemVolume = getIngestLogItemVolume(id);
	        	logItemVolumes.add(logItemVolume);
	        }
		
		
		}catch(Exception e)
		{
			
		}
    	
    	return logItemVolumes;
    	
    }
    
	public IngestLogItemVolume getIngestLogItemVolume(Integer id)
	{
		IngestLogItemVolume logItemVolume = null;
		if(conn == null)
			conn = IngestLog.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = null;
		
		try
		{
			query = "select * from dlc_batch_ingest_log_item_volume where id = ?";
			statement = conn.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				logItemVolume = fillLogItemVolume(resultSet);
			}
			
		}catch(Exception e)
		{
			
		}
		return logItemVolume;
	}
	
    public IngestLogItemVolume fillLogItemVolume(ResultSet resultSet) throws SQLException
    {
    	IngestLogItemVolume logItemVolume = null;
    	logItemVolume = new IngestLogItemVolume();
    	logItemVolume.setId(resultSet.getInt("id"));
    	logItemVolume.setName(resultSet.getString("name"));
    	logItemVolume.setStatus(Status.valueOf(resultSet.getString("status").toUpperCase()));
    	logItemVolume.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
    	logItemVolume.setStartDate(resultSet.getTimestamp("startdate"));
    	logItemVolume.setEndDate(resultSet.getTimestamp("enddate"));
    	logItemVolume.setLogItemId(resultSet.getInt("log_item_id"));
    	logItemVolume.setMessage(resultSet.getString("message"));
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

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}




	


    

	
	

	
	
	

}
