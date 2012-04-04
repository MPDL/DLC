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
package de.mpg.mpdl.dlc.util;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;


@ManagedBean
@SessionScoped
public class InternationalizationHelper
{

    public static final String BEAN_NAME = "InternationalizationHelper";
    private static Logger logger = Logger.getLogger(InternationalizationHelper.class);
    public static final String LABEL_BUNDLE = "Label";
    public static final String MESSAGES_BUNDLE = "Messages";
    public static final String HELP_PAGE_DE = "help/virr_help_de.html";
    public static final String HELP_PAGE_EN = "help/virr_help_en.html";
    private String selectedHelpPage;
    private String userLocaleString = "en";
    private List<String> test = new ArrayList<String>();
    
    
    
    private Locale userLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();

    /**
     * Public constructor.
     */
    public InternationalizationHelper()
    { 
    	userLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        Iterator<Locale> supportedLocales = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
             
        boolean found = false;
        while (supportedLocales.hasNext())
        {
            Locale supportedLocale = supportedLocales.next();
            if (supportedLocale.getLanguage().equals(userLocale.getLanguage()))
            {
                found = true;
                break;
            }
        }
        if (!found)
        {
            userLocale = new Locale("en");
        }
        
        if (userLocale.getLanguage().equals("de"))
        {
            selectedHelpPage = HELP_PAGE_DE;
        }
        else
        {
            selectedHelpPage = HELP_PAGE_EN;
        }
        userLocaleString = userLocale.getLanguage();
    }

    // Getters and Setters
    public String getSelectedLabelBundle()
    {
        return LABEL_BUNDLE + "_" + userLocale.getLanguage();
    }

    public String getSelectedMessagesBundle()
    {
        return MESSAGES_BUNDLE + "_" + userLocale.getLanguage();
    }

    public String getSelectedHelpPage()
    {
        return selectedHelpPage;
    }
 
    /**
     * toggle the locale.
     * @param event
     */
    public void changeLanguage(ValueChangeEvent event)
    {
    	FacesContext fc = FacesContext.getCurrentInstance();
    	if (event.getOldValue() != null && !event.getOldValue().equals(event.getNewValue()))
    	{
    		Locale locale = null;
    		String language = event.getNewValue().toString();
            String country = language.toUpperCase();
            this.userLocaleString = language;
            try
            {
                locale = new Locale(language, country);
                fc.getViewRoot().setLocale(locale);
                Locale.setDefault(locale);
                userLocale = locale;
                logger.debug("New locale: " + language + "_" + country + " : " + locale);
            }
            catch (Exception e)
            {
                logger.error("unable to switch to locale using language = " + language + " and country = " + country, e);
            }
            if (language.equals("de"))
            {
                selectedHelpPage = HELP_PAGE_DE;
            }
            else
            {
                selectedHelpPage = HELP_PAGE_EN;
            }
    	}
    }

    public Locale getUserLocale()
    {
        return userLocale;
    }

    public void setUserLocale(final Locale userLocale)
    {
        this.userLocale = userLocale;
    }

    /**
     * return test list.
     * @return List
     */
    public List<String> getTest()
    {
        if (test.isEmpty())
        {
            test.add("AAA");
            test.add("BBB");
        }
        return test;
    }

    public void setTest(List<String> test)
    {
        this.test = test;
    }
    
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(placeholder);
    }
    
    public String getLabelForEnum(String prefix, String enumString)
    {
    	try {
			enumString = enumString.toLowerCase().replaceAll("\\s", "_");
			return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(prefix + enumString);
		} catch (Exception e) {
			return "???" + prefix + enumString + "???";
		}
    }
    

	public void setUserLocaleString(String userLocaleString) {
		this.userLocaleString = userLocaleString;
	} 

	public String getUserLocaleString() {
		return userLocaleString;
	}

}
