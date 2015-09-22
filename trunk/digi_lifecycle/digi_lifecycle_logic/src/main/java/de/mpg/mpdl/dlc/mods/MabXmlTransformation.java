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
package de.mpg.mpdl.dlc.mods;

import gov.loc.mods.v3.ModsDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import de.ddb.professionell.mabxml.mabxml1.DateiDocument;
import de.ddb.professionell.mabxml.mabxml1.DatensatzType;
import de.ddb.professionell.mabxml.mabxml1.FeldType;

public class MabXmlTransformation {

	DateiDocument mabxml = null;
	DatensatzType mabRecord = null;

	XMLReader reader = null;
	Transformer transformer = null;
	Source XSL = null;
	Source XML = null;

	public File mabToMods(String mabId, File mabFile) {  
		// if input is .mab		
//		File utf8 = mabFileToUtf8(mabFile);
//		File xml = mabUtf8ToXml(utf8);
//		DatensatzType mabRecord = getMabRecord(mabId, xml);
		
		//if input is mabXML
		DatensatzType mabRecord = getMabRecord(mabId, mabFile);
		File mods = getMods(mabRecord);
		return mods;
	}

	public File getMods(DatensatzType mabRecord) {
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
		
		URL url = MabXmlTransformation.class.getClassLoader().getResource("xslt/mabToMods/mab2mods.xsl");

		
		//File xslt = new File("src/main/resources/xslt/mabToMods/mab2mods.xsl");
		// ModsDocument modsDocument = null;
		// XSL = new SAXSource(reader, new InputSource(xslt));
		try {
		XSL = new StreamSource(url.openStream());
//		XSL = new StreamSource(new File("C:/Projects/virr/digi_lifecycle/digi_lifecycle_presentation/src/main/resources/xslt/mabToMods/mab2mods.xsl"));
		XML = new StreamSource(mabRecord.newInputStream());

		
			File transformed = File.createTempFile("transformed", "xml");
			Result result = new StreamResult(transformed);
			TransformerFactory transfFactory = TransformerFactory.newInstance();
			transformer = transfFactory.newTransformer(XSL);
			transformer.transform(XML, result);
			// modsDocument = ModsDocument.Factory.parse(transformed);
			// return modsDocument;
			return transformed;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File getModsAsXhtml(ModsDocument mods, InputStream xslt) {
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
		File resultingXhtml = null;
		XSL = new SAXSource(reader, new InputSource(xslt));
		XML = new StreamSource(mods.newInputStream());
		try {
			resultingXhtml = File.createTempFile("transformed", "xhtml");
			Result result = new StreamResult(resultingXhtml);
			TransformerFactory transfFactory = TransformerFactory.newInstance();
			transformer = transfFactory.newTransformer(XSL);
			transformer.transform(XML, result);
			return resultingXhtml;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public File getModsAsRdf(ModsDocument mods, InputStream xslt) {
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
		File resultingXhtml = null;
		XSL = new SAXSource(reader, new InputSource(xslt));
		XML = new StreamSource(mods.newInputStream());
		try {
			resultingXhtml = File.createTempFile("transformed", "xhtml");
			Result result = new StreamResult(System.out);
			TransformerFactory transfFactory = TransformerFactory.newInstance();
			transformer = transfFactory.newTransformer(XSL);
			transformer.transform(XML, result);
			return resultingXhtml;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DatensatzType getMabRecord(String mab001Id, File mabXmlFile) {
		try {
			mabxml = DateiDocument.Factory.parse(mabXmlFile);
			mabxml.getDatei().getDatensatzArray().toString();
			if (mabxml.getDatei().sizeOfDatensatzArray() > 1) {
				DatensatzType[] mabRecords = mabxml.getDatei()
						.getDatensatzArray();
				for (DatensatzType dst : mabRecords) {
					FeldType mab001Field = dst.getFeldArray(0);
					if (mab001Field.xmlText().contains(mab001Id)) {
						mabRecord = dst;
					}
				}
			}
			else
			{
				mabRecord = mabxml.getDatei().getDatensatzArray(0);
			}
			return mabRecord;
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	public File mabFileToUtf8(File mabFile) {
		File mabUtf8;
		try {
			mabUtf8 = File.createTempFile(mabFile.getName(), "utf8");
			String[] args_utf8 = { "-i", mabFile.getAbsolutePath(), "-o",
					mabUtf8.getAbsolutePath() };
			XMabToUtf8.main(args_utf8);
			return mabUtf8;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConverterException e) {
			e.printStackTrace();
		}
		return null;
	}
  
	public File mabUtf8ToXml(File mabUtf8) {
		File mabXml;
		try {
			mabXml = File.createTempFile(mabUtf8.getName(), "xml");
			String[] args_xml = { "-i", mabUtf8.getAbsolutePath(), "-o",
					mabXml.getAbsolutePath() };
			MabToMabxml.main(args_xml);
			return mabXml;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConverterException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
}
