package de.mpg.mpdl.dlc.escidoc.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.rest.RestStagingHandlerClient;
import de.escidoc.schemas.components.ComponentDocument.Component;
import de.escidoc.schemas.components.ComponentDocument.Component.Content;
import de.escidoc.schemas.components.PropertiesDocument.Properties;
import de.escidoc.schemas.metadatarecords.MdRecordDocument.MdRecord;
import de.escidoc.schemas.metadatarecords.MdRecordsDocument.MdRecords;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.ContentCategory;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.MimeType;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.Visibility;

public class EscidocComponent {
	
	private static Component component = null;
	private static Properties componentProperties = null;
	private static MdRecords componentMdRecords = null;
	private static MdRecord componentMdRecord = null;
	private static Content componentContent = null;
	
	private EscidocComponent() {}
	
	public static Component create(Visibility v, ContentCategory cc, MimeType mt, URL url, File file, String user)
	{
		component = Component.Factory.newInstance();
		componentProperties = EscidocProperties.component(v, cc, mt);
		component.setProperties(componentProperties);
		componentMdRecords = component.addNewMdRecords();
		componentMdRecord = componentMdRecords.addNewMdRecord();
		componentMdRecord.set(MetadataXML.component());
		componentMdRecord.setName("escidoc");
		componentContent = component.addNewContent();
		componentContent.setStorage(Content.Storage.INTERNAL_MANAGED);
		componentContent.setHref(getStagingURL(url, file, user).toExternalForm());
		
		return component;
	}
	
	public static URL getStagingURL(URL url, File file, String user)
	{
		try {
			RestStagingHandlerClient rshc = new RestStagingHandlerClient(url);
			rshc.setHandle(user);
			return rshc.upload(file);
		} catch (InternalClientException e) {
			e.printStackTrace();
		} catch (EscidocException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		}
		return null;
	}
}
