package de.mpg.mpdl.dlc.batchIngest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.util.Consts;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.BatchIngestItem;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsRelatedItem;



public class IngestLog 

{

    /**
     * enum to describe if something went wrong with this element.
     * 
     * - CHECK:     check the batch ingest data
     * - STOPPED:   ingest can not be started because validation failed
     * - STARTED:   data correct, ingest started, in progress
     * - FINISHED:  data correct, ingest completed

     */
	public enum Step
	{
		CHECK, STOPPED, STARTED, FINISHED 
	}
	
    /**
     * enum to describe the general state of the log.
     */
    public enum Status
    {
        PENDING, RELEASED, ROLLBACK
    }
	
    /**
     * enum to describe if something went wrong with this element.
     * 
     * - FINE:      everything is alright
     * - PROBLEM:   import worked, but something could have been done better
     * - ERROR:   items can not be imported because of validation failure
     * - FATAL:     data correct, because of interruption due to system errors can not be completed
     */
    public enum ErrorLevel
    {
        FINE, PROBLEM, ERROR, FATAL
    }
    
    private static Logger logger = Logger.getLogger(IngestLog.class);
    
	private String name;
    private Step step;
	private Status status;
	private ErrorLevel errorLevel;
	private Date startDate;
	private Date endDate;
	private String userId;
    private String contextId;
	private int finishedItems;
	private int totalItems;
	
    private int logId;
    private int id;
	private String userHandle;
    private String message;
    
	private String mab;
	private String tei;
	private String images;
	
	private String server;
	private String username;
	private String password;
	
	HashMap<String, BatchIngestItem> itemsForBatchIngest = new HashMap<String, BatchIngestItem>();
	HashMap<String, BatchIngestItem> errorItems = new HashMap<String, BatchIngestItem>();
	
	private VolumeServiceBean volumeService = new VolumeServiceBean();
	
	private Object currentLog = new Object();
	private Object currentItem = new Object();
	private Object currentItemVolume = new Object();

	
    private Connection connection;
    
    private FTPClient ftp;

    private List<BatchIngestItem> items = new ArrayList<BatchIngestItem>();
    
    private SessionExtenderTask seTask;


