package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import javax.naming.InitialContext;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.UserGroupHandlerClient;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Attributes;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.GrantProperties;
import de.escidoc.core.resources.aa.useraccount.Grants;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.aa.useraccount.UserAccountProperties;
import de.escidoc.core.resources.aa.usergroup.UserGroup;
import de.escidoc.core.resources.aa.usergroup.UserGroupProperties;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.common.reference.RoleRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.user.User;



public class UserAccountServiceBean {
	private static Logger logger = Logger.getLogger(UserAccountServiceBean.class);
	
	private ContextServiceBean contextServiceBean;
	private OrganizationalUnitServiceBean ouServiceBean;
	
	public UserAccountServiceBean()
	{
		try
		{
		InitialContext context = new InitialContext();
		this.contextServiceBean = new ContextServiceBean();
		this.ouServiceBean = new OrganizationalUnitServiceBean();

		}catch(Exception e)
		{
			logger.error("Error init other ejbx " + e.getMessage());
		}
	}
	
	public UserAccount retrieveUserHandle(String handle, String userId)
	{
		UserAccount ua = null;
		logger.info("Retrieving User Account");
		try
		{
			UserAccountHandlerClient client = new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(handle);
			ua = client.retrieve(userId);
		}catch(Exception e)
		{
			logger.error("retrieve User" + e.getMessage());
		}
		return ua;
	}
	
