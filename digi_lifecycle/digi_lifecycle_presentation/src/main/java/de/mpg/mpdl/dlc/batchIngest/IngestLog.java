package de.mpg.mpdl.dlc.batchIngest;

import java.io.File;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.CreateVolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.ErrorLevel;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.Step;
import de.mpg.mpdl.dlc.persistence.entities.BatchLogItem;
import de.mpg.mpdl.dlc.persistence.entities.BatchLogItemVolume;
import de.mpg.mpdl.dlc.util.BatchIngestLogs;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;
import de.mpg.mpdl.dlc.vo.BatchIngestItem;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsRelatedItem;



public class IngestLog 

{    
    private static Logger logger = Logger.getLogger(IngestLog.class);
    
    private String contextId;
	private int finishedItems;
	private int totalItems;
	
    private int id;
	private String userHandle;

	private String mab;
	private String tei;
	private String images;
	
	private String server;
	private boolean ftp;
	private String username;
	private String password;
	
	HashMap<String, BatchIngestItem> itemsForBatchIngest = new HashMap<String, BatchIngestItem>();
	HashMap<String, BatchIngestItem> errorItems = new HashMap<String, BatchIngestItem>();
	
	private VolumeServiceBean volumeService = new VolumeServiceBean();

	

//	private Object currentItem = new Object();
//	private Object currentItemVolume = new Object();

    
    private FTPClient ftpClient;

    private List<BatchIngestItem> items = new ArrayList<BatchIngestItem>();
    
    private BatchLog batchLog;
    private EntityManager em;
    
//    private SessionExtenderTask seTask;

    public IngestLog()
    {
    }


	public IngestLog(String name, String action, String contextId, String userHandle, String server, boolean ftp, String username, String password, String images, String mab, String tei, BatchLog batchLog) throws Exception
	{
		this.contextId = contextId;
		this.userHandle = userHandle;
		this.server = server;
		this.ftp = ftp;
		this.username = username;
		this.password = password;
		this.images = images;
		this.mab = mab;
		this.tei = tei;  
		this.batchLog = batchLog;
		try
		{
			if(ftp)
			{
				this.ftpClient = ftpLogin(server, username, password);
				batchLog.getLogs().add(BatchIngestLogs.FTP_LOGIN);
			}
			else
			{
				this.ftpClient = ftpsLogin(server, username, password);
				batchLog.getLogs().add(BatchIngestLogs.FTPS_Login);
			}
		}catch(Exception e)
		{
			try{
				batchLog.getLogs().add(BatchIngestLogs.FTP_CONNECT_RETRY);
				if(ftp)
				{
					this.ftpClient = ftpLogin(server, username, password);
					batchLog.getLogs().add(BatchIngestLogs.FTP_LOGIN);
				}
				else
				{
					this.ftpClient = ftpsLogin(server, username, password);
					batchLog.getLogs().add(BatchIngestLogs.FTPS_Login);
				}
			}catch(Exception e1)
			{
				batchLog.setErrorLevel(ErrorLevel.ERROR);
				batchLog.setStep(Step.STOPPED);
				batchLog.getLogs().add(BatchIngestLogs.FTP_CONNECT_ERROR);
			}
		}
		finally
		{
			EntityManagerFactory emf = VolumeServiceBean.getEmf();
			this.em = emf.createEntityManager();
			update(batchLog);
		}
		/*
		if(FTPReply.isPositiveCompletion(ftp.getReplyCode()))
		{
			System.err.println("ftp2 = " + ftp.getReplyCode());
			this.seTask = new SessionExtenderTask(userHandle, userId);
		}
		else
		{
			updateLog("errorLevel", ErrorLevel.ERROR.toString());
			updateLog("step", Step.STOPPED.toString());
			logs.add("Error: cannot connect to ftp/ftps server");
			updateLogLogs(logs);
		}
		*/
		
	}

	public static FTPClient ftpLogin(String server, String username, String password) throws IOException
	{
		long start = System.currentTimeMillis();
		System.err.println("ftpLogin");
		FTPClient ftp = new FTPClient();
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode()))
		{
			ftp.connect(server);
			logger.info("connect to server: "+server + " : "+ftp.getReplyCode());	
		}
//       ftp.setDataTimeout(60000); // 10 minutes   
//       ftp.setConnectTimeout(600000); // 10 minutes
        ftp.setControlKeepAliveTimeout(600000);
        ftp.setControlEncoding("UTF-8");
		ftp.login(username, password);
        if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
        	ftp.disconnect();
	        logger.error("FTP server refused connection.");
	    }
        
		long time = System.currentTimeMillis()-start;
		System.err.println("Time FTP Login: " + time);
        
		return ftp;
	}
	
	public static FTPSClient ftpsLogin(String server, String username, String password) throws IOException
	{
		long start = System.currentTimeMillis();
		System.err.println("ftpsLogin");
		boolean isImpicit = false;
		FTPSClient ftps = new FTPSClient("SSL", isImpicit);
		if(!FTPReply.isPositiveCompletion(ftps.getReplyCode()))
		{
			ftps.connect(server, 21000);
			logger.info("Connecting to FTPS Server: "+server + " : "+ftps.getReplyCode());
		}
//      ftps.setDataTimeout(60000); // 10 minutes   
//     	ftps.setConnectTimeout(600000); // 10 minutes
		ftps.setControlKeepAliveTimeout(600000);
		logger.info("Connecting to FTPS Server");
        int reply;
        ftps.login(username, password);
        System.err.println("Login FTPS Success");

        //client.setFileType(FTP.BINARY_FILE_TYPE);
        ftps.setFileType(FTP.NON_PRINT_TEXT_FORMAT);
        ftps.execPBSZ(0);  // Set protection buffer size
        ftps.execPROT("P"); // Set data channel protection to private
        ftps.enterLocalPassiveMode();

        reply = ftps.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply))
        {
        	ftps.disconnect();
            System.err.println("FTP server refused connection.");

        }
		long time = System.currentTimeMillis()-start;
		System.err.println("Time FTPS Login: " + time);

		return ftps;
	}
	
	

	
