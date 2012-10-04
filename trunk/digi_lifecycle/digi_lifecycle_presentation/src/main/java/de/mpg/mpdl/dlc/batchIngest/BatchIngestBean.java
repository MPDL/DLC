package de.mpg.mpdl.dlc.batchIngest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.ErrorLevel;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Step;
import de.mpg.mpdl.dlc.beans.LoginBean;
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

	private String server;
	private String user;
	private String password;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	private String selectedContextId;
	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
	
	private String name;
	
	private IngestProcess ingestProcess;
	
	@PostConstruct
	public void init()
	{
		this.contextSelectItems.clear();
		SelectItem item;
		List<String> ids = new ArrayList();
		//init contexts 
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
			return "";
		}
		MessageHelper.infoMessage(InternationalizationHelper.getMessage("info_batch_ingest_start"));
		try
		{
			ingestProcess = new IngestProcess(name, Step.CHECK, action, ErrorLevel.FINE, loginBean.getUser().getId(), selectedContextId, loginBean.getUserHandle(), server, user, password, mab, tei, images);
			ingestProcess.start();
		}catch(Exception e)
		{
			MessageHelper.infoMessage("login failed");
		}
	   
	    clear();
//	    logger.info("batch ingest finished");

		return "";
	}
	
	public String clear()
	{
		this.name ="";
		this.images = "";
		this.mab = "";
		this.tei = "";
		return "";
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


	


	


	
	}
