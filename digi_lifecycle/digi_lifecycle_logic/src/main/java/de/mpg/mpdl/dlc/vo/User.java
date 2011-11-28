package de.mpg.mpdl.dlc.vo;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;

public class User {
	
	private String id;
	private String loginName;
	private List<Grant> grants = new ArrayList<Grant>();
	
	private OrganizationalUnit ou = new OrganizationalUnit();
	
	private List<OrganizationalUnit> createdOUs = new ArrayList<OrganizationalUnit>();
	private List<Context> createdContexts = new ArrayList<Context>();
	private List<UserAccount> createdUserAccounts = new ArrayList<UserAccount>();
	
	private List<Context> depositorContexts = new ArrayList<Context>();
	private List<Context> moderatorContexts = new ArrayList<Context>();
	

	public User()
	{
		
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLoginName() {
		return loginName;
	}
	
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public List<Grant> getGrants() {
		return grants;
	}
	
	public void setGrants(List<Grant> grants) {
		this.grants = grants;
	}

	public OrganizationalUnit getOu() {
		return ou;
	}

	public void setOu(OrganizationalUnit ou) {
		this.ou = ou;
	}

	public List<OrganizationalUnit> getCreatedOUs() {
		System.out.println(createdOUs.size());
		return createdOUs;
	}
	
	public void setCreatedOUs(List<OrganizationalUnit> createdOUs) {
		this.createdOUs = createdOUs;
	}
	
	public List<Context> getCreatedContexts() {
		createdContexts.get(0).getProperties().getName();
		return createdContexts;
	}
	
	public void setCreatedContexts(List<Context> createdContexts) {
		this.createdContexts = createdContexts;
	}
	
	public List<UserAccount> getCreatedUserAccounts() {
		return createdUserAccounts;
	}
	
	public void setCreatedUserAccounts(List<UserAccount> createdUserAccounts) {
		this.createdUserAccounts = createdUserAccounts;
	}

	public List<Context> getDepositorContexts() {
		return depositorContexts;
	}

	public void setDepositorContexts(List<Context> depositorContexts) {
		this.depositorContexts = depositorContexts;
	}

	public List<Context> getModeratorContexts() {
		return moderatorContexts;
	}

	public void setModeratorContexts(List<Context> moderatorContexts) {
		this.moderatorContexts = moderatorContexts;
	}



}
