package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportResolver implements EntityResolver {

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		String filename = new File(systemId).getName();
		filename = "/opt/xmlbeans-2.4.0/schemas/" + filename;
		InputSource is = new InputSource(ClassLoader.getSystemResourceAsStream(filename));
		is.setSystemId(filename);
		return is;
	}

}
