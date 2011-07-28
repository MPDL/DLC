package de.mpg.mpdl.dlc.escidoc;

import gov.loc.mods.v3.ModsDefinition;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;
import gov.loc.mods.v3.ModsVersionAttributeDefinition;
import gov.loc.mods.v3.VersionType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;
import org.purl.escidoc.metadata.profiles.x01.file.FileDocument;
import org.w3.x1999.xlink.TypeAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.escidoc.core.client.rest.RestItemHandlerClient;
import de.escidoc.core.client.rest.RestStagingHandlerClient;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.schemas.commontypes.LinkForCreate;
import de.escidoc.schemas.components.ComponentDocument.Component;
import de.escidoc.schemas.components.ComponentDocument.Component.Content;
import de.escidoc.schemas.components.ComponentsDocument.Components;
import de.escidoc.schemas.item.ItemDocument;
import de.escidoc.schemas.item.ItemDocument.Item;
import de.escidoc.schemas.item.PropertiesDocument.Properties;
import de.escidoc.schemas.metadatarecords.MdRecordDocument;
import de.escidoc.schemas.metadatarecords.MdRecordDocument.MdRecord;
import de.escidoc.schemas.metadatarecords.MdRecordsDocument.MdRecords;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocComponent;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.ContentCategory;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.MimeType;
import de.mpg.mpdl.dlc.escidoc.xml.MetadataXML;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.Visibility;

public class Ingest {
	
		
	public static void main(String... strings)
	{
		System.out.println(itemXML());
		//createItem();
		//updateItem("escidoc:8003");
	}
	public static String itemXML()
	{
		Login login = new Login();
		String user = null;
		URL fwurl = null;
		try {
			fwurl = new URL("http://latest-coreservice.mpdl.mpg.de:8080");
			user = login.loginVirrUser(fwurl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File file2upload = new File("/home/frank/data/digitization_lifecycle/tei_samples/marx.xml");
		String xml = null;
		ItemDocument itemDoc = ItemDocument.Factory.newInstance();
		Item item = itemDoc.addNewItem();
		Properties itemProperties = EscidocProperties.item("escidoc:5002", "escidoc:2001");
		item.setProperties(itemProperties);
		MdRecords recs = item.addNewMdRecords();
		MdRecord rec = recs.addNewMdRecord();
		ModsDocument md = ModsDocument.Factory.newInstance();
		ModsDefinition mods = md.addNewMods();
		mods.setID("bibl_meta_from_MAB");
		mods.setVersion(ModsVersionAttributeDefinition.X_3_4);
		rec.set(md);
		rec.setName("escidoc");

		Components comps = item.addNewComponents();
		Component comp = EscidocComponent.create(Visibility.PUBLIC, ContentCategory.DLC_TEI_FULLTEXT, MimeType.APPLICATION_XML, fwurl, file2upload, user);
		comps.addNewComponent();
		comps.setComponentArray(0, comp);
		xml = itemDoc.xmlText(XBeanUtils.getItemOpts());
		return xml;
	}
	public static void createItem()
	{
		try {
			Authentication auth = new Authentication(new URL("http://latest-coreservice.mpdl.mpg.de:8080"), "dlc_user", "dlc");
			System.out.println(auth.getServiceAddress().toExternalForm());
			RestItemHandlerClient handler = new RestItemHandlerClient(auth.getServiceAddress());
			handler.setHandle(auth.getHandle());
			
			System.out.println(handler.create(itemXML()));
			
			
		} catch (InternalClientException e) {
			System.out.println("message from ICE " + e.getMessage());
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("message from MUE " + e.getMessage());
			e.printStackTrace();
		} catch (EscidocException e) {
			System.out.println("message from EE " + e.getMessage());
			e.printStackTrace();
		} catch (TransportException e) {
			System.out.println("message from TE " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static URL getStagingURL()
	{
		try {
			RestStagingHandlerClient rshc = new RestStagingHandlerClient(new URL("http://latest-coreservice.mpdl.mpg.de:8080"));
			return rshc.upload(new File("/home/frank/data/digitization_lifecycle/tei_samples/marx.xml"));
		} catch (InternalClientException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (EscidocException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateItem(String id)
	{
		Authentication auth;
		try {
			auth = new Authentication(new URL("http://latest-coreservice.mpdl.mpg.de:8080"), "dlc_user", "dlc");
			RestItemHandlerClient handler = new RestItemHandlerClient(auth.getServiceAddress());
			handler.setHandle(auth.getHandle());
			String itemXML = handler.retrieveComponentMdRecord(id, "escidoc:8002", "escidoc");
			
			MdRecordDocument itemDoc = MdRecordDocument.Factory.parse(itemXML);
			MdRecord rec = itemDoc.getMdRecord();
			XmlCursor cur = rec.newCursor();
			cur.toFirstChild();
			FileDocument file = FileDocument.Factory.parse(cur.xmlText(XBeanUtils.getFileOpts()));
			SimpleLiteral sl = SimpleLiteral.Factory.newInstance();
			XmlString val = XmlString.Factory.newInstance();
			val.setStringValue("TEI_FULLTEXT");
			sl.set(val);
			file.getFile().getContentCategory().set(val);
			System.out.println(file.xmlText(XBeanUtils.getFileOpts()));
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InternalClientException e) {
			e.printStackTrace();
		} catch (EscidocException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		}
		
	}
}
