package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
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

public class TEITransformation {
	
	XMLReader reader = null;
    Transformer transformer = null;
    Source XSL = null;
    Source XML = null;
    
    public void html(File file)
    {
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

        	File xmlFile = new File("/home/frank/data/digitization_lifecycle/tei_samples/marx.xml");
        	File xslFile;
			try {
				//xslFile = new File("src/main/resources/xsl/xhtml2/tei.xsl");
				xslFile = new File("/home/frank/data/digitization_lifecycle/tei_samples/paged_tei.xsl");
				
	        	XSL = new SAXSource(reader, new InputSource(xslFile.getAbsolutePath()));
			    TransformerFactory transfFactory = TransformerFactory.newInstance();
		        XML = new StreamSource(xmlFile);
		        Result result = new StreamResult(new File("/home/frank/data/digitization_lifecycle/tei_samples/test.html"));
		        transformer = transfFactory.newTransformer(XSL);
		        transformer.transform(XML, result);

			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
   

    }


}
