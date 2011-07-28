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
