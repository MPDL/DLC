package de.mpg.mpdl.dlc.batchIngest;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.batchIngest.IngestLog.ErrorLevel;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Step;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;

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
	private String images;
	private String mab;
	private String tei;
	
	private String userName;
	private String password;
	
	private IngestLog log;
	private long lastBeat = 0;
	
	private ContextServiceBean contectServiceBean = new ContextServiceBean();

	
	public IngestProcess(String name, Step step, String action, ErrorLevel errorLevel, String userId, String contextId, String userHandle, String server, String userName, String password, String mab, String tei, String images) 
	{
		this.logName = name;
		this.step = step;
		this.action = action;
		this.errorLevel = errorLevel;
		this.userId = userId;
		this.contextId = contextId;
		this.userHandle = userHandle;
		this.server = server;
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
		log = new IngestLog(logName, step, action, errorLevel, userId, contextId, userHandle, server, userName, password, images, mab, tei);
		try {

			//log.checkAndSaveItems();
			if(log.ftpCheck())
				log.ftpSaveItems();
			else
				log.updateDB();
//			log.clear();
		} catch (Exception e) {
			logger.error("Error while checking ingest data", e);
//			log.clear();
		}
		finally
		{
			log.clear();
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
	
	
	
}
