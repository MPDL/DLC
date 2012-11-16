package de.mpg.mpdl.dlc.batchIngest;

import javax.faces.bean.ManagedProperty;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.batchIngest.IngestLog.ErrorLevel;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Step;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.util.MessageHelper;

public class IngestProcess extends Thread{
	
	private static final Logger logger = Logger.getLogger(IngestProcess.class);
	
	private String logName;
	private Step step;
	private String action;
	private ErrorLevel errorLevel;
	private String userId;
	private String contextId;
	private String userHandle;

	private String server;
	
	/*
	 * true for FTP, and false for FTPS
	 */
	private boolean protocol;
	
	private String images;
	private String mab;
	private String tei;
	
	private String userName;
	private String password;
	
	private IngestLog log;
	
//	@ManagedProperty("#{applicationBean}")
//	private ApplicationBean applicationBean;
//	
//	@ManagedProperty("#{loginBean}")
//	private LoginBean loginBean;


	
	public IngestProcess(String name, Step step, String action, ErrorLevel errorLevel, String userId, String contextId, String userHandle, String server, boolean protocol, String userName, String password, String mab, String tei, String images) 
	{

		this.logName = name;
		this.step = step;
		this.action = action;
		this.errorLevel = errorLevel;
		this.userId = userId;
		this.contextId = contextId;
		this.userHandle = userHandle;
		this.server = server;
		this.protocol = protocol;
		this.userName = userName;
		this.password = password;
		this.mab = mab;
		this.tei = tei;
		this.images = images;

	}

	public void run()
	{
		/*
		log = new IngestLog_NFS_Backup(logName, step, action, errorLevel, userId, contextId, images, mab, tei,  userHandle);
		*/

//		if(applicationBean != null && applicationBean.getUploadThreads().containsKey(loginBean.getUserHandle()))
//		{
//			applicationBean.getUploadThreads().put(loginBean.getUserHandle(), applicationBean.getUploadThreads().get(loginBean.getUserHandle())+1);
//		}
//		else
//			applicationBean.getUploadThreads().put(loginBean.getUserHandle(), 1);
		
		try {
			log = new IngestLog(logName, step, action, errorLevel, userId, contextId, userHandle, server, protocol, userName, password, images, mab, tei);
			try {
				if(log.ftpCheck())
					log.ftpSaveItems();
				else
					log.updateDB();
			} catch (Exception e) {
				logger.error("Error while checking ingest data", e);
				MessageHelper.errorMessage("login error");
			}
			finally
			{
//				applicationBean.getUploadThreads().put(loginBean.getUserHandle(), applicationBean.getUploadThreads().get(loginBean.getUserHandle())-1);
//				if(applicationBean.getUploadThreads().get(loginBean.getUserHandle()) == 0)
//					applicationBean.getUploadThreads().remove(loginBean.getUserHandle());
				
				log.clear();
			}
		} catch (Exception e) {
			logger.error("Exception while batchingest");
		} 

	}
	String getLogName() {
		return logName;
	}
	void setLogName(String logName) {
		this.logName = logName;
	}
	Step getStep() {
		return step;
	}
	void setStep(Step step) {
		this.step = step;
	}
	String getAction() {
		return action;
	}
	void setAction(String action) {
		this.action = action;
	}
	ErrorLevel getErrorLevel() {
		return errorLevel;
	}
	void setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
	}
	String getUserId() {
		return userId;
	}
	void setUserId(String userId) {
		this.userId = userId;
	}
	String getContextId() {
		return contextId;
	}
	void setContextId(String contextId) {
		this.contextId = contextId;
	}
	
	IngestLog getLog() {
		return log;
	}
	
	void setLog(IngestLog log) {
		this.log = log;
	}
	String getMab() {
		return mab;
	}
	void setMab(String mab) {
		this.mab = mab;
	}
	String getTei() {
		return tei;
	}
	void setTei(String tei) {
		this.tei = tei;
	}
	String getImages() {
		return images;
	}
	void setImages(String images) {
		this.images = images;
	}
	String getUserHandle() {
		return userHandle;
	}
	void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	String getServer() {
		return server;
	}

	void setServer(String server) {
		this.server = server;
	}

	String getUserName() {
		return userName;
	}

	void setUserName(String userName) {
		this.userName = userName;
	}

	String getPassword() {
		return password;
	}

	void setPassword(String password) {
		this.password = password;
	}



	public boolean isProtocol() {
		return protocol;
	}

	public void setProtocol(boolean protocol) {
		this.protocol = protocol;
	}
	
	
	
	
	
	
}
