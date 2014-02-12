/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.beans;

import java.net.URLEncoder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;
import org.richfaces.event.ItemChangeEvent;

import com.ocpsoft.pretty.PrettyContext;

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
//		HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
//		String requestURL = request.getRequestURL().toString();
        try 
        {
    		String requestURL = PropertyReader.getProperty("dlc.instance.url") + pc.getContextPath()+pc.getRequestURL().toString();
			fc.getExternalContext().redirect(getLoginUrl().replace("$1", URLEncoder.encode(requestURL, "UTF-8")));
            

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
				this.user = null;
			
				FacesContext fc = FacesContext.getCurrentInstance();
				
				//logout from escidoc
	    		String requestURL =PropertyReader.getProperty("dlc.instance.url")+ "/"+ PropertyReader.getProperty("dlc.context.path") + "/";
				fc.getExternalContext().redirect(getLogoutUrl().replace("$1", URLEncoder.encode(requestURL, "UTF-8")));

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
}
