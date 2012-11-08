package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.list.AllVolumesBean;
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
    private AllVolumesBean avb = new AllVolumesBean();
    
    @ManagedProperty("#{applicationBean}")
    private ApplicationBean appBean;
    
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
			
			this.startpageVolumes = avb.getStartPageVolumes();
			
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
			return "/resources/images/solution_logo_216_75.png";
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
    
}

