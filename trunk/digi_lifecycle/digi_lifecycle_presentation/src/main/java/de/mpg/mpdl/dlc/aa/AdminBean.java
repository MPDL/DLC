package de.mpg.mpdl.dlc.aa;


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
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.UserAccountServiceBean;
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
	
	@EJB
	private UserAccountServiceBean uaServiceBean;

	
	private Organization orga;
	private Organization newEmptyOrga;
	private Collection collection = new Collection();
	private User user = new User();
	
	private List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();

	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
	
	private String editOrgaId;

	


	public AdminBean() throws Exception
	{
		this.newEmptyOrga = initOrganization();
//		initCollection();
	}
	
	public Organization initOrganization() {  
		Organization o = new Organization();
		FoafOrganization foafOU = new FoafOrganization();
		DLCMetadata dlc = new DLCMetadata();
		dlc.setFoafOrganization(foafOU);
		EscidocMetadata escidoc = new EscidocMetadata();
		o.setDlcMd(dlc);
		o.setEscidocMd(escidoc);
		return o;
	}
	public void initCollection()
	{
		DLCAdminDescriptor dlcAD = new DLCAdminDescriptor();
		this.collection.setDlcAD(dlcAD);
		
	}

	@PostConstruct
	public void init()
	{           
		this.ouSelectItems.clear();
		this.contextSelectItems.clear();

		for(Grant grant: loginBean.getUser().getGrants())
		{ 
			try
			{   
				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")))
				{ 
					for(Organization orga : loginBean.getUser().getCreatedOrgas())
					{
						this.ouSelectItems.add(new SelectItem(orga.getId(), orga.getEscidocMd().getTitle()));
					}
				}else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
				{  
					this.ouSelectItems.add(new SelectItem(loginBean.getUser().getOu().getObjid(),loginBean.getUser().getOu().getProperties().getName()));
			  		
					for(Context context : contextServiceBean.retrieveOUContexts(loginBean.getUser().getOu()))
						this.contextSelectItems.add(new SelectItem(context.getObjid()+"|"+context.getProperties().getName(), context.getProperties().getName()));
				}
			}catch(Exception e)
			{
				logger.error("Error init seleteItem ", e);
			}
		}   

	}
 	
	public String editOrga() throws Exception
	{
        
		if(editOrgaId != null && editOrgaId.equals(orga.getId()))
		{
			ouServiceBean.updateOU(orga, loginBean.getUserHandle());
			this.orga = ouServiceBean.retrieveOrganization(orga.getId());
			applicationBean.setOus(ouServiceBean.retrieveOUs());
			this.editOrgaId = "";
			init();
		}
		else
		{
			OrganizationalUnit ou = ouServiceBean.createNewOU(orga, loginBean.getUserHandle());
			applicationBean.getOus().add(ou);
			Organization o = ouServiceBean.retrieveOrganization(ou.getObjid());
			loginBean.getUser().getCreatedOrgas().add(ouServiceBean.retrieveOrganization(ou.getObjid()));
			init();
		}
		return "pretty:admin";
	}
	
	public String closeOrganization() throws Exception
	{  
		OrganizationalUnit ou = ouServiceBean.closeOU(orga.getId(), loginBean.getUserHandle());
		applicationBean.getOus().remove(ou);
		loginBean.getUser().getCreatedOrgas().remove(orga); 
		init();
		return "pretty:admin";
	}
	
	public String createNewContext() throws Exception
	{
		Context c = contextServiceBean.createNewContext(collection, loginBean.getUserHandle());
		loginBean.getUser().getCreatedCollections().add(contextServiceBean.retrieveCollection(c.getObjid(), loginBean.getUserHandle()));
		init();
		return "pretty:admin";
	}
	
	public String closeCollection(Collection collection) throws Exception
	{
		Context c = contextServiceBean.closeContext(collection.getId(), loginBean.getUserHandle());
		loginBean.getUser().getCreatedCollections().remove(collection);
		init();
		return "pretty:admin";
	}
	   
	public String createNewUser() throws Exception
	{      
		UserAccount ua = uaServiceBean.createNewUserAccount(user, loginBean.getUserHandle());
		loginBean.getUser().getCreatedUsers().add(uaServiceBean.retrieveUserById(ua.getObjid(),loginBean.getUserHandle()));
		return "pretty:admin";
	}
	
	public String deleteUser(User user) throws Exception
	{
		String id = uaServiceBean.deleteUserAccount(user.getId(), loginBean.getUserHandle());
		loginBean.getUser().getCreatedUsers().remove(user);
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

	public List<SelectItem> getOuSelectItems() {
		return ouSelectItems;
	}

	public void setOuSelectItems(List<SelectItem> ouSelectItems) {
		this.ouSelectItems = ouSelectItems;
	}

	public List<SelectItem> getContextSelectItems() {
		return contextSelectItems;
	}

	public void setContextSelectItems(List<SelectItem> contextSelectItems) {
		this.contextSelectItems = contextSelectItems;
	}  

	public String getEditOrgaId() {
		return editOrgaId;
	}

	public void setEditOrgaId(String editOrgaId) {
		this.editOrgaId = editOrgaId;
	}
	
	public Organization getNewEmptyOrga() {
		return newEmptyOrga;
	}

	public void setNewEmptyOrga(Organization newEmptyOrga) {
		this.newEmptyOrga = newEmptyOrga;
	}
	
	
	
}
