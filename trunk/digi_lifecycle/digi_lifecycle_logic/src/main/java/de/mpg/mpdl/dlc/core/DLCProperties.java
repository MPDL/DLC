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
package de.mpg.mpdl.dlc.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;


public class DLCProperties
{
    private static Properties properties;
    private static final String DEFAULT_PROPERTY_FILE = "dlc_core.properties";
    
    public static String get(String key)
    {
        if (properties == null)
        {
            loadProperties();
        }
        String value = properties.getProperty(key);
        return value;
    }
    
    private static void loadProperties()
    {
        String propertiesFile = DEFAULT_PROPERTY_FILE;
        InputStream instream = getInputStream(propertiesFile);
        properties = new Properties();
        try
        {
            properties.load(instream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        properties.putAll(properties);
    }
    
    private static InputStream getInputStream(String filepath)
    {
        InputStream instream = null;
        try
        {
            instream = new FileInputStream(filepath);
        }
        catch (Exception e)
        {
            URL url = DLCProperties.class.getClassLoader().getResource(filepath);
            if (url != null)
            {
                try
                {
                    instream = url.openStream();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        if (instream == null)
        {
            try
            {
                throw new FileNotFoundException(filepath);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return instream;
    }
    
    public static Enumeration<Object> getKeys()
    {
        if (properties == null)
        {
            loadProperties();
        }
        return properties.keys();
    }
    
    public static void sysProps()
    {
        System.getProperties().list(System.out);
    }
    
    public static String envVars()
    {
        return System.getenv("FEDORA_HOME");
    }
}
