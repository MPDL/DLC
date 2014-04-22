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

import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;

@ManagedBean
@ViewScoped
@URLMapping(id = "viewMultiVol", pattern = "/viewMmulti/#{viewMultiVol.volumeId}", viewId = "/viewMultiVol.xhtml")
public class ViewMultiVol{

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	private String volumeId;
	
	

	private Volume volume;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	enum ViewType
	{
		LIST, ISBD
	}
	
	private ViewType viewType = ViewType.LIST;
	
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
		{   
			try {
				this.volume = volServiceBean.loadCompleteVolume(volumeId, loginBean.getUserHandle());
				
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
	
	

}
