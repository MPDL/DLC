package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class TEIValidator
{
    public static String errorMessage;
    public static void check(File teiFile)
    {
                        System.out.println(teiFile.getName());
                        if (dom(teiFile))
                        {
                            
                        }
                        else
                        {
                            System.out.println(teiFile.getName() + " IS NOT VALID");
                        }
    }

    public static boolean sax(File file)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            factory.setXIncludeAware(true);
            
            //SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            //    factory.setSchema(schemaFactory.newSchema();
            SAXParser parser = factory.newSAXParser();
            DefaultHandler errorHandler = new SAXErrorHandler();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(errorHandler);
            reader.parse(new InputSource(new FileInputStream(file)));
            if (((SAXErrorHandler)errorHandler).isValid())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (ParserConfigurationException e)
        {
            System.out.println(e.toString());
        }
        catch (SAXException e)
        {
            System.out.println(e.toString());
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        return false;
    }

    public static boolean dom(File file)
    {
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        //dbf.setNamespaceAware(true);
        //dbf.setXIncludeAware(true);
        System.out.println(System.getProperty("javax.xml.transform.TransformerFactory"));

        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            ErrorHandler errorHandler = new DOMErrorHandler();
            db.setErrorHandler(errorHandler);
            db.setEntityResolver(new Resolver());
            Document d = db.parse(file);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            URL dtd = TEIValidator.class.getClassLoader().getResource("tei_all.dtd");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtd.toString());
            Source source = new DOMSource(d);
            Result result = new StreamResult(new FileOutputStream(new File("/tmp/TEItmp.xml")));
            transformer.transform(source, result);
            System.out.println("validating " + file.getName());
            
            if (sax(new File("/tmp/TEItmp.xml")))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (TransformerFactoryConfigurationError e)
        {
            e.printStackTrace();
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean rng(File file)
    {
        System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI, "com.sun.msv.verifier.jarv.RELAXNGFactoryImpl");

        SchemaFactory factory 
         = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        
        File schemaLocation = new File("/home/frank/data/digitization_lifecycle/tei_samples/myTEI_02/myTEI_02.rng");
        
        String input 
         = "file:///home/frank/data/digitization_lifecycle/tei_samples/myTEI_02/myTEI_02.xml";
        Source source = new StreamSource(input);
        try {
            Schema schema = factory.newSchema(schemaLocation);
            Validator validator = schema.newValidator();
            validator.validate(source);
            System.out.println(input + " is valid.");
        }
        catch (SAXException ex) {
            System.out.println(input + " is not valid because ");
            System.out.println(ex.getMessage());
        } catch (IOException e) {
			e.printStackTrace();
		}
		return false;  

    }

    private static class SAXErrorHandler extends DefaultHandler
    {
        public boolean valid = true;
        public StringBuilder sb = null;
        public void warning(SAXParseException e) throws SAXException
        {
            System.out.println("Warning: ");
            sb = new StringBuilder("Warning: \n");
            printInfo(e);
        }

        public void error(SAXParseException e) throws SAXException
        {
            setValid(false);
            System.out.println("Error: ");
            sb = new StringBuilder("Error: \n");
            printInfo(e);
        }

        public void fatalError(SAXParseException e) throws SAXException
        {
            setValid(false);
            System.out.println("Fatal error: ");
            sb = new StringBuilder("Fatal error: \n");
            printInfo(e);
        }

        private void printInfo(SAXParseException e)
        {
            System.out.println("   Public ID: " + e.getPublicId());
            System.out.println("   System ID: " + e.getSystemId());
            System.out.println("   Line number: " + e.getLineNumber());
            System.out.println("   Column number: " + e.getColumnNumber());
            System.out.println("   Message: " + e.getMessage());
            sb.append("   Public ID: " + e.getPublicId() + "\n");
            sb.append("   System ID: " + e.getSystemId() + "\n");
            sb.append("   Line number: " + e.getLineNumber() + "\n");
            sb.append("   Column number: " + e.getColumnNumber() + "\n");
            sb.append("   Message: " + e.getMessage());
            TEIValidator.errorMessage = sb.toString();
        }

        public void setValid(boolean valid)
        {
            this.valid = valid;
        }

        public boolean isValid()
        {
            return valid;
        }
    }

    private static class DOMErrorHandler implements ErrorHandler
    {
        public void warning(SAXParseException e) throws SAXException
        {
            System.out.println("Warning: ");
            printInfo(e);
        }

        public void error(SAXParseException e) throws SAXException
        {
            System.out.println("Error: ");
            printInfo(e);
        }

        public void fatalError(SAXParseException e) throws SAXException
        {
            System.out.println("Fattal error: ");
            printInfo(e);
        }

        private void printInfo(SAXParseException e)
        {
            System.out.println("   Public ID: " + e.getPublicId());
            System.out.println("   System ID: " + e.getSystemId());
            System.out.println("   Line number: " + e.getLineNumber());
            System.out.println("   Column number: " + e.getColumnNumber());
            System.out.println("   Message: " + e.getMessage());
        }
    }

    private static class Resolver implements EntityResolver
    {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
        {
            File f = new File("/home/frank/data/DARIAH/tei-1.5.0/xml/tei/custom/schema/dtd/tei_all.dtd");
            FileInputStream fis = null;
            try
            {
                fis = new FileInputStream(f);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            return new InputSource(fis);
        }
    }
}
