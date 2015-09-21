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
package de.mpg.mpdl.dlc.list;

import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;


@ManagedBean
@SessionScoped
@URLMapping(id="thumbnails", viewId="/thumbnails.xhtml", pattern="/thumbnails/#{thumbnailsBean.volumeId}")
public class ThumbnailsBean extends BasePaginatorBean<Page>{
	
	private static Logger logger = Logger.getLogger(ThumbnailsBean.class); 

	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private String volumeId;
	private Volume volume;
	private int totalNumberOfRecords;
	
	private Context context;

	private List<Page> pageList = new ArrayList<Page>();
    
	public ThumbnailsBean()
	{
		super();		
	}
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{  
		try { 
			if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
			{
				this.volume = volServiceBean.retrieveVolume(volumeId, loginBean.getUserHandle());
				logger.info("Load new book" + volumeId);
				setElementsPerPage(24);
				setCurrentPageNumber(1);
			}
			this.context = contextServiceBean.retrieveContext(volume.getItem().getProperties().getContext().getObjid(), null);
			
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_loadVolume"));
		}
	}
	
	public List<Page> retrieveList(int offset, int limit) throws Exception 
	{
		pageList = volServiceBean.retrieveVolume(volumeId, loginBean.getUserHandle()).getPages();
		totalNumberOfRecords = pageList.size();
		List<Page> subList = pageList.subList(offset-1, (totalNumberOfRecords > (offset-1+limit))?(offset-1+limit): totalNumberOfRecords);

		return subList;
	}
	
//	public int getCurrentPageNumber() 
//	{
//	    int a=viewPages.getSelectedPageNumber();
//	    int b=getElementsPerPage();
//	    int currentPageNr = (double)a/(double)b > (a/b) ? a/b+1 : a/b ;
//	    setCurrentPageNumber(currentPageNr);
//		return currentPageNr ;
//	}
	
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

	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public String getNavigationString() {
		return "pretty:thumbnails";
	}
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	

	public Context getContext() {
		return context;
	}


	public void setContext(Context context) {
		this.context = context;
	}

}
