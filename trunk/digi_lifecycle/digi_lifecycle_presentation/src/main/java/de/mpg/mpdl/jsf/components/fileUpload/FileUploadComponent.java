package de.mpg.mpdl.jsf.components.fileUpload;
import java.util.LinkedList;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.ActionSource2;
import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostValidateEvent;

import org.apache.commons.fileupload.FileItem;

import com.sun.faces.application.MethodBindingMethodExpressionAdapter;
import com.sun.faces.application.MethodExpressionMethodBindingAdapter;

@FacesComponent("de.mpg.mpdl.components.FileUploadComponent")
@ListenerFor(systemEventClass=PostValidateEvent.class)
public class FileUploadComponent extends UINamingContainer {
	
	@Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        
        super.processEvent(event);
        Object request = getFacesContext().getExternalContext().getRequest();
		if(request instanceof MultipartRequest)
		{
            FileUploadEvent evt = new FileUploadEvent(this); 
            evt.setFileItem(((MultipartRequest)request).getFile("file"));
            queueEvent(evt);
        }
    }
	
   

     public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event); 
        if(getAttributes().get("fileUploadListener")!=null)
        {
        	MethodExpression fileUploadExp = (MethodExpression)getAttributes().get("fileUploadListener");
        	fileUploadExp.invoke(this.getFacesContext().getELContext(), new Object[]{((FileUploadEvent)event)});
        }
     }
     



}
