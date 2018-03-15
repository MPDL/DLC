/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.beans;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;
import org.richfaces.event.ItemChangeEvent;

import com.ocpsoft.pretty.PrettyContext;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.user.User;



@ManagedBean
@SessionScoped
public class LoginBean 
{
    private boolean login;
    private static Logger logger = Logger.getLogger(LoginBean.class);
    private static final String LOGIN_URL = "aa/login?target=$1";
//    public static final String LOGOUT_URL = "aa/logout/clear.jsp?target=$1";
    public static final String LOGOUT_URL = "aa/logout?target=$1";
    private String userHandle;
    private User user;
    private String tab = "toc";
    
    private Authentication auth;
    private String userName;
    private String userPass;
    
    private UserAccountServiceBean userAccountServiceBean = new UserAccountServiceBean();
    
    public String getTab() 
    {
		return tab;
	}

	public void setTab(String tab) 
	{
		this.tab = tab;
	}

	public void changeTab(ItemChangeEvent event)
	{  
		setTab(event.getNewItemName());
	}
	

	public LoginBean()
	{
		this.login = false;
		this.user = null;
		this.userHandle = null;
		this.auth = null;
		this.userName = null;
		this.userPass = null;
	}
	


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
                    this.user = userAccountServiceBean.retrieveCurrentUser(newUserHandle);
                    MessageHelper.infoMessage(InternationalizationHelper.getMessage("login_successful"));
                }
                catch (Exception e)
                {
                    this.userHandle = null;
                    this.login = false;
                    this.user = null;
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
 

	public String getUserHandle() 
	{
 		return userHandle;
	}

	public void setUserHandle(String userHandle) 
	{
		this.userHandle = userHandle;
	}
    
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public final String login()
	{    
		FacesContext fc = FacesContext.getCurrentInstance();
		PrettyContext pc = PrettyContext.getCurrentInstance();
		try {
			if (this.getUserName().isEmpty() || this.getUserPass().isEmpty()) {
                MessageHelper.infoMessage(InternationalizationHelper.getMessage("username and/or password MUST NOT be empty!"));
			} else {
			auth = new Authentication(new URL(PropertyReader.getProperty("escidoc.common.login.url")), this.getUserName(), this.getUserPass());
			String handle = auth.getHandle();
			String base64UserHandle = Base64.encode(handle.getBytes());

    		String requestURL = PropertyReader.getProperty("dlc.instance.url") + pc.getContextPath()+pc.getRequestURL().toString();
    		URI uri = new URI(requestURL+ "?eSciDocUserHandle="+ base64UserHandle);
    		// URI redirectUri = UriBuilder.fromUri(requestURL).queryParam("eSciDocUserHandle", base64UserHandle).build();
			fc.getExternalContext().redirect(uri.toString());
			}
			
		} catch (AuthenticationException | TransportException | IOException | URISyntaxException e1) {
            MessageHelper.infoMessage(InternationalizationHelper.getMessage("ERROR! " + e1.getMessage()));
			logger.error("Login error", e1);
		}
				
		
//		HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
//		String requestURL = request.getRequestURL().toString();
		/*
        try 
        {
    		String requestURL = PropertyReader.getProperty("dlc.instance.url") + pc.getContextPath()+pc.getRequestURL().toString();
			fc.getExternalContext().redirect(getLoginUrl().replace("$1", URLEncoder.encode(requestURL, "UTF-8")));
            

		} 
        catch (Exception e) 
        {
			logger.error("Error redirecting to login page", e);
		}
		*/
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
				this.user = null;
			
				FacesContext fc = FacesContext.getCurrentInstance();
				
				//logout from escidoc
	    		//String requestURL =PropertyReader.getProperty("dlc.instance.url")+ "/"+ PropertyReader.getProperty("dlc.context.path") + "/";
				//fc.getExternalContext().redirect(getLogoutUrl().replace("$1", URLEncoder.encode(requestURL, "UTF-8")));
				auth.logout();
				//delete session
		        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		        session.invalidate();
				
				//Direct to hompage when log out, because of changing rights.
//				String dlc_URL = PropertyReader.getProperty("dlc.instance.url") + "/" + PropertyReader.getProperty("dlc.context.path") ;
//				fc.getExternalContext().redirect(dlc_URL);
				
                MessageHelper.infoMessage(InternationalizationHelper.getMessage("logout_successful"));



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
    	return PropertyReader.getProperty("escidoc.common.login.url") + LOGIN_URL;
    }
    
    public static String getLogoutUrl() throws Exception
    {
    	return PropertyReader.getProperty("escidoc.common.login.url") + LOGOUT_URL;
    }

    

	/**
	 * Called by javascript poll every xx seconds to refresh the session timeout
	 */
	public void renewUserHandle()
	{
		try {
			logger.info("Trying to refresh user handle");
			userAccountServiceBean.refreshUserHandle(getUserHandle());
		} catch (Exception e) {
			logger.error("Error while refreshing user handle", e);
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public Authentication getAuth() {
		return auth;
	}

	public void setAuth(Authentication auth) {
		this.auth = auth;
	}
}
