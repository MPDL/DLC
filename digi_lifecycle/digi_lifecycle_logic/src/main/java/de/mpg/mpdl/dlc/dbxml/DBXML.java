package de.mpg.mpdl.dlc.dbxml;

import java.io.Serializable;

import javax.ejb.EJB;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;

public interface DBXML extends Serializable {

	Environment getEnvironment(String pathToEnv) throws DatabaseException;

	Environment getEnvironment(String pathToEnv, EnvironmentConfig envCfg) throws DatabaseException;

	EnvironmentConfig getEnvironmentConfig();

	XmlManager getXmlManager(Environment env) throws XmlException;

	void closeXmlManager(XmlManager manager);

	XmlContainer getXmlContainer(XmlManager manager, String containerName) throws XmlException;

	void deleteXmlContainer(XmlManager manager, String containerName) throws XmlException;

	void closeXmlContainer(XmlContainer container);

}
