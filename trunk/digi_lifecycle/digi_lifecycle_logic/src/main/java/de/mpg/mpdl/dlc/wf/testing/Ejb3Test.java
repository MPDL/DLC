package de.mpg.mpdl.dlc.wf.testing;

import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlManager;

import de.mpg.mpdl.dlc.dbxml.DBXML;


public class Ejb3Test {
	
	private static XmlContainer c = null;
	private static XmlManager mgr = null;
	private static Environment env = null;
	static DBXML bean;
	
	public static void main(String[] args) {
		
		Ejb3Test test = new Ejb3Test();
		try {
			test.doit();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			bean.closeXmlContainer(c);
			bean.closeXmlManager(mgr);
			
		}
		
	}
	
	public void doit() throws NamingException, DatabaseException
	{
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		props.setProperty("java.naming.provider.url", "localhost:1099");  
		InitialContext ctx = new InitialContext(props);
		bean = (DBXML)ctx.lookup("digi_lifecycle_logic_ear-1.0-SNAPSHOT/DBXMLBean/remote");

		String containerName = "DLC_METS";
		env = bean.getEnvironment("/home/frank/data/dlc/dbxml");
		mgr = bean.getXmlManager(env);
		c = bean.getXmlContainer(mgr, containerName);
		System.out.println("The container " + c.getName() + " holds " + c.getNumDocuments() + " documents");
	}

}
