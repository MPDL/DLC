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
package de.mpg.mpdl.dlc.ingest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;


import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.UrlHelper;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsTitle;

@ManagedBean
@SessionScoped
//@URLMapping(id="upload", pattern = "/upload", viewId = "/uploadArchive.xhtml", onPostback=false)
public class UploadBean 
{
	private static Logger logger = Logger.getLogger(UploadBean.class);
	private ArrayList<FileItem> files = new ArrayList<FileItem>();
	private ArrayList<FileItem> uFiles = new ArrayList<FileItem>();	


	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	

	private VolumeServiceBean volumeService = new VolumeServiceBean();

	public UploadBean()
	{
//		Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
//		this.url = ((HttpServletRequest)request).getRequestURL().toString();
	}
	
	//@URLAction(onPostback=false)
	public void status()
	{
		if(UrlHelper.getParameterBoolean("start"))
			try {

				upload();
			} catch (Exception e) {
				logger.error("Error getting images" + e.getMessage());
			}
		else if(UrlHelper.getParameterBoolean("stop"))
			try {
				save();
			} catch (Exception e) {
				logger.error("Error saving images" + e.getMessage());
			}

	}      

   
	public void upload() throws Exception
	{  
    	HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	String name = request.getParameter("name");
        StringTokenizer st = new StringTokenizer(name, ".");
        String format = null;
        while (st.hasMoreTokens())
            format = st.nextToken(); 
        String contentType = "image/" + format;
		ServletInputStream inputStream = request.getInputStream();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int next;
		next = inputStream.read();
		while ( next > -1)
		{
			bos.write(next);
			next = inputStream.read();
		}
		bos.flush();
		byte[] imageStream = bos.toByteArray();
		bos.close();
		UploadedFile file = new UploadedFile(name, contentType, imageStream);
		files.add(file);
    } 
	     
	public void save() throws Exception
	{   
		ModsMetadata md = new ModsMetadata();
		ModsTitle title = new ModsTitle();
		title.setTitle("Test title");
		md.getTitles().add(title);
    	//volumeService.createNewVolume("escidoc:5002", getLoginBean().getUserHandle(), md, files);
    	uFiles.addAll(files);
    	files.clear();
	} 
	
    public ArrayList<FileItem> getuFiles() { 
		return uFiles;
	}

	public void setuFiles(ArrayList<FileItem> uFiles) {
		this.uFiles = uFiles;
	}

	public int getSize() {
        if (getuFiles().size() > 0) {
            return getuFiles().size();
        } else {
            return 0;
        }
    }

	public void clear()
	{
		this.uFiles.clear();
	}
	
	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}
	 
    public void paint(OutputStream stream, Object object) throws Exception {
    	stream.write(getuFiles().get((Integer) object).get());
        stream.close();
    }

    
    

}


