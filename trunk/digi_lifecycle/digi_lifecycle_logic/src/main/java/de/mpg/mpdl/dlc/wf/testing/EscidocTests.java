package de.mpg.mpdl.dlc.wf.testing;

import gov.loc.mods.v3.ModsDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;

import de.ddb.application.MabToMabxml;
import de.ddb.application.XMabToUtf8;
import de.ddb.conversion.ConverterException;
import de.ddb.professionell.mabxml.mabxml1.DatensatzType;
import de.mpg.mpdl.dlc.core.DLCProperties;
import de.mpg.mpdl.dlc.escidoc.Login;
import de.mpg.mpdl.dlc.escidoc.XBeanUtils;
import de.mpg.mpdl.dlc.escidoc.rest.ItemController;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocComponent;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocItem;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.ContentCategory;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.MimeType;
import de.mpg.mpdl.dlc.escidoc.xml.EscidocProperties.Visibility;
import de.mpg.mpdl.dlc.escidoc.xml.MetadataXML;
import de.mpg.mpdl.dlc.mods.MabToMods;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;

public class EscidocTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File mab = new File("/home/frank/data/digitization_lifecycle/dlib-journals/single_mab");
		String mabId = "74525";

		// getFileMd();
		// itemxml("dlc:1234", "dlc:2222");
		//File mabxml = mab2xml(mab);
		//getthetei();
		//createCModel();
		testMab3Mods(mabId, mab);
	}
	
	public static void testMab3Mods(String id, File f)
	{
		InputStream xslt = EscidocTests.class.getClassLoader().getResourceAsStream("xslt/mabToMods/mab2mods.xsl");

		MabXmlTransformation mxt = new MabXmlTransformation();
		File modsFile = mxt.mabToMods(id, f);
		ModsDocument mods;
		try {
			mods = ModsDocument.Factory.parse(modsFile);
			if (XBeanUtils.validation(mods))
			{
				System.out.println(mods.xmlText(XBeanUtils.getModsOpts()));
			}
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void getFileMd()
	{
		URL fwurl;
		Login login = new Login();
		String uh = null;
		try {
			fwurl = new URL("http://latest-coreservice.mpdl.mpg.de:8080");
			uh = login.loginVirrUser(fwurl);

			File file2upload = new File("/home/frank/data/digitization_lifecycle/tei_samples/marx.xml");
			//System.out.println(MetadataXML.component().xmlText(XBeanUtils.getFileOpts()));
			System.out.println(EscidocComponent.create(Visibility.PUBLIC, ContentCategory.DLC_TEI_FULLTEXT, MimeType.APPLICATION_XML, fwurl, file2upload, uh).xmlText(XBeanUtils.getItemOpts()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void itemxml(String ctxid, String cmid)
	{
		ModsDocument md = MetadataXML.mods();
		String itemxml = EscidocItem.createXML(ctxid, cmid, md);
		System.out.println(itemxml);
		File mab = new File(DLCProperties.get("dlc.ingest.mab.file"));
		String mabid = "171221";
		XmlString metadata = MetadataXML.encodedMAB(mabid, mab);
		System.out.println(EscidocItem.addMdRecord(itemxml, "encodedMAB", metadata));
		
	}
	
	public static void mab2mods(File mabxml)
	{
		// File mabxml = new File("/home/frank/data/digitization_lifecycle/dlib-journals/gfi1703 lfg06-10_mab.xml");
		// File xslt = new File("/home/frank/data/digitization_lifecycle/tei_samples/mab2mods3.xsl");
		InputStream xslt = EscidocTests.class.getClassLoader().getResourceAsStream("xslt/mabToMods/mab2mods.xsl");
		InputStream xslt2xhtml = EscidocTests.class.getClassLoader().getResourceAsStream("xslt/mabToMods/mods2xhtml.xsl");
		InputStream xslt2rdf = EscidocTests.class.getClassLoader().getResourceAsStream("xslt/modsToRdf/mods2rdf.xslt");


		String mabId = "74525";
		ModsDocument md = MabToMods.fromMabXml(mabxml, mabId);
		// System.out.println(md.xmlText(XBeanUtils.getModsOpts()));
		
		MabXmlTransformation mxt = new MabXmlTransformation();
		DatensatzType dst = mxt.getMabRecord(mabId, mabxml);
		File modsFile = mxt.getMods(dst);
		
	}
	
	public static void mabToMabXml(File mabfile)
	{
		try {
			File mabXml = new File("/home/frank/data/digitization_lifecycle/dlib-journals/second_sample_mab_utf8.xml");
			String path2jar = "/home/frank/data/digitization_lifecycle/dlib-journals/MAB/dnbConversionTools-1.9.0/tools/";
			Process process = Runtime.getRuntime().exec("java -jar " + path2jar + "Mab2Mabxml-1.9.0.jar -i " + mabfile.getAbsolutePath() + " -o " + mabXml.getAbsolutePath());
			InputStream in = process.getInputStream();
			InputStream err = process.getErrorStream();
			byte b[]=new byte[in.available()];
	        in.read(b,0,b.length);
	        System.out.println(new String(b));
	        byte e[]=new byte[err.available()];
	        err.read(e,0,e.length);
	        System.out.println(new String(e));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File mab2xml(File mab)
	{
		try {
			File mabUtf8 = File.createTempFile(mab.getName(), "utf8");
			File mabXml = File.createTempFile(mab.getName(), "xml");
			String[] args_utf8 = {"-i", mab.getAbsolutePath(), "-o", mabUtf8.getAbsolutePath()};
			String[] args_xml = {"-i", mabUtf8.getAbsolutePath(), "-o", mabXml.getAbsolutePath()};
			XMabToUtf8.main(args_utf8);
			MabToMabxml.main(args_xml);
			return mabXml;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConverterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void getthetei()
	{
		String tei = ItemController.create();
		System.out.println(tei);
		
	}
	
	public static void createCModel()
	{
		ItemController.cmup();
	}
}
