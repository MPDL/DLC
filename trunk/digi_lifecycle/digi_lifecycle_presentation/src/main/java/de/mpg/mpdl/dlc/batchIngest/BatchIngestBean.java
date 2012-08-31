package de.mpg.mpdl.dlc.batchIngest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.Grant;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.ErrorLevel;
import de.mpg.mpdl.dlc.batchIngest.IngestLog.Step;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
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
	private String tei ;

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
	
	public String save(String action) throws Exception
	{
		if("".equals(name))
		{
			Date startDate = new Date();
			name = startDate.toString();
		}
		
		if("".equals(mab) || "".equals(images))
		{
			MessageHelper.errorMessage(ApplicationBean.getResource("Messages", "error_batch_ingest"));	
			return "";
		}
		MessageHelper.infoMessage(ApplicationBean.getResource("Messages", "info_batch_ingest_start"));	
		System.out.println("Vorgang gestartet");
		ingestProcess = new IngestProcess(name, Step.CHECK, action, ErrorLevel.FINE, loginBean.getUser().getId(), selectedContextId, loginBean.getUserHandle(), mab, tei, images);
	    ingestProcess.start();
	    clear();
	    logger.info("batch ingest finished");

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
