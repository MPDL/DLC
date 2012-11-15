/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.dlc.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.batchIngest.IngestLog;
import de.mpg.mpdl.dlc.util.PropertyReader;

@ManagedBean
@ApplicationScoped
public class ApplicationBean
{
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(ApplicationBean.class);
    private String domain;
    private String appTitle;
    private String contextPath;
    
    private String dfgUrl = null;


    private String cmMultiVol;
    private String cmVolume;
    private String cmMono;


	//TODO help_page
    public static final String HELP_PAGE_DE = "help/dlc_help_de.html";
    public static final String HELP_PAGE_EN = "help/dlc_help_en.html";
    
    
    private ContextServiceBean contextServiceBean = new ContextServiceBean();
   
    private OrganizationalUnitServiceBean ouServiceBean = new OrganizationalUnitServiceBean();
    
    private List<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();

    private HashMap<String, Integer> uploadThreads = new HashMap<String, Integer>();
    
    private DataSource dataSource = new BasicDataSource();
    
    /**
     * Public constructor.
     */
    public ApplicationBean()
    {
        this.init();
    }

    /**
     * This method is called when this bean is initially added to application scope. Typically, this occurs as a result
     * of evaluating a value binding or method binding expression, which utilizes the managed bean facility to
     * instantiate this bean and store it into application scope.
     */
    public void init()
    {
    	try {

			InitialContext context = new InitialContext();
			this.contextServiceBean = new ContextServiceBean();
			this.ouServiceBean =  new OrganizationalUnitServiceBean();
			this.dataSource = setupDataSource();
	
    	} catch (NamingException ex) {
			logger.error("Error retriving VolumeSrviceBean: " + ex.getMessage());
		}catch (IOException e) {
			logger.error("Error setting datasource: " + e.getMessage());
		} catch (URISyntaxException e) {
			logger.error("Error setting datasource: " + e.getMessage());
			e.printStackTrace();
		}
    	
    	/*
    	this.userLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    	Iterator<Locale> supportedLocales = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
    	boolean found = false;
        while (supportedLocales.hasNext())
        { 
            Locale supportedLocale = supportedLocales.next();
            if (supportedLocale.getLanguage().equals(userLocale.getLanguage()))
            {
                found = true;
                break;
            }
        }
        if (!found)
        {
            this.userLocale = new Locale("en");
        }
        
        if (this.userLocale.getLanguage().equals("de"))
        {
            this.selectedHelpPage = "help/dlc_help_de.html";
        }
        else
        {
            this.selectedHelpPage = "help/dlc_help_en.html";
        }
        this.userLocaleString = userLocale.getLanguage();
    	*/
    	try {
			this.domain = PropertyReader.getProperty("dlc.instance.url");
	        this.contextPath = PropertyReader.getProperty("dlc.context.path");
	    	this.appTitle = PropertyReader.getProperty("dlc.app.title");
	    	this.ous = ouServiceBean.retrieveOUs();
	    	this.cmMono = PropertyReader.getProperty("dlc.content-model.monograph.id");
	    	this.cmMultiVol = PropertyReader.getProperty("dlc.content-model.multivolume.id");
	    	this.cmVolume = PropertyReader.getProperty("dlc.content-model.volume.id");
		} catch (Exception e) {
			logger.error("Cannot get OUs: " + e.getMessage());
		}      
    }
        
	public List<OrganizationalUnit> getOus() throws Exception {
		return ous;
	}

	public void setOus(List<OrganizationalUnit> ous) {

		this.ous = ous;
	}
	
