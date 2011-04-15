package de.mpg.mpdl.dlc;

import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.mpg.mpdl.dlc.xmldb.DBXML;
import de.mpg.mpdl.dlc.xmldb.DBXMLBean;
import de.mpg.mpdl.dlc.xmldb.DBXMLLocal;
import de.mpg.mpdl.dlc.xmldb.DBXMLRemote;

public class Ejb3Test {
	
	@EJB
	private static DBXML bean;

	public Ejb3Test() {}

	public static void main(String[] args) {
		
		Ejb3Test test = new Ejb3Test();
		test.doit();
		
	}
	
	public void doit()
	{
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		props.setProperty("java.naming.provider.url", "localhost:1099");  
		//InitialContext ctx = new InitialContext(props);
		//DBXMLRemote bean = (DBXMLRemote)ctx.lookup("DBXMLBean/remote");
		System.out.println(bean.getEnv());
		System.out.println(bean.getMgr());
		System.out.println(bean.getCnt());
	}

}
