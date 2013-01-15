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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;


@ManagedBean
@SessionScoped
public class InternationalizationHelper extends Observable
{

    public static final String BEAN_NAME = "InternationalizationHelper";
    private static Logger logger = Logger.getLogger(InternationalizationHelper.class);
    public static final String LABEL_BUNDLE = "Label";
    public static final String MESSAGES_BUNDLE = "Messages";
    //TODO help_page
    public static final String HELP_PAGE_DE = "http://dlcproject.wordpress.com/guidelines/";
    public static final String HELP_PAGE_EN = "http://dlcproject.wordpress.com/guidelines/";
    
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
            super.setChanged();
            super.notifyObservers();
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
    
    /*
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(placeholder);
    }
    
    public String getMessage(String placeholder)
    {
        return ResourceBundle.getBundle(this.getSelectedMessagesBundle()).getString(placeholder);
    }
    
    */
    public String getLabelForEnum(String prefix, String enumString)
    {
    	try {
			enumString = enumString.toLowerCase().replaceAll("\\s", "_");
			return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(prefix + enumString);
		} catch (Exception e) {
			return enumString;
		}
    }
    

	public void setUserLocaleString(String userLocaleString) {
		this.userLocaleString = userLocaleString;
	} 

	public String getUserLocaleString() {
		return userLocaleString;
	}
	
	public List<SelectItem> getStructureTypeSelectItems()
	{
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		
		
		itemList.add(new SelectItem("TITLE_PAGE", getLabel("edit_elementType_title_page")));
		itemList.add(new SelectItem("FIGURE", getLabel("edit_elementType_figure")));
		itemList.add(new SelectItem("acknowledgement", getLabel("structuretype_acknowledgement")));
		itemList.add(new SelectItem("additional", getLabel("structuretype_additional")));
		itemList.add(new SelectItem("advertisement", getLabel("structuretype_advertisement")));
		itemList.add(new SelectItem("appendix", getLabel("structuretype_appendix")));
		itemList.add(new SelectItem("article", getLabel("structuretype_article")));
		itemList.add(new SelectItem("bibliography", getLabel("structuretype_bibliography")));
		itemList.add(new SelectItem("book", getLabel("structuretype_book")));
		itemList.add(new SelectItem("chapter", getLabel("structuretype_chapter")));
		itemList.add(new SelectItem("content", getLabel("structuretype_content")));
		itemList.add(new SelectItem("corrigenda", getLabel("structuretype_corrigenda")));
		itemList.add(new SelectItem("curriculum vitae", getLabel("structuretype_curriculum_vitae")));
		itemList.add(new SelectItem("dedication", getLabel("structuretype_dedication")));
		itemList.add(new SelectItem("imprimatur", getLabel("structuretype_imprimatur")));
		itemList.add(new SelectItem("imprint", getLabel("structuretype_imprint")));
		itemList.add(new SelectItem("index", getLabel("structuretype_index")));
		itemList.add(new SelectItem("letter", getLabel("structuretype_letter")));
		itemList.add(new SelectItem("miscellaneous", getLabel("structuretype_miscellaneous")));
		itemList.add(new SelectItem("miscellany", getLabel("structuretype_miscellany")));
		itemList.add(new SelectItem("musical notation", getLabel("structuretype_musical_notation")));
		itemList.add(new SelectItem("part", getLabel("structuretype_part")));
		itemList.add(new SelectItem("preface", getLabel("structuretype_preface")));
		itemList.add(new SelectItem("privileges", getLabel("structuretype_privileges")));
		itemList.add(new SelectItem("provenance", getLabel("structuretype_provenance")));
		itemList.add(new SelectItem("review", getLabel("structuretype_review")));
		itemList.add(new SelectItem("section", getLabel("structuretype_section")));
		
			
		Collections.sort(itemList, new Comparator<SelectItem>() {

			@Override
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareTo(o2.getLabel());
	
			}
		});
		
		itemList.add(0, new SelectItem("free", getLabel("edit_elementType_free")));
		
		return itemList;

	}
	
	public static String getMessage (String name)
    {
    	return getResource("Messages", name);
    }
	
	public static String getLabel (String name)
    {
    	return getResource("Label", name);
    }
	
	public static String getTooltip (String name)
    {
    	return getResource("Tooltips", name);
    }
	
	public static String getCodicologicalLabel (String name)
    {
    	return getResource("CodicologicalLabel", name);
    }
	
	
	
	
	 public static String getResource (String bundle, String name)
	    {
	    	try{
	    	Application application = FacesContext.getCurrentInstance().getApplication();
		    InternationalizationHelper i18nHelper = (InternationalizationHelper) application.getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "internationalizationHelper");
		    ResourceBundle rBundle = ResourceBundle.getBundle(bundle, i18nHelper.getUserLocale());
		    return rBundle.getString(name);
	    	}
	    	catch(Exception e)
	    	{
	    		//logger.warn("Value: " + name + " not found in resource bundle: " + bundle);
	    		return "???" + name + "???";
	    	}
	    }
	
	

}
