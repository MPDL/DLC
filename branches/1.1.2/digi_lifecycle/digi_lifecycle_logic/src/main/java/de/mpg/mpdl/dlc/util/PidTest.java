/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQStaticContext;

import net.sf.saxon.xqj.SaxonXQDataSource;

import org.w3c.dom.Document;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Result;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;

public class PidTest {

	/**
	 * @param args
	 */
	public static String URL = "http://dev-dlc.mpdl.mpg.de:8080";
	public static String USR = "dlc_mpdl";
	public static String PWD = "demo";

	public static String description = "gekürzt durch MPDL, weil viel zu lang ";

			/*+ "<br/>"
			+ "this is the sixth update to the description of the "
			+ "<a href=\"http://dev-dlc.mpdl.mpg.de:8080/ir/context/escidoc:39001\">DLC test context</a>"
			+ "<br/>"
			+ "this update to this description will extend the length again, since escidoc was able to return the full description again."
			+ "<br/>"
			+ "extension 1: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 2a: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 2b: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 2c: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 2d: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 2e: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 2f: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 3a: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 3b: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 3c: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025"
			+ "extension 3d: 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011 0012 0013 0014 0015 0016 0017 0018 0019 0020 0021 0022 0023 0024 0025";
*/
	public static void main(String[] args) {
		/*
		String id = create();
		System.out.println(id);
		System.out.println(submit(id));
		System.out.println(assignPid(id));
		System.out.println(release(id));
		*/
		// System.out.println(ctx_create());
		// System.out.println(ctx_desc(description));
		// xquery();
		String id = "escidoc:52294";
		System.out.println(release(id));

		//System.out.println(updateAndReleaseAgain(id));
	}
	
