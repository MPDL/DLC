/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.viewer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.organization.Organization;

@ManagedBean
@ViewScoped
@URLMapping(id = "viewMultiVol", pattern = "/viewMulti/#{viewMultiVol.volumeId}", viewId = "/viewMultiVol.xhtml")
public class ViewMultiVol{

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	private OrganizationalUnitServiceBean orgServiceBean = new OrganizationalUnitServiceBean();
	
	private String volumeId;
	
	

	private Volume volume;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	enum ViewType
	{
		LIST, ISBD
	}
	
	private ViewType viewType = ViewType.LIST;
	private Collection collection;
	private Organization volumeOu;

	
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
		{   
			try {
				this.volume = volServiceBean.loadCompleteVolume(volumeId, loginBean.getUserHandle());
				this.collection = contextServiceBean.retrieveCollection(volume.getItem().getProperties().getContext().getObjid(), null);
				volumeOu = orgServiceBean.retrieveOrganization(this.collection.getContext().getProperties().getOrganizationalUnitRefs().getFirst().getObjid());
				if (volumeOu.getDlcMd().getFoafOrganization().getImgURL() != null && !volumeOu.getDlcMd().getFoafOrganization().getImgURL().equals(""))
					{sessionBean.setLogoLink(volumeOu.getId());
					sessionBean.setLogoUrl(volumeOu.getDlcMd().getFoafOrganization().getImgURL());
					sessionBean.setLogoTlt(InternationalizationHelper.getTooltip("main_home")
							.replace("$1", volumeOu.getEscidocMd().getTitle()));}
			} catch (Exception e) {
				MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_loadVolume"));
			}
		}
		volumeLoaded();
	}
	
	protected void volumeLoaded()
	{
	}

	public ViewType getViewType() {
		return viewType;
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}
	
	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public Organization getVolumeOu() {
		return volumeOu;
	}

	public void setVolumeOu(Organization volumeOu) {
		this.volumeOu = volumeOu;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}
	
	

}
