package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.list.AllVolumesBean;
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


    
}

