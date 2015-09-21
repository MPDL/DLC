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
package de.mpg.mpdl.dlc.tei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.mpg.mpdl.dlc.core.DLCProperties;

public class TEITransformer {

	public static final String TRANSFORMERFACTORY_KEY = "javax.xml.transform.TransformerFactory";
	public static final String TRANSFORMERFACTORY_VALUE = DLCProperties
			.get("tei.transformation.transformer.factory");

	static XMLReader reader = null;
	static Transformer transformer = null;
	static Source XSL = null;
	static Source XML = null;
	static StreamResult result = null;

	static File transformToFile(File file2transform,
			InputStream stream2transform, InputStream xslt, String systemId, String pbId,
			File output) {
		System.setProperty(TRANSFORMERFACTORY_KEY, TRANSFORMERFACTORY_VALUE);
		if (xslt != null) {
			XSL = new SAXSource(reader, new InputSource(xslt));
		} else {
			if (systemId != null) {
				XSL = new StreamSource(systemId);
			}
		}
		TransformerFactory transfFactory = TransformerFactory.newInstance();
		if (file2transform != null) {
			XML = new StreamSource(file2transform);
		} else {
			if (stream2transform != null) {
				XML = new StreamSource(stream2transform);
			}
		}
		try {
			if (output != null)	{
				result = new StreamResult(new FileWriter(output));
			}
			transformer = transfFactory.newTransformer(XSL);
			if (pbId != null) {
				transformer.setParameter("pbid", pbId);
			}
			transformer.transform(XML, result);
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static String transformToString(File file2transform,
			InputStream stream2transform, InputStream xslt, String systemId, String pbId,
			File output) {
		System.setProperty(TRANSFORMERFACTORY_KEY, TRANSFORMERFACTORY_VALUE);
		if (xslt != null) {
			XSL = new SAXSource(reader, new InputSource(xslt));
		} else {
			if (systemId != null) {
				XSL = new StreamSource(systemId);
			}
		}
		TransformerFactory transfFactory = TransformerFactory.newInstance();
		if (file2transform != null) {
			XML = new StreamSource(file2transform);
		} else {
			if (stream2transform != null) {
				XML = new StreamSource(stream2transform);
			}
		}
		try {
			result = new StreamResult(new StringWriter());
			transformer = transfFactory.newTransformer(XSL);
			if (pbId != null) {
				transformer.setParameter("pbid", pbId);
			}
			transformer.transform(XML, result);
			return result.getWriter().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String teiFileToXhtml(File file2transform, InputStream xslt) {
		String transformed = transformToString(file2transform, null, xslt, null, null, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static String teiFileToXhtmlUsingSystemId(File file2transform, String systemId) {
		String transformed = transformToString(file2transform, null, null, systemId, null, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static String teiFileToXhtmlByPagebreakId(File file2transform, InputStream xslt, String pbId) {
		String transformed = transformToString(file2transform, null, xslt, null, pbId, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static File teiFileToXhtml(File file2transform,
			InputStream xslt, File output) {
		File transformed = transformToFile(file2transform, null, xslt, null, null, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}

	public static File teiFileToXhtmlByPagebreakId(File file2transform,
			InputStream xslt, String pbId, File output) {
		File transformed = transformToFile(file2transform, null, xslt, null, pbId, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}

	public static String teiStreamToXhtml(InputStream tei2transform,
			InputStream xslt) {
		String transformed = transformToString(null, tei2transform, xslt, null, null, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static String teiStreamToXhtmlByPagebreakId(InputStream tei2transform,
			InputStream xslt, String pbId) {
		String transformed = transformToString(null, tei2transform, xslt, null, pbId, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static File teiStreamToXhtml(InputStream tei2transform,
			InputStream xslt, File output) {
		File transformed = transformToFile(null, tei2transform, xslt, null, null, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}

	public static File teiStreamToXhtmlByPagebreakId(InputStream tei2transform,
			InputStream xslt, String pbId, File output) {
		File transformed = transformToFile(null, tei2transform, xslt, null, pbId, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
}
