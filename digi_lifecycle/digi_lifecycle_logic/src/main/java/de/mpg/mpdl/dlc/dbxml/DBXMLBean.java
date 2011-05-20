package de.mpg.mpdl.dlc.dbxml;

import java.io.File;
import java.io.FileNotFoundException;

import javax.ejb.Stateless;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.LockDetectMode;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlTransaction;

@Stateless
public class DBXMLBean implements DBXMLRemote, DBXMLLocal {

	private static final long serialVersionUID = 1L;
	
	private Environment environment = null;
	private EnvironmentConfig environmentConfig = null;
	private XmlManager xmlManager= null;
	private XmlContainer xmlContainer= null;
	
	@Override
	public Environment getEnvironment(String pathToEnv) throws DatabaseException
	{
		environmentConfig = new EnvironmentConfig();
		environmentConfig.setAllowCreate(true);
		environmentConfig.setInitializeCache(true);
		environmentConfig.setInitializeLocking(true);
		environmentConfig.setInitializeLogging(true);
		//environmentConfig.setRunRecovery(true);
		environmentConfig.setTransactional(true);
		environmentConfig.setTxnMaxActive(1000);
		environmentConfig.setMaxLocks(10000);
		environmentConfig.setMaxLockers(10000);
		environmentConfig.setMaxLockObjects(10000);
		environmentConfig.setLockDetectMode(LockDetectMode.MINWRITE);
		environmentConfig.setLogAutoRemove(true);
		
		try {
			environment = new Environment(new File(pathToEnv), environmentConfig);
			return environment;
		} catch (FileNotFoundException fnfe) {
			System.err.println("openening Environment: " + fnfe.toString());
			System.exit(-1);
		}
		return environment;
	}
	
	@Override
	public Environment getEnvironment(String pathToEnv, EnvironmentConfig envCfg) throws DatabaseException
	{
		try {
			environment = new Environment(new File(pathToEnv), envCfg);
			return environment;
		} catch (FileNotFoundException fnfe) {
			System.err.println("openening Environment: " + fnfe.toString());
			System.exit(-1);
		}
		return environment;
	}

	@Override
	public EnvironmentConfig getEnvironmentConfig()
	{
		environmentConfig = new EnvironmentConfig();
		environmentConfig.setAllowCreate(true);
		environmentConfig.setInitializeCache(true);
		environmentConfig.setInitializeLocking(true);
		environmentConfig.setInitializeLogging(true);
		environmentConfig.setTransactional(true);
		environmentConfig.setTxnMaxActive(1000);
		environmentConfig.setMaxLocks(10000);
		environmentConfig.setMaxLockers(10000);
		environmentConfig.setMaxLockObjects(10000);
		environmentConfig.setLockDetectMode(LockDetectMode.MINWRITE);
		environmentConfig.setLogAutoRemove(true);
		return environmentConfig;
	}
	
	@Override
	public XmlManager getXmlManager(Environment env) throws XmlException
	{
		XmlManagerConfig xmlManagerConfig = new XmlManagerConfig();
		xmlManagerConfig.setAdoptEnvironment(true);
		xmlManagerConfig.setAllowAutoOpen(true);
		xmlManagerConfig.setAllowExternalAccess(true);
		xmlManager = new XmlManager(env, xmlManagerConfig);
		return xmlManager;
	}
	
	@Override
	public void closeXmlManager(XmlManager manager)
	{
		if (manager != null) {
			try {
				manager.close();
			} catch (XmlException xe) {
				System.err.println("closing Environment: manager: "
						+ xe.toString());
				xe.printStackTrace();
			}
		}
	}
	
	@Override
	public XmlContainer getXmlContainer(XmlManager manager, String containerName) throws XmlException
	{
		XmlContainerConfig containerConfig = new XmlContainerConfig();
		containerConfig.setTransactional(true);
		containerConfig.setAllowCreate(true);
		xmlContainer = manager.openContainer(containerName, containerConfig);
		return xmlContainer;
	}
	
	@Override
	public void deleteXmlContainer(XmlManager manager, String containerName) throws XmlException
	{
		XmlTransaction xmlTransaction = manager.createTransaction();
		manager.removeContainer(xmlTransaction, containerName);
	}
	
	@Override
	public void closeXmlContainer(XmlContainer container)
	{
		if (container != null) {
			try {
				container.close();
			} catch (XmlException xe) {
				System.err.println("closing Environment: container: "
						+ xe.toString());
				xe.printStackTrace();
			}
		}
	}
}
