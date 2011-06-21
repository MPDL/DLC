package de.mpg.mpdl.dlc.escidoc;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.logging.XLevel;
import org.w3.x1999.xlink.TypeAttribute;

import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.rest.RestItemHandlerClient;
import de.escidoc.schemas.commontypes.x04.LinkForCreate;
import de.escidoc.schemas.item.x0.ItemDocument;
import de.escidoc.schemas.item.x0.ItemDocument.Item;
import de.escidoc.schemas.item.x0.PropertiesDocument.Properties;

public class Ingest {
	
	public static void main(String... strings)
	{
		//System.out.println(itemXML());
		createItem();
	}
	
	public static String itemXML()
	{
		String xml = null;
		ItemDocument itemDoc = ItemDocument.Factory.newInstance();
		Item item = itemDoc.addNewItem();
		Properties itemProperties = item.addNewProperties();
		LinkForCreate cModelLink = itemProperties.addNewContentModel();
		cModelLink.setTitle("Content Model 4 DLC METS Items");
		cModelLink.setType(TypeAttribute.Type.SIMPLE);
		cModelLink.setHref("/cmm/content-model/dlc:1234");
		LinkForCreate contextLink = itemProperties.addNewContext();
		contextLink.setTitle("DLC KHI Rara Context");
		contextLink.setType(TypeAttribute.Type.SIMPLE);
		contextLink.setHref("/ir/context/dlc:2222");
		xml = itemDoc.xmlText(XBeanUtils.getItemOpts());
		return xml;
	}
	
	public static void createItem()
	{
		try {
			RestItemHandlerClient handler = new RestItemHandlerClient(new URL("http://latest-coreservice.mpdl.mpg.de:8080"));
			System.out.println(handler.retrieve("escidoc:1004"));
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
