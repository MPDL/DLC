/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.list.AllVolumesBean;
import de.mpg.mpdl.dlc.search.AdvancedSearchBean;
import de.mpg.mpdl.dlc.search.AdvancedSearchResultBean;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;

@ManagedBean
@SessionScoped
public class SessionBean implements Serializable
{
    private static Logger logger = Logger.getLogger(SessionBean.class);
    private List<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
    private List<Volume> startpageVolumes = new ArrayList<Volume>();
    //private AllVolumesBean avb = new AllVolumesBean();
    
    @ManagedProperty("#{applicationBean}")
    private ApplicationBean appBean;
	private AdvancedSearchBean advancedSearchBean = new AdvancedSearchBean();
    
    private String logoUrl;
    private String logoLink;
    private String logoTlt;
    private String latestCql = null;
    
	public List<Volume> getStartpageVolumes() {
		return startpageVolumes;
	}

	public void setStartpageVolumes(List<Volume> startpageVolumes) {
		this.startpageVolumes = startpageVolumes;
	}

	public SessionBean()
    { 
        try {

			OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(null);
			SearchRetrieveRequestType request = new SearchRetrieveRequestType();
			
			//this.startpageVolumes = avb.getStartPageVolumes();
			
			client.retrieveOrganizationalUnitsAsList(request);
        } catch (Exception e) {
			logger.error("Error getting ous",e);
		} 

    }
	
	private Boolean useDLCLogo()
	{	//Reset url on common pages to default dlc logo
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
    	
    	if (viewId.equals("/Welcome.xhtml")
    		|| viewId.equals("/ingest.xhtml")
    		|| viewId.equals("/advancedSearch.xhtml")
    		|| viewId.equals("/advancedSearchResult.xhtml")
    		|| viewId.equals("/admin.xhtml"))
    	{	
    		return true;
    	} 
    	else 
    	{
    		return false;
    	}
    	
	}
	
	public String getLogoUrl()
	{    	
		if (useDLCLogo())
		{
			logoUrl = "";
			setLogoTlt("DLC");
		}
		
		if (logoUrl == null || logoUrl.equals("")) 
		{
			return "/resources/images/logo-dlc_150x286.png";
		}
		else return logoUrl;
	}
	
	public void setLogoUrl(String url)
	{
		logoUrl = url;
	}
	
	public String getLogoLink()
	{    	  	
	    if (useDLCLogo())
		{
	    	logoLink = "";
		}
	    if (logoLink == null || logoLink.equals(""))
	    {
	    	return getAppBean().getDomain()+"/"+getAppBean().getContextPath()+"/";
	    }
	    return logoLink;
	}
	
	public void setLogoLink(String id)
	{
		String url = getAppBean().getDomain() + "/"+getAppBean().getContextPath()+"/ou/" + id;
		logoLink = url;
	}
	
	public String getLogoTlt()
	{
    	if (useDLCLogo())
		{
    		logoTlt = "";
		}
    	if (logoTlt == null || logoTlt.equals(""))
    	{
    		logoTlt = InternationalizationHelper.getTooltip("main_home").replace("$1", "DLC");
    	}
    	return logoTlt;
	}
	
	public void setLogoTlt(String tlt)
	{
		logoTlt = tlt;
	}
	
	public String getLatestCql() 
	{
		return latestCql;
	}
	
	public void setLatestCql(String cql) 
	{
		latestCql = cql;
	}
	
	public ApplicationBean getAppBean() 
	{
		return appBean;
	}
	
	public void setAppBean(ApplicationBean appBean) 
	{
		this.appBean = appBean;
	}
	
	public String startLatestSearch()
	{
		return advancedSearchBean.startSearch();
	}
    
}

