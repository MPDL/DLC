package de.mpg.mpdl.dlc.util;

import static java.nio.file.StandardCopyOption.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.bouncycastle.asn1.cmp.OOBCertHash;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ddb.professionell.mabxml.mabxml1.DatensatzType;
import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.Result;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.ItemRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;
import de.escidoc.core.resources.om.item.component.Components;
import de.mpg.mpdl.dlc.images.ImageHelper;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;

public class VirrMigration {

	public static void main(String... strings) {
		// String virr_cm = "mono";
		String virr_id = "escidoc_417464";
		String dlc_id = "escidoc_68775";
		String mab_id = "359290";
		String child_mab_id = null;
		// String image_mab_id = "escidoc_69052";
		// String parent_id = "egal";	
		
		transformTOC2TEI(virr_id, dlc_id, mab_id, child_mab_id);
		
		try {
			addComponents("escidoc:68775");
		} catch (TransportException | MalformedURLException | EscidocException
				| InternalClientException | FileNotFoundException
				| XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		String dlc_id = stepByStep1(virr_cm, mab_id, image_mab_id,  parent_id);
		System.out.println("step 1: " + dlc_id);
		
		stepByStep2(dlc_id, virr_id, mab_id);
		System.out.println("step 2: " + dlc_id);
		*/
		/*
		String[] children = new String[]{"escidoc:68751", "escidoc:68752", "escidoc:68753", "escidoc:68754"};
		List<String> list = Arrays.asList(children);
		try {
			adddChildRelations(parent_id, list);
		} catch (TransportException | MalformedURLException | EscidocException
				| InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		ArrayList<String> pbs = collectVirrGenres();
		HashSet<String> uniqueValues = new HashSet<>(pbs);
		for (String s : uniqueValues) {
			System.out.println(s);
		}
		*/	
		//getallTEIs();
	}
	
	
	private static String stepByStep1(String virr_cm, String virr_mab_id, String image_mab_id, String parent_id) {
		try {
			String dlc_id = createItem(virr_cm, (Element) getMods(virr_mab_id), parent_id);
			if (virr_cm.equalsIgnoreCase("multi")) {
				
			} else {
			// savaAllImages(virr_mab_id, image_mab_id);
			renameFiles(image_mab_id, dlc_id.replace(":", "_"));
			}
			return dlc_id;
		} catch (AuthenticationException | TransportException
				| IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void stepByStep2(String dlc_id, String virr_id, String virr_mab_id) {
		
		String link = EscidocExtractor.extractXLink("/ir/container/" + virr_id, "relations:relation");
		String toc = EscidocExtractor.extractXLink(link, "comp:component");
		ArrayList<String> list = EscidocExtractor.extractMetsStructure(toc);
		createMets(list, dlc_id.replace(":", "_"));
		
		try {
			submit(dlc_id);
		} catch (TransportException | MalformedURLException | EscidocException
				| InternalClientException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void deleteComponents(String id) throws Exception {
		Authentication auth = new Authentication(new URL(
				"http://dlc.mpdl.mpg.de"), "virr_user", "migration");
		ItemHandlerClient ihc = new ItemHandlerClient(new URL(
				"http://dlc.mpdl.mpg.de"));
		Components comps = new Components();
		Item item = ihc.retrieve(id);
		while (item.getComponents().size() > 0) {
			item.getComponents().remove();
		}
		item = ihc.update(id, item);
		System.out.println("removed TEIs from " + item.getObjid());
	}
	
	private static void addComponents(String escidoc_id) throws TransportException, MalformedURLException, EscidocException, InternalClientException, FileNotFoundException, XMLStreamException {
		
		String file_id = escidoc_id.replace(":", "_");
		File tei_sd = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/toc_xml/" + file_id + "_tei.xml");
		File tei_paged = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/toc_xml/" + file_id + "_tei_paged.xml");

		Authentication auth = new Authentication(new URL(
				"http://dlc.mpdl.mpg.de"), "virr_user", "migration");
		ItemHandlerClient ihc = new ItemHandlerClient(new URL(
				"http://dlc.mpdl.mpg.de"));
		StagingHandlerClient staging = new StagingHandlerClient(new URL("http://dlc.mpdl.mpg.de"));
		staging.setHandle(auth.getHandle());
		
		ihc.setHandle(auth.getHandle());
		Item item = ihc.retrieve(escidoc_id);
		Components comps = new Components();
		while (item.getComponents().size() > 0) {
			item.getComponents().remove();
		}
		item.setComponents(comps);
		Component sd = new Component();
		ComponentProperties sd_props = new ComponentProperties();
		sd.setProperties(sd_props);
		sd.getProperties().setVisibility("public");
		sd.getProperties().setContentCategory("tei-sd");
		sd.getProperties().setMimeType("text/xml");
		ComponentContent sd_content = new ComponentContent();
		sd.setContent(sd_content);
		sd.getContent().setStorageType(StorageType.INTERNAL_MANAGED);
		sd.getContent().setXLinkHref(staging.upload(tei_sd).toExternalForm());
		item.getComponents().add(sd);
		
		Component paged = new Component();
		ComponentProperties paged_props = new ComponentProperties();
		paged.setProperties(paged_props);
		paged.getProperties().setVisibility("public");
		paged.getProperties().setContentCategory("tei-paged");
		paged.getProperties().setMimeType("text/xml");
		ComponentContent paged_content = new ComponentContent();
		paged.setContent(paged_content);
		paged.getContent().setStorageType(StorageType.INTERNAL_MANAGED);
		paged.getContent().setXLinkHref(staging.upload(tei_paged).toExternalForm());
		item.getComponents().add(paged);

		item = ihc.update(escidoc_id, item);
		System.out.println("added TEIs to " + item.getObjid());
		/*
		TaskParam param = new TaskParam();
		param.setComment("submitted after adding comps");
		param.setLastModificationDate(item.getLastModificationDate());
		Result result = ihc.submit(escidoc_id, param);
		*/
	}
	
	private static void updateMets(String id) {
		
		String uri = "http://dev-dlc.mpdl.mpg.de/ir/item/escidoc:69054/md-records/md-record/mets";
		
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLEventFactory ef = XMLEventFactory.newInstance();
		try {
			URL url = new URL(uri);
			Authentication auth = new Authentication(new URL(
					"http://dev-dlc.mpdl.mpg.de"), "virr", "migration");
			ItemHandlerClient ihc = new ItemHandlerClient(new URL(
					"http://dev-dlc.mpdl.mpg.de"));
			ihc.setHandle(auth.getHandle());
			Item item = ihc.retrieve(id);
			/*
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			XMLWriter w  = new XMLWriter(outputStream, OutputFormat.createPrettyPrint());
			System.out.println(item.getMetadataRecords().get("mets").getContent().toString());
			w.write(item.getMetadataRecords().get("mets").getContent());
			w.close();
			InputStream in = new ByteArrayInputStream(outputStream.toByteArray());
			*/
			OutputStream out = System.out;
			XMLEventReader reader = factory.createXMLEventReader(url.openStream());
            XMLEventWriter writer = xof.createXMLEventWriter(out);
            
            while (reader.hasNext()) {
            	
            	XMLEvent event = (XMLEvent) reader.next();
            	if (event.isStartElement()) {
            		StartElement start = event.asStartElement();
            		if (start.getName().getLocalPart().equals("mets:div")) {
            			Attribute label = start.getAttributeByName(new QName("LABEL"));
                		if (label.getValue().contains("d0001")) {
                			Attribute type = ef.createAttribute("TYPE", "titlePage");
                			writer.add(type);
                		}
                		writer.add(event);
            		} else {
            			writer.add(event);
            		}
            	} else {
            		writer.add(event);
            	}
            }
            writer.flush();
            writer.close();
            reader.close();
            out.close();
            // in.close();
			
		} catch (TransportException
				| EscidocException | InternalClientException | IOException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void createMets(ArrayList<String> list, String escidoc_id) {
		
	    final String NAME_SPACE = "http://www.loc.gov/METS/";

		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		try {
			XMLStreamWriter writer = factory
					.createXMLStreamWriter(new FileWriter(
							"/home/frank/data/wilhelm/misc_shit/virr2dlc/mets/"
									+ escidoc_id + ".xml"));
			writer.writeStartDocument();
			writer.setPrefix("mets", NAME_SPACE);
			writer.writeStartElement(NAME_SPACE, "mets");
			writer.writeNamespace("mets", NAME_SPACE);
			writer.writeStartElement(NAME_SPACE, "structMap");
			writer.writeAttribute("TYPE", "physical");
			writer.writeStartElement(NAME_SPACE, "div");
			writer.writeAttribute("DMDID", "dmd_0");
			for (String s : list) {
				String[] values = s.split("###");
				writer.writeStartElement(NAME_SPACE, "div");
				writer.writeAttribute("CONTENTIDS", escidoc_id + "/" + values[0]
						+ ".jpg");
				writer.writeAttribute("ID", values[1].replace("physical_", ""));
				writer.writeAttribute("LABEL", values[0]);
				writer.writeAttribute("ORDER", values[2]);
				if (values.length > 3) {
					writer.writeAttribute("ORDERLABEL", values[3]);
				} else {
					writer.writeAttribute("ORDERLABEL", "");
				}
				writer.writeEndElement();
			}
			writer.writeEndElement();
			writer.writeEndElement();
			writer.writeEndElement();
			writer.writeEndDocument();
			
			writer.flush();
			writer.close();

		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
private static void createMetsManually(String escidoc_id) {
	
	File imageDir = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/images/original/" + escidoc_id);
	try {
		System.out.println(imageDir.getCanonicalPath());
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	File[] imageFiles = imageDir.listFiles(new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			return name.contains("=d00");
		}
	});
	Arrays.sort(imageFiles);
	System.out.println("images: " + imageFiles.length);
	
	File[] preBound = imageDir.listFiles(new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			return name.contains("=v000");
		}
	});
	Arrays.sort(preBound);
	System.out.println("prebound: " + preBound.length);
	
	File[] postBound = imageDir.listFiles(new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			return name.contains("=n000");
		}
	});
	Arrays.sort(postBound);
	System.out.println("postbound: " + postBound.length);
		
	    final String NAME_SPACE = "http://www.loc.gov/METS/";

		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		try {
			XMLStreamWriter writer = factory
					.createXMLStreamWriter(new FileWriter(
							"/home/frank/data/wilhelm/misc_shit/virr2dlc/mets/"
									+ escidoc_id + ".xml"));
			writer.writeStartDocument();
			writer.setPrefix("mets", NAME_SPACE);
			writer.writeStartElement(NAME_SPACE, "mets");
			writer.writeNamespace("mets", NAME_SPACE);
			writer.writeStartElement(NAME_SPACE, "structMap");
			writer.writeAttribute("TYPE", "physical");
			writer.writeStartElement(NAME_SPACE, "div");
			writer.writeAttribute("DMDID", "dmd_0");
			
			// Cover
			writer.writeStartElement(NAME_SPACE, "div");
			writer.writeAttribute("CONTENTIDS", escidoc_id + "/dt9ag371=e0001.tif.jpg");
			writer.writeAttribute("ID", "page_0");
			writer.writeAttribute("LABEL", "dt9ag371=e0001.tif");
			writer.writeAttribute("ORDER","0");
			writer.writeAttribute("ORDERLABEL", "");
			writer.writeEndElement();
			writer.writeStartElement(NAME_SPACE, "div");
			writer.writeAttribute("CONTENTIDS", escidoc_id + "/dt9ag371=e0002.tif.jpg");
			writer.writeAttribute("ID", "page_1");
			writer.writeAttribute("LABEL", "dt9ag371=e0002.tif");
			writer.writeAttribute("ORDER","1");
			writer.writeAttribute("ORDERLABEL", "");
			writer.writeEndElement();
			for (int i = 2; i < preBound.length +2; i++) {
				writer.writeStartElement(NAME_SPACE, "div");
				writer.writeAttribute("CONTENTIDS", escidoc_id + "/" + preBound[i -2].getName());
				writer.writeAttribute("ID", "page_" +i);
				writer.writeAttribute("LABEL", preBound[i -2].getName().replace(".jpg", ""));
				writer.writeAttribute("ORDER", new Integer(i).toString());
				writer.writeAttribute("ORDERLABEL", "");
				writer.writeEndElement();
			}
			for (int i = 6; i < imageFiles.length +6; i++) {
				writer.writeStartElement(NAME_SPACE, "div");
				writer.writeAttribute("CONTENTIDS", escidoc_id + "/" + imageFiles[i -6].getName());
				writer.writeAttribute("ID", "page_" +i);
				writer.writeAttribute("LABEL", imageFiles[i -6].getName().replace(".jpg", ""));
				writer.writeAttribute("ORDER", new Integer(i).toString());
				writer.writeAttribute("ORDERLABEL", "");
				writer.writeEndElement();
			}
			for (int i = 104; i < postBound.length +104; i++) {
				writer.writeStartElement(NAME_SPACE, "div");
				writer.writeAttribute("CONTENTIDS", escidoc_id + "/" + postBound[i -104].getName());
				writer.writeAttribute("ID", "page_" +i);
				writer.writeAttribute("LABEL", postBound[i -104].getName().replace(".jpg", ""));
				writer.writeAttribute("ORDER", new Integer(i).toString());
				writer.writeAttribute("ORDERLABEL", "");
				writer.writeEndElement();
			}
			
			// Cover
						writer.writeStartElement(NAME_SPACE, "div");
						writer.writeAttribute("CONTENTIDS", escidoc_id + "/dt9ag371=e0003.tif.jpg");
						writer.writeAttribute("ID", "page_106");
						writer.writeAttribute("LABEL", "dt9ag371=e0003.tif");
						writer.writeAttribute("ORDER","106");
						writer.writeAttribute("ORDERLABEL", "");
						writer.writeEndElement();
						writer.writeStartElement(NAME_SPACE, "div");
						writer.writeAttribute("CONTENTIDS", escidoc_id + "/dt9ag371=e0004.tif.jpg");
						writer.writeAttribute("ID", "page_107");
						writer.writeAttribute("LABEL", "dt9ag371=e0004.tif");
						writer.writeAttribute("ORDER","107");
						writer.writeAttribute("ORDERLABEL", "");
						writer.writeEndElement();
						
			writer.writeEndElement();
			writer.writeEndElement();
			writer.writeEndElement();
			writer.writeEndDocument();
			
			writer.flush();
			writer.close();

		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void savaAllImages(String mab_id, String image_mab_id) throws IOException {

		String baseUrl = "http://r-coreservice.mpdl.mpg.de";

		File baseDir = new File(
				"/home/frank/data/wilhelm/misc_shit/virr2dlc/images");
		boolean isReady = new File(baseDir + "/" + mab_id).mkdir();
		if (isReady) {
			ArrayList<String> list = EscidocExtractor.extractImageUrls(image_mab_id);
			for (String s : list) {
				String[] params = s.split("###");
				URL imageUrl = new URL(baseUrl + params[1]);
				 System.out.println(imageUrl.toExternalForm());
				InputStream in = imageUrl.openStream();
				File targetFile = new File(baseDir + "/" + mab_id + "/"
						+ params[0]);
				OutputStream out = new FileOutputStream(targetFile);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				in.close();
				out.close();
			}
		}
	}
	
	private static void saveTocs2File() throws IOException {
		
		ArrayList<String> urls = EscidocExtractor.extractTocURLs();
		
		String baseUrl = "http://r-coreservice.mpdl.mpg.de";

		File baseDir = new File(
				"/home/frank/data/wilhelm/misc_shit/virr2dlc/toc_xml");

		for (String s : urls) {
			String[]  vals = s.split("###");
			URL tocUrl = new URL(baseUrl + vals[0]);
			// System.out.println(tocUrl.toExternalForm());
			InputStream in = tocUrl.openStream();
			File targetFile = new File(baseDir + "/" + vals[1].replace(":", "_") + ".xml");
			OutputStream out = new FileOutputStream(targetFile);
			byte[] buffer = new byte[8 * 1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			in.close();
			out.close();
		}
	}

	private static void renameFiles(String mab_id, String escidoc_id) {
		File imageDir = new File(
				"/home/frank/data/wilhelm/misc_shit/virr2dlc/images");
		File original_dir = new File(imageDir + "/" + "original" + "/" + mab_id);
		File web_dir = new File(imageDir + "/" + "web"+ "/" + mab_id);
		File thumbnails_dir = new File(imageDir + "/" + "thumbnails"+ "/" + mab_id);
		Path renamed = null;
		if (original_dir.exists()) {
			try {
				renamed = Files.move(Paths.get(original_dir.getAbsolutePath()), Paths.get(imageDir + "/original/" + escidoc_id), REPLACE_EXISTING);
				System.out.println("renamed " + renamed.getFileName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (web_dir.exists()) {
			try {
				renamed = Files.move(Paths.get(web_dir.getAbsolutePath()), Paths.get(imageDir + "/web/" + escidoc_id), REPLACE_EXISTING);
				System.out.println("renamed " + renamed.getFileName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (thumbnails_dir.exists()) {
			try {
				renamed = Files.move(Paths.get(thumbnails_dir.getAbsolutePath()), Paths.get(imageDir + "/thumbnails/" + escidoc_id), REPLACE_EXISTING);
				System.out.println("renamed " + renamed.getFileName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		File virrDir = new File(imageDir + "/" + mab_id);
		
		boolean makeOriginal = new File(original_dir + "/" + escidoc_id)
				.mkdir();
		boolean makeWeb = new File(web_dir + "/" + escidoc_id).mkdir();
		boolean makeThumb = new File(thumbnails_dir + "/" + escidoc_id).mkdir();
	*/
		/*
		for (File f : virrDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.contains(".max");
			}
		})) {
			File renamed = new File(original_dir + "/" + escidoc_id + "/"
					+ f.getName().replace(".max", ".tif"));
			if (makeOriginal) {
				f.renameTo(renamed);
			}
		}

		for (File f : virrDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.contains(".web");
			}
		})) {
			File renamed = new File(web_dir + "/" + escidoc_id + "/"
					+ f.getName().replace(".web", ".tif"));
			if (makeWeb) {
				f.renameTo(renamed);
			}
		}

		for (File f : virrDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.contains(".min");
			}
		})) {
			File renamed = new File(thumbnails_dir + "/" + escidoc_id + "/"
					+ f.getName().replace(".min", ".tif"));
			if (makeThumb) {
				f.renameTo(renamed);
			}
		}
		*/
	}

	private static Node stream2dom(XMLStreamReader reader) {

		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer t = tf.newTransformer();
			if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
				DOMResult result = new DOMResult();
				t.transform(new StAXSource(reader), result);
				Node domNode = result.getNode();
				return domNode.getFirstChild();
			}
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void submit(String escidoc_id) throws TransportException,
			MalformedURLException, EscidocException, InternalClientException {

			Authentication auth = new Authentication(new URL(
					"http://dlc.mpdl.mpg.de"), "virr_user", "migration");
			ItemHandlerClient ihc = new ItemHandlerClient(new URL(
					"http://dlc.mpdl.mpg.de"));
			ihc.setHandle(auth.getHandle());
			Item item = ihc.retrieve(escidoc_id);
			MetadataRecord mdRecord = new MetadataRecord("mets");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			Document doc = null;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/mets/" + escidoc_id.replace(":", "_") + ".xml"));
				mdRecord.setContent((Element) doc.getFirstChild());
				item.getMetadataRecords().add(mdRecord);
				item = ihc.update(escidoc_id, item);
				TaskParam param = new TaskParam();
				param.setComment("submitted");
				param.setLastModificationDate(item.getLastModificationDate());
				Result result = ihc.submit(escidoc_id, param);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	private static void updateMdRecord(String escidoc_id) throws TransportException,
	MalformedURLException, EscidocException, InternalClientException {

	Authentication auth = new Authentication(new URL(
			"http://dev-dlc.mpdl.mpg.de"), "virr", "migration");
	ItemHandlerClient ihc = new ItemHandlerClient(new URL(
			"http://dev-dlc.mpdl.mpg.de"));
	ihc.setHandle(auth.getHandle());
	Item item = ihc.retrieve(escidoc_id);
	MetadataRecord mdRecord = item.getMetadataRecords().get("mets");
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;
	Document doc = null;
	try {
		db = dbf.newDocumentBuilder();
		doc = db.parse(new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/mets/" + escidoc_id.replace(":", "_") + ".xml"));
		mdRecord.setContent((Element) doc.getFirstChild());
		//item.getMetadataRecords().add(mdRecord);
		item = ihc.update(escidoc_id, item);
		TaskParam param = new TaskParam();
		param.setComment("submitted");
		param.setLastModificationDate(item.getLastModificationDate());
		Result result = ihc.submit(escidoc_id, param);
	} catch (ParserConfigurationException | SAXException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
}
	
	private static void adddChildRelations(String escidoc_id, List<String> list) throws TransportException,
	MalformedURLException, EscidocException, InternalClientException {
		
		String predicate = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart";

	Authentication auth = new Authentication(new URL(
			"http://dlc.mpdl.mpg.de"), "virr_user", "migration");
	ItemHandlerClient ihc = new ItemHandlerClient(new URL(
			"http://dlc.mpdl.mpg.de"));
	ihc.setHandle(auth.getHandle());
	Item item = ihc.retrieve(escidoc_id);
	Relations relations = new Relations();
	for (String ref : list) {
		Relation relation = new Relation(new ItemRef(ref));
		relation.setPredicate(predicate);
		relations.add(relation);
	}
	if (relations.size() == list.size()) {
		item.setRelations(relations);
		item = ihc.update(escidoc_id, item);
		TaskParam param = new TaskParam();
		param.setComment("submitted");
		param.setLastModificationDate(item.getLastModificationDate());
		Result result = ihc.submit(escidoc_id, param);
	}
}

	private static String createItem(String cModel, Element mods, String parent_id)
			throws AuthenticationException, TransportException,
			MalformedURLException {

		Authentication auth = new Authentication(new URL(
				"http://dlc.mpdl.mpg.de"), "virr_user", "migration");
		ItemHandlerClient ihc = new ItemHandlerClient(new URL(
				"http://dlc.mpdl.mpg.de"));
		ihc.setHandle(auth.getHandle());

		Item item = new Item();
		ItemProperties props = new ItemProperties();
		switch (cModel) {
		case "mono":
			props.setContentModel(new ContentModelRef("escidoc:1"));
			break;
		case "multi":
			props.setContentModel(new ContentModelRef("escidoc:2"));
			break;
		case "vol":
			props.setContentModel(new ContentModelRef("escidoc:3"));
			Relations rels = new Relations();
			if (parent_id != null) {
			Relation rel = new Relation(new ItemRef(parent_id));
			rel.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isPartOf");
			rels.add(rel);
			item.setRelations(rels);
			}
			break;
		}
		props.setContext(new ContextRef("escidoc:68726"));
		item.setProperties(props);
		MetadataRecords mdRecords = new MetadataRecords();
		MetadataRecord mdRecord = new MetadataRecord("escidoc");
		mdRecord.setContent(mods);
		mdRecords.add(mdRecord);
		item.setMetadataRecords(mdRecords);
		try {
			item = ihc.create(item);
			return item.getObjid();
		} catch (EscidocException | InternalClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public static Node getMods(String mab_id) {
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
		
		URL url = VirrMigration.class.getClassLoader().getResource("export/mab2mods.xsl");
		try {
		Source XSL = new StreamSource(url.openStream());
		Source XML = new StreamSource(new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/virr(48)/" + mab_id + ".mab.xml"));

		
			File transformed = File.createTempFile("transformed", "xml");
			// File transformed = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/virr(48)/" + mab_id + ".mods.xml");
			StreamResult  result = new StreamResult(new FileWriter(transformed));
			TransformerFactory transfFactory = TransformerFactory.newInstance();
			Transformer transformer = transfFactory.newTransformer(XSL);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(XML, result);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(transformed);
			return doc.getFirstChild();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void getallTEIs() {
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/home/frank/data/wilhelm/misc_shit/virr2dlc/toc2tei.log"));
			String line, dlc_id, virr_id, mab, child_mab, type;
		    while ((line = reader.readLine()) != null) {
		       String[] values = line.split("\t");
		       type = values[1];
		       virr_id = values[0];
	    	   dlc_id = values[2];
		       if (type.equalsIgnoreCase("mono")) {
		    	  mab = values[4];
		    	  transformTOC2TEI(virr_id, dlc_id, mab, null);
		    	  System.out.println("transforming " + type + " " + virr_id + " " + mab);
		       } else if (type.equalsIgnoreCase("volume")) {
		    	   mab = values[5];
		    	   child_mab = values[4];
		    	  transformTOC2TEI(virr_id, dlc_id, mab, child_mab);
			    	  System.out.println("transforming " + type + " " + virr_id + " " + mab + " " + child_mab);
		       }         
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void transformTOC2TEI(String virr_id, String dlc_id, String mab_id, String child_mab_id) {
				
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
		
		String toc_dir = "/home/frank/data/wilhelm/misc_shit/virr2dlc/toc_xml/";
		String mets_url = "/home/frank/data/wilhelm/misc_shit/virr2dlc/mets/" + dlc_id + ".xml";;
		String mods_url = "/home/frank/data/wilhelm/misc_shit/virr2dlc/virr(48)/" + mab_id + ".mods.xml";
		String child_mods_url = null;
		if (child_mab_id != null) {
			child_mods_url = "/home/frank/data/wilhelm/misc_shit/virr2dlc/virr(48)/" + child_mab_id + ".mods.xml";
		}


		URL toc2tei_url = VirrMigration.class.getClassLoader().getResource("export/toc2tei.xsl");
		URL tei2paged_url = VirrMigration.class.getClassLoader().getResource("export/teiToPagedTei.xsl");
		try {
			Source toc2tei_xsl = new StreamSource(toc2tei_url.openStream());
			Source tei2paged_xsl = new StreamSource(tei2paged_url.openStream());
			Source toc_xml = new StreamSource(new File(toc_dir + virr_id + ".xml"));
			File transformed_tei_file = new File(toc_dir + dlc_id + "_tei.xml");
			File transformed_paged_file = new File(toc_dir + dlc_id + "_tei_paged.xml");

			StreamResult  result = new StreamResult(new FileWriter(transformed_tei_file));

			TransformerFactory transfFactory = TransformerFactory.newInstance();
			Transformer transformer = transfFactory.newTransformer(toc2tei_xsl);
			transformer.setParameter("modsUrl", mods_url);
			if (child_mods_url != null) {
				transformer.setParameter("childmodsUrl", child_mods_url);
			}
			transformer.setParameter("metsUrl", mets_url);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(toc_xml, result);
			System.out.println("transformed toc to " + transformed_tei_file.getCanonicalPath());
			
			Source tei_xml = new StreamSource(transformed_tei_file);
			result = new StreamResult(new FileWriter(transformed_paged_file));
			transformer = transfFactory.newTransformer(tei2paged_xsl);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(tei_xml, result);
			System.out.println("transformed tei to paged " + transformed_paged_file.getCanonicalPath());
			
		} catch (IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private static void reRenderImages(String id) {
		
		// ImageHelper helper = new ImageHelper();
		File inDir = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/images/original/" + id);
		String path2dest = "/home/frank/data/wilhelm/misc_shit/virr2dlc/images/rerendered/";
		File destDir = new File(path2dest + id);
		if (destDir.mkdir()) {
			File[] image_files = inDir.listFiles();
			for (File iFile : image_files) {
				try {
					//System.out.println("scaling " + iFile.getName());
					File tempfile = ImageHelper.scaleImage(iFile, iFile.getName(), ImageHelper.Type.WEB);
					File rerendered = new File(destDir + "/" + iFile.getName());
					Files.copy(tempfile.toPath(), rerendered.toPath());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static ArrayList<String> collectVirrGenres() {
		ArrayList<String> list = new ArrayList<>();
		File tocDir = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/toc_xml");
		Pattern pat = Pattern.compile("escidoc_[0-9]{6}.xml");
		File[] tocs = tocDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.matches("escidoc_[0-9]{6}.xml");
			}
		});
		ArrayList<String> genres;
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/output.txt"));
			for (File f : tocs) {
				genres = EscidocExtractor.extractVirrStructElems(f.getAbsolutePath());
				System.out.println(f.getName());
				writer.append(f.getName() + "\n");
				System.out.println(genres);
				writer.append(genres + "\n");
				list.addAll(genres);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	private static ArrayList<String> collectCorruptedTEIs() {
		ArrayList<String> list = new ArrayList<>();
		File tocDir = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/toc_xml");
		File metsDir = new File("/home/frank/data/wilhelm/misc_shit/virr2dlc/mets");
		File[] tocs = tocDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.matches("escidoc_[0-9]{5}_tei.xml");
			}
		});
		ArrayList<String> genres;
		String pb_num, image_num;
		Arrays.sort(tocs);
		for (File f : tocs) {
			
			pb_num = EscidocExtractor.extractXLink(f.getAbsolutePath(), "tei:pb");
			image_num = EscidocExtractor.extractXLink(metsDir + "/" + f.getName().replace("_tei", ""), "mets:div/@LABEL");
			System.out.println(f.getName() + " pbs " + pb_num + " pages " + image_num);
			/*
			genres = EscidocExtractor.extractPageBreaks(f.getAbsolutePath());
			System.out.println(f.getName() + "  " + genres);
			*/
		}
		return list;
	}

}
