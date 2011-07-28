package de.mpg.mpdl.dlc.wf.testing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

public class DynamicMods {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		dynamic();
        
	}
	public static void dynamic()
	{
		FileInputStream xsdInputStream;
		System.setProperty("com.sun.tools.xjc.api.impl.s2j.SchemaCompilerImpl.noCorrectnessCheck", "true");
		try {
			xsdInputStream = new FileInputStream("/opt/xmlbeans-2.4.0/schemas/customer.xsd");
			DynamicJAXBContext jaxbContext = 
	            DynamicJAXBContextFactory.createContextFromXSD(xsdInputStream, null, null, null);
			FileInputStream xmlInputStream = new FileInputStream("/home/frank/data/digitization_lifecycle/customer_sample.xml");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			DynamicEntity mods = (DynamicEntity) unmarshaller.unmarshal(xmlInputStream);
			System.out.println(mods.get("name"));
			DynamicEntity addr = mods.get("address");
			System.out.println(addr.get("street") + "   " + addr.get("city"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
