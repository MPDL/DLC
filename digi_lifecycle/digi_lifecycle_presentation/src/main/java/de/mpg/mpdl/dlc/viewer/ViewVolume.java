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
package de.mpg.mpdl.dlc.viewer;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.export.Export;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Page;


@ManagedBean
@SessionScoped
@URLMapping(id="viewVolume", pattern = "/volume/#{viewVolume.volumeId}/#{viewVolume.currentPageNumber}", viewId = "/viewVolume.xhtml")
public class ViewVolume {
	private static Logger logger = Logger.getLogger(ViewVolume.class);
	

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	
	private String volumeId;
	private Volume volume;
	private int currentPageNumber;
	private int position;
	

	@URLAction(onPostback=false)
	public void loadVolume()
	{  
		try { 
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{
				this.volume = volServiceBean.loadCompleteVolume(volumeId, null);
				logger.info("Load new book" + volumeId);
			}
			
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_loadVolume"));
		}
	}
	
	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}
	
	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public String getVolumeId() {
		return volumeId;
	}
	
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber= currentPageNumber;
	}
	 
	public int getPagePosition(Page p)
	{ 
		position = getVolume().getPages().indexOf(p) + 1;
		return position;
	}

}
