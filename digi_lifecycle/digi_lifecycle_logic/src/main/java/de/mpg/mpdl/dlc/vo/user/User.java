package de.mpg.mpdl.dlc.vo.user;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;

public class User {
	
	private String id;
	private String name;
	private String loginName;
	private String password;
	private List<Grant> grants = new ArrayList<Grant>();
	
	private OrganizationalUnit ou = new OrganizationalUnit();
	private String ouId;
	
	private List<OrganizationalUnit> createdOUs = new ArrayList<OrganizationalUnit>();
	private List<Context> createdContexts = new ArrayList<Context>();
	private List<UserAccount> createdUserAccounts = new ArrayList<UserAccount>();
	
	private List<Context> depositorContexts = new ArrayList<Context>();
	private List<Context> moderatorContexts = new ArrayList<Context>();
	
	private List<String> depositorContextIds = new ArrayList<String>();
	private List<String> moderatorContextIds = new ArrayList<String>();

	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginName() {
		return loginName;
	}
	
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	

	public String getOuId() {
		return ouId;
	}

	public void setOuId(String ouId) {
		this.ouId = ouId;
	}

	public List<OrganizationalUnit> getCreatedOUs() {
		return createdOUs;
	}
	
	public void setCreatedOUs(List<OrganizationalUnit> createdOUs) {
		this.createdOUs = createdOUs;
	}
	
	public List<Context> getCreatedContexts() {
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

	public List<String> getDepositorContextIds() {
		return depositorContextIds;
	}

	public void setDepositorContextIds(List<String> depositorContextIds) {
		this.depositorContextIds = depositorContextIds;
	}

	public List<String> getModeratorContextIds() {
		return moderatorContextIds;
	}

	public void setModeratorContextIds(List<String> moderatorContextIds) {
		this.moderatorContextIds = moderatorContextIds;
	}


	
	



}
