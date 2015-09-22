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
package de.mpg.mpdl.dlc.viewer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.component.Component;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.export.Export.ExportTypes;
import de.mpg.mpdl.dlc.export.ExportServlet;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.mets.MetsDiv;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.organization.Organization;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;

@ManagedBean
@ViewScoped
@URLMapping(id = "fullScreen", pattern = "/fullView/#{fullScreen.volumeId}/#{fullScreen.viewTypeText}/#{fullScreen.selectedPageNumber}", viewId = "/fullscreen.xhtml", onPostback=false)
public class FullScreen {
	@URLQueryParameter("dl")
	private String digilibQueryString;
	
	private String volumeId;
	
	private Volume volume;
	
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	private ContextServiceBean contextServiceBean = new ContextServiceBean();
	
	private OrganizationalUnitServiceBean orgServiceBean = new OrganizationalUnitServiceBean();
	

	
	@ManagedProperty("#{sessionBean}") 
	private SessionBean sessionBean;
	
	private Collection collection;
	
	private static Logger logger = Logger.getLogger(FullScreen.class);
	
	private List<Page> pageList = new ArrayList<Page>();
	
	private boolean hasSelected = false;
	
	private int selectedPageNumber;
	
	private Page selectedPage;
	
	private String viewTypeText;
	
	private Map<String, String> clientIdMap = new HashMap<String, String>();
	
	private List<SelectItem> pageListMenu = new ArrayList<SelectItem>();
	
	private Organization volumeOu;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{     
 

		if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
		{   
			try {
				
				this.volume = volServiceBean.loadCompleteVolume(volumeId, getLoginBean().getUserHandle());
				this.collection = contextServiceBean.retrieveCollection(volume.getItem().getProperties().getContext().getObjid(), null);

				//Set the logo of application to collection logo
				volumeOu = orgServiceBean.retrieveOrganization(this.collection.getContext().getProperties().getOrganizationalUnitRefs().getFirst().getObjid());
				if (volumeOu.getDlcMd().getFoafOrganization().getImgURL() != null && !volumeOu.getDlcMd().getFoafOrganization().getImgURL().equals(""))
					{sessionBean.setLogoLink(volumeOu.getId());
					sessionBean.setLogoUrl(volumeOu.getDlcMd().getFoafOrganization().getImgURL());
					sessionBean.setLogoTlt(InternationalizationHelper.getTooltip("main_home").replace("$1", volumeOu.getEscidocMd().getTitle()));}
				
				initPageListMenu();
			} catch (Exception e) {
				//MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_loadVolume"));
				logger.error("Error while loading volume", e);
			}
		}
		volumeLoaded();
	}
	
	private void initPageListMenu()
	{
		this.pageListMenu.clear();
		for(Page p : volume.getPages())
		{
			pageListMenu.add(new SelectItem(p.getOrder()+1, "Seite " + p.getOrderLabel() + " / Bild " + (p.getOrder()+1)));
		}
	}
	
	public void pageNumberChanged()
	{
		System.out.println("Go to page changed!!!!");
		loadVolume();
	}
	
	protected void volumeLoaded() {
		if(FacesContext.getCurrentInstance().isPostback())
		{
			this.digilibQueryString = "";
		}
		Page pageforNumber = volume.getPages().get(getSelectedPageNumber()-1);
		this.setSelectedPage(pageforNumber);
		
	}
	
	
	private static void fillExpansionMap(Map<PbOrDiv, Boolean> expansionMap, List<PbOrDiv> currentDivs)
	{
//		System.out.println("Fill exp map");
		for(PbOrDiv pbOrDiv : currentDivs)
		{
			expansionMap.put(pbOrDiv, true);
			fillExpansionMap(expansionMap, pbOrDiv.getPbOrDiv());
		}
	}
	
	
	public List<Page> getPageList() throws Exception
	{
		pageList = volume.getPages();
		return pageList;
	}
	public void setPageList(List<Page> pageList)
	{
		this.pageList = pageList;
	}
	
	public String goToNextPage()
	{
       selectedPageNumber ++;
       loadVolume();
        
        return "";
	}
	
	public String goToPreviousPage()
	{
		if (selectedPageNumber -1 > 0)
        {
           selectedPageNumber --;
           loadVolume();
        }
        return null;
	} 
	
	public String goToLastPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = volume.getPages().size();
        loadVolume();
        return null;
	}
	
	public String goToFirstPage()
	{
//		selectedPageNumber = volume.getPages().indexOf(selectedPage) + 1;
        selectedPageNumber = 1; 
        loadVolume();
        return null;
	}

	
	public List<SelectItem> getPageListMenu() {
		return pageListMenu;
	}
	
	
	public String getViewTypeText() {
		return viewTypeText;
	}

	public void setViewTypeText(String viewTypeText) {
		this.viewTypeText = viewTypeText;
	}
	
	public void setPageListMenu(List<SelectItem> pageListMenu) {
		this.pageListMenu = pageListMenu;
	}
	
	public Page getSelectedPage() {
		return selectedPage;
	}

	public void setSelectedPage(Page selectedPage) {
		this.selectedPage = selectedPage;
	}

	public boolean isHasSelected() {
		return hasSelected;
	}

	public void setHasSelected(boolean hasSelected) {
		this.hasSelected = hasSelected;
	}

	public void goToPage(Page p)
	{
		logger.info("Go to page " + p.getId());
		selectedPageNumber = volume.getPages().indexOf(p) + 1 ;
		loadVolume();
	}
	
	public MetsDiv getNextPage(MetsDiv div)
	{
		if(div.getType()!=null && div.getType().equals("page"))
		{
			return div;
		}
		else
		{	
			
			for(MetsDiv subDiv : div.getDivs())
			{
				return getNextPage(subDiv);
			}
			
		}
		return null;
			
	}

	
	public int getSelectedPageNumber() {
		return selectedPageNumber;
	}

	public void setSelectedPageNumber(int selectedPageNumber) {
		this.selectedPageNumber = selectedPageNumber;
	}

	public Volume getVolume() {
		return volume;
	}


	public void setVolume(Volume volume) {
		this.volume = volume;
	}


	public String getVolumeId() {
		return volumeId;
	}
	
	public String getVolumeIdWithoutObjId() {
		String id = this.getVolumeId();
		if (id.split(":").length >= 2)
		{
			id = "escidoc:" + id.split(":")[1];
		}
		return id;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}


	public String addToClientIdMap(String divId, String clientId)
	{
		clientIdMap.put(divId, clientId);
		System.out.println("Added to client id map: " + divId + " " + clientId);
		return "";
	}


	public LoginBean getLoginBean() {
		return loginBean;
	}


	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}


	public String getDigilibQueryString() {
		return digilibQueryString;
		/*
		if(digilibQueryString == null || digilibQueryString.length()<=1)
		{
			return "";
		}
		else
		{
			System.out.println("DLC query string: " + digilibQueryString);
			return digilibQueryString;
			
		}
		*/
		
	}

	public void setDigilibQueryString(String digilibQueryString) {
		this.digilibQueryString = digilibQueryString;
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

	public Organization getVolumeOu() {
		return volumeOu;
	}

	public void setVolumeOu(Organization volumeOu) {
		this.volumeOu = volumeOu;
	}

}
