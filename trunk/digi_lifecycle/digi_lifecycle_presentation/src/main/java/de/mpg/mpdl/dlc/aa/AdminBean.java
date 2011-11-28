package de.mpg.mpdl.dlc.aa;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.jboss.logging.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.vo.organization.DLCMetadata;
import de.mpg.mpdl.dlc.vo.organization.EscidocMetadata;
import de.mpg.mpdl.dlc.vo.organization.FoafOrganization;
import de.mpg.mpdl.dlc.vo.organization.Organization;

@ManagedBean
@SessionScoped
@URLMapping(id="admin", pattern = "/admin", viewId = "/admin.xhtml", onPostback=false)
public class AdminBean {
	private static Logger logger = Logger.getLogger(AdminBean.class);
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{applicationBean}")
	private ApplicationBean applicationBean;
	
	@EJB
	private OrganizationalUnitServiceBean ouServiceBean;

	
	private Organization orga = new Organization();
	
	public AdminBean() throws Exception
	{
		createNewOrganization();
	}
	
	private void createNewOrganization() {

		FoafOrganization foafOU = new FoafOrganization();
		DLCMetadata dlc = new DLCMetadata();
		dlc.setFoafOrganization(foafOU);
		EscidocMetadata escidoc = new EscidocMetadata();
		this.orga.setDlcMd(dlc);
		this.orga.setEscidocMd(escidoc);
		this.orga.setId(null);
	}
	
	public String create() throws Exception
	{       
		ouServiceBean.createOU(orga, loginBean.getUserHandle());
		loginBean.getUser().setCreatedOUs( ouServiceBean.retrieveOUsCreatedBy(loginBean.getUserHandle(), loginBean.getUser().getId()));
		return "pretty:admin";
	}



	public Organization getOrga() {
		return orga;
	}

	public void setOrga(Organization orga) {
		this.orga = orga;
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
	
	
	
	
	
}
