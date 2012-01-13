package de.mpg.mpdl.dlc.viewer;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.jboss.logging.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
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
	
	@ManagedProperty("#{applicationBean}")
	private ApplicationBean applicationBean;
	
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
	
	public String save() throws Exception
	{  
		ouServiceBean.updateOU(orga, loginBean.getUserHandle());
		this.orga = ouServiceBean.retrieveOrganization(orga.getId());
		this.orga.setEditable(false);
		applicationBean.setOus(ouServiceBean.retrieveOUs());
		loginBean.getUser().setCreatedOrgas(ouServiceBean.retrieveOrgasCreatedBy(loginBean.getUserHandle(), loginBean.getUser().getId()));
		return "pretty:ou";
	}
  
	public String edit()
	{  
		this.orga.setEditable(true);
		return "pretty:ou";
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


	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}

	public Organization getOrga() {
		return orga;
	}

	public void setOrga(Organization orga) {
		this.orga = orga;
	}
}