    /**
     * The data format that is used to display start- and end-date.
     * Example: 2009-12-31 23:59
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public IngestLog()
    {
    	
    }


	public IngestLog(String name, Step step, String action, ErrorLevel errorLevel, String userId, String contextId, String userHandle, String server, String username, String password, String images, String mab, String tei) 
	{
		this.name = name;
    	this.step = step;
    	try {
			if(action.equalsIgnoreCase("save"))
				this.status = Status.PENDING;
			else
				this.status = Status.RELEASED;
		} catch (Exception e) {
			// TODO Auto-generat
			e.printStackTrace();

		}
    	this.errorLevel = errorLevel;
		this.startDate = new Date();
		this.userId = userId;
		this.contextId = contextId;
		this.userHandle = userHandle;
		this.server = server;
		this.username = username;
		this.password = password;
		this.images = images;
		this.mab = mab;
		this.tei = tei;
		
		this.connection = getConnection();
		this.ftp = ftpLogin(server, username, password);
		
		this.seTask = new SessionExtenderTask(userHandle, userId);
		
		saveLog();
	}
	
	public static FTPClient ftpLogin(String server, String username, String password)
	{
		FTPClient ftp = new FTPClient();
		try{
			int reply;
			ftp.connect(server);
			
	        ftp.setDataTimeout(600000); // 10 minutes
	        ftp.setConnectTimeout(600000); // 10 minutes
	        ftp.setControlEncoding("UTF-8");

			ftp.login(username, password);
			
	        System.out.println("Connected to " + server + ".");
	        System.out.print(ftp.getReplyString());
	        // After connection attempt, you should check the reply code to verify  success.
	        reply = ftp.getReplyCode();
	        if(!FTPReply.isPositiveCompletion(reply)) {
		          ftp.disconnect();
		          System.err.println("FTP server refused connection.");
		          }
		}catch(Exception e)
		{
			logger.error("ftpLogin error" + e.getMessage());
		}
		return ftp;
	}
	
	public static Connection getConnection()
	{
		String url;

		Connection connection = null;
		try{
			Properties props = new Properties();
			props.setProperty("user",PropertyReader.getProperty("dlc.batch_ingest.database.admin_user.name"));
			props.setProperty("password",PropertyReader.getProperty("dlc.batch_ingest.database.admin_user.password"));
			Class.forName("org.postgresql.Driver");
			url = PropertyReader.getProperty("dlc.batch_ingest.database.connection.url");
			connection = DriverManager.getConnection(url, props);

			URL sqlURL = IngestLog.class.getClassLoader().getResource("batch_ingest_init.sql");

		    //File file = new File(sqlURL.toURI());
		    StringBuilder fileContents = new StringBuilder();
		    Scanner scanner = new Scanner(sqlURL.openStream());
		    String lineSeparator = System.getProperty("line.separator");
		    String dbScript;

		    try {
		        while(scanner.hasNextLine())        
		            fileContents.append(scanner.nextLine() + lineSeparator);
		       
		        dbScript = fileContents.toString();
		    } finally {
		        scanner.close();
		    }

	        String[] queries = dbScript.split(";");
            for (String query : queries)
            {
                logger.debug("Executing statement: " + query);
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
            }
		}
		catch(Exception e)
		{
			logger.error("cannot set up Batch Ingest Databae"+e.getMessage());
		}
		return connection;
	}
	
	public static void closeConnection(Connection connection)
	{
		try {
			connection.close();
		} catch (SQLException e) {
			logger.error("cannot close database connection", e);
		}
	}
	
	private int saveLog()
	{
		synchronized(currentLog){
			try
	        {
	            PreparedStatement statement = this.connection.prepareStatement("insert into dlc_batch_ingest_log "
	                    + "(name, step, status, errorlevel, startdate, user_id, context_id) "
	                    + "values (?, ?, ?, ?, ?, ?, ?)");
	            
	            statement.setString(1, this.name);
	            statement.setString(2, this.step.toString());
	            statement.setString(3, this.status.toString());
	            statement.setString(4, this.errorLevel.toString());
	            statement.setTimestamp(5, new Timestamp(this.startDate.getTime()));
	            statement.setString(6, this.userId);
	            statement.setString(7, this.contextId);
	            statement.executeUpdate();
	            //statement.close();
	            
	            statement = this.connection.prepareStatement("select max(id) as maxid from dlc_batch_ingest_log");
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next())
	            {
	                this.logId = resultSet.getInt("maxid");
	                resultSet.close();
	                statement.close();
	            }
	            else
	            {
	                resultSet.close();
	                statement.close();
	                throw new RuntimeException("Error saving log");
	            }
	        }
	        catch (Exception e)
	        {
	            throw new RuntimeException("Error saving log", e);
	        }
		return logId;
		}
	}
	
	/**
	 * 
	 * @param name database key name
	 * @param value database key value
	 */
	public void updateLog(String name, String value)
	{
		try
		{
            if(connection == null)
            {
            	connection = getConnection();
            }
			PreparedStatement statement = this.connection.prepareStatement("UPDATE dlc_batch_ingest_log SET " + name +" = '" + value + "' WHERE id = "+ logId);
            statement.executeUpdate();
            statement.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error while uploading log", e);
		}
	}
	
