/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.aa;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.client.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.client.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.UserAccountServiceBean;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.collection.DLCAdminDescriptor;
import de.mpg.mpdl.dlc.vo.organization.DLCMetadata;
import de.mpg.mpdl.dlc.vo.organization.EscidocMetadata;
import de.mpg.mpdl.dlc.vo.organization.FoafOrganization;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.user.User;

@ManagedBean
@ViewScoped
@URLMapping(id="admin", pattern = "/admin", viewId = "/admin.xhtml", onPostback=true)
public class AdminBean{
	private static Logger logger = Logger.getLogger(AdminBean.class);
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{applicationBean}")
	private ApplicationBean applicationBean;
	
	
	private OrganizationalUnitServiceBean ouServiceBean = new OrganizationalUnitServiceBean();

	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();

	private UserAccountServiceBean uaServiceBean = new UserAccountServiceBean();

	
	private Organization orga;
	private Organization newEmptyOrga;
	private Collection collection;
	private Collection newEmptyCollection;
	private User user;
	private User newEmptyUser;
	
	private List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();

	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
	
	private String editOrgaId="";
	
	private String editUserId="";
	
	private String editCollectionId="";


	
	public AdminBean() throws Exception
	{
		this.orga = initOrganization();
		this.newEmptyOrga = initOrganization();
		this.collection = initCollection();
		this.newEmptyCollection = initCollection();
		this.user = new User();
		this.newEmptyUser = new User();
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
	
	public Collection initCollection()
	{
		Collection c = new Collection();
		DLCAdminDescriptor dlcAD = new DLCAdminDescriptor();
		c.setDlcAD(dlcAD);
		return c;
	}

	@PostConstruct
	public void init()
	{      
		if(loginBean == null || loginBean.getUser() == null)
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			try {
				String dlc_URL = PropertyReader.getProperty("dlc.instance.url") + "/" + PropertyReader.getProperty("dlc.context.path") ;
				fc.getExternalContext().redirect(dlc_URL);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		else
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
				  		
						for(Context context : contextServiceBean.retrieveOUContexts(loginBean.getUser().getOu().getObjid(), true))
							this.contextSelectItems.add(new SelectItem(context.getObjid(), context.getProperties().getName()));
					}
				}catch(Exception e)
				{
					logger.error("Error init seleteItem ", e);
				}
			}   
		}

	}
 	
	public String editOrga() throws Exception
	{
        
		if(editOrgaId != null && editOrgaId.equals(orga.getId()))
		{
			ouServiceBean.updateOU(orga, loginBean.getUserHandle());
			applicationBean.setOus(ouServiceBean.retrieveOUs());
			loginBean.getUser().setCreatedOrgas(ouServiceBean.retrieveOrgasCreatedBy(loginBean.getUserHandle(), loginBean.getUser().getId()));
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
	
	public String deleteOrganization() throws Exception
	{  
		OrganizationalUnit ou = ouServiceBean.deleteOU(orga.getId(), loginBean.getUserHandle());
		applicationBean.getOus().remove(ou);
		loginBean.getUser().getCreatedOrgas().remove(orga); 
		init();
		return "pretty:admin";
	}
	
	public String editContext()
	{
		if(collection.getName().equals("") || collection.getDescription().equals(""))
		{
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_admin_collection_validation"));
			Collection editCo= contextServiceBean.retrieveCollection(editCollectionId, loginBean.getUserHandle());
			loginBean.getUser().getCreatedCollections().remove(collection);
			loginBean.getUser().getCreatedCollections().add(editCo);
			return "pretty:admin";
		}
		if(editCollectionId != null && editCollectionId.equals(collection.getId()))
		{
			try {
				contextServiceBean.updateContext(collection, loginBean.getUserHandle());
			} catch (ContextNameNotUniqueException e) 
			{ 
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_admin_collection_name_exists"));
				
			}

			Collection editCo= contextServiceBean.retrieveCollection(editCollectionId, loginBean.getUserHandle());
			loginBean.getUser().getCreatedCollections().remove(collection);
			loginBean.getUser().getCreatedCollections().add(editCo);
			return "pretty:admin";
		} 
		else
		{
			Context c = null;
			try { 
				c = contextServiceBean.createNewContext(collection, loginBean.getUserHandle());
			} catch (ContextNameNotUniqueException e) {
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_admin_collection_name_exists"));
				return "pretty:admin";
			}
			if(c != null )
			{
				loginBean.getUser().getCreatedCollections().add(contextServiceBean.retrieveCollection(c.getObjid(), loginBean.getUserHandle()));		
				init();
			}
		}
		return "pretty:admin";
	}
	 
	public String closeCollection() throws Exception
	{
		Context c = contextServiceBean.closeContext(collection.getId(), loginBean.getUserHandle());
		loginBean.getUser().getCreatedCollections().remove(collection);
		init();
		return "pretty:admin";
	}
	
	
	public String deleteCollection() throws Exception
	{
		Context c = contextServiceBean.deleteContext(collection.getId(), loginBean.getUserHandle());
		loginBean.getUser().getCreatedCollections().remove(collection);
		init();
		return "pretty:admin";
	}
	   
	public String editUser() throws Exception
	{           

		if(editUserId !=null && editUserId.equals(user.getId()))
		{      
			if(user.getName().equals("") || user.getLoginName().equals(""))
			{
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_admin_user_edit_validation"));
				return "";
			}	
			try
			{
				uaServiceBean.updateUserAccount(user, loginBean.getUserHandle());
				
			}
			catch(UniqueConstraintViolationException e)
			{
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_admin_user_loginname_exists"));
				
			}
			User editUser = uaServiceBean.retrieveUserById(user.getId(), loginBean.getUserHandle());
			loginBean.getUser().getCreatedUsers().remove(user);
			loginBean.getUser().getCreatedUsers().add(editUser);
			return "";
		}
		else
		{
			if(user.getName().equals("") || user.getLoginName().equals("") || user.getPassword().equals(""))
			{
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_admin_user_validation"));
				return "";
			}
			UserAccount ua = null;
			try{
				ua = uaServiceBean.createNewUserAccount(user, loginBean.getUserHandle());
			}catch(UniqueConstraintViolationException e)
			{
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_admin_user_loginname_exists"));
				return "";
			}
			if(ua != null)
				loginBean.getUser().getCreatedUsers().add(uaServiceBean.retrieveUserById(ua.getObjid(),loginBean.getUserHandle()));
		}
		return "";
	}
	
	public String deleteUser() throws Exception
	{
		String id = uaServiceBean.deleteUserAccount(user.getId(), loginBean.getUserHandle());
		loginBean.getUser().getCreatedUsers().remove(user);
		return "";
	}
	
	public String deactivateUser() throws Exception
	{
		UserAccount ua = uaServiceBean.deactivateUserAccount(user.getId(), loginBean.getUserHandle());
		user.setUserAccount(ua);
		//loginBean.getUser().getCreatedUsers().remove(user);
		return "";
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

	public Collection getNewEmptyCollection() {
		return newEmptyCollection;
	}

	public void setNewEmptyCollection(Collection newEmptyCollection) {
		this.newEmptyCollection = newEmptyCollection;
	}

	public User getNewEmptyUser() {
		return newEmptyUser;
	}

	public void setNewEmptyUser(User newEmptyUser) {
		this.newEmptyUser = newEmptyUser;
	}

	public String getEditUserId() {
		return editUserId;
	}  

	public void setEditUserId(String editUserId) {
		//this.user = uaServiceBean.retrieveUserById(editUserId, loginBean.getUserHandle());
		this.editUserId = editUserId;
	}

	public String getEditCollectionId() {
		return editCollectionId;
	}

	public void setEditCollectionId(String editCollectionId) {
		this.editCollectionId = editCollectionId;
	}



	
	

}
