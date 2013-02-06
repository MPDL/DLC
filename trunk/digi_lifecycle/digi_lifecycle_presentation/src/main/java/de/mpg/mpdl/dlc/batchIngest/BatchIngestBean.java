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
package de.mpg.mpdl.dlc.batchIngest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.Status;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.Step;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.collection.Collection;



@ManagedBean
@SessionScoped
@URLMapping(id="batchIngest", viewId = "/batchIngest.xhtml", pattern = "/ingest/batch")
public class BatchIngestBean {

	private static Logger logger = Logger.getLogger(BatchIngestBean.class);
	private String images;
	private String mab;
	private String tei;
	private String cdc;

	private String server;
	private String user;
	private String password;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;

	
	private String selectedContextId;
	private String selecetedContextName;
	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
	
	private String name;
	
	private IngestProcess ingestProcess;
	
	private boolean protocol;
	
	@URLAction(onPostback=false)	
	public void init()
	{                 		
		this.protocol = true;
		this.contextSelectItems.clear();
		SelectItem item;
		List<String> ids = new ArrayList();
		//init contexts 
		if(loginBean == null || loginBean.getUser() == null)
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			try {
				String dlc_URL = PropertyReader.getProperty("dlc.instance.url") + "/" + PropertyReader.getProperty("dlc.context.path") + "/";
				fc.getExternalContext().redirect(dlc_URL);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		else
		{
		for(Grant grant: loginBean.getUser().getGrants())
		{ 
			try  
			{
				if(grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.system.admin")) || grant.getProperties().getRole().getObjid().equals(PropertyReader.getProperty("escidoc.role.ou.admin")))
				{
					for(Collection c : loginBean.getUser().getCreatedCollections())
						this.contextSelectItems.add(new SelectItem(c.getId(),c.getName()));
				}
				else {
					for(Collection c : loginBean.getUser().getDepositorCollections())
					{  
						if(!ids.contains(c.getId()))
						{
							ids.add(c.getId());
							item = new SelectItem(c.getId(),c.getName());
							this.contextSelectItems.add(item);
						}
					}
					for(Collection c : loginBean.getUser().getModeratorCollections())
					{  
						if(!ids.contains(c.getId()))
						{
							ids.add(c.getId());
							item = new SelectItem(c.getId(),c.getName());
							this.contextSelectItems.add(item);
						}
					}
				}
			}catch(Exception e)
			{
					
			}
		}
		if(contextSelectItems.size()>0)
			this.selectedContextId = (String) contextSelectItems.get(0).getValue();	
			this.selecetedContextName = (String)contextSelectItems.get(0).getLabel();
		
		}
	}

	public String save(String action)
	{ 
		if("".equals(name))
		{
			Date startDate = new Date();
			name = startDate.toString();
		}
		
		if("".equals(mab) || "".equals(images))
		{
			MessageHelper.errorMessage(InternationalizationHelper.getMessage("error_batch_ingest"));	
			return "pretty:batchIngest";
		}

		if(images.startsWith("/"))
			images = images.substring(1);
		if(mab.startsWith("/"))
			mab = mab.substring(1);
		if(tei != "" && tei.startsWith("/"))
			tei = tei.substring(1);
		if(cdc != "" && cdc.startsWith("/") )
			cdc = cdc.substring(1);
		
		BatchLog batchLog = new BatchLog();
		batchLog.setName(name);
		batchLog.setStep(Step.CHECK);
		if(action.equalsIgnoreCase("save"))
			batchLog.setStuatus(Status.SUBMITTED);
		else
			batchLog.setStuatus(Status.RELEASED);
		batchLog.setUserId(loginBean.getUser().getId());
		batchLog.setUserName(loginBean.getUser().getName());
		
		batchLog.setContextId(selectedContextId);
		batchLog.setContextName(selecetedContextName);
		
		ingestProcess = new IngestProcess(name, action, selectedContextId, loginBean.getUserHandle(), server, protocol, user, password, images, mab, tei, cdc, batchLog);
		ingestProcess.start();
		

		MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_batch_ingest_start"));

		clear();

		return "pretty:myItemsBatch";
	}
	

	
	public String clear()
	{
		this.name ="";
//		this.images = "";
//		this.mab = "";
//		this.tei = "";
		return "pretty:batchIngest";
	}
	

	public String getMab() {
		return mab;
	}



	public void setMab(String mab) {
		this.mab = mab;
	}



	public String getTei() {
		return tei;
	}



	public void setTei(String tei) {
		this.tei = tei;
	}


	

	public String getCdc() {
		return cdc;
	}

	public void setCdc(String cdc) {
		this.cdc = cdc;
	}

	public String getImages() {
		return images;
	}



	public void setImages(String images) {
		this.images = images;
	}
	
	
	public String getSelectedContextId() {
		return selectedContextId;
	}

	public void setSelectedContextId(String selectedContextId) {
		System.err.println("Set context Id= " + selectedContextId);
		for(SelectItem si : contextSelectItems)
		{
			if(si.getValue().equals(selectedContextId))
				this.selecetedContextName = si.getLabel();
		}
		this.selectedContextId = selectedContextId;
	}

	public List<SelectItem> getContextSelectItems() {
		return contextSelectItems;
	}
	
	public void setContextSelectItems(List<SelectItem> contextSelectItems) {
		this.contextSelectItems = contextSelectItems;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isProtocol() {
		return protocol;
	}

	public void setProtocol(boolean protocol) {
		this.protocol = protocol;
	}

	
	
	





	


	


	
	}
