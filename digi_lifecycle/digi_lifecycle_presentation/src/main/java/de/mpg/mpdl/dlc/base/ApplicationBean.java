/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.dlc.base;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;



@ManagedBean
@ApplicationScoped
public class ApplicationBean
{
    private static final long serialVersionUID = 1L;

    public static final String APP_TITLE = "Digitization Lifecycle";
    private String appContext = "";
    private static Logger logger = Logger.getLogger(ApplicationBean.class);

    /**
     * Public constructor.
     */
    public ApplicationBean()
    {
        this.init();
    }

    /**
     * This method is called when this bean is initially added to application scope. Typically, this occurs as a result
     * of evaluating a value binding or method binding expression, which utilizes the managed bean facility to
     * instantiate this bean and store it into application scope.
     */
    public void init()
    {

    }

    /**
     * Returns an appropriate character encoding based on the Locale defined for the current JavaServer Faces view. If
     * no more suitable encoding can be found, return "UTF-8" as a general purpose default. The default implementation
     * uses the implementation from our superclass, FacesBean.
     * 
     * @return the local character encoding
     */
    public String getLocaleCharacterEncoding()
    {
        return System.getProperty("file.encoding"); // super.getLocaleCharacterEncoding();
    }


    /**
     * Returns the title and version of the application, shown in the header.
     * 
     * @return applicationtitle, including version
     */
    public String getPureAppTitle()
    {
           return ApplicationBean.APP_TITLE;
    }


    /**
     * Returns the current application context.
     * 
     * @return the application context
     */
    public String getAppContext()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        this.appContext = fc.getExternalContext().getRequestContextPath() + "/faces/";
        logger.info(appContext);
        return appContext;
    }

    /**
     * Returns the current application context.
     * 
     * @return the application context
     */
    public String getContextPath()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        this.appContext = fc.getExternalContext().getRequestContextPath();
        return appContext;
    }

    /**
     * Sets the application context.
     * 
     * @param appContext the new application context
     */
    public void setAppContext(String appContext)
    {
        this.appContext = appContext;
    }

    /**
     * creates list of VirrBooks used for the tree model.
     * 
     * @return ArrayList of Books
     * @throws ServiceException (SearchHandler)
     * @throws URISyntaxException (SearchHandler)
     * @throws Exception (XmlException)
     */
     
    public String createFilterParam(List<String> ids)
    {
        StringBuffer param = new StringBuffer();
        param.append("<param>");
        for (String id : ids)
        {
            param.append("<filter name=\"/id\">" + id + "</filter>");
        }
        param.append("</param>");
        return param.toString();
    }



    public String getHelpAnchor()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        String helpAnchor = "";
        if (fc.getViewRoot().getViewId() != null)
        {
            helpAnchor = fc.getViewRoot().getViewId().replace("/", "");
            helpAnchor = "#" + helpAnchor.replaceAll(".jsp", "");
        }
        return helpAnchor;
    }




    
   
}
