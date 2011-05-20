package de.mpg.mpdl.dlc.wf.testing;

import java.io.*;
import javax.xml.validation.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class TEITest extends DefaultHandler {

    private TypeInfoProvider provider;
    
    public TEITest(TypeInfoProvider provider) {
        this.provider = provider;
    }

    public static void main(String[] args) throws SAXException, IOException {

        SchemaFactory factory 
         = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schemaLocation = new File("/home/frank/data/src/jaxb-ri-20101209/samples/tei_ms.xsd");
        Schema schema = factory.newSchema(schemaLocation);
    
        ValidatorHandler vHandler = schema.newValidatorHandler();
        TypeInfoProvider provider = vHandler.getTypeInfoProvider();
        ContentHandler   cHandler = new TEITest(provider);
        vHandler.setContentHandler(cHandler);
        
        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(vHandler);
        parser.parse("/home/frank/data/digitization_lifecycle/tei_samples/TestOxygen.xml");
        
    }
    
    public void startElement(String namespace, String localName,
      String qualifiedName, Attributes atts) throws SAXException {
        String type = provider.getElementTypeInfo().getTypeName();
        System.out.println(qualifiedName + ": " + type);
    }

}