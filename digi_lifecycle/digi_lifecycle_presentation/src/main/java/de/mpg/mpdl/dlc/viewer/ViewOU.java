package de.mpg.mpdl.dlc.viewer;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.jboss.logging.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.vo.organization.Organization;


@ManagedBean
@SessionScoped
@URLMapping(id = "ou", viewId = "/ou.xhtml", pattern = "/ou/#{viewOU.id}")
public class ViewOU {

	
	private static Logger logger = Logger.getLogger(ViewOU.class);
	
	private String id;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private OrganizationalUnitServiceBean ouServiceBean;
	
	private Organization orga;
	
	@URLAction(onPostback=false)
	public void loadOu()
	{  
		try{
			if(orga==null || !id.equals(orga.getId()))
			{
				this.orga = ouServiceBean.retrieveOrganization(id);
				logger.info("load ou " + id);
			}
		}catch(Exception e){
			logger.error("Problem with loading ou", e);
		}
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}



	public Organization getOrga() {
		return orga;
	}

	public void setOrga(Organization orga) {
		this.orga = orga;
	}

	public String save() throws Exception
	{  
		ouServiceBean.updateOU(orga, loginBean.getUserHandle());
		this.orga = update(orga.getId());
		return "pretty:ou";
	}

	public Organization update(String id) throws Exception
	{
		return ouServiceBean.retrieveOrganization(id);
	}
	
}
