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
package de.mpg.mpdl.dlc.vo.user;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;

public class User {
	
	
	private UserAccount userAccount;
	
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
	private List<Collection> mdEditorCollections = new ArrayList<Collection>();
	
	private List<String> depositorContextIds = new ArrayList<String>();
	private List<String> moderatorContextIds = new ArrayList<String>();
	private List<String> mdEditorContextIds = new ArrayList<String>();

	
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

	
	public boolean isModerator()
	{
		return moderatorContextIds.size()>0;
	}
	
	public boolean isDepositor()
	{	
		return depositorContextIds.size() >0;
	}
	
	public boolean releasable(Volume vol)
	{
		String versionStatus = vol.getItem().getProperties().getVersion().getStatus();
		String publicStatus = vol.getItem().getProperties().getPublicStatus().getXmlValue(); 
		
		boolean stateCondition = !"withdrawn".equals(publicStatus) && "submitted".equals(versionStatus);
		
		if(moderatorContextIds.contains(vol.getProperties().getContext().getObjid()))
		{
			return stateCondition;
		}
		else if(id.equals(vol.getProperties().getCreatedBy().getObjid()))
		{
			return stateCondition;
		}
		
		return false;
	}
	
	public boolean deletable(Volume vol)
	{
		String versionStatus = vol.getItem().getProperties().getVersion().getStatus();
		String publicStatus = vol.getItem().getProperties().getPublicStatus().getXmlValue(); 
		boolean stateCondition = !"released".equals(publicStatus) && !"withdrawn".equals(publicStatus);
		if(moderatorContextIds.contains(vol.getProperties().getContext().getObjid()))
		{
			
			
			return stateCondition;
					
		}
		else if(id.equals(vol.getProperties().getCreatedBy().getObjid()))
		{
			return stateCondition;
		}
		
		return false;
				
		
	}
	
	public boolean withdrawable(Volume vol)
	{
		String versionStatus = vol.getItem().getProperties().getVersion().getStatus();
		String publicStatus = vol.getItem().getProperties().getPublicStatus().getXmlValue(); 
		
		boolean stateCondition = "released".equals(publicStatus);
		
		if(moderatorContextIds.contains(vol.getProperties().getContext().getObjid()))
		{
			return stateCondition;
		}
		else if(id.equals(vol.getProperties().getCreatedBy().getObjid()))
		{
			return stateCondition;
		}
		
		return false;
	}
	
	public boolean structureEditable(Volume vol)
	{
		String versionStatus = vol.getItem().getProperties().getVersion().getStatus();
		String publicStatus = vol.getItem().getProperties().getPublicStatus().getXmlValue(); 
		
		boolean stateCondition = !"withdrawn".equals(publicStatus);
		
		boolean teiNotAvailable = true;

		
		for(Component c : vol.getItem().getComponents())
		{
			if("tei".equalsIgnoreCase(c.getProperties().getContentCategory()))
			{
				teiNotAvailable =  false;
			}
		}
		
		
		if(moderatorContextIds.contains(vol.getProperties().getContext().getObjid()) || mdEditorContextIds.contains(vol.getProperties().getContext().getObjid()) )
		{
			return teiNotAvailable && stateCondition;
		}
		else if(id.equals(vol.getProperties().getCreatedBy().getObjid()))
		{
			return teiNotAvailable && stateCondition;
		}
		
		return false;
	}
	
	public boolean mdEditable(Volume vol)
	{
		String versionStatus = vol.getItem().getProperties().getVersion().getStatus();
		String publicStatus = vol.getItem().getProperties().getPublicStatus().getXmlValue(); 
		
		boolean stateCondition = !"withdrawn".equals(publicStatus);
		
		
		
		if(moderatorContextIds.contains(vol.getProperties().getContext().getObjid()))
		{
			return stateCondition;
		}
		else if(id.equals(vol.getProperties().getCreatedBy().getObjid()))
		{
			return stateCondition;
		}
		
		return false;
	}

	public List<Collection> getMdEditorCollections() {
		return mdEditorCollections;
	}

	public void setMdEditorCollections(List<Collection> mdEditorCollections) {
		this.mdEditorCollections = mdEditorCollections;
	}

	public List<String> getMdEditorContextIds() {
		return mdEditorContextIds;
	}

	public void setMdEditorContextIds(List<String> mdEditorContextIds) {
		this.mdEditorContextIds = mdEditorContextIds;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	
	



}
