package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Attributes;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.GrantProperties;
import de.escidoc.core.resources.aa.useraccount.Grants;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.aa.useraccount.UserAccountProperties;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.common.reference.RoleRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.user.User;


@Stateless
public class UserAccountServiceBean {
	private static Logger logger = Logger.getLogger(UserAccountServiceBean.class);
	
	private ContextServiceBean contextServiceBean;
	private OrganizationalUnitServiceBean ouServiceBean;
	
	public UserAccountServiceBean()
	{
		try
		{
		InitialContext context = new InitialContext();
		this.contextServiceBean = (ContextServiceBean) context.lookup("java:module/ContextServiceBean");
		this.ouServiceBean = (OrganizationalUnitServiceBean) context.lookup("java:module/OrganizationalUnitServiceBean");

		}catch(Exception e)
		{
			logger.error("Erroe init other ejbx " + e.getMessage());
		}
	}
	public User retrieveUser(String userHandle)
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
			UserAccount userAccount = client.retrieveCurrentUser();
			String userId = userAccount.getObjid();
			user.setId(userId);
			user.setLoginName(userAccount.getProperties().getLoginName());
		  	
			Grants grants = client.retrieveCurrentGrants(userAccount);
			for(Grant grant : grants)
			{
	//			user.getGrants().add(grant);
				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")))
				{
					user.getGrants().add(grant);
					user.setCreatedOUs(ouServiceBean.retrieveOUsCreatedBy(userHandle, userId));
					user.setCreatedContexts(contextServiceBean.retrieveContextsCreatedBy(userHandle,userId));
					user.setCreatedUserAccounts(retrieveCreatedUsers(userHandle,userId));
				}
				
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
	        	{
					user.getGrants().add(grant);
					Attributes attributes = client.retrieveAttributes(userAccount.getObjid());
					String ouID = attributes.get(0).getValue();
					user.setOu(ouServiceBean.retrieveOU(ouID));
					
	//				user.setCreatedOUs(ouServiceBean.retrieveOUsCreatedBy(userId));
					user.setCreatedContexts(contextServiceBean.retrieveContextsCreatedBy(userHandle, userId));
					user.setCreatedUserAccounts(retrieveCreatedUsers(userHandle,userId));
	        	}
				
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.depositor")))
	        	{
					user.getGrants().add(grant);
					Context c = contextServiceBean.retrieveContext(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getDepositorContexts().add(c);
	        	}			
				
				else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.moderator")))
	        	{
					user.getGrants().add(grant);
					Context c = contextServiceBean.retrieveContext(grant.getProperties().getAssignedOn().getObjid(), userHandle);
					user.getModeratorContexts().add(c);
	        	}
	
			}
		}catch(Exception e)
		{
			logger.error("Error while Retrieving login User", e);
		}

		return user;
	}
	
	public List<UserAccount> retrieveCreatedUsers(String userHandle, String id)
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
	
	
	public UserAccount createNewUserAccount(User user, String userHandle) 
	{
		logger.info("Creating new user Account");
		UserAccount ua = prepareUserAccount(user);
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
			if(user.getDepositorContextIds() != null)
			{
				for(String contextInfo : user.getDepositorContextIds())
				{
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.depositor"), contextInfo);
					client.createGrant(ua.getObjid(), grant);
				}				
			}
			if(user.getModeratorContextIds() != null)
			{
				for(String contextInfo : user.getModeratorContextIds())
				{
					grant = newGrant(grant, PropertyReader.getProperty("escidoc.role.user.moderator"), contextInfo);
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
	
	private Grant newGrant(Grant grant, String roleId, String contextInfo) throws Exception {

		GrantProperties grantProperties = new GrantProperties();
	 	grantProperties.setGrantRemark("new context grant");
	 	RoleRef roleRef = new RoleRef(roleId);
	 	grantProperties.setRole(roleRef);
	 	String[] info= contextInfo.split("\\|");
	 	Reference ref = new ContextRef("/ir/context/"+info[0] , info[1]);
	 	
	 	grantProperties.setAssignedOn(ref);
	 	grant.setGrantProperties(grantProperties);
	 	return grant;
	}
	
    private UserAccount prepareUserAccount(User user) {

        UserAccount userAccount = new UserAccount();

        UserAccountProperties properties = new UserAccountProperties();
        properties.setLoginName(user.getLoginName());
        properties.setName(user.getName());
        userAccount.setProperties(properties);

        return userAccount;
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