	public User retrieveUserById(String userId,String userHandle)
	{
		User user = new User();
		logger.info("Retrieving User " + userId);
		try
		{
			UserAccountHandlerClient client = new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			UserAccount ua = client.retrieve(userId);
			
			user.setId(userId);
			user.setName(ua.getProperties().getName());
			user.setLoginName(ua.getProperties().getLoginName());
			
			for(Grant grant : client.retrieveCurrentGrants(userId))
			{
				//			user.getGrants().add(grant);
//				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")))
//				{
//					user.getGrants().add(grant);
//					user.setCreatedOrgas(ouServiceBean.retrieveOrgasCreatedBy(userHandle, userId));
//					user.setCreatedCollections(contextServiceBean.retrieveCollectionsCreatedBy(userHandle,userId));
//					user.setCreatedUsers(retrieveAllUsers(userHandle));
//				}
				
				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
	        	{
					user.getGrants().add(grant);
					Attributes attributes = client.retrieveAttributes(userId);
					String ouID = attributes.get(0).getValue();
					user.setOu(ouServiceBean.retrieveOU(ouID));
					
	//				user.setCreatedOUs(ouServiceBean.retrieveOUsCreatedBy(userId));
					user.setCreatedCollections(contextServiceBean.retrieveCollectionsCreatedBy(userHandle, userId));
					user.setCreatedUsers(retrieveUsersCreatedBy(userHandle,userId));
	        	}
				
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.depositor")))
	        	{
					user.getGrants().add(grant);
					Collection c = contextServiceBean.retrieveCollection(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getDepositorCollections().add(c);
					user.getDepositorContextIds().add(c.getId());
	        	}			
				
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.moderator")))
	        	{
					user.getGrants().add(grant);
					Collection c = contextServiceBean.retrieveCollection(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getModeratorCollections().add(c);
					user.getModeratorContextIds().add(c.getId());
	        	}
				  
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.md-editor")))
	        	{
					user.getGrants().add(grant);
					Collection c = contextServiceBean.retrieveCollection(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getMdEditorCollections().add(c);
					user.getMdEditorContextIds().add(c.getId());
	        	}
			}
		}catch(Exception e)
		{
			logger.error("Error while Retrieving User" + userId, e);
		}
		return user;
	}
	
	public User retrieveCurrentUser(String userHandle)
	{
		User user = new User();
		logger.info("Retrieving login User");
		try
		{
			UserAccountHandlerClient client = new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
	
			if(userHandle != null)
			{
				client.setHandle(userHandle);
			}
			UserAccount ua = client.retrieveCurrentUser();
			String userId = ua.getObjid();
			user.setId(userId);
			user.setName(ua.getProperties().getName());
			user.setLoginName(ua.getProperties().getLoginName());
		  	
			Grants grants = client.retrieveCurrentGrants(ua);
			for(Grant grant : grants)
			{
//				user.getGrants().add(grant);
				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")))
				{
					user.getGrants().add(grant);
					user.setCreatedOrgas(ouServiceBean.retrieveOrgasCreatedBy(userHandle, userId));
					user.setCreatedCollections(contextServiceBean.retrieveCollectionsCreatedBy(userHandle,userId));
					user.setCreatedUsers(retrieveAllUsers(userHandle));
				}
				
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
	        	{
					user.getGrants().add(grant);
					Attributes attributes = client.retrieveAttributes(userId);
					String ouID = attributes.get(0).getValue();
					user.setOu(ouServiceBean.retrieveOU(ouID));
					  
//					user.setCreatedOUs(ouServiceBean.retrieveOUsCreatedBy(userId));
					user.setCreatedCollections(contextServiceBean.retrieveCollectionsCreatedBy(userHandle, userId));
					user.setCreatedUsers(retrieveUsersCreatedBy(userHandle,userId));
	        	}
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.moderator")))
	        	{
					user.getGrants().add(grant);
					Collection c = contextServiceBean.retrieveCollection(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getModeratorCollections().add(c);
					user.getModeratorContextIds().add(c.getId());
	        	}				
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.depositor")))
	        	{
					Collection c = contextServiceBean.retrieveCollection(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getGrants().add(grant);
					user.getDepositorCollections().add(c);
					user.getDepositorContextIds().add(c.getId());
	        	}		
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.md-editor")))
	        	{   
					user.getGrants().add(grant);
					Collection c = contextServiceBean.retrieveCollection(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getMdEditorCollections().add(c);
					user.getMdEditorContextIds().add(c.getId());
	        	}
			}			

		}catch(Exception e)
		{
			logger.error("Error while Retrieving login User", e);
		}

		return user;
	}
	
	public List<User> retrieveUsersCreatedBy(String userHandle, String id)
	{
		logger.info("Retrieving DLC Users created by" + id);
		List<User> users = new ArrayList<User>();
		for(UserAccount ua : retrieveUserAccountsCreatedBy(userHandle, id))
			users.add(retrieveUserById(ua.getObjid(),userHandle));
		return users;
	}
	
	public List<User> retrieveAllUsers(String userHandle)
	{
		logger.info("Retrieving All DLC Users");
		List<User> users = new ArrayList<User>();
		for(UserAccount ua : retrieveAllUserAccounts(userHandle))
			users.add(retrieveUserById(ua.getObjid(),userHandle));
		return users;
	}
	
	public List<UserAccount> retrieveUserAccountsCreatedBy(String userHandle, String id)
	{
		logger.info("Retrieving created Users by " + id);
		List<UserAccount> uas = new ArrayList<UserAccount>();
		
		try
		{
			UserAccountHandlerClient client =  new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery("\"/properties/created-by/id\"="+id);
			SearchRetrieveResponse resp = client.retrieveUserAccounts(req);
			for(SearchResultRecord rec:resp.getRecords())
			{
				UserAccount ua= (UserAccount)rec.getRecordData().getContent();
				uas.add(ua);
			}
		}catch(Exception e)
		{
			logger.error("Error while Retrieving created Users", e);
		}
		return uas;
	}
	
	public List<UserAccount> retrieveAllUserAccounts(String userHandle)
	{
		logger.info("Retrieving all users");
		List<UserAccount> uas = new ArrayList<UserAccount>();
		
		try
		{
			UserAccountHandlerClient client =  new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery("");
			SearchRetrieveResponse resp = client.retrieveUserAccounts(req);
			for(SearchResultRecord rec:resp.getRecords())
			{
				UserAccount ua= (UserAccount)rec.getRecordData().getContent();
				uas.add(ua);
			}
		}catch(Exception e)
		{
			logger.error("Error while Retrieving all Users", e);
		}
		return uas;		
	}
	
	public UserAccount updateUserAccount(User user, String userHandle)
	{  
		logger.info("updating user account " + user.getId());
		UserAccount ua = new UserAccount();
		try
		{ 
			UserAccountHandlerClient client =  new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			ua = client.retrieve(user.getId());
			
			ua = prepareUserAccount(ua, user);

			ua = client.update(ua);
			
			TaskParam taskParam = new TaskParam();
			//update Password
			if(user.getPassword()!=null && user.getPassword() != "")
			{
				taskParam.setPassword(user.getPassword());
				taskParam.setLastModificationDate(ua.getLastModificationDate());
				client.updatePassword(ua.getObjid(), taskParam);	
			
				ua = client.retrieve(ua.getObjid());
			}
			//update Grant
			if(user.getOuId() != null )
			{
				Attribute a = client.retrieveAttributes(ua.getObjid()).get(0);
				if(!user.getOuId().equals(a.getValue()))
				{    
//					//remove old Attribute
//					client.deleteAttribute(ua.getObjid(), a.getObjid());
	 				
				a.setLastModificationDate(ua.getLastModificationDate());
				a.setValue(user.getOuId());
				client.updateAttribute(ua.getObjid(), a);
				ua = client.retrieve(ua.getObjid());
//					//set new Attribute
//					Attribute attribute = new Attribute("o", user.getOuId());
//					client.createAttribute(ua.getObjid(), attribute);
				}
			} 
			Grant grant = new Grant();
			if(user.getDepositorContextIds().size()>0 || user.getModeratorContextIds().size()>0 || user.getMdEditorContextIds().size() >0)
			{

				for(Grant g : client.retrieveCurrentGrants(ua.getObjid()))
				{
					taskParam = new TaskParam();
					taskParam.setLastModificationDate(ua.getLastModificationDate());
					client.revokeGrant(ua.getObjid(), g.getObjid(), taskParam);
				}
				
				for(String id : user.getDepositorContextIds())
				{
					String contextTitle = contextServiceBean.retrieveContext(id, null).getXLinkTitle();
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.depositor"), id, contextTitle);
					client.createGrant(ua.getObjid(), grant);
				}				
				for(String id : user.getModeratorContextIds())
				{
					String contextTitle = contextServiceBean.retrieveContext(id, null).getXLinkTitle();
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.depositor"), id, contextTitle);
					client.createGrant(ua.getObjid(), grant);
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.moderator"), id, contextTitle);
					client.createGrant(ua.getObjid(), grant);

				}
				for(String id : user.getMdEditorContextIds())
				{
					String contextTitle = contextServiceBean.retrieveContext(id, null).getXLinkTitle();
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.md-editor"), id, contextTitle);
					client.createGrant(ua.getObjid(), grant);
				}
				
				ua = client.retrieve(ua.getObjid());
			}
			
		}catch(Exception e)
		{
			logger.error("Error while Updating ua", e);
		}
		return ua;
	}
	
	
	public UserAccount createNewUserAccount(User user, String userHandle) 
	{
		logger.info("Creating new user Account");
		UserAccount ua = new UserAccount();
		ua = prepareUserAccount(ua, user);
		try
		{
			UserAccountHandlerClient client =  new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			ua = client.create(ua);
			
			//update Password
			TaskParam taskParam = new TaskParam();
			taskParam.setPassword(user.getPassword());
			taskParam.setLastModificationDate(ua.getLastModificationDate());
			client.updatePassword(ua.getObjid(), taskParam);
			
			//Set Grant
			Grant grant = new Grant();
			if(user.getOuId() != null)
			{
				grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.ou.admin"));
				client.createGrant(ua.getObjid(), grant);
				grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.context.admin"));
				client.createGrant(ua.getObjid(), grant);
				grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.userAccount.admin"));
				client.createGrant(ua.getObjid(), grant);
				
				//set Attribute
				Attribute attribute = new Attribute("o", user.getOuId());
				client.createAttribute(ua.getObjid(), attribute);
			}
			if(user.getDepositorContextIds().size()>0)
			{
				for(Collection c : user.getDepositorCollections())
				{
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.depositor"), c.getId(), c.getName());
					client.createGrant(ua.getObjid(), grant);
				}				
			}
			if(user.getModeratorContextIds().size()>0)
			{
				for(Collection c : user.getModeratorCollections())
				{
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.depositor"), c.getId(), c.getName());
					client.createGrant(ua.getObjid(), grant);
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.moderator"), c.getId(), c.getName());
					client.createGrant(ua.getObjid(), grant);
				}
			}
			if(user.getMdEditorContextIds().size() >0)
			{
				for(Collection c : user.getMdEditorCollections())
				{
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.md-editor"), c.getId(), c.getName());
					client.createGrant(ua.getObjid(), grant);
				}
			}
			
		}catch(Exception e)
		{
			logger.error("Error while creating new user", e);
		}
		return ua;
	}
	
	private Grant newGrant(Grant grant, String roleId) throws Exception {

		GrantProperties grantProperties = new GrantProperties();
	 	grantProperties.setGrantRemark("new context grant");
	 	RoleRef roleRef = new RoleRef(roleId);
	 	grantProperties.setRole(roleRef);
	 	grant.setGrantProperties(grantProperties);
	 	return grant;
	}
	
	private Grant newGrant(Grant grant, String roleId, String contextId, String contextTitle) throws Exception {

		GrantProperties grantProperties = new GrantProperties();
	 	grantProperties.setGrantRemark("new context grant");
	 	RoleRef roleRef = new RoleRef(roleId);
	 	grantProperties.setRole(roleRef);
	 	Reference ref = new ContextRef("/ir/context/"+contextId, contextTitle);
	 	
	 	grantProperties.setAssignedOn(ref);
	 	grant.setGrantProperties(grantProperties);
	 	return grant;
	}
	
    private UserAccount prepareUserAccount(UserAccount ua, User user) {

        UserAccountProperties properties = new UserAccountProperties();
        properties.setLoginName(user.getLoginName());
        properties.setName(user.getName());
        ua.setProperties(properties);

        return ua;
    }

	public String deleteUserAccount(String id, String userHandle) 
	{

		logger.info("Deleting user Account");
		try
		{
			UserAccountHandlerClient client =  new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			
			client.delete(id);

			
		}catch(Exception e)
		{
			logger.error("Error while deleting user", e);
		}
		return id;
	}
	
	public String deactivateUserAccount(String id, String userHandle)
	{
		logger.info("deaktive user Account");
		UserAccount ua;
		try
		{
			UserAccountHandlerClient client = new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			
			TaskParam taskParam = new TaskParam();
			taskParam.setComment("deactivate user account");
			ua = client.retrieve(id);
			taskParam.setLastModificationDate(ua.getLastModificationDate());
			
			client.deactivate(id, taskParam);
			
		}catch(Exception e)
		{
			
		}
		return id;
	}

	public static void main(String[] args) throws Exception {
		String userAccountID = "escidoc:15006";
        String roleId = "escidoc:role-ou-administrator";
        //String roleId = "escidoc:role-context-administrator";
        //String roleId = "escidoc:role-user-account-administrator";
//		addOUAdminRole(userAccountID, roleId);
        
        
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlc");
		UserAccountHandlerClient client = new UserAccountHandlerClient(auth.getServiceAddress());

		client.setHandle(auth.getHandle());
		Grants grants = client.retrieveCurrentGrants(userAccountID);

		for(Grant grant : grants)
			{
			System.out.println(grant.getProperties().getRole().getObjid());
//			System.out.println(grant.getProperties().getAssignedOn().getObjid());

			}
		Attributes attributes = client.retrieveAttributes(userAccountID);
		System.out.println(attributes.get(0).getValue());
	}
	
	
	private static void newUserGroup() throws Exception
	{
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlc");
		UserGroupHandlerClient ugc = new UserGroupHandlerClient(auth.getServiceAddress());
		ugc.setHandle(auth.getHandle());
		
		UserGroup ug = new UserGroup();
		
		UserGroupProperties properties = new UserGroupProperties();
        properties.setName("test");
		ug.setProperties(properties);
		
		ugc.create(ug);
		
		
		//Set Grant
		Grant grant = new Grant();

		GrantProperties grantProperties = new GrantProperties();
		
		
	 	grantProperties.setGrantRemark("new usergroup grant");
	 	RoleRef roleRef = new RoleRef("escidoc.role.userAccount.admin");
	 	grantProperties.setRole(roleRef);
	 	grant.setGrantProperties(grantProperties);
	 	ugc.createGrant(ug, grant);	 	
	 	
	 	grantProperties.setGrantRemark("new context grant");
	 	RoleRef roleRef2 = new RoleRef("escidoc.role.user.depositor");
	 	grantProperties.setRole(roleRef);

	 	Reference ref = new ContextRef("" , "");
	 	
	 	grantProperties.setAssignedOn(ref);
	 	grant.setGrantProperties(grantProperties);
		
		


		
	}
	

//	private static void addOUAdminRole(String userAccountId,String roleId) throws Exception {
//        Grant grant = new Grant();
//        GrantProperties grantProperties = new GrantProperties();
//        grantProperties.setGrantRemark("new context grant");
//        RoleRef roleRef = new RoleRef(roleId);
//        grantProperties.setRole(roleRef);
//        grant.setGrantProperties(grantProperties);
//        grant = createGrant(userAccountId, grant);
//        
//        System.out.println("Grants set for User Account with user account id='" + userAccountId + "' at '" + grant.getLastModificationDate() + "'.");
//	}
	
	
    private static Grant createGrant(final String userAccountId, final Grant grant) throws Exception
    {
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlc");
		UserAccountHandlerClient client = new UserAccountHandlerClient(auth.getServiceAddress());
		client.setHandle(auth.getHandle());

		return client.createGrant(userAccountId, grant);
}



}
