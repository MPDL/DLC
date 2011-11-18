package de.mpg.mpdl.dlc.vo;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;

public class UserRole {
	private String roleName;
    private List<Context> contexts = new ArrayList<Context>();
    
    private List<UserAccount> userAccounts = new ArrayList<UserAccount>();

	private OrganizationalUnit ou = new OrganizationalUnit();
    
    public UserRole(String roleName, List<Context> contexts, List<UserAccount> userAccounts, OrganizationalUnit ou){
    	this.roleName = roleName;
    	this.contexts = contexts;
    	this.userAccounts = userAccounts;
    	this.ou = ou;
    }
    
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public List<Context> getContexts() {
		return contexts;
	}
	public void setContexts(List<Context> contexts) {
		this.contexts = contexts;
	} 
	
    public List<UserAccount> getUserAccounts() {
		return userAccounts;
	}

	public void setUserAccounts(List<UserAccount> userAccounts) {
		this.userAccounts = userAccounts;
	}

	public OrganizationalUnit getOu() {
		return ou;
	}

	public void setOu(OrganizationalUnit ou) {
		this.ou = ou;
	}
	
}
