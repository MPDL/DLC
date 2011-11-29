package de.mpg.mpdl.dlc.aa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.jboss.logging.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.collection.DLCAdminDescriptor;
import de.mpg.mpdl.dlc.vo.organization.DLCMetadata;
import de.mpg.mpdl.dlc.vo.organization.EscidocMetadata;
import de.mpg.mpdl.dlc.vo.organization.FoafOrganization;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.user.User;

@ManagedBean
@SessionScoped
@URLMapping(id="admin", pattern = "/admin", viewId = "/admin.xhtml", onPostback=true)
public class AdminBean{
	private static Logger logger = Logger.getLogger(AdminBean.class);
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{applicationBean}")
	private ApplicationBean applicationBean;
	
	@EJB
	private OrganizationalUnitServiceBean ouServiceBean;
	
	@EJB
	private ContextServiceBean contextServiceBean;

	
	private Organization orga = new Organization();
	
	private Collection collection = new Collection();
	private List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();
	
	private User user = new User();
	
	public AdminBean() throws Exception
	{
		initOrganization();
		initCollection();

	}
	
	public void initOrganization() {
		FoafOrganization foafOU = new FoafOrganization();
		DLCMetadata dlc = new DLCMetadata();
		dlc.setFoafOrganization(foafOU);
		EscidocMetadata escidoc = new EscidocMetadata();
		this.orga.setDlcMd(dlc);
		this.orga.setEscidocMd(escidoc);
	}
	public void initCollection()
	{
		DLCAdminDescriptor dlcAD = new DLCAdminDescriptor();
		collection.setDlcAD(dlcAD);
		
	}
	@PostConstruct
	public void initContexts()
	{           
//		this.ouSelectItems.clear();
		SelectItem item;
		List<String> ids = new ArrayList();
		for(Grant grant: loginBean.getUser().getGrants())
		{ 
			try
			{
				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")))
				{
					for(OrganizationalUnit ou : loginBean.getUser().getCreatedOUs())
						this.ouSelectItems.add(new SelectItem(ou.getObjid(),ou.getProperties().getName()));
				}else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
				{
					this.ouSelectItems.add(new SelectItem(loginBean.getUser().getOu().getObjid(),loginBean.getUser().getOu().getProperties().getName()));
				}
			}catch(Exception e)
			{
					
			}
		}
		this.collection.setOrgaId((String) ouSelectItems.get(0).getValue());	
	}

	
	
	public String createNewOrga() throws Exception
	{       
		ouServiceBean.createNewOU(orga, loginBean.getUserHandle());
		loginBean.getUser().setCreatedOUs( ouServiceBean.retrieveOUsCreatedBy(loginBean.getUserHandle(), loginBean.getUser().getId()));
		return "pretty:admin";
	}
	
	public String createNewContext() throws Exception
	{
		contextServiceBean.createNewContext(collection, loginBean.getUserHandle());
		loginBean.getUser().setCreatedContexts(contextServiceBean.retrieveContextsCreatedBy(loginBean.getUserHandle(), loginBean.getUser().getId()));
		return "pretty:admin";
	}
	
	public String createNewUser() throws Exception
	{
		return "pretty:admin";
	}

	public Organization getOrga() {
		return orga;
	}

	public void setOrga(Organization orga) {
		this.orga = orga;
	}
	
	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
