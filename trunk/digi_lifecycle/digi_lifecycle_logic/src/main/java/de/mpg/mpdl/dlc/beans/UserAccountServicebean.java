package de.mpg.mpdl.dlc.beans;

import java.net.URL;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.GrantProperties;
import de.escidoc.core.resources.common.reference.RoleRef;

public class UserAccountServicebean {
	
	
	
	public static void main(String[] args) throws Exception {
		String userAccountID = "escidoc:15006";
        String roleId = "escidoc:role-ou-administrator";
		addOUAdminRole(userAccountID, roleId);
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