	public boolean ftpCheck()
	{ 
		this.itemsForBatchIngest.clear();
		this.errorItems.clear();

		seTask.start();
		
		System.err.println("ftpCheck result " + (errorItems.size() == 0));
		
		try {
			if(!"230".equals(ftp.getReplyString()))
				ftp = ftpLogin(this.server, this.username, this.password);
					
			ftpCheckImages(itemsForBatchIngest, ftp, this.images);
		
			if(!"".equals(tei))
			{
				ftpReadTeiFiles(itemsForBatchIngest, errorItems, ftp, this.tei);
			}
			
 
			ftpReadMabFiles(itemsForBatchIngest, errorItems, ftp, this.mab);

			int totalItems = itemsForBatchIngest.size() + errorItems.size();
			updateLog("total_items", Integer.toString(totalItems));
			
			if(itemsForBatchIngest.size() >0 )
			{
				itemsForBatchIngest = saveLogItems(itemsForBatchIngest, ErrorLevel.FINE);
			}
			if(errorItems.size() > 0)
			{
				errorItems = saveLogItems(errorItems, ErrorLevel.ERROR);
			}
		
		} catch (Exception e) {
			updateLog("errorlevel", ErrorLevel.ERROR.toString());
			updateLog("step", Step.STOPPED.toString());
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_batch_ingest_stop"));
		}
//		finally
//		{  
//			seTask.stop();
//			ftpLogout(ftp);
//			List<String> dirs = new ArrayList<String>();
//			
//			if(itemsForBatchIngest.size()>0)
//			{
//				for(BatchIngestItem item : itemsForBatchIngest.values())
//				
//					dirs.add(item.getDlcDirectory());
//			
//			}
//			if(errorItems.size()>0)
//			{
//				for(BatchIngestItem item : errorItems.values())
//				{
//					dirs.add(item.getDlcDirectory());
//				}
//			}   
//			itemsForBatchIngest.clear();
//			System.gc();
//			for(String d : dirs)
//			{
//				File dir = new File(d);
//				try {
//					deleteDir(dir);
//				} catch (Exception e) {
//					logger.error("Error while deleting local Batch Ingest Data " + e.getMessage());
//					e.printStackTrace();
//				}
//			}
//		}    
		System.err.println("ftpCheck result " + (errorItems.size() == 0));
		return errorItems.size()==0;
	}
	
