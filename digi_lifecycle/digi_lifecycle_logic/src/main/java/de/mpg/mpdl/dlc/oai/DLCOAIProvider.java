/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/

package de.mpg.mpdl.dlc.oai;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.escidoc.core.resources.om.item.Item;
import de.mpg.mpdl.dlc.util.PropertyReader;

public class DLCOAIProvider {
	
	private static Logger logger = Logger.getLogger(DLCOAIProvider.class);
	private static DLCOAIUtils utils = new DLCOAIUtils();
	
	public DLCOAIProvider() {
		
	}
	
	public void addOrUpdateOAIDataStore(Item item) {
		String filename = null;
		String xslt_path = null;
		String source_path = null;
		String result_path = null;
		String tmpString = item.getObjid().replace("escidoc:", "dlc_");
		if (tmpString.contains(":")) {
			filename = tmpString.substring(0, tmpString.indexOf(":")) + ".xml";
		} else {
			filename = tmpString + ".xml";
		}
		xslt_path = "export/escidoc2mets.xsl";
		HashMap<String, String> map = utils.itemParameterMap(item);
		try {
			source_path = PropertyReader.getProperty("escidoc.common.framework.url") + item.getMetadataRecords().get("escidoc").getXLinkHref().substring(1);
			result_path = PropertyReader.getProperty("oai.provider.data.store") + "/" + filename;
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		if (map != null) {
			transform(xslt_path, source_path, result_path, map);
			logger.info("added or updated file " + filename + " in OAI data store");
			boolean setSpecExists = checkSetDefinition(item.getProperties().getContext().getObjid());
			if (!setSpecExists) {
				addSetDefinition(item);
			}
		} else {
			logger.error("file " + filename + " couldn't be added to OAI data store");
		}
	}
	
	public void removeFromOAIDataStore(String itemId) {
		String filename = null;
		String tmpString = itemId.replace("escidoc:", "dlc_");
		if (tmpString.contains(":")) {
			filename = tmpString.substring(0, tmpString.indexOf(":")) + ".xml";
		} else {
			filename = tmpString + ".xml";
		}
		try {
			File file2delete = new File(PropertyReader.getProperty("oai.provider.data.store") + "/" + filename);
			if (file2delete.delete()) {
				logger.info("Deleted file: " + filename + " from OAI data store");
			}
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	public void addSetDefinition(Item item) {
		String xslt_path = null;
		String source_path = null;
		String result_path = null;
		
		xslt_path = "export/updateSetDefinition.xsl";
		HashMap<String, String> map = utils.ctxParameterMap(item);
		try {
			result_path = PropertyReader.getProperty("oai.provider.set.definition");
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		if (map != null) {
			transform(xslt_path, source_path, result_path, map);
			logger.info("Added new set definition for " + map.get("ctxId"));
		} else {
			logger.error("Couldn't add new set definition!");
		}
		
	}
	
	public boolean checkSetDefinition(String ctx_id) {
		try {
			ctx_id = ctx_id.replace("escidoc:", "ctx_");
			String set_spec_xml = PropertyReader.getProperty("oai.provider.set.definition");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document cfg_doc = db.parse(new File(set_spec_xml));
			NodeList setSpec_nodes = cfg_doc.getElementsByTagName("setSpec");
			for (int i = 0; i < setSpec_nodes.getLength(); i++) {
				Node setSpec = setSpec_nodes.item(i);
				if (setSpec.getTextContent().equalsIgnoreCase(ctx_id)) {
					return true;
				}
			}
			
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return false;
	}

	public void transform(String xslt_path, String source_path, String result_path, HashMap<String, String> paramMap) {
		
		InputStream xslt = DLCOAIProvider.class.getClassLoader().getResourceAsStream(xslt_path);
		StreamSource xslt_src = new StreamSource(xslt);
		TransformerFactory transformerFactory = new TransformerFactoryImpl();
		try {
			Transformer transformer = transformerFactory.newTransformer(xslt_src);
			for (Entry<String, String> e : paramMap.entrySet()) {
				transformer.setParameter(e.getKey(), e.getValue());
			}
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamSource stream = null;
			if (source_path == null) {
				stream = new StreamSource(new File(result_path));
			} else {
				stream = new StreamSource(source_path);
			}
			StreamResult result = new StreamResult(new File(result_path));
	 
			transformer.transform(stream, result);
			
		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (TransformerException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		} finally {
			if (xslt!=null) {
				try {
					xslt.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					e.printStackTrace();
				}
			}
		}
	}
}
