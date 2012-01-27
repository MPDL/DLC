package de.mpg.mpdl.dlc.wf.testing;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataFactory;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQMetaData;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQStaticContext;

import net.sf.saxon.xqj.SaxonXQDataSource;

public class YQueryProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		query1();
	}
	
	public static void query1() {
		XQDataSource xqds = new SaxonXQDataSource();
		/*
		for (String prop : xqds.getSupportedPropertyNames()) {
			System.out.println(prop);
		}
		*/
		try {
			XQConnection conn = xqds.getConnection();
			// XQMetaData meta = conn.getMetaData();
			// System.out.println(meta.getProductName() + "  " + meta.getProductVersion());
			XQStaticContext context = conn.getStaticContext();
			//context.setBaseURI("http://latest-coreservice.mpdl.mpg.de:8080");
			context.setBaseURI("http://latest-coreservice.mpdl.mpg.de:8080");
			context.declareNamespace("mets", "http://www.loc.gov/METS/");
			context.declareNamespace("item", "http://www.escidoc.de/schemas/item/0.10");
			context.declareNamespace("comp", "http://www.escidoc.de/schemas/components/0.9");
			context.declareNamespace("prop", "http://escidoc.de/core/01/properties/");
			context.declareNamespace("xlink", "http://www.w3.org/1999/xlink");
			conn.setStaticContext(context);
			XQExpression expr = conn.createExpression();
			String search = "/srw/search/dlc_index?query=escidoc.objecttype=item%20and%20escidoc.context.objid=escidoc:1001&amp;maximumRecords=50";
			URI uri = new URI(search);
			String query = "for $item in doc('" + search + "')//item:item\n";
			query += "for $comp in $item//comp:component\n";
			query += "where $comp/comp:properties/prop:content-category = 'mets'\n";
			// query += "let $href := $comp/comp:content/@xlink:href\n";
			query += "return ";
			query += "for $flocat in doc($comp/comp:content/@xlink:href)//mets:FLocat/@xlink:href\n";
			query += "return $flocat";
			XQResultSequence result = expr.executeQuery(query);
			while (result.next()) {
				// System.out.println(result.getItemType());
				System.out.println(result.getNode().getNodeValue());
				// System.out.println(result.getAtomicValue());
			}
			result.close();
			expr.close();
			conn.close();
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
 
	}

}
