package de.mpg.mpdl.dlc.base;

import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import de.mpg.mpdl.dlc.util.BeanHelper;

public class MessagesBean extends BeanHelper{

	public MessagesBean()
	{
		//Show logout message if logged out
		Map<String, String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (parameters.containsKey("loggedOut"))
        {
        	if(parameters.get("loggedOut").equals("normal"))
        	{
        		info(getMessage("logout_successful"));
        	}
        	 
        	else if (parameters.get("loggedOut").equals("forced"))
        	{
        		error(getMessage("login_wrongRights"));
        	}
        }
	}
	
	 public boolean getHasErrorMessages()
	    {
	        for (Iterator<FacesMessage> i = FacesContext.getCurrentInstance().getMessages(); i.hasNext();)
	        {
	            FacesMessage fm = i.next();
	            if (fm.getSeverity().equals(FacesMessage.SEVERITY_ERROR) || fm.getSeverity().equals(FacesMessage.SEVERITY_WARN) || fm.getSeverity().equals(FacesMessage.SEVERITY_FATAL))
	            {
	                return true;
	            }
	        }
	        return false;
	    }
	    
	    public int getNumberOfMessages()
	    {
	        int number = 0;
	        for (Iterator<FacesMessage> i = FacesContext.getCurrentInstance().getMessages(); i.hasNext();)
	        {
	            i.next();
	            number++;
	        }
	        return number;
	    }
	    
	    public boolean getHasMessages()
	    {
	    	return FacesContext.getCurrentInstance().getMessages().hasNext();
	    }
}
