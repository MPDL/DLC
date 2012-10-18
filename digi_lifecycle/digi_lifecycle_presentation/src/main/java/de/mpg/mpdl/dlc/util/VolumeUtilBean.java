package de.mpg.mpdl.dlc.util;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.apache.log4j.Logger;

import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.sb.search.HitWord;
import de.escidoc.core.resources.sb.search.TextFragment;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.images.ImageHelper;
import de.mpg.mpdl.dlc.images.ImageHelper.Type;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.vo.mets.MetsFile;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mods.ModsIdentifier;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsName;
import de.mpg.mpdl.dlc.vo.mods.ModsNote;
import de.mpg.mpdl.dlc.vo.mods.ModsPart;
import de.mpg.mpdl.dlc.vo.mods.ModsPublisher;
import de.mpg.mpdl.dlc.vo.mods.ModsRelatedItem;
import de.mpg.mpdl.dlc.vo.mods.ModsTitle;
import de.mpg.mpdl.dlc.vo.teisd.Back;
import de.mpg.mpdl.dlc.vo.teisd.Body;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.DocAuthor;
import de.mpg.mpdl.dlc.vo.teisd.Front;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.PersName;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;

@ManagedBean
@RequestScoped
public class VolumeUtilBean {

	private static Logger logger = Logger.getLogger(VolumeUtilBean.class);
 
	
	
	public static String getDigilibScalerUrlForPage(Page p, int width, int height, String additionalQuery)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.scaler.url");
			String url = null; 
			if(p.getContentIds()!=null)
				url = digilibUrl + "?fn=" + URLEncoder.encode(p.getContentIds(), "UTF-8") + "&dh=" + height + "&dw=" + width;
			if(additionalQuery!=null)
			{
				url += additionalQuery;
			}
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for page " + p + "(" + width + "," + height + ")", e);
			return null;
		}
	}
	

	public static String getDigilibScalerUrlForPage(Page p, int width, int height)
	{
		return getDigilibScalerUrlForPage(p, width, height, null);
	}
	
	public static String getDigilibScalerUrlForPagebreak(Pagebreak pb, int width, int height)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.scaler.url");
			String url = null;
			if(pb.getFacs()!=null)
				url = digilibUrl + "?fn=" + URLEncoder.encode(pb.getFacs(), "UTF-8") + "&dh=" + height + "&dw=" + width;
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for page " + pb + "(" + width + "," + height + ")", e);
			return null;
		}
	}
	
	public static String getImageServerUrl(String subUrl, String type)
	{
		return ImageHelper.getFullImageUrl(subUrl, Type.valueOf(type));
	}
	
	public static String getDigilibJQueryUrlForPage(Page p)
	{
		try {
			String digilibUrl = PropertyReader.getProperty("digilib.jquery.url");
			String url = digilibUrl + "?fn=" + URLEncoder.encode(p.getContentIds(), "UTF-8");
			return url;
		} catch (Exception e) {
			logger.error("Error getting URL for image", e);
			return null;
		}
	}
	
	public static int getSize(Collection<Object> objs)
	{
		if(objs != null)
			return objs.size();
		else
			return 0;
	}
	
