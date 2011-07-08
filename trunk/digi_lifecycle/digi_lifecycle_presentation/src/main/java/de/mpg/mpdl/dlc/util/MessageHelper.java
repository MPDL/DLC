package de.mpg.mpdl.dlc.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class MessageHelper {

	public static void infoMessage(String msg)
	{
		addMessage(msg, null, FacesMessage.SEVERITY_INFO, null);
	}
	
	public static void errorMessage(String msg)
	{
		addMessage(msg, null, FacesMessage.SEVERITY_ERROR, null);
	}
	
	
	public static void warnMessage(String msg)
	{
		addMessage(msg, null, FacesMessage.SEVERITY_WARN, null);
	}
	
	private static void addMessage(String summary, String detail, Severity severity, String clientId)
	{
		FacesMessage facesMsg = new FacesMessage(severity, summary, detail);
		FacesContext.getCurrentInstance().addMessage(clientId, facesMsg);
	}
	
}
