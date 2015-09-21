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
package de.mpg.mpdl.jsf.components.fileUpload;
import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostValidateEvent;

import org.apache.log4j.Logger;

@FacesComponent("de.mpg.mpdl.jsf.components.FileUploadComponent")
@ListenerFor(systemEventClass=PostValidateEvent.class)
public class FileUploadComponent extends UINamingContainer {
	Logger logger = Logger.getLogger(FileUploadComponent.class);
	
	@Override
	 public void decode(FacesContext context)
	{
		super.decode(context);
		//System.out.println("DECODE");
		//HTML 5 uploads & Flash
		if(context.getPartialViewContext().isAjaxRequest())
		{
			checkFileUpload();
		}
		
		
	}
	
	
	@Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        super.processEvent(event);
        //HTML 4 uploads
        if(!FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest())
		{
        	//logger.info("processEvent: " + event);	
			checkFileUpload();
		}
    }
    
	
   

     public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        //System.out.println("BROADCAST");
        if(getAttributes().get("fileUploadListener")!=null)
        {
        	MethodExpression fileUploadExp = (MethodExpression)getAttributes().get("fileUploadListener");
        	fileUploadExp.invoke(this.getFacesContext().getELContext(), new Object[]{((FileUploadEvent)event)});
        }
     }
     
     private void checkFileUpload()
     {
    	 
 		//System.out.println("DECODE");
 		 Object request = getFacesContext().getExternalContext().getRequest();
 		//logger.info("checkFileUpload: ");	
 		 
 		 if(request instanceof MultipartRequest)
 			{
 			 //logger.info("request is multipart, get file for client id " + getClientId());	
 				if(((MultipartRequest)request).getFile(getClientId())!=null)
 				{
 					//logger.info("append to event queue");
 					FileUploadEvent evt = new FileUploadEvent(this); 
 					evt.setFileItem(((MultipartRequest)request).getFile(getClientId()));
 		            queueEvent(evt);
 				}
 	            
 	        }
     }
     



}
