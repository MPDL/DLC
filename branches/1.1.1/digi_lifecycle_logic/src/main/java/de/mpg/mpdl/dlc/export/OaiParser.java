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
package de.mpg.mpdl.dlc.export;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.escidoc.core.common.exceptions.remote.application.notfound.ResourceNotFoundException;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;

/**
 * This class handles the response from the escidoc oai provider, 'ListRecords' in oai_dc format and transforms
 * the xml in mets/mods format.
 * @author kleinfe1
 *
 */
public class OaiParser {

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	/**
	 * Process the record
	 * @param oaiDcXml
	 * @return
	 */
	public String parseListRecords(String oaiDcXml)
	{
		String oaiZvddXml = "";
		NodeList ids = null;
		Volume volume = null;
		
		oaiZvddXml += this.getOaiStart();
		
		try {
			//1. Parse the xml for the dc entries
			ids = this.parseDocumentForId(oaiDcXml);
		
			//2. Get the item (by id) and transform to zvdd
			for (int i = 0; i < ids.getLength(); i++)
			{
				String id = ids.item(i).getTextContent();
				volume = this.getVolumeById(id);
				oaiZvddXml += this.getOaiRecord(volume);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		oaiZvddXml += this.getOaiEnd();
		
		return oaiZvddXml;
	}
	
	/**
	 * Add the oai prolog
	 * @return
	 */
	private String getOaiStart()
	{
		String start ="";
		try {
			start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
							"\n <OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"" +
							" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
							" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/" +
			                " http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">" +
			                "<responseDate>"+ new Date(System.currentTimeMillis()) +"</responseDate>" +
			                "<request verb=\"ListRecords\" metadataPrefix=\"zvdd\">" +
			                PropertyReader.getProperty("escidoc.common.login.url") + "oai/" +"</request>" +
			                "<ListRecords>";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		return start;
	}
	
	private String getOaiEnd()
	{
		String end = 	"</ListRecords>" +
						"</OAI-PMH>";		
		return end;
	}
	
	/**
	 * Create the metd/mods record
	 * @param volume
	 * @return
	 */
	private String getOaiRecord(Volume volume)
	{
		String record = "<record xmlns=\"http://www.openarchives.org/OAI/2.0/\">" +
							"<header>" + 
								"<identifier>"+ "oai:escidoc.org:"+ volume.getItem().getObjid() +"</identifier>" +
								"<datestamp>"+ volume.getItem().getProperties().getCreationDate() +"</datestamp>" + 
								"<setSpec>"+ "context_" + volume.getProperties().getContext().getObjid().replace(":", "_") +"</setSpec>" +
								"</header>" +
								"<metadata>";
		//get mods metadata
		Export export = new Export();
		try {
			String md = new String (export.metsModsExport(volume.getItem().getObjid(), false));
			md = md.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			record += md;
		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		record += 		"</metadata>" +
					"</record>";
		
		return record;
	}
	
	private Volume getVolumeById(String id)
	{
		Volume volume = null;
		
		try {
			volume = volServiceBean.loadCompleteVolume(id, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return volume;
	}
	
	private NodeList parseDocumentForId(String xml) throws SAXException, IOException, ParserConfigurationException
	{
		NodeList ids = null;
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        Document recordDOM; 
        DocumentBuilder builder;
        
        builder = domFactory.newDocumentBuilder();
        recordDOM = builder.parse(new InputSource(new StringReader(xml)));
        
        ids = recordDOM.getElementsByTagName("dc:identifier");

		return ids;
	}
}
