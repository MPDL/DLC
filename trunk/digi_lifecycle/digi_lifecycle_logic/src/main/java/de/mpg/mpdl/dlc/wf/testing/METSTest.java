package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;

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
		TEIParser.transformByPosition(null, new File("/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/BHR_Dg450-2971_withIds.tei"), 8);
		TEIParser.transform(null, new File("/home/frank/data/digitization_lifecycle/tei_samples/marx_with_ids.xml"), "d1e125");
		TEIParser.transform(null, new File("/home/frank/data/digitization_lifecycle/tei_bhr_khi/tei/BHR_Dg450-2971_withIds.tei"), "d1e315");
		TEIParser.transformByPosition(null, new File("/home/frank/data/digitization_lifecycle/tei_samples/marx_with_ids.xml"), 6);

		//listpbpositions();
		pagebyid();
		//applyids();
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
