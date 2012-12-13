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
