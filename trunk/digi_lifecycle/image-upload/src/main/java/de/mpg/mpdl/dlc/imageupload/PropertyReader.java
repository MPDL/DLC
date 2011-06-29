package de.mpg.mpdl.dlc.imageupload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyReader {

public static Properties props;
	
	public static String getProperty(String key)
	{
		try {
			if(props==null)
			{
				props=new Properties();
				URL propUrl = ImageServlet.class.getClassLoader().getResource("image-upload.properties");
				InputStream is = propUrl.openStream();
				props.load(is);
				
			}
			return props.getProperty(key);
		} catch (IOException e) {
			Logger.getLogger(PropertyReader.class).error("Error loading properties", e);
			return null;
		}
	}

	
}