//	/**
//	 * Update logs in dlc_batch_ingest_log Table
//	 * @param value database key value
//	 */	
//	public void updateLogLogs(List<String> logs)
//	{
//		try
//		{
//            if(connection == null)
//            {
//            	connection = getConnection();
//            }
//            String[] array = logs.toArray(new String[logs.size()]);
//            Array aArray = connection.createArrayOf("text", (Object[])array);
//			statement = this.connection.prepareStatement("UPDATE dlc_batch_ingest_log SET logs = ? WHERE id = "+ logId);
//			statement.setArray(1, aArray);
//            statement.executeUpdate();
//            statement.close();
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException("Error while uploading log", e);
//		}
//	}

	
	public boolean ftpCheck()
	{ 
		logger.info("CHECK DATA");
		this.itemsForBatchIngest.clear();
		this.errorItems.clear();
		try {		
			batchLog.getLogs().add("CHECK DATA");
			ftpCheckImages(itemsForBatchIngest, this.images);		
			if(!"".equals(tei))
				{
					ftpReadTeiFiles(itemsForBatchIngest, errorItems, this.tei);
				}
			ftpReadMabFiles(itemsForBatchIngest, errorItems, this.mab);
	
			int totalItems = 0;

			if(itemsForBatchIngest.size()>0)
			{
				Iterator i = itemsForBatchIngest.entrySet().iterator();
				while(i.hasNext())
				{
					Entry item = (Entry) i.next();
					BatchIngestItem bi = (BatchIngestItem) item.getValue();
					if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.monograph.id")))
					{
						totalItems ++;
					}
					else if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.multivolume.id")))
					{
						totalItems ++;
						totalItems = totalItems + bi.getVolumes().size();
					}
				}
				itemsForBatchIngest = saveLogItems(itemsForBatchIngest, ErrorLevel.FINE);
			}
			if(errorItems.size() >0)
			{

				Iterator i = errorItems.entrySet().iterator();
				while(i.hasNext())
				{
					Entry item = (Entry) i.next();
					BatchIngestItem bi = (BatchIngestItem) item.getValue();
					if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.monograph.id")))
					{
						totalItems ++;
					}
					else if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.multivolume.id")))
					{
						totalItems ++;
						totalItems = totalItems + bi.getVolumes().size();
					}
				}
				errorItems = saveLogItems(errorItems, ErrorLevel.ERROR);
			}
			batchLog.setTotalItems(totalItems);
		} catch (Exception e) {
			batchLog.setErrorLevel(ErrorLevel.ERROR);
			batchLog.setStep(Step.STOPPED);
			MessageHelper.errorMessage("error stop");
		}
		finally
		{  
			update(batchLog);
			if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
				ftpLogout(ftpClient);
		}    
		return (errorItems.size()==0 && itemsForBatchIngest.size()>0);

	}
	
	public String clear()
	{
		batchLog.setEndDate(new Date());
		if(ErrorLevel.FATAL.equals(batchLog.getErrorLevel()) || ErrorLevel.ERROR.equals(batchLog.getErrorLevel()))
		{
			batchLog.setStep(Step.STOPPED);
		}
		
		em.getTransaction().begin();
		em.merge(batchLog);
		em.getTransaction().commit();
		em.close();
		
		if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
			ftpLogout(ftpClient);
        
		List<String> dirs = new ArrayList<String>();
		
		if(itemsForBatchIngest.size()>0)
		{
			for(BatchIngestItem item : itemsForBatchIngest.values())
			{
			
				dirs.add(item.getDlcDirectory());
				if(item.getVolumes().size()>0)
					for(BatchIngestItem vol : item.getVolumes())
						dirs.add(vol.getDlcDirectory());
			}
		
		}
		if(errorItems.size()>0)
		{
			for(BatchIngestItem item : errorItems.values())
			{
				dirs.add(item.getDlcDirectory());
			}
		}
		itemsForBatchIngest.clear();
		itemsForBatchIngest = null;
		errorItems.clear();
		errorItems = null;
		System.gc();
		for(String d : dirs)
		{
			if(d != null)
			{
				File dir = new File(d);
				try {
					deleteDir(dir);
				} catch (Exception e) {
					logger.error("Error while deleting local Batch Ingest Data " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}
	
	public String ftpSaveItems()
	{   
		if(itemsForBatchIngest.size()>0)
		{
			saveItems(itemsForBatchIngest);
		}
		else
		{
			batchLog.setStep(Step.STOPPED);
		}
		update(batchLog);
		return "";
	}
	
	
	
//	private List<String> getMabFileNames(FTPClient ftp)
//	{
//		List<String> fileNames = new ArrayList<String>();
//		try{
//		// Poll for files.
//		FTPFile[] filesList = ftp.listFiles();
//		for(FTPFile tmpFile : filesList)
//		{
//			if(tmpFile.isDirectory())
//				continue;
//
//			fileNames.add(splitSuffix(tmpFile.getName()));
//
//		}
//		
//		}catch(Exception e)
//		{
//			logger.error("error while reading mab filenames"+e.getMessage());
//		}
//		return fileNames;
//	}

	
	private void ftpCheckImages(HashMap<String, BatchIngestItem> items, String directory)
	{
 		try{
 			batchLog.getLogs().add("CHECK IMAGES");
			if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
			{
				ftpClient.disconnect();
				if(ftp)
					this.ftpClient = ftpLogin(server, username, password);
				else
					this.ftpClient = ftpsLogin(server, username, password);
			}
			FTPFile[] ds = ftpClient.listFiles(directory);
			if(ds.length == 0)
			{
				batchLog.getLogs().add(BatchIngestLogs.IMAGE_DIRECTORY_ERROR);
				batchLog.setErrorLevel(ErrorLevel.ERROR);
			}
			for(FTPFile d : ds)
			{
				if(d.isDirectory() && !".".equals(d.getName()) && !"..".equals(d.getName()))
				{
					String dlcDirectory = System.getProperty("java.io.tmpdir") + "/" +  UUID.randomUUID().toString();
					new File(dlcDirectory).mkdir();
					
					String name = d.getName();
					String imagesDirectory = directory + "/" + name;
					
					BatchIngestItem newItem = new BatchIngestItem();
					newItem.setDlcDirectory(dlcDirectory);
					newItem.setName(name);
					newItem.setImagesDirectory(imagesDirectory);
					
					FTPFile[] filesList = ftpClient.listFiles(imagesDirectory);
					int imageNr = 0;
					int footerNr = 0;					
					for(FTPFile tmpFile : filesList)
					{
						if(tmpFile.isDirectory())
							continue;

						if(tmpFile.getName().startsWith("footer"))
						{
							footerNr ++;
							File footer = new File(dlcDirectory + "/"+ tmpFile.getName());
							newItem.setFooter(footer);
						}
						else
						{
						imageNr ++;
						File image = new File(dlcDirectory + "/"+ tmpFile.getName());
						newItem.getImageFiles().add(image);
						}
					}
					newItem.setFooterNr(footerNr);
					if(footerNr > 1)
					{
						String error = BatchIngestLogs.MULTIFOOTER_ERROR;
						newItem.getLogs().add(error);
					}
					newItem.setImageNr(imageNr);
					items.put(name, newItem);
				}
			}
		}catch (IOException e) {
			batchLog.setErrorLevel(ErrorLevel.ERROR);
			batchLog.setStep(Step.STOPPED);
			batchLog.getLogs().add(BatchIngestLogs.FTP_CONNECT_ERROR);
		}
		finally
		{
			update(batchLog);
		}
	}
	
	private void ftpReadTeiFiles(HashMap<String, BatchIngestItem> items, HashMap<String, BatchIngestItem> errorItems,String directory)
	{		
 		try {
			batchLog.getLogs().add("CHECK TEI FILES");
			if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
			{
				if(ftp)
					this.ftpClient = ftpLogin(server, username, password);
				else
					this.ftpClient = ftpsLogin(server, username, password);
			}
			FTPFile[] filesList = ftpClient.listFiles(directory);
			if(filesList.length == 0)
			{
				batchLog.getLogs().add(BatchIngestLogs.TEI_DIRECTORY_ERROR);
				batchLog.setErrorLevel(ErrorLevel.ERROR);
			}

			for(FTPFile tmpFile : filesList)
			{
				if(tmpFile.isDirectory())
					continue;
//				InputStream is = ftp.retrieveFileStream(directory+"/"+tmpFile.getName());
//				byte buf[] = new byte[1024];
//				int len;
//				while((len=is.read(buf))>0)
//					out.write(buf,0,len);
//				out.close();
//				is.close();
					
				String name = splitSuffix(tmpFile.getName());
				BatchIngestItem item = items.get(name);

				if(item == null)
				{
					item = new BatchIngestItem();
					String dlcDirectory = System.getProperty("java.io.tmpdir") + "/" +  UUID.randomUUID().toString();
					new File(dlcDirectory).mkdir();
					item.setName(name);
					item.setDlcDirectory(dlcDirectory);
					
					File tFile = new File(dlcDirectory + "/"+ tmpFile.getName());
					FileOutputStream out = new FileOutputStream(tFile);
					ftpClient.retrieveFile(directory+"/"+tmpFile.getName(), out);
					out.flush();
					out.close();
					item.setTeiFile(tFile);
					String errorMessage = BatchIngestLogs.SINGLE_TEI_ERROR;
					logger.error(errorMessage + name);
					item.getLogs().add(errorMessage);
					errorItems.put(tFile.getName(), item);
				}
				else 
				{
					String dlcDirectory = item.getDlcDirectory();
					File tFile = new File(dlcDirectory + "/"+ tmpFile.getName());
					FileOutputStream out = new FileOutputStream(tFile);
					ftpClient.retrieveFile(directory+"/"+tmpFile.getName(), out);
					out.flush();
					out.close();
					item.setTeiFile(tFile);

					logger.info("read tei for " + name);
					try {
						//InputStream teiIs = new FileInputStream(tFile);
						CreateVolumeServiceBean.validateTei(new StreamSource(tFile));
						List<XdmNode> pbList = VolumeServiceBean.getAllPbs(new StreamSource(tFile));
						item.setImageFiles(sortImagesByTeiFile(item.getImageFiles(), pbList));
						int numberOfTeiPbs = pbList.size();
						int numberOfImages = item.getImageNr();
						if(numberOfTeiPbs != numberOfImages)
						{
							String errorMessage = BatchIngestLogs.PBS_NOTEQUAL_IMAGES_ERROR + "("+ numberOfTeiPbs + " != " + numberOfImages + ")";
							logger.error(errorMessage + name);
							item.getLogs().add(errorMessage);
							items.remove(name);
							errorItems.put(name, item);
						}
					} catch (Exception e) {
						String error = BatchIngestLogs.TEI_SYNTAX_ERROR;
						logger.error(error + name);
						item.getLogs().add(error);
						items.remove(name);
						errorItems.put(name, item);
					} 
	
				}

			}

		}catch (Exception e) {
			batchLog.setErrorLevel(ErrorLevel.ERROR);
			batchLog.setStep(Step.STOPPED);
			batchLog.getLogs().add(BatchIngestLogs.FTP_CONNECT_ERROR);
		}
		finally
		{
			update(batchLog);
		}
		
	}
	
	
	public static List<File> sortImagesByTeiFile(List<File> imageFiles, List<XdmNode> teiPbFacsValues)
	{
		//Sort images using pb facs attribute in tei file
				
		List<File> imageFilesSorted = new ArrayList<File>();
		if(teiPbFacsValues != null && imageFiles.size() == teiPbFacsValues.size())
		{
			
			for(XdmNode node : teiPbFacsValues)
			{
				String facs = node.getAttributeValue(new QName("facs"));
				boolean found = false;
				if(facs!=null)
				{
					
					for(File imgFile : imageFiles)
					{
						if(facs.equals(imgFile.getName()))
						{
							imageFilesSorted.add(imgFile);
							found=true;
							break;
						}
					}
				}
				if(!found)
				{
					
					imageFilesSorted = imageFiles;
					break;
				}
				
			}
			
		}
		else
		{
			imageFilesSorted = imageFiles;
		}
		
		return imageFilesSorted;
	
	}
	

	
	private void ftpReadMabFiles(HashMap<String, BatchIngestItem> items, HashMap<String, BatchIngestItem> errorItems, String directory)
	{
		
//		ftp.changeWorkingDirectory(directory);
 		try {
 			batchLog.getLogs().add("CHECK MAB FILES");
			HashMap<String, BatchIngestItem> multiVolumes = new HashMap<String, BatchIngestItem>();
			HashMap<String, BatchIngestItem> volumes = new HashMap<String, BatchIngestItem>();
	 			
			if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
			{
				if(ftp)
					this.ftpClient = ftpLogin(server, username, password);
				else
					this.ftpClient = ftpsLogin(server, username, password);
			}
 			FTPFile[] filesList = ftpClient.listFiles(directory);
			if(filesList.length == 0)
			{
				batchLog.getLogs().add(BatchIngestLogs.MAB_DIRECTORYE_RROR);
				batchLog.setErrorLevel(ErrorLevel.ERROR);				
			}
			for(FTPFile tmpFile : filesList)
			{
				if(tmpFile.isDirectory())
					continue;

				String name = splitSuffix(tmpFile.getName());
				BatchIngestItem item;
				//has images -> volume or monograph
				if((item = items.get(name)) != null)
				{	
					String dlcDirectory = item.getDlcDirectory();
				
	//				File tFile = new File(dlcDirectory + "/"+ tmpFile.getName());
	//				FileOutputStream out = new FileOutputStream(tFile);
	//				ftp.retrieveFile(directory+"/"+tmpFile.getName(), out);
				
				
					File mFile = new File(dlcDirectory + "/"+ tmpFile.getName());
					FileOutputStream out = new FileOutputStream(mFile);
					ftpClient.retrieveFile(directory+"/"+tmpFile.getName(), out);		
					out.flush();
					out.close();

				
					MabXmlTransformation transform = new MabXmlTransformation();
					try {
						File modsFile = transform.mabToMods(null, mFile);
						ModsMetadata md =  VolumeServiceBean.createModsMetadataFromXml(new FileInputStream(modsFile));
						item.setModsMetadata(md);
						logger.info("read mab for " + name);
						
						String parentId = getParentId(md);
						if(parentId.equals(""))
						{
							item.setContentModel(PropertyReader.getProperty("dlc.content-model.monograph.id"));
							logger.info("set content Model "+ " for Monograph "+ name);
	
						}
						else{
							item.setContentModel(PropertyReader.getProperty("dlc.content-model.volume.id"));
							logger.info("set content Model "+ " for Volume "+ name);
							item.setParentId(parentId);
							items.remove(name);
							volumes.put(name, item);
							}
						} catch (Exception e) {
						String errorMessage = BatchIngestLogs.MAB_DIRECTORYE_RROR;
						logger.error(errorMessage , e);
						item.getLogs().add(errorMessage);
						items.remove(name);
						errorItems.put(name, item);

					}
				}
				else if((item = errorItems.get(name))!= null)
				{
					String dlcDirectory = item.getDlcDirectory();
					File mFile = new File(dlcDirectory + "/"+ tmpFile.getName());
					FileOutputStream out = new FileOutputStream(mFile);
					ftpClient.retrieveFile(directory+"/"+tmpFile.getName(), out);	
					out.flush();
					out.close();
					MabXmlTransformation transform = new MabXmlTransformation();
	
					File modsFile = transform.mabToMods(null, mFile);
					try {
						ModsMetadata md =  VolumeServiceBean.createModsMetadataFromXml(new FileInputStream(modsFile));
				
						
						item.setModsMetadata(md);
						logger.info("read mab for " + name);
						
						String parentId = getParentId(md);
						if(parentId.equals(""))
						{
							item.setContentModel(PropertyReader.getProperty("dlc.content-model.monograph.id"));
							logger.info("set content Model "+ " for Monograph "+ name);
	
						}
						else{
							item.setContentModel(PropertyReader.getProperty("dlc.content-model.volume.id"));
							logger.info("set content Model "+ " for Volume "+ name);
							item.setParentId(parentId);
						}
						} catch (Exception e) {
						String errorMessage = BatchIngestLogs.MAB_TRANSFORM_ERROR;
						logger.error(errorMessage , e);
						item.getLogs().add(errorMessage);
					}
				}
				else
				{
					item = new BatchIngestItem();
					
					String dlcDirectory = System.getProperty("java.io.tmpdir") + "/" +  UUID.randomUUID().toString();
					new File(dlcDirectory).mkdir();
					item.setName(name);
					item.setDlcDirectory(dlcDirectory);
					
					File mFile = new File(dlcDirectory + "/"+ tmpFile.getName());
					FileOutputStream out = new FileOutputStream(mFile);
					ftpClient.retrieveFile(directory+"/"+tmpFile.getName(), out);	
					out.flush();
					out.close();
					MabXmlTransformation transform = new MabXmlTransformation();
					File modsFile = transform.mabToMods(null, mFile);
					try {
						ModsMetadata md =  VolumeServiceBean.createModsMetadataFromXml(new FileInputStream(modsFile));
						item.setModsMetadata(md);
						item.setContentModel(PropertyReader.getProperty("dlc.content-model.multivolume.id"));
						//item.setModsMetadata(md);
						logger.info("read mab for Multivolume" + name);
						//item.setContentModel(PropertyReader.getProperty("dlc.content-model.multivolume.id"));
						logger.info("set content Model "+ " for Multivolume "+ name);

				
					multiVolumes.put(name, item);
					} catch (Exception e) {
						String errorMessage = BatchIngestLogs.MAB_TRANSFORM_ERROR;
						logger.error(errorMessage , e);
						item.getLogs().add(errorMessage);
						errorItems.put(name, item);
					}
				}

			}
			if(multiVolumes.size() >0 || volumes.size() >0 )
				updateItemsForBatchIngest(items, errorItems, multiVolumes, volumes);
		}catch (IOException e) {
			batchLog.setErrorLevel(ErrorLevel.ERROR);
			batchLog.setStep(Step.STOPPED);
			batchLog.getLogs().add(BatchIngestLogs.FTP_CONNECT_ERROR);
		}
		finally
		{
			update(batchLog);
		}
		
		
	}
	
	private void updateItemsForBatchIngest(HashMap<String, BatchIngestItem> items, HashMap<String, BatchIngestItem> errorItems, HashMap<String, BatchIngestItem> multiVolumes, HashMap<String, BatchIngestItem> volumes)
	{
		if(multiVolumes.size() > 0 )
		{
			Iterator mi = multiVolumes.entrySet().iterator();

			while(mi.hasNext())
			{
				
				Entry multi = (Entry) mi.next();
				
				String catalogueId = (String)multi.getKey();
				BatchIngestItem multivolume = (BatchIngestItem) multi.getValue();

				List<BatchIngestItem> vols = new ArrayList<BatchIngestItem>();
				Iterator vi = volumes.entrySet().iterator();
				while(vi.hasNext())
				{
					Entry volume = (Entry) vi.next();
					String volName = (String)volume.getKey();
					
					BatchIngestItem vol = (BatchIngestItem) volume.getValue();

					if(catalogueId.equals(vol.getParentId()))
					{
						vols.add(vol);
						vi.remove();
					}
				}
				if(vols.size() > 0)
				{
					multivolume.setVolumes(vols);
					items.put(catalogueId, multivolume);
				}
				else
				{
					String errorMessage = BatchIngestLogs.SINGLE_MULTIVOLUME_ERROR;
					multivolume.getLogs().add(errorMessage);
					errorItems.put(multivolume.getName(), multivolume);
				}
			}
			
			Iterator restVolumes = volumes.entrySet().iterator();
			while(restVolumes.hasNext())
			{
				Entry vol = (Entry) restVolumes.next();
				String name = (String) vol.getKey();
				BatchIngestItem item = (BatchIngestItem) vol.getValue();
				String error = BatchIngestLogs.SINGLE_VOLUME_ERROR;
				logger.error(error);
				item.getLogs().add(error);
				errorItems.put(name, item);
			}
		}
		else if(volumes.size() >0)
		{
			Iterator v = volumes.entrySet().iterator(); 
			while(v.hasNext())
			{
				Entry vol = (Entry)v.next();
				String name = (String) vol.getKey();
				BatchIngestItem item = (BatchIngestItem) vol.getValue();
				String error = BatchIngestLogs.SINGLE_VOLUME_ERROR;
				logger.error(error);
				item.getLogs().add(error);
				errorItems.put(name, item);
			}
		}
	}
	
//	public int getLogItemIdWithName(String name)
//	{
//		try {
//			PreparedStatement statement = this.connection.prepareStatement("SELECT id as itemId FROM dlc_batch_ingest_log WHERE 10<id AND id<15 AND name='Thu Aug 16 10:26:10 CEST 2012'");
//			ResultSet resultSet = statement.executeQuery();
//			return resultSet.getInt("itemId");
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return 0;
//		}
//        
//	}
	
	private void downloadImages(BatchLogItem logItem, BatchLogItemVolume logItemVolume, String imagesDirectory, String dlcDirectory, List<File> images, File footer) throws Exception
	{

		if(logItem != null)
		{
			logItem.getLogs().add(BatchIngestLogs.DOWNLOAD_IMAGES);
		}
		else
		{
			logItemVolume.getLogs().add(BatchIngestLogs.DOWNLOAD_IMAGES);
		}
		try{
			if(ftp)
				this.ftpClient = ftpLogin(server, username, password);
			else
				this.ftpClient = ftpsLogin(server, username, password);	

			for(File i : images)
			{
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(i);
				} catch (FileNotFoundException e) {
					if(logItem != null)
					{
						logItem.getLogs().add("Image not found: " + i.getName());
					}
					else
					{
						logItemVolume.getLogs().add("Image not found: " + i.getName());
					}				
					throw e;
				}
				try{
				
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					ftpClient.retrieveFile(imagesDirectory+"/"+ i.getName(), out);
					out.flush();
					out.close();
					if(logItem != null)
					{
						logItem.getLogs().add("downloading image Name: " + i.getName() + " | Size: " + i.length());
					}
					else
					{
						logItemVolume.getLogs().add("downloading image Name: " + i.getName() + " | Size: " + i.length());
					}
					logger.info("downloading image to " + dlcDirectory + " | Name: " + i.getName() + " | Size: " + i.length());
				}catch(IOException e)
				{
					logger.info("Error while copying Image from FTP Server--Retry: " + i.getName() + " .(Message): " + e.getMessage());
					if(logItem != null)
					{
						logItem.getLogs().add("Error while copying Image from FTP Server--Retry: " + i.getName() + " .(Message): " + e.getMessage());
					}
					else
					{
						logItemVolume.getLogs().add("Error while copying Image from FTP Server--Retry: " + i.getName() + " .(Message): " + e.getMessage());
					}
					try 
					{
						if(ftp)
							this.ftpClient = ftpLogin(server, username, password);
						else
							this.ftpClient = ftpsLogin(server, username, password);
	
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
						ftpClient.retrieveFile(imagesDirectory+"/"+ i.getName(), out);
						out.flush();
						out.close();
						if(logItem != null)
						{
							logItem.getLogs().add("Retry downloading image Name: " + i.getName() + " | Size: " + i.length());
						}
						else
						{
							logItemVolume.getLogs().add("Retry downloading image Name: " + i.getName() + " | Size: " + i.length());
						}		
						logger.info("Retry :downloading image to " + dlcDirectory + " | Name: " + i.getName() + " | Size: " + i.length());
					} catch (IOException e1) {
						if(logItem != null)
						{
							logItem.getLogs().add("Error while copying Image from FTP Server: " + i.getName() + " .(Message): " + e.getMessage());
						}
						else
						{
							logItemVolume.getLogs().add("Error while copying Image from FTP Server: " + i.getName() + " .(Message): " + e.getMessage());
						}
						logger.error("Error while copying Image from FTP Server: " + i.getName() + " .(Message): " + e.getMessage());
						throw e;
					}
					logger.info("Retry--downloading image to " + dlcDirectory + " | Name: " + i.getName() + " | Size: " + i.length());
				
				}
			}
			if(footer != null)
			{
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(footer);
				} catch (FileNotFoundException e2) {
					if(logItem != null)
					{
						logItem.getLogs().add("Footer not found: " + footer.getName());
					}
					else
					{
						logItemVolume.getLogs().add("Footer not found: " + footer.getName());
					}				
					throw e2;
				}
				try {
	
					if(ftp)
						this.ftpClient = ftpLogin(server, username, password);
					else
						this.ftpClient = ftpsLogin(server, username, password);
					ftpClient.retrieveFile(imagesDirectory+"/"+ footer.getName(), out);
					out.flush();
					out.close();
					if(logItem != null)
					{
						logItem.getLogs().add("downloading footer Name: " + footer.getName() + " | Size: " + footer.length());
					}
					else
					{
						logItemVolume.getLogs().add("downloading footer Name: " + footer.getName() + " | Size: " + footer.length());
					}
					logger.info("downloading footer to " + dlcDirectory + " | Name:  " + footer.getName() + " | Size: " + footer.length());
					
				} catch (IOException e) 
				{
					logger.info("Error while copying Footer from FTP Server--Retry: " + footer.getName() + " .(Message): " + e.getMessage());
					if(logItem != null)
					{
						logItem.getLogs().add("Error while copying Image from FTP Server--Retry: " + footer.getName() + " .(Message): " + e.getMessage());
					}
					else
					{
						logItemVolume.getLogs().add("Error while copying Image from FTP Server--Retry: " + footer.getName() + " .(Message): " + e.getMessage());
					}
					try {
						if(ftp)
							this.ftpClient = ftpLogin(server, username, password);
						else
							this.ftpClient = ftpsLogin(server, username, password);
	
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
						ftpClient.retrieveFile(imagesDirectory+"/"+ footer.getName(), out);
						out.flush();
						out.close();
						if(logItem != null)
						{
							logItem.getLogs().add("Retry downloading footer Name: " + footer.getName() + " | Size: " + footer.length());
						}
						else
						{
							logItemVolume.getLogs().add("Retry downloading footer Name: " + footer.getName() + " | Size: " + footer.length());
						}
						logger.info("downloading footer to " + dlcDirectory + " | Name:  " + footer.getName() + " | Size: " + footer.length());
						
					} catch (IOException e1) {
						if(logItem != null)
						{
							logItem.getLogs().add("Error while copying Footer from FTP Server: " + footer.getName() + " .(Message): " + e.getMessage());
						}
						else
						{
							logItemVolume.getLogs().add("Error while copying Footer from FTP Server: " + footer.getName() + " .(Message): " + e.getMessage());
						}
						logger.error("Error while copying Footer from FTP Server: " + footer.getName() + " .(Message): " + e.getMessage());
						throw e;
					}
					logger.info("Retry--downloading image to " + dlcDirectory + " | Name: " + footer.getName() + " | Size: " + footer.length());
				}
			}
		}catch(Exception e){
			logger.error("Exception while downloading images from FTP Server");
		}
		finally
		{
			if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
				ftpLogout(ftpClient);
			if(logItem != null)
			{
				update(logItem);
			}
			else
			{
				update(logItemVolume);
			}

		}



	}
	
	private void saveItems(HashMap<String, BatchIngestItem> items)
	{
		batchLog.setStep(Step.STARTED);
		batchLog.getLogs().add("INGEST PROZESS STARTED");
		update(batchLog);
		Iterator i = items.entrySet().iterator();
		String operation = batchLog.getStatus().toString().toLowerCase();
		if(operation.startsWith("release"))
			operation = "release";

		while(i.hasNext())
		{
			Entry item = (Entry) i.next();
			  
//			String logItemId = (String)item.getKey();
			BatchIngestItem bi = (BatchIngestItem) item.getValue();

			
			try {
				if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.monograph.id")))
				{
					TypedQuery<BatchLogItem> query = em.createNamedQuery(BatchLogItem.ITEM_BY_ID, BatchLogItem.class);
					query.setParameter("id", bi.getDbID());				
					BatchLogItem logItem = query.getSingleResult();
					logItem.setStartDate(new Date());
					logItem.setStep(Step.STARTED);
					
					downloadImages(logItem, null , bi.getImagesDirectory(), bi.getDlcDirectory(), bi.getImageFiles(), bi.getFooter());
					
					update(logItem);

					CreateVolumeServiceBean cvsb = new CreateVolumeServiceBean(logItem, em);
					Volume vol = new Volume();
					try{
						vol = cvsb.createNewItem(operation, PropertyReader.getProperty("dlc.content-model.monograph.id"), contextId, null, userHandle, bi.getModsMetadata(), bi.getImageFiles(), bi.getFooter() !=null ? bi.getFooter() : null, bi.getTeiFile() != null ? CreateVolumeServiceBean.fileToDiskFileItem(bi.getTeiFile()) : null, null);
						logItem.setEscidocId(vol.getItem().getOriginObjid());
						logItem.setShortTitle(VolumeUtilBean.getShortTitleView(vol));
						logItem.setSubTitle(VolumeUtilBean.getSubTitleView(vol));
						logItem.getLogs().add("successfully created");
						logItem.setStep(Step.FINISHED);
					}catch(Exception e)
					{
						logItem.setStep(Step.STOPPED);
						batchLog.setErrorLevel(ErrorLevel.PROBLEM);
					}
					finally
					{
						update(batchLog);
						update(logItem);
					}
					
					
				}
				else if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.multivolume.id")))
				{
					  
					Volume mv;
					TypedQuery<BatchLogItem> query = em.createNamedQuery(BatchLogItem.ITEM_BY_ID, BatchLogItem.class);
					query.setParameter("id", bi.getDbID());				
					BatchLogItem logItem_multivolume = query.getSingleResult();
					logItem_multivolume.setStartDate(new Date());
					logItem_multivolume.setStep(Step.STARTED);
					
					update(logItem_multivolume);
			  		
					CreateVolumeServiceBean cvsb = new CreateVolumeServiceBean(logItem_multivolume, em);
					try{
						mv = cvsb.createNewMultiVolume(operation, PropertyReader.getProperty("dlc.content-model.multivolume.id"), contextId, userHandle, bi.getModsMetadata());
						logItem_multivolume.setEscidocId(mv.getItem().getObjid());
						logItem_multivolume.setShortTitle(VolumeUtilBean.getShortTitleView(mv));
						logItem_multivolume.setSubTitle(VolumeUtilBean.getSubTitleView(mv));
						logItem_multivolume.getLogs().add("successfully created");
						
						update(logItem_multivolume);
						
						ArrayList<String> volIds = new ArrayList<String>();

						
						for(BatchIngestItem vol : bi.getVolumes())
						{
							TypedQuery<BatchLogItemVolume> q = em.createNamedQuery(BatchLogItemVolume.ITEM_BY_ID, BatchLogItemVolume.class);
							q.setParameter("id", vol.getDbID());
							BatchLogItemVolume logItemVolume = q.getSingleResult();
							logItemVolume.setStartDate(new Date());
							logItemVolume.setStep(Step.STARTED);

							downloadImages(null, logItemVolume, vol.getImagesDirectory(), vol.getDlcDirectory(), vol.getImageFiles(), vol.getFooter());
							
							update(logItemVolume);
							
							String mvId = mv.getItem().getObjid();
							CreateVolumeServiceBean cvsb2 = new CreateVolumeServiceBean(logItemVolume, em);
							try{
								
								Volume v = cvsb2.createNewItem(operation, PropertyReader.getProperty("dlc.content-model.volume.id"), contextId, mvId, userHandle, vol.getModsMetadata(), vol.getImageFiles(), vol.getFooter() !=null ? vol.getFooter() : null, vol.getTeiFile() !=null ? CreateVolumeServiceBean.fileToDiskFileItem(vol.getTeiFile()) : null, null);
								volIds.add(v.getItem().getObjid());
								logItem_multivolume.setFinished_volumes_nr(volIds.size());
								logItemVolume.setEscidocId(v.getItem().getObjid());
								logItemVolume.setShortTitle(VolumeUtilBean.getVolumeShortTitleView(v));
								logItemVolume.setSubTitle(VolumeUtilBean.getVolumeSubTitleView(v));
								logItemVolume.getLogs().add("successfully created");
								logItemVolume.setStep(Step.FINISHED);
								update(logItemVolume);
							}catch(Exception e)
							{
								batchLog.setErrorLevel(ErrorLevel.PROBLEM);
								logItem_multivolume.setErrorlevel(ErrorLevel.PROBLEM);
								logItemVolume.setStep(Step.STOPPED);
							}
							finally{
								finishedItems++;
								batchLog.setFinishedItems(finishedItems);
								update(batchLog);
								update(logItem_multivolume);
								update(logItemVolume);
							}
														
						}
						if(volIds.size()==0)
						{
							cvsb.rollbackCreation(mv, userHandle);
							logItem_multivolume.setShortTitle("");
							logItem_multivolume.setSubTitle("");
							logItem_multivolume.getLogs().add("Multivolume Rollback");
							logItem_multivolume.setEscidocId("");
						}
						else
						{
							volumeService.updateMultiVolumeFromId(mv.getItem().getObjid(), volIds, userHandle);
						}
						logItem_multivolume.setEndDate(new Date());
						logItem_multivolume.setStep(Step.FINISHED);

					}catch(Exception e)
					{
						logItem_multivolume.setStep(Step.STOPPED);
						batchLog.setErrorLevel(ErrorLevel.ERROR);
					}
					finally{
						update(logItem_multivolume);				
						update(batchLog);
					}
				} 

			}catch (Exception e) {
				batchLog.setErrorLevel(ErrorLevel.PROBLEM);
			} 
			finally
			{
				finishedItems++;
				batchLog.setFinishedItems(finishedItems);
				update(batchLog);				
			}
		}
		batchLog.setStep(Step.FINISHED);
		batchLog.getLogs().add("INGEST PROZESS FINISHED");
		update(batchLog);
	}
	


	private HashMap<String, BatchIngestItem> saveLogItems(HashMap<String, BatchIngestItem> items, ErrorLevel errorLevel)
	{
		Iterator i = items.entrySet().iterator();
	//	HashMap<String, BatchIngestItem> newItems = new HashMap<String, BatchIngestItem>();

		while(i.hasNext())
		{
			Entry item = (Entry) i.next();
			String name = (String)item.getKey();
			BatchIngestItem bi = (BatchIngestItem) item.getValue();
			try {
				BatchLogItem logItem = new BatchLogItem();
//				synchronized(currentItem)
//				{
				logItem.setName(bi.getName());
				logItem.setErrorlevel(errorLevel);
				logItem.setLogs(bi.getLogs());
				logItem.setContent_model(bi.getContentModel());

				if(bi.getContentModel() != PropertyReader.getProperty("dlc.content-model.multivolume.id"))
					logItem.setImages_nr(bi.getImageNr());
				else
					logItem.setVolumes_nr(bi.getVolumes().size());
				logItem.setTeiFileName((bi.getTeiFile() != null) ? bi.getTeiFile().getName() : null);
				logItem.setfFileName((bi.getFooter() != null) ? bi.getFooter().getName() : null);
				
				addNewLogItem(logItem);
				bi.setDbID(logItem.getId());
//				}
		//		newItems.put(Long.toString(logItem.getId()), bi);
				if(PropertyReader.getProperty("dlc.content-model.multivolume.id").equals(bi.getContentModel()))
				{
					for(BatchIngestItem vol : bi.getVolumes())
					{
//						synchronized(currentItemVolume)
//						{
						BatchLogItemVolume logItemVolume = new BatchLogItemVolume();
						logItemVolume.setName(vol.getName());
						logItemVolume.setErrorlevel(errorLevel);
						logItemVolume.setLogs(vol.getLogs());
						logItemVolume.setContent_model(vol.getContentModel());
						logItemVolume.setImages_nr(vol.getImageNr());
						logItemVolume.setTeiFileName((vol.getTeiFile() != null) ? vol.getTeiFile().getName() : null);
						logItemVolume.setfFileName((vol.getFooter()!=null)? vol.getFooter().getName() : null);
						
						addNewLogItemVolume(logItem, logItemVolume);
						vol.setDbID(logItemVolume.getId());
//						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	//	return newItems;
		return items;
	}
	
	private void addNewLogItem(BatchLogItem logItem)
	{

			em.getTransaction().begin();
			em.persist(logItem);
			batchLog.addItem(logItem);
			em.getTransaction().commit();
		
	}
	
	private void addNewLogItemVolume(BatchLogItem logItem, BatchLogItemVolume logItemVolume)
	{
		em.getTransaction().begin();
		em.persist(logItemVolume);
		logItem.addItemVolume(logItemVolume);
		em.getTransaction().commit();
	}
	
	
	private void update(Object log)
	{
		em.getTransaction().begin();
		em.merge(log);
		em.getTransaction().commit();
	}
	
	private String splitSuffix(String name)
	{
//		int dot = name.lastIndexOf('.');
		int dot = name.indexOf('.');
//		String extension = (dot == -1) ? "" : name.substring(dot+1);
		String base = (dot == -1) ? name : name.substring(0, dot);
		return base;
	}
	
	
	private String getParentId(ModsMetadata md)
	{
		String parentId = "";
		for(ModsRelatedItem ri : md.getRelatedItems())
		{
			if(ri.getParentId_010() != "" && ri.getParentId_010() != null)
				parentId = ri.getParentId_010();
		}
		return parentId;
	}
	
	public static void ftpLogout(FTPClient ftp)
	{
        try {
			ftp.logout();
		} catch (IOException e) {
			logger.error("FTP logout error: " + e.getMessage());
		}
	    finally {
	    	logger.info("FTP disconnected");
	        if(ftp.isConnected()) {
	          try {
	            ftp.disconnect();
	          } catch(IOException ioe) {
	          }
	        }

	      }
	}
	
	public boolean deleteDir(File dir) throws Exception{
		
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
			    // The directory is now empty so delete it
		return dir.delete();
			
		
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public int getFinishedItems() {
		return finishedItems;
	}

	public void setFinishedItems(int finishedItems) {
		this.finishedItems = finishedItems;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}


	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public HashMap<String, BatchIngestItem> getItemsForBatchIngest() {
		return itemsForBatchIngest;
	}

	public void setItemsForBatchIngest(HashMap<String, BatchIngestItem> itemsForBatchIngest) {
		this.itemsForBatchIngest = itemsForBatchIngest;
	}

	public HashMap<String, BatchIngestItem> getErrorItems() {
		return errorItems;
	}

	public void setErrorItems(HashMap<String, BatchIngestItem> errorItems) {
		this.errorItems = errorItems;
	}
	public String getMab() {
		return mab;
	}
	public void setMab(String mab) {
		this.mab = mab;
	}
	public String getTei() {
		return tei;
	}
	public void setTei(String tei) {
		this.tei = tei;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}

	public List<BatchIngestItem> getItems() {
		return items;
	}


	public void setItems(List<BatchIngestItem> items) {
		this.items = items;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public boolean isFtp() {
		return ftp;
	}


	public void setFtp(boolean ftp) {
		this.ftp = ftp;
	}	
	
}
