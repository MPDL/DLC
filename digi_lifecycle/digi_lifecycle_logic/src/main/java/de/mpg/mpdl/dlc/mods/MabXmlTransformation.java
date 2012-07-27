package de.mpg.mpdl.dlc.mods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import gov.loc.mods.v3.ModsDocument;

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

import de.ddb.application.MabToMabxml;
import de.ddb.application.XMabToUtf8;
import de.ddb.conversion.ConverterException;
import de.ddb.professionell.mabxml.mabxml1.DateiDocument;
import de.ddb.professionell.mabxml.mabxml1.DatensatzType;
import de.ddb.professionell.mabxml.mabxml1.FeldType;
import de.mpg.mpdl.dlc.wf.testing.EscidocTests;

public class MabXmlTransformation {

	DateiDocument mabxml = null;
	DatensatzType mabRecord = null;

	XMLReader reader = null;
	Transformer transformer = null;
	Source XSL = null;
	Source XML = null;

	public File mabToMods(String mabId, File mabFile) {  
		File utf8 = mabFileToUtf8(mabFile);
		File xml = mabUtf8ToXml(utf8);
		DatensatzType mabRecord = getMabRecord(mabId, xml);
//		DatensatzType mabRecord = getMabRecord(mabId, mabFile);
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
}
