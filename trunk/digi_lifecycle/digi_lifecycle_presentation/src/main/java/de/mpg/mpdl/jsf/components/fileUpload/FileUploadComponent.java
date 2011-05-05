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
public class FileUploadComponent extends UINamingContainer implements ActionSource2{

	private MethodExpression exp;
	List<ActionListener> listeners=new LinkedList<ActionListener>();
	
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

	


    public MethodExpression getActionExpression() {
        return exp;
    }

   
    public void setActionExpression(MethodExpression action) {
        exp=action; 
    }

    
    public MethodBinding getAction() {
        return exp != null ? new MethodBindingMethodExpressionAdapter(exp): null;
    }

    
    public void setAction(MethodBinding action) {
        setActionExpression(new MethodExpressionMethodBindingAdapter(action));
    }

    private MethodBinding actionListener;

    
    public MethodBinding getActionListener() {
        return actionListener;
    }

    
    public void setActionListener(MethodBinding actionListener) {
        this.actionListener=actionListener;
    }

    private boolean i;

   
    public boolean isImmediate() {
        return i;
    }

    
    public void setImmediate(boolean immediate) {
        this.i=immediate;
    }


    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

   
    public ActionListener[] getActionListeners() {
        return listeners.toArray(new ActionListener[0]);
    }

    
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }

   

     public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
		if(event instanceof FileUploadEvent) 
		{
            FacesContext context = getFacesContext(); 
            
            for(ActionListener al : getActionListeners())
            {
            	al.processAction((FileUploadEvent)event);
            }
            
        
		}
     }
     



}
