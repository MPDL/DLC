package de.mpg.mpdl.dlc.batchIngest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.SocketException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import de.mpg.mpdl.dlc.util.BatchIngestLogs;
import de.mpg.mpdl.dlc.vo.BatchIngestItem;

public class FTPClientTest {
	
	private static FTPClient ftp;
	private static  String server = "134.76.8.5";
	private static  String proxyUser = "dlcadmin";
	private static  String proxyPassword = "+2jZyzuD";
	private static String mab = "DLC_Test/Discursos/mab";
	private static String tei = "DLC_Test/Discursos/tei";
	private static String images = "DLC_Test/Discursos/images";
	
	public static FTPClient ftpLogin(String server, String username, String password) throws IOException
	{
		ftp = new FTPClient();
		long start = System.currentTimeMillis();
		System.out.println("ftpLogin");
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode()))
		{
			ftp.connect(server, 21);
			System.out.println("connect to server: "+server + " : "+ftp.getReplyCode());	 
		}
//        ftpClient.setDataTimeout(6000000); // 100 minutes
//        ftpClient.setConnectTimeout(6000000); // 100 minutes
		ftp.setControlKeepAliveTimeout(1000);
        ftp.setControlEncoding("UTF-8");
        
		ftp.login(username, password);
        if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
        	ftp.disconnect();
	        System.err.println("FTP server refused connection.");
	    }
		long time = System.currentTimeMillis()-start;
		System.err.println("Time FTP Login: " + time);
        
		return ftp;
	}
	
	public static void report(String process)
	{
		System.out.println("------------------------");
        System.err.println(process + " - isConnected: " + ftp.isConnected());
        System.err.println(process + " - isAvailable: " + ftp.isAvailable());
        System.err.println(process + " - isPositiv: " + FTPReply.isPositiveCompletion(ftp.getReplyCode()));
        System.err.println(process + " - isProtected: " + FTPReply.isProtectedReplyCode(ftp.getReplyCode()));
        System.err.println(process +  " - ReplayCode: " + ftp.getReplyCode());
        System.err.println(process + " - ReplyString: " + ftp.getReplyString());
        System.out.println("------------------------");
	}
	
	public static void main(String[] args) {
	
//	    String server = "files.kewerner.name";
//	    String proxyUser = "f006c3b4";
//	    String proxyPassword = "SooFEPykq3DQM6EF";
//	    String mab = "/mab";
//	    String tei = "/tei";
//	    String images = "/tif";
		
		

	    
	    try {
			ftpLogin(server, proxyUser,proxyPassword);
			report("login");
		    ftp.logout();
		    report("logout");
		    ftp.disconnect();
		    report("disconnect");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

	    

        ftpImagesRead(images);
	    


		/*
		File tei = new File("C://Users//yu//AppData//Local//Temp//c5c68232-29a6-4411-b4d2-15974aa978af//47363.tei.xml");
		try {
			File teiFileWithPbConvention = applyPbConventionToTei(new FileInputStream(tei));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	    
	    
//	    System.setProperty("javax.net.debug", "ssl");


	    
//	    String mab = "MAB";
//	    String tei = "";
//	    String images = "JPEG";
//	    ftpCheck(server, proxyUser, proxyPassword, mab, tei, images);
	
		/*
		
		String server = "mpibftp2.mpib-berlin.mpg.de";
	    String proxyUser = "dlcingtest";
	    String proxyPassword = "DLC102012!";  
	    
	    String mab = "/MAB";
	    String image = "/JPEG";
	    String localDic = "C:/Users/yu/Desktop/PH240819/";
	    String protocol = "SSL"; // TLS / null (SSL)
	    int port = 21000;
	    int timeoutInMillis = 10000;
	    boolean isImpicit = false;

	    FTPSClient client = new FTPSClient(protocol, isImpicit);

	    client.setDataTimeout(timeoutInMillis);
//	    client.addProtocolCommandListener(new PrintCommandListener(new  PrintWriter(System.out)));

	    System.out.println("################ Connecting to Server ################################");

	    try
	    {
	        int reply;
	        System.out.println("################ Connect Call ################################");
	        client.connect(server, 21000);
	        System.err.println("connect " + client.getReplyCode());


	        client.login(proxyUser, proxyPassword);
	        System.err.println("login " + client.getReplyCode());

	        System.out.println("################ Login Success ################################");

	        //client.setFileType(FTP.BINARY_FILE_TYPE);
	        client.setFileType(FTP.NON_PRINT_TEXT_FORMAT);
	        client.execPBSZ(0);  // Set protection buffer size
	        client.execPROT("P"); // Set data channel protection to private
	        client.enterLocalPassiveMode();
	        FTPClient ftp = client;
			System.err.println("ftp = " + ftp.getReplyCode());
//	        System.out.println("Connected to " + server + ".");
	        reply = client.getReplyCode();

	        if (!FTPReply.isPositiveCompletion(reply))
	        {
	            client.disconnect();
	            System.err.println("FTP server refused connection.");
	            System.exit(1);
	        }

	        client.listFiles();
	        System.err.println("print1 = " + client.getReplyCode());

			FTPFile[] mabs = ftp.listFiles(mab);

			for(FTPFile m : mabs)
			{
		
				if(m.isDirectory())
					continue;
				String name = splitSuffix(m.getName());
				
				String dlcDirectory = localDic+ UUID.randomUUID().toString();
				new File(dlcDirectory).mkdir();
				File tFile = new File(dlcDirectory + "/"+ m.getName());
				FileOutputStream out = new FileOutputStream(tFile);
				ftp.retrieveFile( mab + "/" + m.getName(), out);
				out.flush();
				out.close();
			}
			
			FTPFile[] images = ftp.listFiles(image);
			

			for(FTPFile id : images)
			{

				if(id.isDirectory() && !".".equals(id.getName()) && !"..".equals(id.getName()))
				{
					String name = id.getName();
					String imagesDirectory = image + "/" + name;
					String dlcDirectory = localDic+ UUID.randomUUID().toString();
					new File(dlcDirectory).mkdir();
					
					FTPFile[] filesList =ftp.listFiles(imagesDirectory);
					for(FTPFile i : filesList)
					{
						if(i.isDirectory())
							continue;
						String n = splitSuffix(i.getName());
						

						File tFile = new File(dlcDirectory + "/"+ i.getName());
						FileOutputStream out = new FileOutputStream(tFile);
						ftp.retrieveFile( imagesDirectory + "/" + i.getName(), out);
						out.flush();
						out.close();
					}


					
				}
			}
			
	        
	    }
	    catch (Exception e)
	    {
	        if (client.isConnected())
	        {
	            try
	            {
	                client.disconnect();
	            }
	            catch (IOException ex)
	            {
	                ex.printStackTrace();
	            }
	        }
	        System.err.println("Could not connect to server.");
	        e.printStackTrace();
	        return;
	    }
	    finally
	    {
	        //client.disconnect();
	        try {
				client.logout();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println("# client disconnected");
	    }
	    
	    */
	}
	    

	
	
	public static File applyPbConventionToTei(InputStream teiXml)throws Exception
	{
		
			File xslt = new File("C://Projects//virr//digi_lifecycle//digi_lifecycle_presentation//src//main//resources//xslt//teiToMets//tei_apply_pb_convention.xslt");
			
//			SAXSource xsltSource = new SAXSource(new InputSource(s));
			
			
			Source teiXmlSource = new StreamSource(teiXml);
			Source xsltSource = new StreamSource(xslt);

			StringWriter wr = new StringWriter();
			File temp = File.createTempFile("tei_with_pb_convention", "xml");
			javax.xml.transform.Result result = new StreamResult(temp);
			TransformerFactory transfFact = TransformerFactory.newInstance();

			Transformer transformer = transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return temp;
		
	}
	
	public static boolean ftpCheck(String server, String proxyUser, String proxyPassword, String mab, String tei, String images)
	{
		HashMap<String, BatchIngestItem> itemsForBatchIngest = new HashMap<String, BatchIngestItem>();
		HashMap<String, BatchIngestItem> errorItems = new HashMap<String, BatchIngestItem>();

		FTPClient ftp = new FTPClient();
		try {

	        ftpLogin(ftp, server, proxyUser, proxyPassword);
	        ftpCheckImages(itemsForBatchIngest, ftp, images);
	        if(!"".equals(tei))
	        	ftpReadTeiFiles(itemsForBatchIngest, errorItems, ftp, tei);
	        System.out.println("stop");
		} catch (Exception e) {

			e.printStackTrace();
		}
		finally
		{
			ftpLogout(ftp);
			List<String> dirs = new ArrayList<String>();
			
			for(BatchIngestItem item : itemsForBatchIngest.values())
			{
				dirs.add(item.getDlcDirectory());
			}
			itemsForBatchIngest.clear();
			System.gc();
			for(String d : dirs)
			{
				File dir = new File(d);
				try {
					System.out.println(deleteDir(dir));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return true;
	}
	
	
	public static void ftpReadTeiFiles(HashMap<String, BatchIngestItem> items, HashMap<String, BatchIngestItem> errorItems, FTPClient ftp, String directory)
	{
		try {
//			ftp.changeWorkingDirectory("/DLC_Test");
//			
//			ftp.changeWorkingDirectory("/Discursos");
//			ftp.changeWorkingDirectory("/tei");
			FTPFile[] teiList = ftp.listFiles("/DLC_Test/Discursos/images");
			System.out.println(teiList.length);
			for(FTPFile tmpFile : teiList)
			{
				if(tmpFile.isDirectory())
					continue;
//				String dlcDirectory = System.getProperty("java.io.tmpdir")+"\"+ UUID.randomUUID().toString();
				String name = splitSuffix(tmpFile.getName());
				String dlcDirectory = "C://Users//yu//AppData//Local//Temp//"+ UUID.randomUUID().toString();
				new File(dlcDirectory).mkdir();
				File tFile = new File(dlcDirectory + "//"+ tmpFile.getName());
				FileOutputStream out = new FileOutputStream(tFile);
				ftp.retrieveFile(directory+"/"+tmpFile.getName(), out);
//				InputStream is = ftp.retrieveFileStream(directory+"/"+tmpFile.getName());
//				byte buf[] = new byte[1024];
//				int len;
//				while((len=is.read(buf))>0)
//					out.write(buf,0,len);
//					out.close();
//					is.close();
				BatchIngestItem item = items.get(name);
				if(item == null)
				{
					item = new BatchIngestItem();
					item.setName(name);
					item.setTeiFile(tFile);
					item.setDlcDirectory(dlcDirectory);
					String errorMessage = BatchIngestLogs.SINGLE_TEI_ERROR;
//					logger.error(errorMessage + name);
					item.getLogs().add(errorMessage);
					errorItems.put(tFile.getName(), item);
				}
				else
				{
					item.setDlcDirectory(dlcDirectory);
					item.setTeiFile(tFile);
 
				}
				


			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void ftpCheckImages(HashMap<String, BatchIngestItem> items, FTPClient ftp, String directory)
	{
		try {
			// Poll for files.
			FTPFile[] ds = ftp.listFiles(directory);

			for(FTPFile d : ds)
			{
			
				if(d.isDirectory() && !".".equals(d.getName()) && !"..".equals(d.getName()))
				{
					String name = d.getName();
					String imagesDirectory = directory + "/" + name;
					BatchIngestItem newItem = new BatchIngestItem();
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
							footerNr ++;
						imageNr ++;
					}
					newItem.setFooterNr(footerNr);
					if(footerNr > 1)
					{
						String e = BatchIngestLogs.MULTIFOOTER_ERROR;
						newItem.getLogs().add(e);
					}
					newItem.setImageNr(imageNr);
					items.put(name, newItem);
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void ftpsCheckImages(HashMap<String, BatchIngestItem> items, FTPSClient ftps, String directory) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
	{
		try {
			// Poll for files.
			if(ftps == null)
				ftps = ftpsLogin(ftps, "mpibftp2.mpib-berlin.mpg.de/MAB", "dlcingtest", "DLC102012!");
			System.out.println(ftps.getReplyString());
			directory = "MAB";
			System.out.println(directory);
			FTPClient ftp = ftps;
//			ftps.execPROT("E");
//			ftps.execPBSZ(0);
			System.err.println(ftps.pwd());
			System.err.println(ftps.pasv());
			String local = "C:/Users/yu/Desktop/515621.mab.xml";
			InputStream input;
			input = new FileInputStream(local);
			ftp.storeFile(directory, input);
			System.out.println(ftp.list(directory));
			System.out.println(ftps.list());

			input.close();
//			FTPFile[] ds = ftp.listFiles(directory);
//			for(FTPFile d : ds)
//			{
//		
//				System.out.println(d.getRawListing());
//				if(d.isDirectory() && !".".equals(d.getName()) && !"..".equals(d.getName()))
//				{
//					String name = d.getName();
//					String imagesDirectory = directory + "/" + name;
//					BatchIngestItem newItem = new BatchIngestItem();
//					newItem.setName(name);
//					newItem.setImagesDirectory(imagesDirectory);
//					
//					FTPFile[] filesList = ftps.listFiles(imagesDirectory);
//					int imageNr = 0;
//					int footerNr = 0;					
//					for(FTPFile tmpFile : filesList)
//					{
//						if(tmpFile.isDirectory())
//							continue;
//
//						if(tmpFile.getName().startsWith("footer"))
//							footerNr ++;
//						imageNr ++;
//					}
//					newItem.setFooterNr(footerNr);
//					if(footerNr > 1)
//					{
//						String e = Consts.MULTIFOOTER;
//						newItem.getErrorMessage().add(e);
//					}
//					newItem.setImageNr(imageNr);
//					items.put(name, newItem);
//					
//				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static String splitSuffix(String name)
	{
		int dot = name.lastIndexOf('.');
//		String extension = (dot == -1) ? "" : name.substring(dot+1);
		String base = (dot == -1) ? name : name.substring(0, dot);
		return base;
	}
	
	public static void ftpFileRead(FTPClient ftp, String directory)
	{
		try {
			FTPFile[] filesList = ftp.listFiles(directory);
			for(FTPFile tmpFile : filesList)
			{
				if(tmpFile.isDirectory())
					continue;

				File f = new File(System.getProperty("java.io.tmpdir")+ tmpFile.getName());
				FileOutputStream fileOut = new FileOutputStream(f);
				ftp.retrieveFile(tmpFile.getName(), fileOut);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
	
	public static void ftpImagesRead(String directory)
	{
		try {
			// Poll for files.


	        FTPFile[] ds = ftp.listFiles(directory);

			for(FTPFile d : ds)
			{
			
				if(d.isDirectory() && !".".equals(d.getName()) && !"..".equals(d.getName()))
				{
					String pathname = '/'+ d.getName();
					
					FTPFile[] filesList = ftp.listFiles(directory + pathname);
					
					for(FTPFile tmpFile : filesList)
					{
						if(tmpFile.isDirectory())
							continue;
						System.out.println(pathname + " : " + tmpFile.getName());
					}
					
				}
			}
		} catch (IOException e) {
			try{
				System.err.println("retry");
	        	ftpLogin(server, proxyUser, proxyPassword);
		        FTPFile[] ds = ftp.listFiles(directory);
	
				for(FTPFile d : ds)
				{
				
					if(d.isDirectory() && !".".equals(d.getName()) && !"..".equals(d.getName()))
					{
						String pathname = '/'+ d.getName();
						
						FTPFile[] filesList = ftp.listFiles(directory + pathname);
						
						for(FTPFile tmpFile : filesList)
						{
							if(tmpFile.isDirectory())
								continue;
							System.out.println(pathname + " : " + tmpFile.getName());
						}
						
					}
				}
			}catch(Exception e1)
			{
				System.out.println(e1.getMessage());
			}
		}
		
		
	}
	
	
	public static boolean ftpLogin(FTPClient ftp, String server, String proxyUser, String proxyPassword)
	{
		

	    try {
			long start = System.currentTimeMillis();
	        ftp.connect(server, 21);
			System.out.println("connect server: "+ftp.getReplyCode());	        
//	        ftpClient.setDataTimeout(6000000); // 100 minutes
//	        ftpClient.setConnectTimeout(6000000); // 100 minutes
			ftp.setControlKeepAliveTimeout(1000);
//	        ftpClient.setControlEncoding("UTF-8");
	        ftp.login(proxyUser, proxyPassword);
			long time = System.currentTimeMillis()-start;
			System.err.println("Time FTP Login: " + time);
			System.out.println("login: "+ftp.getReplyCode());			
	        // After connection attempt, you should check the reply code to verify  success.
	        if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
	          ftp.disconnect();
	          System.err.println("FTP server refused connection.");
	        }
	        return true;
	      } catch(IOException e) 
	      {
	        e.printStackTrace();
	        return false;
	      } 
	   
	}
	

	
	public static boolean ftpsCheck(String server, String proxyUser, String proxyPassword, String mab, String tei, String images)
	{
		HashMap<String, BatchIngestItem> itemsForBatchIngest = new HashMap<String, BatchIngestItem>();
		HashMap<String, BatchIngestItem> errorItems = new HashMap<String, BatchIngestItem>();

		FTPSClient ftps = null;
		try {

	        ftps = ftpsLogin(ftps, server, proxyUser, proxyPassword);
	        ftpsCheckImages(itemsForBatchIngest, ftps, images);
//	        if(!"".equals(tei))
//	        	ftpsReadTeiFiles(itemsForBatchIngest, errorItems, ftps, tei);

	        
	        System.out.println("stop");
		} catch (Exception e) {

			e.printStackTrace();
		}
		finally
		{
			ftpsLogout(ftps);
			List<String> dirs = new ArrayList<String>();
			
			for(BatchIngestItem item : itemsForBatchIngest.values())
			{
				dirs.add(item.getDlcDirectory());
			}
			itemsForBatchIngest.clear();
			System.gc();
			for(String d : dirs)
			{
				File dir = new File(d);
				try {
					System.out.println(deleteDir(dir));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return true;
	}
	
	public static FTPSClient ftpsLogin(FTPSClient ftps, String server, String proxyUser, String proxyPassword) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException
	{

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(null, null);
		KeyManager km = kmf.getKeyManagers()[0];
//		ftps = new FTPSClient("SSL");
		ftps = new FTPSClient("SSL");
		ftps.setKeyManager(km);

		boolean error = false;
	    try {
	        int reply;
	        System.err.println(server);
	        ftps.connect(server, 21000);
//	        ftps.connect(server);
	        
	        ftps.setDataTimeout(6000000); // 100 minutes
	        ftps.setConnectTimeout(6000000); // 100 minutes
	        ftps.setControlEncoding("UTF-8");
	        ftps.login(proxyUser, proxyPassword);
	        
	        System.out.println("Connected to " + server + ".");
	        System.out.print(ftps.getReplyString());

	        // After connection attempt, you should check the reply code to verify  success.
	        reply = ftps.getReplyCode();

	        if(!FTPReply.isPositiveCompletion(reply)) {
	          ftps.disconnect();
	          System.err.println("FTP server refused connection.");
	        }
	      } catch(IOException e) 
	      {
	        error = true;
	        e.printStackTrace();
	      } 

	    return ftps;
	   
	}
	
	public static void ftpLogout(FTPClient ftp)
	{
        try {
        	if(ftp.isConnected());
				ftp.logout();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    finally {
	        if(ftp.isConnected()) {
	          try {
	            ftp.disconnect();
	          } catch(IOException ioe) {
	            // do nothing
	          }
	        }

	      }
	}
	
	public static void ftpsLogout(FTPSClient ftps)
	{
        try {
			ftps.logout();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    finally {
	        if(ftps.isConnected()) {
	          try {
	            ftps.disconnect();
	          } catch(IOException ioe) {
	            // do nothing
	          }
	        }

	      }
	}
	
	public static boolean deleteDir(File dir) throws Exception{
	
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
		    
	


	
	
}
