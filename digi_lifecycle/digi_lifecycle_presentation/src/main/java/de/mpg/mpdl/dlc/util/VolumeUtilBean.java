package de.mpg.mpdl.dlc.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mods.ModsIdentifier;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.mods.ModsNote;
import de.mpg.mpdl.dlc.vo.mods.ModsPublisher;
import de.mpg.mpdl.dlc.vo.mods.ModsTitle;
import de.mpg.mpdl.dlc.vo.teisd.Back;
import de.mpg.mpdl.dlc.vo.teisd.Body;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.Front;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;

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
			logger.error("Error getting URL for page " + p + "(" + width + "," + height + ")", e);
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
	
	public static int getSize(List<Object> objs)
	{
		if(objs != null)
			return objs.size();
		else
			return 0;
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
	
	public static ModsTitle getUniformTitle(ModsMetadata md)
	{
		for(ModsTitle mt : md.getTitles())
			if(mt.getType() == "uniform")
				return mt;
		return new ModsTitle();
	}
	
	public static ModsTitle getSubTitle(ModsMetadata md)
	{
		for(ModsTitle mt : md.getTitles())
		{
			if(mt.getSubTitle() != "" )
				return mt;
		}
		return new ModsTitle();
	}
	
	public static ModsName getFirstNamePart(ModsMetadata md)
	{
		for(ModsName mn : md.getNames())
			if(mn.getName() != null)
				return mn;
		
		return new ModsName();
	}
	
	public static ModsName getSecondNamePart(ModsMetadata md)
	{
		int i = 1;
		for(ModsName mn : md.getNames())
			if(mn.getName() != null)
			{
				i++;
				if(i>2)
					return mn;
			}
		return new ModsName();
	}
	
	public static ModsPublisher getMainPublisher(ModsMetadata md)
	{
		for(ModsPublisher mp : md.getPublishers())
			if(mp != null)
				return mp;
		return new ModsPublisher();
	}
	
	public static String[] getPublicationDates(Volume vol) throws Exception
	{
        int length = vol.getRelatedVolumes().size();
        String start ="";
        String end ="";
        if(length > 0 )
            start = getMainPublisher(getVolume(vol.getRelatedVolumes().get(0)).getModsMetadata()).getDateIssued_425().getDate();

	    if(length > 1)  
	        end = getMainPublisher(getVolume(vol.getRelatedVolumes().get(length-1)).getModsMetadata()).getDateIssued_425().getDate();

        return new String[] { start, end };
	}
	
	public static ModsNote getNoteSOR(ModsMetadata md)
	{
		for(ModsNote mn : md.getNotes())
			if(mn.getType() == "statementOfResponsibility")
				return mn;
		return new ModsNote();
	}
	
	public static ModsNote getMainNote(ModsMetadata md)
	{
		for(ModsNote mn : md.getNotes())
			if(mn.getType() == null || mn.getType().isEmpty())
				return mn;
		return new ModsNote();
	}
	
	
	public static Volume getVolume(String id) throws Exception
	{
		VolumeServiceBean volServiceBean = new VolumeServiceBean();
		LoginBean loginBean = new LoginBean();
		return volServiceBean.retrieveVolume(id, loginBean.getUserHandle());
	}

	

	public Pagebreak getPagebreakForPage (TeiSd tei, Page p)
	{

		if(tei!=null)
		{
			return (Pagebreak)tei.getDivMap().get(p.getId());
		}
		
		return null;
		
	}
	
	public Div getPartForPagebreak (Pagebreak p)
	{

		PbOrDiv parent = p.getParent();
		while(parent!=null)
		{
			if(parent instanceof Front || parent instanceof Body || parent instanceof Back)
			{
				return (Div)parent;
			}
			parent=parent.getParent();
		}
		return null;
		
	}
	
	
	
	public void removeListMember (Object pos, List list)
	{
		list.remove(pos);
	}
	
	public void addNewModsTitle (ModsTitle pos, List<ModsTitle> list)
	{

		if(pos!=null)
		{
			list.add(list.indexOf(pos)+1, new ModsTitle());
		}
		else
		{
			list.add(new ModsTitle());
		}
		
		
	}

	public void addNewModsName (ModsName pos, List<ModsName> list)
	{

		if(pos!=null)
		{
			list.add(list.indexOf(pos)+1, new ModsName());
		}
		else
		{
			list.add(new ModsName());
		}
	}
	
	public void addNewModsNote (ModsNote pos, List<ModsNote> list)
	{

		if(pos!=null)
		{
			list.add(list.indexOf(pos)+1, new ModsNote());
		}
		else
		{
			list.add(new ModsNote());
		}
		
		
	}
	
	public void addNewModsPublisher (ModsPublisher pos, List<ModsPublisher> list)
	{

		if(pos!=null)
		{
			list.add(list.indexOf(pos)+1, new ModsPublisher());
		}
		else
		{
			list.add(new ModsPublisher());
		}
		
		
	}
	
	public void addNewModsIdentifier (ModsIdentifier pos, List<ModsIdentifier> list)
	{

		if(pos!=null)
		{
			list.add(list.indexOf(pos)+1, new ModsIdentifier());
		}
		else
		{
			list.add(new ModsIdentifier());
		}
		
		
	}
	
	

		
}
