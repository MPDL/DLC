package de.mpg.mpdl.dlc.batchIngest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.ErrorLevel;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.Step;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem.IngestStatus;
import de.mpg.mpdl.dlc.util.MessageHelper;

public class IngestProcess extends Thread{
	
	private static final Logger logger = Logger.getLogger(IngestProcess.class);
	
	private String logName;
	private String action;

	private String contextId;
	private String userHandle;

	private String server;
	
	/*
	 * true for FTP, and false for FTPS
	 */
	private boolean ftp;
	
	private String images;
	private String mab;
	private String tei;
	
	private String userName;
	private String password;
	
	private IngestLog log;
	
	private BatchLog batchLog;
	private EntityManager em;
	
	public IngestProcess(String name, String action, String contextId, String userHandle, String server, boolean ftp, String userName, String password, String mab, String tei, String images, BatchLog batchLog) 
	{

		this.logName = name;
		this.action = action;
		this.contextId = contextId;
		this.userHandle = userHandle;
		this.server = server;
		this.ftp = ftp;
		this.userName = userName;
		this.password = password;
		this.mab = mab;
		this.tei = tei;
		this.images = images;
		this.batchLog = batchLog;
		saveLog(batchLog);
		

	}

	private void saveLog(BatchLog batchLog)
	{

		EntityManagerFactory emf = VolumeServiceBean.getEmf();
		this.em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(batchLog);
		em.getTransaction().commit();
		this.setUncaughtExceptionHandler(new BatchIngestExceptionHandler(em, batchLog));


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
			log = new IngestLog(logName, action, contextId, userHandle, server, ftp, userName, password, images, mab, tei, batchLog);
			try {  
				if(log.ftpCheck())
					log.ftpSaveItems();
				else
				{
					batchLog.setErrorLevel(ErrorLevel.ERROR);
					batchLog.setStep(Step.STOPPED);
				}

			} catch (Exception e) {
				logger.error("Error while checking ingest data", e);
				MessageHelper.errorMessage("login error");
			}
			finally
			{
//				applicationBean.getUploadThreads().put(loginBean.getUserHandle(), applicationBean.getUploadThreads().get(loginBean.getUserHandle())-1);
//				if(applicationBean.getUploadThreads().get(loginBean.getUserHandle()) == 0)
//					applicationBean.getUploadThreads().remove(loginBean.getUserHandle());
				em.getTransaction().begin();
				em.merge(batchLog);
				em.getTransaction().commit();
				em.close();
				log.clear();
			}
		} catch (Exception e) {
			logger.error("Exception while batchingest");
		} 

	}


	public class BatchIngestExceptionHandler implements Thread.UncaughtExceptionHandler
	{
		
		private EntityManager em;
		private BatchLog batchLog;

		public BatchIngestExceptionHandler(EntityManager em, BatchLog batchLog)
		{
			this.em = em;
			this.batchLog = batchLog; 
		}

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			batchLog.setErrorLevel(ErrorLevel.ERROR);
			em.getTransaction().begin();
			em.persist(batchLog);
			em.getTransaction().commit();
			em.close();
			
			
		}
		
	}


	
	
	
	
	
	
}
