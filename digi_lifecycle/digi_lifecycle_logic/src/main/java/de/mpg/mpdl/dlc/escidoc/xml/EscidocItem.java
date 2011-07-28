package de.mpg.mpdl.dlc.escidoc.xml;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import de.escidoc.schemas.components.ComponentDocument.Component;
import de.escidoc.schemas.components.ComponentsDocument.Components;
import de.escidoc.schemas.item.ItemDocument;
import de.escidoc.schemas.item.ItemDocument.Item;
import de.escidoc.schemas.item.PropertiesDocument.Properties;
import de.escidoc.schemas.metadatarecords.MdRecordDocument.MdRecord;
import de.escidoc.schemas.metadatarecords.MdRecordsDocument.MdRecords;
import de.escidoc.schemas.relations.RelationsDocument.Relations;
import de.escidoc.schemas.relations.RelationsDocument.Relations.Relation;
import de.mpg.mpdl.dlc.escidoc.XBeanUtils;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.Href;

public class EscidocItem {
	
	public static final String RELATION_ONTOLOGY_URL = "http://www.escidoc.de/ontologies/mpdl-ontologies/"
        + "content-relations#";
	public static final String DEFAULT_MDRECORD_NAME = "escidoc";
	
	private static ItemDocument itemDocument;
	private static Item item;
	private static Properties itemProperties;
	private static MdRecords itemMdRecords;
	private static MdRecord itemMdRecord;
	private static Components itemComponents;
	private static Component itemComponent;
	private static Relations itemrelations;
	private static Relation itemRelation;

	public static String createXML(String contextId, String cmodelId, XmlObject metadata)
	{
		String xml = null;
		itemDocument = ItemDocument.Factory.newInstance();
		item = itemDocument.addNewItem();
		itemProperties = EscidocProperties.item(contextId, cmodelId);
		item.setProperties(itemProperties);
		itemMdRecords = item.addNewMdRecords();
		itemMdRecord = itemMdRecords.addNewMdRecord();
		itemMdRecord.set(metadata);
		itemMdRecord.setName(DEFAULT_MDRECORD_NAME);
		//item.getMdRecords().setMdRecordArray(0, itemMdRecord);
		itemrelations = item.addNewRelations();
		itemRelation = itemrelations.addNewRelation();
		XmlAnySimpleType predicate = XmlAnySimpleType.Factory.newInstance();
		predicate.setStringValue(RELATION_ONTOLOGY_URL + "hasDerivation");
		itemRelation.setPredicate(predicate);
		itemRelation.setHref(Href.ITEM + "escidoc:123");
		
		xml = itemDocument.xmlText(XBeanUtils.getItemOpts());
		return xml;
	}
	
	public static String addMdRecord(String itemXml, String mdRecordName, XmlObject metadata)
	{
		String xml = null;
		try {
			itemDocument = ItemDocument.Factory.parse(itemXml);
			item = itemDocument.getItem();
			itemMdRecords = item.getMdRecords();
			itemMdRecord = itemMdRecords.addNewMdRecord();
			itemMdRecord.set(metadata);
			itemMdRecord.setName(mdRecordName);
			xml = itemDocument.xmlText(XBeanUtils.getItemOpts());
			return xml;
		} catch (XmlException e) {
			e.printStackTrace();
		}
		return null;
	}
}
