package de.mpg.mpdl.dlc.util;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.log4j.Logger;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import com.ocpsoft.pretty.PrettyContext;

import de.escidoc.core.resources.om.item.Item;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.vo.user.User;

@ManagedBean
@RequestScoped
public class UtilBean {
	
	private static String standardDateTimePattern = "dd.MM.yyyy - HH:mm";

	private static Logger logger = Logger.getLogger(UtilBean.class);
	static
	{
		try {
			URL in = UtilBean.class.getClassLoader().getResource("antisamy_policy/antisamy-slashdot-1.4.4.xml");
			policy = Policy.getInstance(in);
		} catch (PolicyException e) {
			logger.error("Could not instantiate antisamy policy", e);
		}  	
	}
	
	private static Policy policy;
	
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

	public String getStandardDateTimePattern() {
		return standardDateTimePattern;
	}

	public void setStandardDateTimePattern(
			String standardDateTimePattern) {
		UtilBean.standardDateTimePattern = standardDateTimePattern;
	}
	
	public String getShortenedText(String text, int length)
	{
		if(text!=null)
		{
			if(text.length()<=length)
			{
				return text;
			}
			
			else
			{
				String shortText = text.substring(0, length);
				
				int lastBlank = shortText.lastIndexOf(" ");
				if(lastBlank != -1)
				{
					return shortText.substring(0, lastBlank);
				}
				else
				{
					return shortText;
				}
			}
		}
		
		return "";
		
		
	}
	
	public String cleanHtml(String html)
	{
		try {
			AntiSamy as = new AntiSamy();
			CleanResults cr = as.scan(html, policy);
			//logger.info("Scan Time Antisamy: " +cr.getScanTime());
			return cr.getCleanHTML();
		} catch (ScanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PolicyException e) {
			logger.error("Could not sanitize html", e);
			
		}
		return "";
		
	}
	
	public UserAgent getUserAgent()
	{
		try {
			HttpServletRequest httpReq = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
			UserAgent ua = parser.parse(httpReq.getHeader("User-Agent"));
			return ua;
		} catch (Exception e) {
			logger.info("could not parse User Agent", e);
			return null;
		}
		
	}
}
