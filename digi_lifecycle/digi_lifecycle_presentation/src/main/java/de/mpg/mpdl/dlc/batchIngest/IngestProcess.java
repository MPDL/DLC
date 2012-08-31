package de.mpg.mpdl.dlc.batchIngest;

import java.util.Date;

import java.util.concurrent.Executors;

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

	private String images;
	private String mab;
	private String tei;
	
	private IngestLog log;
	private long lastBeat = 0;
	
	private ContextServiceBean contectServiceBean = new ContextServiceBean();

	
	public IngestProcess(String name, Step step, String action, ErrorLevel errorLevel, String userId, String contextId, String userHandle, String mab, String tei, String images) 
	{
		this.logName = name;
		this.step = step;
		this.action = action;
		this.errorLevel = errorLevel;
		this.userId = userId;
		this.contextId = contextId;
		this.userHandle = userHandle;
		this.mab = mab;
		this.tei = tei;
		this.images = images;
	}

	public void run()
	{
		log = new IngestLog(logName, step, action, errorLevel, userId, contextId, images, mab, tei,  userHandle);
		try {

			log.checkAndSaveItems();
		} catch (Exception e) {
			logger.error("Erroe while checking ingest data", e);
			
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
	
}
