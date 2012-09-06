package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
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
    private ApplicationBean appBean = new ApplicationBean();
    
    private static String logoUrl;
    private static String logoLink;
    private static String logoTlt;
    private static String latestCql = null;
    
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

	  public String getLogoUrl()
	    {    	
	    	
	    	//Reset url on common pages to default dlc logo
	    	String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
	    	if (viewId.equals("/Welcome.xhtml")
	    			|| viewId.equals("/volumes.xhtml")
	    			|| viewId.equals("/ingest.xhtml")
	    			|| viewId.equals("/advancedSearch.xhtml")
	    			||viewId.equals("/admin.xhtml"))
//	    			||viewId.equals("/ou.xhtml"))
	    		{logoUrl = "";
	    		setLogoTlt("DLC");} 
	    	if (logoUrl == null || logoUrl.equals("")) 
	    		{return "/resources/images/solution_logo_216_75.png";}
	    	else return logoUrl;
	    }
	    
	    
	    public void setLogoUrl(String url)
	    {
	    	logoUrl = url;
	    }
	    
	    public String getLogoLink()
	    {    	  	
	    	//Reset link on common pages to default dlc logo
	    	String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
	    	if (viewId.equals("/Welcome.xhtml")
	    			|| viewId.equals("/volumes.xhtml")
	    			|| viewId.equals("/ingest.xhtml")
	    			|| viewId.equals("/advancedSearch.xhtml")
	    			||viewId.equals("/admin.xhtml")
	    			||viewId.equals("/ou.xhtml"))
	    		{logoLink = "";} 
	    	if (logoLink == null || logoLink.equals("")) 
	    		{return appBean.getDomain()+"/"+appBean.getContextPath()+"/";}
	    	return logoLink;
	    }
	    
	    
	    public void setLogoLink(String id)
	    {	
	    	String url = appBean.getDomain() + "/"+appBean.getContextPath()+"/ou/" + id;
	    	logoLink = url;
	    }
	    
	    public String getLogoTlt()
	    {
	    	//Reset tlt on common pages to default dlc logo
	    	String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
	    	if (viewId.equals("/Welcome.xhtml")
	    			|| viewId.equals("/volumes.xhtml")
	    			|| viewId.equals("/ingest.xhtml")
	    			|| viewId.equals("/advancedSearch.xhtml")
	    			||viewId.equals("/admin.xhtml")
	    			||viewId.equals("/ou.xhtml"))
	    		{logoTlt = "";} 
	    	if (logoTlt == null || logoTlt.equals("")) 
	    		{logoTlt = InternationalizationHelper.getTooltip("main_home").replace("$1", "DLC");}
	    	return logoTlt;
	    }
	    
	    public void setLogoTlt(String tlt)
	    {
	    	logoTlt = tlt;
	    }
	    
	    
	    public String getLatestCql() {
			return latestCql;
		}

		public void setLatestCql(String cql) {
			latestCql = cql;
		}
    
}