	public List<Context> getContext(String ouId)
	{
		try {
			return contextServiceBean.retrieveOUContexts(ouId, false);
		} catch (Exception e) {
			return null;
		}
	}
    
    
    public String getDomain()
    {
		return domain;
    }
    
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	/**
     * Returns the title and version of the application, shown in the header.
     * 
     * @return applicationtitle, including version
     * @throws URISyntaxException 
     * @throws Exception 
     */
    public String getAppTitle() throws Exception
    {
    	return appTitle;
    }

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}
    
    /**
     * Returns the current application context.
     * 
     * @return the application context
     */
    public String getContextPath()
    {   
        return contextPath;
    }

	public void setContextPath(String contextPath)
	{
		this.contextPath = contextPath;
	}

   
	
    
    
    public String getCmMultiVol() {
		return cmMultiVol;
	}

	public void setCmMultiVol(String cmMultiVol) {
		this.cmMultiVol = cmMultiVol;
	}

	public String getCmVolume() {
		return cmVolume;
	}

	public void setCmVolume(String cmVolume) {
		this.cmVolume = cmVolume;
	}

	public String getCmMono() {
		return cmMono;
	}

	public void setCmMono(String cmMono) {
		this.cmMono = cmMono;
	}

	
    
    public String getHelpAnchor()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        String helpAnchor = "";
        if (fc.getViewRoot().getViewId() != null)
        {
            helpAnchor = fc.getViewRoot().getViewId().replace("/", "");
            helpAnchor = "#" + helpAnchor.replaceAll(".jsp", "");
        }
        return helpAnchor;
    }
    
    /*
    public static String getResource (String bundle, String name)
    {
    	try{
    	Application application = FacesContext.getCurrentInstance().getApplication();
	    InternationalizationHelper i18nHelper = (InternationalizationHelper) application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "internationalizationHelper");
	    ResourceBundle rBundle = ResourceBundle.getBundle(bundle, i18nHelper.getUserLocale());
	    return rBundle.getString(name);
    	}
    	catch(Exception e)
    	{
    		logger.warn("Value: " + name + " not found in resource bundle: " + bundle);
    		return name;
    	}
    }
    */
    
    public String getDfgUrl() {
    	if (dfgUrl == null)
    	{
    		try {
				dfgUrl = PropertyReader.getProperty("dfg.viewer.baseurl");
			} catch (Exception e) {
				e.printStackTrace();
			} 
    	}
		return dfgUrl;
	}

	public HashMap<String, Integer> getUploadThreads() {
		return uploadThreads;
	}

	public void setUploadThreads(HashMap<String, Integer> uploadThreads) {
		this.uploadThreads = uploadThreads;
	}
	
    public static DataSource setupDataSource() throws IOException, URISyntaxException {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(PropertyReader.getProperty("dlc.batch_ingest.database.admin_user.name"));
        ds.setPassword(PropertyReader.getProperty("dlc.batch_ingest.database.admin_user.password"));
        ds.setUrl(PropertyReader.getProperty("dlc.batch_ingest.database.connection.url"));
        ds.setMaxWait(30000);
        ds.setInitialSize(50);
        return ds;
    }
    
    public void setupDBTable()
    {
		Connection connection = null;
        Statement stmt = null;
		try{
			this.dataSource.getConnection();
	
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
	            stmt = connection.createStatement();
	            stmt.executeUpdate(query);
	            stmt.close();
	        }
		}catch(SQLException e) {
            e.printStackTrace();
        }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (connection != null) connection.close(); } catch(Exception e) { }
        }
    }

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
    
    

    
//    /**
//     * creates list of VirrBooks used for the tree model.
//     * 
//     * @return ArrayList of Books
//     * @throws ServiceException (SearchHandler)
//     * @throws URISyntaxException (SearchHandler)
//     * @throws Exception (XmlException)
//     */
//     
//    public String createFilterParam(List<String> ids)
//    {
//        StringBuffer param = new StringBuffer();
//        param.append("<param>");
//        for (String id : ids)
//        {
//            param.append("<filter name=\"/id\">" + id + "</filter>");
//        }
//        param.append("</param>");
//        return param.toString();
//    }
//    
//
//    /**
//     * Returns an appropriate character encoding based on the Locale defined for the current JavaServer Faces view. If
//     * no more suitable encoding can be found, return "UTF-8" as a general purpose default. The default implementation
//     * uses the implementation from our superclass, FacesBean.
//     * 
//     * @return the local character encoding
//     */
//    public String getLocaleCharacterEncoding()
//    {
//        return System.getProperty("file.encoding"); // super.getLocaleCharacterEncoding();
//    }



  


    
   
}
