package de.mpg.mpdl.dlc.beans;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.Grants;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.om.context.Context;
import de.mpg.mpdl.dlc.util.PropertyReader;

@ManagedBean
@SessionScoped
public class LoginBean 
{
    private boolean login;
    private static Logger logger = Logger.getLogger(LoginBean.class);
    private static final String LOGIN_URL = "/aa/login?target=$1";
    public static final String LOGOUT_URL = "/aa/logout?target=$1";
    private String userHandle;
    private UserAccount userAccount;
    private Grants grants;
    private List<Context> depositorContexts = new ArrayList<Context>();
    
    
    
    public final boolean getLoginState()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        if (request.getParameter("eSciDocUserHandle") != null)
        {
        	String newUserHandle = null;
            try
            {
                newUserHandle = new String(Base64.decode(request.getParameter("eSciDocUserHandle")), "UTF-8");
            }
            catch (Exception e)
            {
                logger.error("Error decoding userHandle", e);
            }
            if (newUserHandle != null && !"".equals(newUserHandle) && !newUserHandle.equals(userHandle))
            {
                try
                {  
                	this.userHandle = newUserHandle;
                    this.login = true;
                    UserAccountHandlerClient client = new UserAccountHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
                    client.setHandle(userHandle);
                    this.userAccount = client.retrieveCurrentUser();
                    this.setGrants(client.retrieveCurrentGrants(userAccount));
                    
                    ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
                    depositorContexts.clear();
                    for(Grant grant : grants)
                    {
                    	if(grant.getProperties().getRole().getObjid().equals("escidoc:role-depositor"))
                    	{
                    		Context c = contextClient.retrieve(grant.getProperties().getAssignedOn().getObjid());
                    		depositorContexts.add(c);
                    	}
                    	
                    }
                    
                }
                catch (Exception e)
                {
                    this.userHandle = null;
                    this.login = false;
                    this.userAccount = null;
                    logger.error("Error authenticating user", e);
                }
            }
        }  
        return false;
    }
    
    
	public boolean isLogin() 
	{
		return login;
	}
	
	public void setLogin(boolean login) 
	{
		this.login = login;
	} 
 
	public UserAccount getUserAccount() 
	{ 
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserHandle() 
	{
 		return userHandle;
	}

	public void setUserHandle(String userHandle) 
	{
		this.userHandle = userHandle;
	}
    
	public final String login()
	{ 
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
		String requestURL = request.getRequestURL().toString();
        try 
        {
			fc.getExternalContext().redirect(getLoginUrl().replace("$1", requestURL));
		} 
        catch (Exception e) 
        {
			logger.error("Error redirecting to login page", e);
		}
		return null;
	}
	
	public final String logout()
	{
		if(userHandle != null)
		{
			try
			{
				this.userHandle = null;
				this.login = false;
				this.userAccount = null;
				FacesContext fc = FacesContext.getCurrentInstance();
				HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
				String requestURL = request.getRequestURL().toString();
                FacesContext.getCurrentInstance().getExternalContext().redirect(getLogoutUrl().replace("$1", requestURL));
			}
			catch(Exception e)
			{
				logger.error("Error logging out from framework",e);
			}
		}
		return null;
	}


    public static String getLoginUrl() throws Exception
    {
    	return PropertyReader.getProperty("escidoc.common.framework.url")+LOGIN_URL;
    }
    
    public static String getLogoutUrl() throws Exception
    {
    	return PropertyReader.getProperty("escidoc.common.framework.url")+LOGOUT_URL;
    }


	public Grants getGrants() {
		return grants;
	}


	public void setGrants(Grants grants) {
		this.grants = grants;
	}


	public List<Context> getDepositorContexts() {
		return depositorContexts;
	}


	public void setDepositorContexts(List<Context> depositorContexts) {
		this.depositorContexts = depositorContexts;
	}
    

}
