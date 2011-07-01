package de.mpg.mpdl.dlc.vo;

import gov.loc.mets.DivType;
import gov.loc.mets.FileType;
import gov.loc.mets.MetsDocument;
import gov.loc.mets.MetsType.FileSec.FileGrp;
import gov.loc.mets.StructMapType;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.w3c.dom.Node;


import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.Properties;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;

public class Volume {

	private List<Page> pages = new ArrayList<Page>();
	private ModsType modsMetadata;
	private ItemProperties properties;
	private Item item;
	

	public Volume ()
	{
	
	}
	
	public Volume (Item item) throws Exception
	{
		this.item = item;
		this.properties = item.getProperties();
		MetadataRecord mdRec = item.getMetadataRecords().get("escidoc");
		MetsDocument metsDoc = MetsDocument.Factory.parse(mdRec.getContent());
		
		Node child = metsDoc.getMets().getDmdSecArray(0).getMdWrap().getXmlData().getDomNode().getFirstChild();
		
		XmlCursor xmlDataCursor = metsDoc.getMets().getDmdSecArray(0).getMdWrap().getXmlData().newCursor();
		xmlDataCursor.toChild(0);
		ModsDocument modsDoc = ModsDocument.Factory.parse(xmlDataCursor.getDomNode());
		xmlDataCursor.dispose();
		this.modsMetadata = modsDoc.getMods();
		
		Map<String, FileType> fileMap = new HashMap<String, FileType>();
		
		for(FileGrp fileGroup : metsDoc.getMets().getFileSec().getFileGrpArray())
		{
			System.out.println(fileGroup.getFileArray(0).getFLocatArray().length);
			if(fileGroup.getUSE().equals("scans"))
			{
				for(FileType fileType : fileGroup.getFileArray())
				{
					fileMap.put(fileType.getID(), fileType);
					System.out.println(fileType.getFLocatArray().length);
				}
				
			}
		}
		
		for(StructMapType structMap : metsDoc.getMets().getStructMapArray())
		{
			if(structMap.getTYPE().equals("physical"))
			{
				DivType mainDiv = structMap.getDiv();
				for(DivType pageDiv : mainDiv.getDivArray())
				{
					FileType fileType = fileMap.get(pageDiv.getFptrArray(0).getFILEID());
					Page p = new Page(pageDiv.getORDERLABEL(), fileType.getFLocatArray(0).getHref());
					getPages().add(p);
				}
				
			}
		}
		
		
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setModsMetadata(ModsType modsMetadata) {
		this.modsMetadata = modsMetadata;
	}

	public ModsType getModsMetadata() {
		return modsMetadata;
	}

	public void setProperties(ItemProperties properties) {
		this.properties = properties;
	}

	public ItemProperties getProperties() {
		return properties;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	
	
	
	
	
	
}
