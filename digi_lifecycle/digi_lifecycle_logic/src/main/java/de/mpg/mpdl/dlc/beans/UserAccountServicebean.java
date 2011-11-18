package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.resources.aa.useraccount.Attributes;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.GrantProperties;
import de.escidoc.core.resources.aa.useraccount.Grants;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.common.reference.RoleRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.User;
import de.mpg.mpdl.dlc.vo.UserRole;


@Stateless
public class UserAccountServicebean {
	private static Logger logger = Logger.getLogger(UserAccountServicebean.class);
	
	private ContextServiceBean contextServiceBean;
	private OrganizationalUnitServiceBean ouServiceBean;
	
	public UserAccountServicebean()
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
	public User retriveUser(String userHandle) throws Exception
	{
		User user = new User();
		List<UserRole> roles = new ArrayList<UserRole>();
		UserAccountHandlerClient client = new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
        

		if(userHandle != null)
		{
			client.setHandle(userHandle);
		}
		UserAccount userAccount = client.retrieveCurrentUser();
		user.setLoginName(userAccount.getProperties().getLoginName());
	  	
		Grants grants = client.retrieveCurrentGrants(userAccount);
		for(Grant grant : grants)
		{
			user.getGrants().add(grant);
			if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")))
			{
				//TODO ous, contexts for system-admin
				List<UserAccount> createdUserAccounts = retrieveCreatedUsers(client, userAccount.getObjid());
				roles.add(new UserRole(PropertyReader.getProperty("dlc.role.system.admin"),null, createdUserAccounts, null));
			}
			
			else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
        	{
				List<UserAccount> createdUserAccounts = retrieveCreatedUsers(client, userAccount.getObjid());	
				Attributes attributes = client.retrieveAttributes(userAccount.getObjid());
				String ouID = attributes.get(0).getValue();
				OrganizationalUnit ou = ouServiceBean.retrieveOu(ouID);
				List<Context> contexts = contextServiceBean.retrieveOUContexts(ou);
				roles.add(new UserRole(PropertyReader.getProperty("dlc.role.ou.admin"),contexts, createdUserAccounts, ou));
        	}
			
			else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.depositor")))
        	{
				List<Context> contexts = new ArrayList<Context>();
				Context c = contextServiceBean.retrieveContext(grant.getProperties().getAssignedOn().getObjid(), userHandle);
				contexts.add(c);
				roles.add(new UserRole(PropertyReader.getProperty("dlc.role.user.depositor"),contexts, null, null));
        	}
			else if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.user.moderator")))
        	{
				List<Context> contexts = new ArrayList<Context>();
				Context c = contextServiceBean.retrieveContext(grant.getProperties().getAssignedOn().getObjid(), userHandle);
				contexts.add(c);
				roles.add(new UserRole(PropertyReader.getProperty("dlc.role.user.moderator"),contexts, null, null));
        	}

		}
		user.setUserRoles(roles);
		return user;
	}
	
	public List<UserAccount> retrieveCreatedUsers(UserAccountHandlerClient client,String id) throws Exception
	{
		List<UserAccount> uas = new ArrayList<UserAccount>();
		SearchRetrieveRequestType req = new SearchRetrieveRequestType();
		req.setQuery("\"/properties/created-by/id\"="+id);
		SearchRetrieveResponse resp = client.retrieveUserAccounts(req);
		for(SearchResultRecord rec:resp.getRecords())
		{
			UserAccount ua= (UserAccount)rec.getRecordData().getContent();
			uas.add(ua);
		}
		return uas;
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

	private static void addOUAdminRole(String userAccountId,String roleId) throws Exception {
        Grant grant = new Grant();
        GrantProperties grantProperties = new GrantProperties();
        grantProperties.setGrantRemark("new context grant");
        RoleRef roleRef = new RoleRef(roleId);
        grantProperties.setRole(roleRef);
        grant.setGrantProperties(grantProperties);
        grant = createGrant(userAccountId, grant);
        
        System.out.println("Grants set for User Account with user account id='" + userAccountId + "' at '" + grant.getLastModificationDate() + "'.");
	}
	
	
    private static Grant createGrant(final String userAccountId, final Grant grant) throws Exception
    {
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlc");
		UserAccountHandlerClient client = new UserAccountHandlerClient(auth.getServiceAddress());
		client.setHandle(auth.getHandle());

		return client.createGrant(userAccountId, grant);
}

}
