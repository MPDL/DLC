package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.LockDetectMode;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;

public class DBXMLDAO {

	public static Environment getEnvironment(String path) throws DatabaseException {
		Environment environment = null;
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setInitializeCache(true);
		envConfig.setInitializeLocking(true);
		envConfig.setInitializeLogging(true);
		//envConfig.setRunRecovery(true);
		envConfig.setTransactional(true);
		envConfig.setTxnMaxActive(1000);
		envConfig.setMaxLocks(10000);
		envConfig.setMaxLockers(10000);
		envConfig.setMaxLockObjects(10000);
		envConfig.setLockDetectMode(LockDetectMode.MINWRITE);
		envConfig.setLogAutoRemove(true);

		try {
			environment = new Environment(new File(path), envConfig);
			return environment;
		} catch (FileNotFoundException fnfe) {
			System.err.println("openening Environment: " + fnfe.toString());
			System.exit(-1);
		}
		return null;

	}

	public static XmlManager getManager(Environment environment) throws XmlException {
		XmlManager xmlManager = null;
		XmlManagerConfig managerConfig = new XmlManagerConfig();
		managerConfig.setAdoptEnvironment(true);
		managerConfig.setAllowAutoOpen(true);
		managerConfig.setAllowExternalAccess(true);
		xmlManager = new XmlManager(environment, managerConfig);
		return xmlManager;
	}

	public static XmlContainer getContainer(XmlManager manager, String name)
			throws XmlException {
		XmlContainer xmlContainer = null;
		XmlContainerConfig containerConfig = new XmlContainerConfig();
		containerConfig.setTransactional(true);
		containerConfig.setAllowCreate(true);
		xmlContainer = manager.openContainer(name, containerConfig);
		return xmlContainer;
	}

	public static void close(XmlContainer container, XmlManager manager) {
		if (container != null) {
			try {
				container.close();
			} catch (XmlException xe) {
				System.err.println("closing Environment: container: "
						+ xe.toString());
				xe.printStackTrace();
			}
		}
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
}
