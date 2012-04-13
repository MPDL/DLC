package de.mpg.mpdl.dlc.util;

import java.awt.peer.SystemTrayPeer;
import java.net.URL;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.pretty.PrettyContext;

import de.escidoc.core.resources.om.item.Item;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.user.User;

@ManagedBean
@RequestScoped
public class UtilBean {

	public static int getListSize(List list)
	{
		if(list!= null)
		{
			return list.size();
		}
		else
		{
			return 0;
		}
	}
	
	public  String getAbsoluteUrl() throws Exception
	{
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		PrettyContext prettyContext = PrettyContext.getCurrentInstance();
		
		
		String file = prettyContext.getRequestURL().toString();
		if (!prettyContext.getRequestQueryString().toQueryString().isEmpty()) {
		   file += '?' + prettyContext.getRequestQueryString().toQueryString();
		}
		
		URL reconstructedURL = new URL(request.getScheme(),
		                               request.getServerName(),
		                               request.getServerPort(),
		                               file);
		
		return reconstructedURL.toExternalForm();

	}
	
	public void setAbsoluteUrl(String url) throws Exception
	{
	}
	
	public static String getUnqualifiedClassName(Object obj)
	{
		long start = System.currentTimeMillis();
		if(obj!=null)
		{
			Class cls = obj.getClass();
		    String name = cls.getName();
		    if (name.lastIndexOf('.') > 0)
		    {
		      name = name.substring(name.lastIndexOf('.') + 1); // Map$Entry
		      name = name.replace('$', '.');      // Map.Entry
		    }
		    long time = System.currentTimeMillis() - start;
			//System.out.println("Unqualified classname for " + obj + " took " +time);
		    return name;
		}
		
		return "null";
		
	}
	
	
	public static boolean ifOwner(Item item, User user)
	{

		String itemContextId = item.getProperties().getContext().getObjid(); 
		if(user != null && user.getModeratorContextIds().contains(itemContextId))
			return true;
		else if(user != null && item.getProperties().getCreatedBy().getObjid().equals(user.getId()))
				return true;
		else
			return false;
	}
	
	
	public static boolean editable(Volume volume)
	{

		if(volume.getItem().getComponents() == null || volume.getItem().getComponents().size() < 3)
			return true;
		else
			return false;
	}
}