	public String clear()
	{
		updateLog("enddate", new Date().toString());
		seTask.stop();
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode()))
			ftpLogout(ftp);
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
	
	public String updateDB()
	{
		updateLog("errorlevel", ErrorLevel.ERROR.toString());
		updateLog("step", Step.STOPPED.toString());
//		MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_batch_ingest_stop"));
		return "";
	}
	
	public String ftpSaveItems()
	{
		updateLog("step", Step.STARTED.toString());
		updateLog("startdate", new Date().toString());
		saveItems(itemsForBatchIngest);
		updateLog("step", Step.FINISHED.toString());
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

	
	private void ftpCheckImages(HashMap<String, BatchIngestItem> items, FTPClient ftp, String directory) 
	{
   
		try {
			// Poll for files.
			if(!"230".equals(ftp.getReplyString()))
				ftp = ftpLogin(this.server, this.username, this.password);
				
			FTPFile[] ds = ftp.listFiles(directory);
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
					
					FTPFile[] filesList = ftp.listFiles(imagesDirectory);
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
						String e = Consts.MULTIFOOTER;
						newItem.getErrorMessage().add(e);
					}
					newItem.setImageNr(imageNr);
					items.put(name, newItem);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void ftpReadTeiFiles(HashMap<String, BatchIngestItem> items, HashMap<String, BatchIngestItem> errorItems, FTPClient ftp, String directory)
	{
		
		try{
			if(!"230".equals(ftp.getReplyString()))
				ftp = ftpLogin(this.server, this.username, this.password);
			FTPFile[] filesList = ftp.listFiles(directory);
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
					ftp.retrieveFile(directory+"/"+tmpFile.getName(), out);
					out.flush();
					out.close();
					item.setTeiFile(tFile);
					String errorMessage = Consts.TEIWITHOUTIMAGESERROR;
					logger.error(errorMessage + name);
					item.getErrorMessage().add(errorMessage);
					errorItems.put(tFile.getName(), item);
				}
				else 
				{
					String dlcDirectory = item.getDlcDirectory();
					File tFile = new File(dlcDirectory + "/"+ tmpFile.getName());
					FileOutputStream out = new FileOutputStream(tFile);
					ftp.retrieveFile(directory+"/"+tmpFile.getName(), out);
					out.flush();
					out.close();
					item.setTeiFile(tFile);

					logger.info("read tei for " + name);
					try {
						//InputStream teiIs = new FileInputStream(tFile);
						VolumeServiceBean.validateTei(new StreamSource(tFile));
						List<XdmNode> pbList = VolumeServiceBean.getAllPbs(new StreamSource(tFile));
						item.setImageFiles(sortImagesByTeiFile(item.getImageFiles(), pbList));
						int numberOfTeiPbs = pbList.size();
						System.out.println(numberOfTeiPbs);
						int numberOfImages = item.getImageNr();
						if(numberOfTeiPbs != numberOfImages)
						{
							String errorMessage = Consts.PBSNOTEQUALTOIMAGESERROR + "("+ numberOfTeiPbs + " != " + numberOfImages + ")";
							logger.error(errorMessage + name);
							item.getErrorMessage().add(errorMessage);
							items.remove(name);
							errorItems.put(name, item);
						}
					} catch (Exception e) {
						String errorMessage = Consts.TEISYNTAXERROR;
						logger.error(errorMessage + name);
						item.getErrorMessage().add(errorMessage);
						items.remove(name);
						errorItems.put(name, item);
					} 
	
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
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
	

	
	private void ftpReadMabFiles(HashMap<String, BatchIngestItem> items, HashMap<String, BatchIngestItem> errorItems, FTPClient ftp, String directory) throws Exception
	{
		
//		ftp.changeWorkingDirectory(directory);

		HashMap<String, BatchIngestItem> multiVolumes = new HashMap<String, BatchIngestItem>();
		HashMap<String, BatchIngestItem> volumes = new HashMap<String, BatchIngestItem>();

		if(!"230".equals(ftp.getReplyString()))
			ftp = ftpLogin(this.server, this.username, this.password);
		FTPFile[] filesList = ftp.listFiles(directory);
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
				ftp.retrieveFile(directory+"/"+tmpFile.getName(), out);		
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
						items.remove(name);
						volumes.put(name, item);
						}
					} catch (Exception e) {
					String errorMessage = Consts.MABTRANSFORMERROR;
					logger.error(errorMessage , e);
					item.getErrorMessage().add(errorMessage);
					items.remove(name);
					errorItems.put(name, item);
				}
			}
			else if((item = errorItems.get(name))!= null)
			{
				String dlcDirectory = item.getDlcDirectory();
				File mFile = new File(dlcDirectory + "/"+ tmpFile.getName());
				FileOutputStream out = new FileOutputStream(mFile);
				ftp.retrieveFile(directory+"/"+tmpFile.getName(), out);	
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
					String errorMessage = Consts.MABTRANSFORMERROR;
					logger.error(errorMessage , e);
					item.getErrorMessage().add(errorMessage);

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
				ftp.retrieveFile(directory+"/"+tmpFile.getName(), out);	
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
					String errorMessage = Consts.MABTRANSFORMERROR;
					logger.error(errorMessage , e);
					item.getErrorMessage().add(errorMessage);
					errorItems.put(name, item);
				}
			}

			}
		
		
		try {
			if(multiVolumes.size() >0 || volumes.size() >0 )
				updateItemsForBatchIngest(items, errorItems, multiVolumes, volumes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void updateItemsForBatchIngest(HashMap<String, BatchIngestItem> items, HashMap<String, BatchIngestItem> errorItems, HashMap<String, BatchIngestItem> multiVolumes, HashMap<String, BatchIngestItem> volumes) throws Exception
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
					String errorMessage = Consts.MULTIVOLUMEWITHOUTVOLUMEWRROR;
					multivolume.getErrorMessage().add(errorMessage);
					errorItems.put(name, multivolume);
				}
			}
			
			Iterator restVolumes = volumes.entrySet().iterator();
			while(restVolumes.hasNext())
			{
				Entry vol = (Entry) restVolumes.next();
				String name = (String) vol.getKey();
				BatchIngestItem item = (BatchIngestItem) vol.getValue();
				String errorMessage = Consts.VOLUMEWITHOUTMULTIVOLUMEERROR;
				logger.error(errorMessage);
				item.getErrorMessage().add(errorMessage);
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
				String errorMessage = Consts.VOLUMEWITHOUTMULTIVOLUMEERROR;
				logger.error(errorMessage);
				item.getErrorMessage().add(errorMessage);
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
	
	private void downloadImages(String imagesDirectory, String dlcDirectory, List<File> images, File footer)
	{
		FTPClient ftp = ftpLogin(server, username, password);
		for(File i : images)
		{
			try{
				FileOutputStream out = new FileOutputStream(i);
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				ftp.retrieveFile(imagesDirectory+"/"+ i.getName(), out);
				out.flush();
				out.close();
				logger.info("downloading image to " + dlcDirectory + " | Name: " + i.getName() + " | Size: " + i.length());

			}catch(Exception e)
			{
				logger.error("Error while copying Image from FTP Server: " + i.getName() + " .(Message): " + e.getMessage());
			}
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(footer);
			ftp.retrieveFile(imagesDirectory+"/"+ footer.getName(), out);
			out.flush();
			out.close();
			logger.info("downloading footer to " + dlcDirectory + " | Name:  " + footer.getName() + " | Size: " + footer.length());
		} catch (Exception e) {
			logger.error("Error while copying Image from FTP Server: " + footer.getName() + " .(Message): " + e.getMessage());
		}

	}
	
	private void saveItems(HashMap<String, BatchIngestItem> items)
	{
		Iterator i = items.entrySet().iterator();

		while(i.hasNext())
		{
			Entry item = (Entry) i.next();
			
			String logItemId = (String)item.getKey();
			BatchIngestItem bi = (BatchIngestItem) item.getValue();
			
			try {
			
				Date sDate = new Date();
				updateLogItem(logItemId, "startdate", sDate.toString());
				String itemId = null;
				if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.monograph.id")))
				{
					
					
					downloadImages(bi.getImagesDirectory(), bi.getDlcDirectory(), bi.getImageFiles(), bi.getFooter());
					DiskFileItem teiFile = null;

					itemId = volumeService.createNewItem(status.toString(), PropertyReader.getProperty("dlc.content-model.monograph.id"), contextId, null, userHandle, bi.getModsMetadata(), bi.getImageFiles(), bi.getFooter() !=null ? bi.getFooter() : null, bi.getTeiFile() != null ? VolumeServiceBean.fileToDiskFileItem(bi.getTeiFile()) : null, null);
					System.out.println(itemId);
					if(itemId != null)
						updateLogItem(logItemId, "item_id", itemId);
					updateLogItem(logItemId, "enddate", new Date().toString());
					if(itemId == null)
					{
						updateLogItem(logItemId, "errorlevel", ErrorLevel.ERROR.toString());
						updateLog("errorlevel", ErrorLevel.PROBLEM.toString());
					}
				}
				else if(bi.getContentModel().equals(PropertyReader.getProperty("dlc.content-model.multivolume.id")))
				{
					try{
						
						for(BatchIngestItem v : bi.getVolumes())
							downloadImages(v.getImagesDirectory(), v.getDlcDirectory(), v.getImageFiles(), v.getFooter());
						itemId = volumeService.createNewMultiVolume("save", PropertyReader.getProperty("dlc.content-model.multivolume.id"), contextId, userHandle, bi.getModsMetadata()).getItem().getObjid();
				
					}catch(Exception e)
					{
						itemId = null;
					}
					Date eDate;
					if(itemId == null)
					{
						updateLogItem(logItemId, "errorlevel", ErrorLevel.ERROR.toString());

						updateLog("errorlevel", ErrorLevel.PROBLEM.toString());
						for(BatchIngestItem vol : bi.getVolumes())
						{
							updateLogItemVolume(logItemId, vol.getName(), "message", Consts.MULTIVOLUMEROLLBACKERROR);
						}
					}
					else
					{
						ArrayList<String> volIds = new ArrayList<String>();
						for(BatchIngestItem vol : bi.getVolumes())
						{
							String volId = volumeService.createNewItem(status.toString(), PropertyReader.getProperty("dlc.content-model.volume.id"), contextId, itemId, userHandle, bi.getModsMetadata(), vol.getImageFiles(), bi.getFooter() !=null ? bi.getFooter() : null, bi.getTeiFile() !=null ? VolumeServiceBean.fileToDiskFileItem(bi.getTeiFile()) : null, null);
							eDate = new Date();
							System.out.println(volId);
							if(volId != null)
								updateLogItemVolume(logItemId, vol.getName(), sDate.toString(), volId, eDate.toString());
							if(volId == null)
							{
								updateLogItemVolume(logItemId, vol.getName(), "errorLevel", ErrorLevel.ERROR.toString());

								updateLogItem(logItemId, "message", Consts.VOLUMEROLLBACKERROR);
								updateLogItem(logItemId, "errorlevel", ErrorLevel.PROBLEM.toString());

								updateLog("errorlevel", ErrorLevel.PROBLEM.toString());
							}
							else
								volIds.add(volId);
						}
						
						itemId = volumeService.updateMultiVolumeFromId(itemId, volIds, userHandle);
						System.out.println(itemId);
						if(itemId != null)
							updateLogItem(logItemId, "item_id", itemId);
						if(status.toString().equalsIgnoreCase("release"))
							volumeService.releaseVolume(itemId, userHandle);
						eDate = new Date();
						updateLogItem(logItemId, "enddate", eDate.toString());
					}
					
				}
			} catch (Exception e) {
				updateLogItem(logItemId, "errorlevel", ErrorLevel.ERROR.toString());
				updateLog("errorlevel", ErrorLevel.ERROR.toString());
			} 
			finishedItems++;
			updateLog("finished_items", Integer.toString(finishedItems));

		}
	}
	
	/**
	 * set startDate, escidocId, endDate in the database
	 * @param logItemId Multivolume database ID
	 * @param logItemVolumeName Volume name
	 * @param startDateValue
	 * @param itemId escidoc ID
	 * @param endDateValue
	 */
	private void updateLogItemVolume(String logItemId, String logItemVolumeName, String startDateValue, String itemId, String endDateValue)
	{
		try
		{
            if(connection == null)
            {
            	connection = getConnection();
            }
			PreparedStatement statement = this.connection.prepareStatement("UPDATE dlc_batch_ingest_log_item_volume SET startdate = '" + startDateValue + "', item_id = '"+ itemId+ "', enddate = '" + endDateValue + "' WHERE log_item_id = "+ logItemId + " AND name = '" + logItemVolumeName + "'");
            statement.executeUpdate();
            statement.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error while uploading log", e);
		}
	}
	
	/**
	 * update database
	 * @param logItemId Multivolume database ID
	 * @param logItemVolumeName Volume name
	 * @param name one key name 
	 * @param value key value
	 */
	private void updateLogItemVolume(String logItemId, String logItemVolumeName, String name, String value)
	{
		try
		{
            if(connection == null)
            {
            	connection = getConnection();
            }
			PreparedStatement statement = this.connection.prepareStatement("UPDATE dlc_batch_ingest_log_item_volume SET " + name +" = '" + value + "' WHERE log_item_id = "+ logItemId + " AND name = '" + logItemVolumeName + "'");
            statement.executeUpdate();
            statement.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error while uploading log", e);
		}
	}

	
	/**
	 * update database
	 * @param logItemId
	 * @param name one key name 
	 * @param value key value
	 */
	private void updateLogItem(String logItemId, String name, String value)
	{
		try
		{
            if(connection == null)
            {
            	connection = getConnection();
            }
			PreparedStatement statement = this.connection.prepareStatement("UPDATE dlc_batch_ingest_log_item SET " + name +" = '" + value + "' WHERE id = "+ logItemId);
            statement.executeUpdate();
            statement.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error while uploading log", e);
		}
	}
	

	private HashMap<String, BatchIngestItem> saveLogItems(HashMap<String, BatchIngestItem> items, ErrorLevel errorLevel)
	{
		Iterator i = items.entrySet().iterator();
		HashMap<String, BatchIngestItem> newItems = new HashMap<String, BatchIngestItem>();

		while(i.hasNext())
		{
			Entry item = (Entry) i.next();
			
			String name = (String)item.getKey();
			BatchIngestItem bi = (BatchIngestItem) item.getValue();
			int logItemId = 0;
			try {
				logItemId = saveLogItem(logId, bi, errorLevel);
				newItems.put(Integer.toString(logItemId), bi);
				if(PropertyReader.getProperty("dlc.content-model.multivolume.id").equals(bi.getContentModel()))
				{
					for(BatchIngestItem vol : bi.getVolumes())
					{

						logItemId = saveLogItemVolume(logItemId,vol, errorLevel);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return newItems;
	}
	
	private int saveLogItem(int logId, BatchIngestItem item, ErrorLevel errorLevel)
	{
		synchronized(currentItem){
			int logItemId;
			try
	        {
	            PreparedStatement statement = this.connection.prepareStatement("insert into dlc_batch_ingest_log_item"
	            		+ " (name, errorLevel, log_id, message, content_model, images_nr, tei, footer) "
	                    + "values (?, ?, ?, ?, ?, ?, ?, ?)");
	            
	            statement.setString(1,item.getName());
	            statement.setString(2, errorLevel.toString());
	            statement.setInt(3, logId);
	            String message = "";
	            if(item.getErrorMessage()!=null)
	            {
		            for(String s: item.getErrorMessage())
		            	message += s;
	            }
	            statement.setString(4, message);
	            statement.setString(5, item.getContentModel());
	        
	            statement.setInt(6, (item.getImageFiles() != null) ? item.getImageFiles().size() : 0);
	            statement.setBoolean(7, item.getTeiFile()!=null);
	            statement.setBoolean(8, item.getFooter() != null);
	            statement.executeUpdate();
	            //statement.close();
	            
	            if(this.connection == null)
	            	this.connection = getConnection();
	            statement = this.connection.prepareStatement("select max(id) as maxid from dlc_batch_ingest_log_item");
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next())
	            {
	            	logItemId = resultSet.getInt("maxid");
	                resultSet.close();
	                statement.close();
	            }
	            else
	            {
	                resultSet.close();
	                statement.close();
	                //TODO update Log
	                throw new RuntimeException("Error saving log");
	            }
	        }
	        catch (Exception e)
	        {
	        	//TODO update Log
	            throw new RuntimeException("Error saving log", e);
	        }
		return logItemId;
		}
	}
	
	private int saveLogItemVolume(int logItemId, BatchIngestItem item, ErrorLevel errorLevel)
	{
		synchronized(currentItemVolume){
			int logItemIdVolume;
			try
	        {
	            PreparedStatement statement = this.connection.prepareStatement("insert into dlc_batch_ingest_log_item_volume"
	            		+ " (name, errorLevel, log_item_id, message, content_model, images_nr, tei, footer) "
	                    + "values (?,?, ?, ?, ?, ?, ?, ?)");
	            
	            statement.setString(1,item.getName());
	            statement.setString(2, errorLevel.toString());
	            statement.setInt(3, logItemId);
	            String message = "";
	            if(item.getErrorMessage()!=null)
	            {
		            for(String s: item.getErrorMessage())
		            	message += s;
	            }
	            statement.setString(4, message);
	            statement.setString(5, item.getContentModel());
	        
	            statement.setInt(6, (item.getImageFiles() != null) ? item.getImageFiles().size() : 0);
	            statement.setBoolean(7, item.getTeiFile()!=null);
	            statement.setBoolean(8, item.getFooter() != null);
	            statement.executeUpdate();
	            //statement.close();
	            
	            if(this.connection == null)
	            	this.connection = getConnection();
	            statement = this.connection.prepareStatement("select max(id) as maxid from dlc_batch_ingest_log_item");
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next())
	            {
	            	logItemIdVolume = resultSet.getInt("maxid");
	                resultSet.close();
	                statement.close();
	            }
	            else
	            {
	                resultSet.close();
	                statement.close();
	                //TODO update Log
	                throw new RuntimeException("Error saving log");
	            }
	        }
	        catch (Exception e)
	        {
	        	//TODO update Log
	            throw new RuntimeException("Error saving log", e);
	        }
		return logItemIdVolume;
		}
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
			e.printStackTrace();
		}
	    finally {
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
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}

	public void setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
    
	
	
	
	
	
	
}
