package de.mpg.mpdl.dlc.batchIngest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.ErrorLevel;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.Step;

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
	private String cdc;
	
	private String userName;
	private String password;
	
	private IngestLog log;
	
	private BatchLog batchLog;
	private EntityManager em;
	
	public IngestProcess(String name, String action, String contextId, String userHandle, String server, boolean ftp, String userName, String password, String images, String mab, String tei, String cdc, BatchLog batchLog) 
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
		this.cdc = cdc;
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
		Thread thisThread = Thread.currentThread();
		/*
		log = new IngestLog_NFS_Backup(logName, step, action, errorLevel, userId, contextId, images, mab, tei,  userHandle);
		*/
		try {  
			log = new IngestLog(logName, action, contextId, userHandle, server, ftp, userName, password, images, mab, tei, cdc, batchLog);

			if(log.ftpCheck())
			{
					log.ftpSaveItems();
			}
			else
			{
				batchLog.setErrorLevel(ErrorLevel.ERROR);
				batchLog.setStep(Step.STOPPED);
			}
			
		} catch (Exception e) {
			batchLog.setErrorLevel(ErrorLevel.PROBLEM);
		}
		finally
		{
			em.getTransaction().begin();
			em.merge(batchLog);
			em.getTransaction().commit();
			em.close();
			log.clear();
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
