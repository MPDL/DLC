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
			InputStream stream2transform, InputStream xslt, String pbId,
			File output) {
		System.setProperty(TRANSFORMERFACTORY_KEY, TRANSFORMERFACTORY_VALUE);
		XSL = new SAXSource(reader, new InputSource(xslt));
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
			InputStream stream2transform, InputStream xslt, String pbId,
			File output) {
		System.setProperty(TRANSFORMERFACTORY_KEY, TRANSFORMERFACTORY_VALUE);
		XSL = new SAXSource(reader, new InputSource(xslt));
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
		String transformed = transformToString(file2transform, null, xslt, null, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static String teiFileToXhtmlByPagebreakId(File file2transform, InputStream xslt, String pbId) {
		String transformed = transformToString(file2transform, null, xslt, pbId, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static File teiFileToXhtml(File file2transform,
			InputStream xslt, File output) {
		File transformed = transformToFile(file2transform, null, xslt, null, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}

	public static File teiFileToXhtmlByPagebreakId(File file2transform,
			InputStream xslt, String pbId, File output) {
		File transformed = transformToFile(file2transform, null, xslt, pbId, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}

	public static String teiStreamToXhtml(InputStream tei2transform,
			InputStream xslt) {
		String transformed = transformToString(null, tei2transform, xslt, null, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static String teiStreamToXhtmlByPagebreakId(InputStream tei2transform,
			InputStream xslt, String pbId) {
		String transformed = transformToString(null, tei2transform, xslt, pbId, null);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
	
	public static File teiStreamToXhtml(InputStream tei2transform,
			InputStream xslt, File output) {
		File transformed = transformToFile(null, tei2transform, xslt, null, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}

	public static File teiStreamToXhtmlByPagebreakId(InputStream tei2transform,
			InputStream xslt, String pbId, File output) {
		File transformed = transformToFile(null, tei2transform, xslt, pbId, output);
		if (transformed != null) {
			return transformed;
		} else {
			return null;
		}
	}
}
