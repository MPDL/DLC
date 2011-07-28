package de.mpg.mpdl.dlc.escidoc.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Element;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.escidoc.core.resources.HttpInputStream;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.cmm.ContentModelProperties;
import de.escidoc.core.resources.cmm.MetadataRecordDefinition;
import de.escidoc.core.resources.cmm.MetadataRecordDefinitions;
import de.escidoc.core.resources.cmm.ResourceDefinition;
import de.escidoc.core.resources.cmm.ResourceDefinitions;
import de.escidoc.core.resources.cmm.Schema;
import de.escidoc.core.resources.cmm.Xslt;
import de.escidoc.core.resources.common.ContentStream;
import de.escidoc.core.resources.common.ContentStreams;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;

public class ItemController {
	
	static Authentication auth = null;

	public static String create() {
	try
	{
		auth = new Authentication(new URL("http://latest-coreservice.mpdl.mpg.de:8080"), "dlc_user", "dlc");
	ItemHandlerClient ih = new ItemHandlerClient(auth.getServiceAddress());
	ih.setHandle(auth.getHandle());
	HttpInputStream hin = ih.retrieveContent("escidoc:1004", "escidoc:3002");
	MetadataRecord md = ih.retrieveMdRecord("escidoc:1004", "escidoc");
	Element e = md.getContent();
	XmlObject content = XmlObject.Factory.parse(e);
	return content.xmlText();
	
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}
	
	public static void cm()
	{
		try {
			auth = new Authentication(new URL("http://latest-coreservice.mpdl.mpg.de:8080"), "sysadmin", "sysadmin");
			ContentModelHandlerClient cmhc = new ContentModelHandlerClient(auth.getServiceAddress());
			cmhc.setHandle(auth.getHandle());
			ContentModel model = new ContentModel();
			ContentModelProperties modelProps = new ContentModelProperties();
			modelProps.setName("dlc_item");
			modelProps.setDescription("Content Model 4 DLC");
			model.setProperties(modelProps);
			MetadataRecordDefinitions mdrDefs = new MetadataRecordDefinitions();
			MetadataRecordDefinition definition1 = new MetadataRecordDefinition();
			definition1.setName("escidoc");
			definition1.setSchema(new Schema("http://www.loc.gov/mods/v3/mods-3-4.xsd"));
			mdrDefs.add(definition1);
			model.setMetadataRecordDefinitions(mdrDefs);
			ResourceDefinitions resDefs = new ResourceDefinitions();
			ResourceDefinition definition2 = new ResourceDefinition();
			definition2.setName("mods2rdf");
			definition2.setMetadataRecordName("escidoc");
			definition2.setXslt(new Xslt("file:///home/frank/data/SVN/digi_lifecycle/digi_lifecycle_logic/src/main/resources/xslt/modsToRdf/mods2rdf.xslt"));
			resDefs.add(definition2);
			model.setResourceDefinitions(resDefs);
			cmhc.create(model);
			
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void cmup()
	{
		try {
			auth = new Authentication(new URL("http://latest-coreservice.mpdl.mpg.de:8080"), "sysadmin", "sysadmin");
			ContentModelHandlerClient cmhc = new ContentModelHandlerClient(auth.getServiceAddress());
			cmhc.setHandle(auth.getHandle());
			ContentModel model = cmhc.retrieve("escidoc:9001");
			String url = auth.getServiceAddress().toExternalForm() + model.getResourceDefinitions().get("mods2rdf").getXslt().getXLinkHref();
			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod(url);
			client.executeMethod(get);
			System.out.println(get.getResponseBodyAsString());
			
			//model.getMetadataRecordDefinitions().get(0).setSchema(new Schema("http://www.loc.gov/mods/v3/mods-3-4.xsd"));
			// model.getResourceDefinitions().get(0).setXslt(new Xslt("http://simile.mit.edu/repository/RDFizers/marcmods2rdf/stylesheets/mods2rdf.xslt"));
			//cmhc.update("escidoc:9001", model);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
