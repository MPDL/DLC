package de.mpg.mpdl.dlc.escidoc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;

import de.mpg.mpdl.dlc.core.DLCProperties;

/**
 * TODO Description.
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Login
{
    protected String userHandle;
    private static final int NUMBER_OF_URL_TOKENS = 2;

    /**
     * Logs in the given user with the given password.
     * 
     * @param userid The id of the user to log in.
     * @param password The password of the user to log in.
     * @return The handle for the logged in user.
     * @throws HttpException
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    protected String login(String userid, String password, URL fwurl) throws HttpException, IOException, ServiceException,
            URISyntaxException
    {
    	String frameworkUrl;
    	if (fwurl != null)
    	{
    		frameworkUrl = fwurl.toString();
    	}
    	else
    	{
    		frameworkUrl = DLCProperties.get("escidoc.core.url");
    	}
        StringTokenizer tokens = new StringTokenizer(frameworkUrl, "//");
        if (tokens.countTokens() != NUMBER_OF_URL_TOKENS)
        {
            throw new IOException("Url in the config file is in the wrong format, needs to be http://<host>:<port>");
        }
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
        if (hostPort.countTokens() != NUMBER_OF_URL_TOKENS)
        {
            throw new IOException("Url in the config file is in the wrong format, needs to be http://<host>:<port>");
        }
        String host = hostPort.nextToken();
        int port = Integer.parseInt(hostPort.nextToken());
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost(host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);
        client.executeMethod(login);
        // System.out.println("Login form post: " + login.getStatusLine().toString());
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(host, port, "/", false, client.getState().getCookies());
        // System.out.println("Logon cookies:");
        Cookie sessionCookie = logoncookies[0];
        /*
         * if (logoncookies.length == 0) { System.out.println("None"); } else { for (int i = 0; i < logoncookies.length;
         * i++) { System.out.println("- " + logoncookies[i].toString()); } }
         */
        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
        // System.out.println("Login second post: " + postMethod.getStatusLine().toString());
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }
        userHandle = null;
        Header[] headers = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
            }
        }
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }

   

    /**
     * Logs in the virr user and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws Exception
     */
    public String loginVirrUser(URL fwurl) throws Exception
    {
        if (this.userHandle == null)
        {
            String userId = DLCProperties.get("dlc.ingest.uid");
            String pwd = DLCProperties.get("dlc.ingest.pwd");
            return login(userId, pwd, fwurl);
        }
        else
        {
            return this.userHandle;
        }
    }
    
    public String getUserHandle()
    {
        return userHandle;
    }

    public void setUserHandle(String userHandle)
    {
        this.userHandle = userHandle;
    }
}
