package de.mpg.mpdl.dlc.util;

import java.net.URLEncoder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsTitle;

@ManagedBean
@RequestScoped
public class VolumeUtilBean {

	private static Logger logger = Logger.getLogger(VolumeUtilBean.class);

	
	public static String getDigilibScalerUrlForPage(Page p, int width, int height)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.scaler.url");
			String url = null;
			if(p.getFile().getHref()!=null)
				url = digilibUrl + "?fn=" + URLEncoder.encode(p.getFile().getHref(), "UTF-8") + "&dh=" + height + "&dw=" + width;
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for image", e);
			return null;
		}
	}
	
	public static String getDigilibJQueryUrlForPage(Page p)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.jquery.url");
			String url = digilibUrl + "?fn=" + URLEncoder.encode(p.getFile().getHref(), "UTF-8");
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for image", e);
			return null;
		}
	}
	
	public static ModsTitle getMainTitle(ModsMetadata md)
	{
		
		for(ModsTitle mt : md.getTitles())
		{
			if(mt.getType() == null || mt.getType().isEmpty())
			{
				return mt;
				
			}
		}
		return new ModsTitle();
	}

		
}
