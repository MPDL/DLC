package de.mpg.mpdl.dlc.util;

import java.net.URI;

import javax.faces.context.FacesContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class UrlHelper
{
    public static String getParameterValue(String parameterName)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
    }

    public static boolean getParameterBoolean(String parameterName)
    {
        String str = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
        if ("1".equals(str))
        {
            return true;
        }
        return false;
    }

    public static boolean isValidURI(URI uri)
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(uri.toString());
            client.executeMethod(method);
            return true;
        }
        catch (Exception e)
        {
            BeanHelper.error("'" + uri + "' is not a valid URL");
        }
        return false;
    }
}