	public static void xquery() {
		XQDataSource xqds = new SaxonXQDataSource();
		XQConnection conn;
		try {
			conn = xqds.getConnection();
			XQStaticContext context = conn.getStaticContext();
			context.setBaseURI("http://dlc.mpdl.mpg.de:8080");
			context.declareNamespace("mets", "http://www.loc.gov/METS/");
			context.declareNamespace("mods", "http://www.loc.gov/mods/v3");
			context.declareNamespace("item", "http://www.escidoc.de/schemas/item/0.10");
			context.declareNamespace("md", "http://www.escidoc.de/schemas/metadatarecords/0.5");
			context.declareNamespace("comp", "http://www.escidoc.de/schemas/components/0.9");
			context.declareNamespace("prop", "http://escidoc.de/core/01/properties/");
			context.declareNamespace("srel", "http://escidoc.de/core/01/structural-relations/");
			context.declareNamespace("xlink", "http://www.w3.org/1999/xlink");
			context.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
			context.declareNamespace("ntei", "http://www.tei-c.org/ns/notTEI");
			conn.setStaticContext(context);
			XQExpression expr = conn.createExpression();
			String search = "/srw/search/dlc_index?query=escidoc.objecttype=item%20and%20escidoc.context.objid=escidoc:23165&amp;maximumRecords=50";
			URI uri = new URI(search);
			String query = "for $item in doc('" + search + "')//item:item\n";
			query += "for $comp in $item//comp:component\n";
			query += "let $href := $comp/comp:content/@xlink:href\n";
			query += "where $comp/comp:properties/prop:content-category = 'tei'\n";
			query += "return ";
			query += "for $title in doc($href)//tei:titlePage/tei:docTitle/tei:titlePart\n";
			query += "return $title";
			/*
			query += "for $div in doc($href)//tei:div\n";
			query += "where $div/@type = 'chapter'\n";
			query += "return concat($href, \":   \", $div/@xml:id, \"   \", $div/tei:head)";
			*/
			// query += "return $div/tei:head";
			// query += "for $flocat in doc($comp/comp:content/@xlink:href)//mets:FLocat/@xlink:href\n";
			// query += "for $page in doc($comp/comp:content/@xlink:href)//ntei:page\n";
			// query += "for $head in $page//tei:head\n";
			// query += "where contains($head, 'Kreuz')\n";
			// query += "return $flocat";
			// query += "return concat($item/@xlink:href, \":   \", $page/@n, \"   \", $head)";
			Properties props = new Properties();
            props.setProperty("method", "xml");
            props.setProperty("encoding", "utf-8");
            props.setProperty("indent", "yes");
            
			XQResultSequence result = expr.executeQuery(query);
			
			while (result.next()) {
				// System.out.println(result.getItemType());

				// result.writeItem(System.out, props);
				if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
					// System.out.println(result.getItem().getNode().getTextContent());
					System.out.println(result.getItem().toString());
				} else {
					System.out.println(result.getAtomicValue());
				}
			}
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String updateAndReleaseAgain(String id) {
		ItemHandlerClient client;
		try {
			client = new ItemHandlerClient(new URL(URL));
			Authentication auth = new Authentication(new URL(URL), USR, PWD);
			client.setHandle(auth.getHandle());
			Item item = client.retrieve(id);
			MetadataRecords mdrecs = item.getMetadataRecords();
			MetadataRecord mdrec = new MetadataRecord("number_four");
			mdrecs.add(mdrec);
			item.setMetadataRecords(mdrecs);
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			JAXBContext ctx = JAXBContext.newInstance(new Class[] { ModsMetadata.class });		
			Marshaller marshaller = ctx.createMarshaller();
			ModsMetadata modsMetadata = new ModsMetadata();
			modsMetadata.setSignature_544("444444444444");
			marshaller.marshal(modsMetadata, d);
			mdrec.setContent(d.getDocumentElement());
			item = client.update(item);
			submit(id);
			item = client.retrieve(id);
			TaskParam tp = new TaskParam();
			tp.setLastModificationDate(item.getLastModificationDate());
			tp.setUrl(new URL(URL + "/ir/item/" + id + ":" + item.getProperties().getVersion().getNumber()));
			Result result = client.assignVersionPid(id, tp);
			System.out.println(result.getFirst().getTextContent());
			return release(id);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
		
	public static String create() {
		try {
			ItemHandlerClient client = new ItemHandlerClient(new URL(URL));
			Authentication auth = new Authentication(new URL(URL), USR, PWD);
			client.setHandle(auth.getHandle());
			Item item = new Item();
			item.getProperties().setContentModel(new ContentModelRef("escidoc:1001"));
			item.getProperties().setContext(new ContextRef("escidoc:15014"));
			// item.getProperties().setContentModel(new ContentModelRef("escidoc:36017"));
			// item.getProperties().setContext(new ContextRef("escidoc:1002"));
			MetadataRecords mdRecs = new MetadataRecords();
			MetadataRecord mdRec = new MetadataRecord("escidoc");
			mdRecs.add(mdRec);
			item.setMetadataRecords(mdRecs);
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			JAXBContext ctx = JAXBContext.newInstance(new Class[] { ModsMetadata.class });		
			Marshaller marshaller = ctx.createMarshaller();
			ModsMetadata modsMetadata = new ModsMetadata();
			modsMetadata.setSignature_544("123456789");
			marshaller.marshal(modsMetadata, d);
			mdRec.setContent(d.getDocumentElement());
			item = client.create(item);
			return item.getObjid();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String submit(String id) {
		ItemHandlerClient client;
		try {
			client = new ItemHandlerClient(new URL(URL));
			Authentication auth = new Authentication(new URL(URL), USR, PWD);
			client.setHandle(auth.getHandle());
			Item item = client.retrieve(id);
			TaskParam taskParam = new TaskParam();
			taskParam.setLastModificationDate(item.getLastModificationDate());
			taskParam.setComment("submitted Item ...");
			Result result = client.submit(item, taskParam);
			return result.getLastModificationDate().toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String release(String id) {
		ItemHandlerClient client;
		try {
			client = new ItemHandlerClient(new URL(URL));
			Authentication auth = new Authentication(new URL(URL), USR, PWD);
			client.setHandle(auth.getHandle());
			Item item = client.retrieve(id);
			TaskParam taskParam = new TaskParam();
			taskParam.setLastModificationDate(item.getLastModificationDate());
			taskParam.setComment("releasing Item ...");
			Result result = client.release(item, taskParam);
			return result.getLastModificationDate().toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String assignPid(String id) {
		ItemHandlerClient client;

		try {
			client = new ItemHandlerClient(new URL(URL));
			Authentication auth = new Authentication(new URL(URL), USR, PWD);
			client.setHandle(auth.getHandle());
			Item item = client.retrieve(id);
			TaskParam taskParam = new TaskParam();
			taskParam.setLastModificationDate(item.getLastModificationDate());
			taskParam.setComment("assign objectPID ...");
			taskParam.setUrl(new URL(URL + "/ir/item/" + id));
			Result result = client.assignObjectPid(id, taskParam);
			// item = client.retrieve(id);
			System.out.println(result.getFirst().getTextContent());
			taskParam.setLastModificationDate(result.getLastModificationDate());
			taskParam.setComment("assign versionPID ...");
			taskParam.setUrl(new URL(URL + "/ir/item/" + id));
			Result result2 = client.assignVersionPid(id, taskParam);
			// item = client.retrieve(id);
			return result2.getFirst().getTextContent();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	private static String ctx_create() {
		ContextHandlerClient ctx_client;
		try {
			ctx_client = new ContextHandlerClient(new URL("http://dev-dlc.mpdl.mpg.de:8080"));
			Authentication auth = new Authentication(new URL("http://dev-dlc.mpdl.mpg.de:8080"), "dlc_admin", "demo");
			ctx_client.setHandle(auth.getHandle());
			Context ctx = new Context();
			ctx.getProperties().setName("DLC test context");
			ctx.getProperties().setDescription("this is the original description of the DLC test context");
			ctx.getProperties().setType("DLC");
			OrganizationalUnitRefs ouRefs = new OrganizationalUnitRefs();
			OrganizationalUnitRef ou = new OrganizationalUnitRef("escidoc:2001");
			ouRefs.add(ou);
			ctx.getProperties().setOrganizationalUnitRefs(ouRefs);
			ctx = ctx_client.create(ctx);
			TaskParam taskParam = new TaskParam();
			taskParam.setLastModificationDate(ctx.getLastModificationDate());
			taskParam.setComment("DLC test context opened ...");
			Result result = ctx_client.open(ctx, taskParam);
			return ctx.getObjid();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static String ctx_desc(String desc) {
		ContextHandlerClient ctx_client;
		String id = "escidoc:23174";
		try {
			ctx_client = new ContextHandlerClient(new URL("http://dlc.mpdl.mpg.de:8080"));
			Authentication auth = new Authentication(new URL("http://dlc.mpdl.mpg.de:8080"), "mpib_admin", "mpib_admin");
			ctx_client.setHandle(auth.getHandle());
			Context ctx = ctx_client.retrieve(id);
			ctx.getProperties().setDescription(desc);
			TaskParam taskParam = new TaskParam();
			taskParam.setLastModificationDate(ctx.getLastModificationDate());
			taskParam.setComment("changing description for the 3rd time ...");
			ctx = ctx_client.update(ctx);
			return ctx.getProperties().getDescription();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EscidocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
