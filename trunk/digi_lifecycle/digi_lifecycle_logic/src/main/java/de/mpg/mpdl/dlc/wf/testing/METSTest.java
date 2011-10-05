package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlException;
import org.xml.sax.InputSource;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.tei.TEIParser;
import de.mpg.mpdl.dlc.tei.TEITransformer;

import gov.loc.mets.MetsDocument;
import gov.loc.mets.MetsDocument.Mets;
import gov.loc.mets.MetsType.FileSec;

public class METSTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			FileInputStream fin = new FileInputStream(new File("/home/frank/data/digitization_lifecycle/test/berlin.tei"));
			System.out.println(tei2mets(fin));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//listpbpositions();
		//pagebyid();
		//applyids();
 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String tei2mets(InputStream in) throws Exception {
		URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToMets/tei_to_mets.xslt");
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
		SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
		
		
		Source teiXmlSource = new StreamSource(in);

		FileWriter wr = new FileWriter(new File("/home/frank/data/digitization_lifecycle/test/berlin.mets"));
		javax.xml.transform.Result result = new StreamResult(wr);
		
		TransformerFactory transfFact = TransformerFactory.newInstance();
		Transformer transformer = transfFact.newTransformer(xsltSource);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(teiXmlSource, result);
		
		return wr.toString();
	}
	public static void listpbpositions()
	{
		LinkedHashMap<String, Integer> map = TEIParser.getPageBreakPositions(null, new File("/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/BHR_Dg450-2971_withIds.tei"));
		Iterator<Entry<String, Integer>> iter = map.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, Integer> e = (Entry<String, Integer>) iter.next();
			if (e.getKey().equalsIgnoreCase("d1e256"))
			{
				Entry<String, Integer> next = (Entry<String, Integer>) iter.next();
				System.out.println("current:" + e.getKey() + "   " + e.getValue());
				System.out.println("andnext:" + next.getKey() + "   " + next.getValue());
			}
		}
		// for (Entry<String, Integer> e : map.entrySet())
		// {
		// 	System.out.println(e.getKey() + "   " + e.getValue());
		// }
	}
	
	public static void pagebyid()
	{
		long time = System.currentTimeMillis();

		//File xml = new File("/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/BHR_Dg450-2971_withIds.tei");
		File xml = new File("/home/frank/data/digitization_lifecycle/tei_samples/marx_with_ids.xml");
		String xslt = "/home/frank/data/digitization_lifecycle/tei_samples/tei_pageByPbId2xhtml.xsl";
		File out = new File("/home/frank/data/digitization_lifecycle/tei_samples/marx_page.xhtml");

		InputStream in;
		//in = new FileInputStream(xslt);
		in = METSTest.class.getClassLoader().getResourceAsStream("xslt/teiToXhtml/tei_pageByPbId2xhtml.xsl");
		System.out.println(TEITransformer.teiFileToXhtmlByPagebreakId(xml, in, "d1e789", out));
		time = System.currentTimeMillis() - time;
		System.out
				.println("total time to transform TEI: "
						+ time + " ms");
		
	}
	
	public static void applyids()
	{
		File xml = new File("/home/frank/data/digitization_lifecycle/tei_samples/marx.xml");
		String xslt = "/home/frank/data/digitization_lifecycle/tei_samples/tei_add_ids.xslt";
		File out = new File("/home/frank/data/digitization_lifecycle/tei_samples/marx_with_ids.xml");
		InputStream in;
		try {
			in = new FileInputStream(xslt);
			System.out.println(TEITransformer.teiFileToXhtmlByPagebreakId(xml, in, null, out));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
