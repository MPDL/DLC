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


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
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
    private String footerSnippet;


	//TODO help_page
    //public static final String HELP_PAGE_DE = "help/dlc_help_de.html";
    //public static final String HELP_PAGE_EN = "help/dlc_help_en.html";
    
    
    private ContextServiceBean contextServiceBean = new ContextServiceBean();
   
    private OrganizationalUnitServiceBean ouServiceBean = new OrganizationalUnitServiceBean();
    
    private List<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();

//    private HashMap<String, Integer> uploadThreads = new HashMap<String, Integer>();
    
    private HashMap<Long, Thread> uploadThreads = new HashMap<Long, Thread>();
    
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


	
    	} catch (NamingException ex) {
			logger.error("Error retriving VolumeSrviceBean: " + ex.getMessage());
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
	    	this.footerSnippet = PropertyReader.getProperty("dlc.footer.snippet");
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

	public HashMap<Long, Thread> getUploadThreads() {
		return uploadThreads;
	}

	public void setUploadThreads(HashMap<Long, Thread> uploadThreads) {
		this.uploadThreads = uploadThreads;
	}

	public String getFooterSnippet() {
		return footerSnippet;
	}

	public void setFooterSnippet(String footerSnippet) {
		this.footerSnippet = footerSnippet;
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
