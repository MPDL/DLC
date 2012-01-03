package de.mpg.mpdl.dlc.aa;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.UserAccountServiceBean;
import de.mpg.mpdl.dlc.vo.user.User;


@ManagedBean
@SessionScoped
@URLMapping(id="user", pattern = "/user", viewId = "/user.xhtml", onPostback=true)

public class UserBean {
	private static Logger logger = Logger.getLogger(UserBean.class);
	
	@ManagedProperty("#{loginBean}")	
	private LoginBean loginBean;
	
	private User user;
	
	@EJB
	private UserAccountServiceBean uaServiceBean;
	
	public UserBean() throws Exception
	{

	}
	
	@PostConstruct
	public void init()
	{
		this.user = loginBean.getUser();		
	}
	
	public String editUser() throws Exception
	{           
		UserAccount ua = uaServiceBean.updateUserAccount(user, loginBean.getUserHandle());
		loginBean.setUser(user);

		return "pretty:user";
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
  
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	

}