//	public static ModsTitle getMainTitle(ModsMetadata md)
//	{
//		
//		for(ModsTitle mt : md.getTitles())
//		{
//			if(mt.getType() == null || mt.getType().isEmpty())
//			{
//				return mt;
//				
//			}
//		}
//		return new ModsTitle();
//	}
	
	
	public static ModsName getFirstAuthor(ModsMetadata md)
	{

		for(ModsName mn : md.getNames())
		{
			if("author1".equalsIgnoreCase(mn.getDisplayLabel()))
				return mn;
		}
		return new ModsName();
	}
	
	public static ModsName getFirstBody(ModsMetadata md)
	{
		for(ModsName mn : md.getNames())
		{
			if("body1".equalsIgnoreCase(mn.getDisplayLabel()))
				return mn;
		}
		return new ModsName();
	}
	
	
	//get alternative title or maintitle for titlelist and head
	public static ModsTitle getTitle(ModsMetadata md)
	{  
		for(ModsTitle mt : md.getTitles())
		{
			if("alternative".equalsIgnoreCase(mt.getType()))
				return mt;
			else if("mainTitle".equalsIgnoreCase(mt.getDisplayLabel()))
				return mt;
		}
		return new ModsTitle();
	}
	
	public static ModsPublisher getPublisher(ModsMetadata md)
	{ 
		for(ModsPublisher mp : md.getPublishers())
		{
			if("publisher1".equalsIgnoreCase(mp.getDisplayLabel()))
				return mp;
			else if("printer1".equalsIgnoreCase(mp.getDisplayLabel()))
				return mp;
		}
		return null;
	}

	public static ModsPublisher getFirstPublisher(ModsMetadata md)
	{ 
		for(ModsPublisher mp : md.getPublishers())
		{
			if("publisher1".equalsIgnoreCase(mp.getDisplayLabel()))
				return mp;
		}
		return null;
	}
	
	
	public static ModsName getFirstEditor(ModsMetadata md)
	{

		for(ModsName mn : md.getNames())
			if("editor1".equalsIgnoreCase(mn.getDisplayLabel()))
				return mn;
		return new ModsName();
	}
	
	public static ModsTitle getMainTitle(ModsMetadata md)
	{    
		for(ModsTitle mt : md.getTitles())
		{
			if("mainTitle".equalsIgnoreCase(mt.getDisplayLabel()))
				return mt;
		}
		return new ModsTitle();
	}	
	

	

	
	
	public static ModsTitle getAlternativeTitle(ModsMetadata md)
	{  
		for(ModsTitle mt : md.getTitles())
		{
			if("alternative".equalsIgnoreCase(mt.getType()))
				return mt;
		}
		return new ModsTitle();
	}
	

	
	public static ModsTitle getUniformTitle(ModsMetadata md)
	{
		for(ModsTitle mt : md.getTitles())
			if("uniform".equalsIgnoreCase(mt.getType()))
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
	
	public static ModsTitle getParallelTitle(ModsMetadata md)
	{
		for(ModsTitle mt : md.getTitles())
			if("parallelTitle".equalsIgnoreCase(mt.getDisplayLabel()))
				return mt;
		return new ModsTitle();
	}
	

	

	public static ModsRelatedItem getSeries451(ModsMetadata md)
	{
		for(ModsRelatedItem ms : md.getRelatedItems())
			if("series1".equalsIgnoreCase(ms.getDisplayLabel()))
				return ms;
		return new ModsRelatedItem();
	}
	
	
	public static ModsRelatedItem getSeries451a(ModsMetadata md)
	{
		for(ModsRelatedItem ms : md.getRelatedItems())
			if("series2".equalsIgnoreCase( ms.getDisplayLabel()))
				return ms;
		return new ModsRelatedItem();
	}
	
	public static ModsNote getNoteThesis(ModsMetadata md)
	{
		for(ModsNote mn : md.getNotes())
			if("thesis".equalsIgnoreCase( mn.getType()))
				return mn;
		return new ModsNote();
	}
	
	public static List<ModsNote> getFootNotes(ModsMetadata md)
	{ 
		List<ModsNote> mns = new ArrayList<ModsNote>();
		if(md != null)
			for(ModsNote mn : md.getNotes())
				if(mn.getType()!=null && mn.getType()=="" && mn.getNote() != "")
					mns.add(mn);
		return mns;
	}
	
	public static ModsIdentifier getISBN(ModsMetadata md)
	{
		for(ModsIdentifier mi : md.getIdentifiers())
			if(mi.getType().equals("isbn") && mi.getInvalid()!="yes")
				return mi;
		return new ModsIdentifier();
	}
	
	public static ModsIdentifier getISSN(ModsMetadata md)
	{
		for(ModsIdentifier mi : md.getIdentifiers())
			if(mi.getType().equals("issn") && mi.getInvalid()!="yes")
				return mi;
		return new ModsIdentifier();
	}	
	
	

//	
//	public static List<String> getKeywordsISBD(ModsMetadata md)
//	{
//		List<String> keywords = new ArrayList<String>();
//		int i =1;
//		String keyword="";
//		for(String kw : md.getKeywords())
//		{
//			keyword += kw;
//			i++;
//			if(i)
//			
//		}
//		return keywords;
//	}
	
	
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
	        int length = vol.getRelatedChildVolumes().size();
	        String start ="";
	        String end ="";
	        if(length > 0 ) 
	        {
	            //Volume reVol = getVolume(vol.getRelatedVolumes().get(0));
	            ModsPublisher pub = getMainPublisher(vol.getRelatedChildVolumes().get(0).getModsMetadata());
	        	if(pub != null && pub.getDateIssued_425() != null)
	        		start = pub.getDateIssued_425().getDate();
	        }
	
		    if(length > 1)  
	        {
	            //Volume reVol = getVolume(vol.getRelatedVolumes().get(length-1));
	            ModsPublisher pub = getMainPublisher(vol.getRelatedChildVolumes().get(length-1).getModsMetadata());
	        	if(pub != null && pub.getDateIssued_425() != null)
	        		end = pub.getDateIssued_425().getDate();
	        }
	
	        return new String[] { start, end };
	}
	
	public static ModsNote getNoteSOR(ModsMetadata md)
	{
		for(ModsNote mn : md.getNotes())
			if("statement of responsibility".equalsIgnoreCase(mn.getType()))
				return mn;
		return new ModsNote();
	}
	
	public static ModsNote getNoteSubseries(ModsMetadata md)
	{
		for(ModsNote mn : md.getNotes())
			if("subseries".equalsIgnoreCase(mn.getType()))
				return mn;
		return new ModsNote();
	}
	
	public static ModsPart getPart_361(ModsMetadata md)
	{
		for(ModsPart mp : md.getParts())
			if("constituent".equalsIgnoreCase(mp.getType()))
				return mp;
		return new ModsPart();
	}
	
	public static String getPart_089(ModsMetadata md)
	{
		for(ModsPart mp : md.getParts())
			if(mp.getVolumeDescriptive_089()!="" || mp.getVolumeDescriptive_089()!=null)
				return mp.getVolumeDescriptive_089();
		return null;
	}
	 
	public static ModsNote getMainNote(ModsMetadata md)
	{
		for(ModsNote mn : md.getNotes())
			if(mn.getType() == null || mn.getType().isEmpty())
				return mn;
		return new ModsNote();
	}
	
	
	
	public static String getFullTitleView(Volume vol)
	{
		String shortTitle = getShortTitleView(vol);
		String subTitle = getSubTitleView(vol);
		
		if(!isEmpty(subTitle))
		{
			return shortTitle + ". - " + subTitle + ".";
		}
		else
		{
			return shortTitle + ".";
		}
		
	}
	
	public static String getSubTitleView(Volume vol)
	{
		if(vol.getModsMetadata()!=null)
		{
			StringBuffer sb = new StringBuffer();
			ModsPublisher modsPublisher = VolumeUtilBean.getPublisher(vol.getModsMetadata());
			String publisher, place, dateIssued;
			publisher = place = dateIssued = null;
			
			if(modsPublisher!=null)
			{
				publisher = modsPublisher.getPublisher();
				place = modsPublisher.getPlace();
				if(modsPublisher.getDateIssued_425()!=null)
				{
					dateIssued = modsPublisher.getDateIssued_425().getDate();
				}
			}

					
				if(!isEmpty(place))
				{
					sb.append(place);
				}
				
				if(!isEmpty(place) && !isEmpty(publisher))
				{
					sb.append(" : ");
				}
				
				if(!isEmpty(publisher))
				{
					sb.append(publisher);
				}
				
				
				
				if(!isEmpty(dateIssued))
				{
					if(!isEmpty(place) || !isEmpty(publisher))
					{
						sb.append(", ");
					}
					sb.append(dateIssued);
				}

				
			return sb.toString();
	}
		return null;
	}
	
	public static String getShortTitleView(Volume vol)
	{
		if(vol.getModsMetadata()!=null)
		{
			StringBuffer sb = new StringBuffer();
			
			ModsName firstModsAuthor = VolumeUtilBean.getFirstAuthor(vol.getModsMetadata());
			
			ModsTitle modsTitle = VolumeUtilBean.getTitle(vol.getModsMetadata());
			
			ModsName firstModsEditor = VolumeUtilBean.getFirstEditor(vol.getModsMetadata());
			

			String firstAuthor, title, firstEditor;
			firstAuthor = title = firstEditor = null;
			
			if(firstModsAuthor!=null)
			{
				firstAuthor = firstModsAuthor.getName();
			}
			if(modsTitle!=null)
			{
				title = modsTitle.getTitle();
			}
			if(firstModsEditor!=null)
			{
				firstEditor = firstModsEditor.getName();
			}

			//Verfasser 1
			if(!isEmpty(firstAuthor))
			{
				sb.append(firstAuthor);
				sb.append(": ");
			}
			
			//Ansetzungstitel oder Titel
			if(!isEmpty(title))
			{
				sb.append(title);
			}
			else
			{
				// n/a falls kein Titel vorhanden 
				sb.append(InternationalizationHelper.getLabel("na_no_volume_title"));
			}
			
			//Herausgeber 1, falls kein Verfasser vorhanden
			if(isEmpty(firstAuthor) && !isEmpty(firstEditor))
			{
				sb.append(" / ");
				sb.append(firstEditor);
				sb.append(" (" + InternationalizationHelper.getLabel("view_dtls_editor_suffix") + ")");
				
			}
			
			
			return sb.toString();
			


		}
		
		return null;
	}
	
	public static String getVolumeShortTitleView(Volume vol)
	{
		
		if(vol.getModsMetadata()!=null)
		{
			StringBuffer sb = new StringBuffer();
			
			String part = VolumeUtilBean.getPart_089(vol.getModsMetadata());
			
			ModsTitle modsTitle = VolumeUtilBean.getMainTitle(vol.getModsMetadata());
			
			ModsPublisher modsPublisher = VolumeUtilBean.getPublisher(vol.getModsMetadata());
			

			String  title, dateIssued;
			title = dateIssued = null;
		
			if(modsTitle!=null)
			{
				title = modsTitle.getTitle();
			}
			
			if(modsPublisher!=null)
			{
				if(modsPublisher.getDateIssued_425()!=null)
				{
					dateIssued = modsPublisher.getDateIssued_425().getDate();
				}
			}
			
			if(!isEmpty(part))
			{
				sb.append(part + ". ");
			}
			if(!isEmpty(title))
			{
				sb.append(title);
			}
			
			return sb.toString();
		}
			
		return null;
		
	}
	
	public static String getVolumeSubTitleView(Volume vol)
	{
		
		if(vol.getModsMetadata()!=null)
		{
			ModsPublisher modsPublisher = VolumeUtilBean.getPublisher(vol.getModsMetadata());
			if(modsPublisher!=null && modsPublisher.getDateIssued_425()!=null && modsPublisher.getDateIssued_425().getDate()!=null)
			{
				return modsPublisher.getDateIssued_425().getDate();
			}
			
			
		}
			
		return null;
		
	}
	
	public static String getVolumeFullTitleView(Volume vol)
	{
		
		String shortTitle = getVolumeShortTitleView(vol);
		String subTitle = getVolumeSubTitleView(vol);
		
		if(!isEmpty(subTitle))
		{
			return shortTitle + ". - " + subTitle + ".";
		}
		else
		{
			return shortTitle + ".";
		}
	}
	
	
	
	public static boolean isEmpty(String s)
	{
		return s==null || s.isEmpty();
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
	
	public void removeListMember (Integer row, List<SearchCriterion> scList)
	{  
		Object o = scList.get(row);
		scList.remove(o);
	} 
	
	public void addNewModsKeyword (Integer rowKey, List<String> keywords)
	{
		keywords.add(rowKey+1, "");
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
 
	public void addNewModsName (Integer rowKey, List<ModsName> list)
	{ 
  
		list.add(rowKey+1, new ModsName());

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
	
	
	public void addNewTeiDocAuthor(Integer pos, List<DocAuthor> list)
	{

		if(pos!=null)
		{
			list.add(pos+1, new DocAuthor());
		}
		else
		{
			list.add(new DocAuthor());
		}
		
		
	}
	
	public void addNewPersName(Integer pos, List<PersName> list)
	{

		if(pos!=null)
		{
			list.add(pos+1, new PersName());
		}
		else
		{
			list.add(new PersName());
		}
		
		
	}
	
	public void addNewSearchCriterion(SearchCriterion sc, List<SearchCriterion> scList)
	{		
			scList.add(scList.indexOf(sc)+1, new SearchCriterion(SearchType.FREE, ""));		
	}
	
	public void addNewContextList(List<SearchCriterion> contextList)
	{		
			contextList.add(new SearchCriterion(SearchType.CONTEXT_ID, ""));		
	}
	
	public String emphasizeWordsinTextFragment(TextFragment tf, String styleClass)
	{
		//long start = System.currentTimeMillis();
		StringBuffer emphasizedText = new StringBuffer();
		
		int lastEnd = -1;
		
		for(HitWord hw : tf.getHitWords())
		{

			int startIndex = (int) hw.getStartIndex();
			int endIndex = (int) hw.getEndIndex();
		
			
			//Add string before hit
			emphasizedText.append(tf.getTextFragmentData().substring(lastEnd + 1, startIndex));
			
			//Add opening <span> tag
			emphasizedText.append("<span class=\"" + styleClass + "\">");
			
			//Add search hit
			emphasizedText.append(tf.getTextFragmentData().substring(startIndex, endIndex+1));
			
			//Add closing </span> tag
			emphasizedText.append("</span>");
			
			lastEnd = endIndex;
		}
		
		//Add string after last search hit
		emphasizedText.append(tf.getTextFragmentData().substring(lastEnd + 1, tf.getTextFragmentData().length()));
		
		//long time = System.currentTimeMillis() - start;
		//System.out.println("Time emphasizing: " + time);
		return emphasizedText.toString();
		
		
	}
	
	public int getPageNumberForPageId(Volume v, String pageId)
	{
		for(Page p : v.getPages())
		{
			if(p.getId().equals(pageId))
			{
				return v.getPages().indexOf(p) + 1;
			}
		}
		
		return 0;
		
	}
	
	
	/**
	 * Creates a HTML <span> Element with the given styleClass around every case-insensitive occurrence of the given words in the text.
	 * Also splitted words with dashes(-) are taken into account
	 */
	public String emphasizeWordsinHtml(String html, String[] words, String styleClass)
	{
		StringBuffer emphasizedText = new StringBuffer();
		StringBuffer wordPattern = new StringBuffer();
		
		int n=0;
		for(String word : words)
		{

			if(word.trim().length()>0)
			{
			
			
				if(n>0)
				{
					wordPattern.append("|");
				}
				wordPattern.append("(\\b");
				
				//Also match words that are seperated by a dash and a linebreak
				StringBuffer wordWithDashMatches = new StringBuffer();
				for(char c : word.toCharArray())
				{
					if(c != ' ')
					{
						wordWithDashMatches.append(c + "(\\-<br\\s*/>){0,1}");
					}
					else wordWithDashMatches.append(c);
				}
				
				word = wordWithDashMatches.toString();
				
				String[] splittedWords = word.split(" ");
				
				//Replace blanks with a pattern that matches any whitespaces and any empty html element.
				for(int i = 0; i<splittedWords.length; i++)
				{
					String splittedWord = splittedWords[i];
					if(i>0)
					{
						wordPattern.append("(\\s+|\\s*<[\\S]+\\s*/>\\s*)");
					}
					wordPattern.append(splittedWord);
				}
				wordPattern.append("\\b)");
				n++;
			}

		}
		//System.out.println(wordPattern);
		
		if(n>0)
		{
			Pattern myPattern = Pattern.compile(wordPattern.toString(), Pattern.CASE_INSENSITIVE);
			Matcher matcher = myPattern.matcher(html);
			
			//add a <span> element around each match
			while(matcher.find())
			{
				
				matcher.appendReplacement(emphasizedText, "<span class=\"" + styleClass + "\">" + matcher.group() + "</span>");
			}
			matcher.appendTail(emphasizedText);
			
			return emphasizedText.toString();
		}
		return html;
		
	}
	
	public String replaceLineBreaksWithBlanks(String teiText)
	{
		
		String replacedText = teiText.replaceAll("<lb\\s*/>", " ");
		return replacedText;
		
	}
	
	
	public static List<PbOrDiv> getListWithoutPagebreaks(List<PbOrDiv> givenList)
	
	{
		List<PbOrDiv> returnList = new ArrayList<PbOrDiv>();
		for(PbOrDiv given : givenList)
		{
			if(!(given instanceof Pagebreak))
			{
				returnList.add(given);
			}
		}
		return returnList;
		
		
	}
	
	
	
	
	public static Page getTitlePage(Volume v)
	{
		if(v.getPages()!=null && v.getPages().size()>0)
		{
			
			for(Page p : v.getPages())
			{
				if("titlePage".equalsIgnoreCase(p.getType()))
				{
					return p;
				}
			}
			
			return v.getPages().get(0);
		}
		
		return null;
		
	}
	
	
	public static String getTypeAttribute(XdmNode pbNode)
	{
		if(pbNode!=null)
		{
			return pbNode.getAttributeValue(new QName("type"));
		}
		
		return null;
		
		
		
	}
	

	
	
	

		
}
