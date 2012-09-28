package de.mpg.mpdl.dlc.vo.user;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;

public class User {
	
	private String id;
	private String name;
	private String loginName;
	private String password;
	private List<Grant> grants = new ArrayList<Grant>();
	
	//just for OU-Admin
	private OrganizationalUnit ou = new OrganizationalUnit();
	private String ouId;
	
	
	
	private List<Organization> createdOrgas = new ArrayList<Organization>();
	private List<Collection> createdCollections = new ArrayList<Collection>();
	private List<User> createdUsers = new ArrayList<User>();
	
	private List<Collection> depositorCollections = new ArrayList<Collection>();
	private List<Collection> moderatorCollections = new ArrayList<Collection>();
	
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

	public List<Organization> getCreatedOrgas() {
		return createdOrgas;
	}

	public void setCreatedOrgas(List<Organization> createdOrgas) {
		this.createdOrgas = createdOrgas;
	}

	public List<Collection> getCreatedCollections() {
		return createdCollections;
	}

	public void setCreatedCollections(List<Collection> createdCollections) {
		this.createdCollections = createdCollections;
	}

	public List<User> getCreatedUsers() {
		return createdUsers;
	}

	public void setCreatedUsers(List<User> createdUsers) {
		this.createdUsers = createdUsers;
	}

	public List<Collection> getDepositorCollections() {
		return depositorCollections;
	}

	public void setDepositorCollections(List<Collection> depositorCollections) {
		this.depositorCollections = depositorCollections;
	}

	public List<Collection> getModeratorCollections() {
		return moderatorCollections;
	}

	public void setModeratorCollections(List<Collection> moderatorCollections) {
		this.moderatorCollections = moderatorCollections;
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

	
	public boolean releasable(String contextId)
	{
		if(moderatorContextIds.contains(contextId))
			return true;
		else
			return false;
	}
	
	public boolean deletable(Volume vol)
	{
		return id.equals(vol.getItem().getProperties().getCreatedBy().getObjid());
		
	}

	
	



}
