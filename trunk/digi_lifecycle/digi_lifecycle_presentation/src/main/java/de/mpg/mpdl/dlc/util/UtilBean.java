package de.mpg.mpdl.dlc.util;

import java.net.URL;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.pretty.PrettyContext;

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
		Class cls = obj.getClass();
	    String name = cls.getName();
	    if (name.lastIndexOf('.') > 0)
	    {
	      name = name.substring(name.lastIndexOf('.') + 1); // Map$Entry
	      name = name.replace('$', '.');      // Map.Entry
	    }
	    return name;
	}
}
