package de.mpg.mpdl.dlc.util;

import java.util.ArrayList;
import java.util.Properties;

import javax.xml.stream.XMLStreamReader;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQStaticContext;

import net.sf.saxon.xqj.SaxonXQDataSource;

public class EscidocExtractor {
	
	private static XQStaticContext nameSpacees(XQStaticContext context) throws XQException {
		//context.setBaseURI("https://coreservice.mpdl.mpg.de");
		context.declareNamespace("publication", "http://purl.org/escidoc/metadata/profiles/0.1/publication");
		context.declareNamespace("eterms", "http://purl.org/escidoc/metadata/terms/0.1/");
		context.declareNamespace("person", "http://purl.org/escidoc/metadata/profiles/0.1/person");
		context.declareNamespace("organization", "http://purl.org/escidoc/metadata/profiles/0.1/organization");
		context.declareNamespace("dc", "http://purl.org/dc/elements/1.1/");
		context.declareNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		context.declareNamespace("dcterms", "http://purl.org/dc/terms/");
		context.declareNamespace("event", "http://purl.org/escidoc/metadata/profiles/0.1/event");
		context.declareNamespace("file", "http://purl.org/escidoc/metadata/profiles/0.1/file");
		context.declareNamespace("item", "http://www.escidoc.de/schemas/item/0.10");
		context.declareNamespace("md", "http://www.escidoc.de/schemas/metadatarecords/0.5");
		context.declareNamespace("comp", "http://www.escidoc.de/schemas/components/0.9");
		context.declareNamespace("prop", "http://escidoc.de/core/01/properties/");
		context.declareNamespace("srel", "http://escidoc.de/core/01/structural-relations/");
		context.declareNamespace("xlink", "http://www.w3.org/1999/xlink");
		context.declareNamespace("relations", "http://www.escidoc.de/schemas/relations/0.3");
		context.declareNamespace("content", "http://www.escidoc.de/schemas/contentstreams/0.7");
	    context.declareNamespace("user-account", "http://www.escidoc.de/schemas/useraccount/0.7");
	    context.declareNamespace("organizational-unit", "http://www.escidoc.de/schemas/organizationalunit/0.8");
	    context.declareNamespace("mdou", "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit");
	    context.declareNamespace("user-group", "http://www.escidoc.de/schemas/usergroup/0.6");
		return context;
	}
	
	private static XQStaticContext nameSpacees12(XQStaticContext context) throws XQException {
		context.setBaseURI("http://r-coreservice.mpdl.mpg.de");
		context.declareNamespace("container", "http://www.escidoc.de/schemas/container/0.8");
		context.declareNamespace("toc", "http://www.escidoc.de/schemas/tableofcontent/0.1");
		context.declareNamespace("struct-map", "http://www.escidoc.de/schemas/structmap/0.4");
		context.declareNamespace("relations", "http://www.escidoc.de/schemas/relations/0.3");
		context.declareNamespace("md", "http://www.escidoc.de/schemas/metadatarecords/0.5");
		context.declareNamespace("prop", "http://escidoc.de/core/01/properties/");
		context.declareNamespace("srel", "http://escidoc.de/core/01/structural-relations/");
		context.declareNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		context.declareNamespace("xlink", "http://www.w3.org/1999/xlink");
		context.declareNamespace("virr", "http://purl.org/escidoc/metadata/profiles/0.1/virrelement");
		context.declareNamespace("mods", "http://www.loc.gov/mods/v3");
		context.declareNamespace("mets", "http://www.loc.gov/METS/");
		context.declareNamespace("comp", "http://www.escidoc.de/schemas/components/0.9");
		context.declareNamespace("item", "http://www.escidoc.de/schemas/item/0.9");
		context.declareNamespace("file", "http://purl.org/escidoc/metadata/profiles/0.1/file");
		context.declareNamespace("dc", "http://purl.org/dc/elements/1.1/");
		context.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
		context.declareNamespace("functx", "http://www.functx.com");
		return context;
	}
	
	public static ArrayList<String> extractPageBreaks(String doc) {
		XQDataSource xqds = new SaxonXQDataSource();
	    XQConnection conn;
	    try {
	      conn = xqds.getConnection();
	      XQStaticContext context = conn.getStaticContext();
	      conn.setStaticContext(nameSpacees12(context));
	      XQExpression expr = conn.createExpression();
	      String query = "let $pbs := doc('" + doc + "')//tei:pb \n";
	      query += "for $pb in distinct-values($pbs/@xml:id)\n";
	      query += "where count($pbs[@xml:id eq $pb]) gt 1\n";
	      query += "return $pb";
	     // query += "return concat(data($div/toc:ptr[@LOCTYPE='URL']/@xlink:title), \"###\", $div/@ID, \"###\", $div/@ORDER, \"###\", $div/@ORDERLABEL)";
	      Properties props = new Properties();
	      props.setProperty("method", "xml");
	      props.setProperty("encoding", "utf-8");
	      props.setProperty("indent", "yes");

	      XQResultSequence result = expr.executeQuery(query);
	      ArrayList<String> pagebreaks = new ArrayList<String>();

	      while (result.next()) {
	        // System.out.println(result.getItemType());

	        // result.writeItem(System.out, props);
	        if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          pagebreaks.add( result.getItemAsString(props));
	        } else {
	         pagebreaks.add( result.getAtomicValue());
	        }
	      }
	      return pagebreaks;
	    } catch (XQException e) {
	      e.printStackTrace();
	    }
	    return null;
		
	}
	
	public static ArrayList<String> extractVirrStructElems(String doc) {
		XQDataSource xqds = new SaxonXQDataSource();
	    XQConnection conn;
	    try {
	      conn = xqds.getConnection();
	      XQStaticContext context = conn.getStaticContext();
	      conn.setStaticContext(nameSpacees12(context));
	      XQExpression expr = conn.createExpression();
	      String query = "let $genres := doc('" + doc + "')//mods:mods \n";
	      //query += "for $genre in distinct-values($genres/descendant-or-self::*/local-name(.))\n";
	      query += "for $part in $genres/mods:titleInfo/mods:subTitle\n";
	      query += "return data($part)";
	     // query += "return concat(data($div/toc:ptr[@LOCTYPE='URL']/@xlink:title), \"###\", $div/@ID, \"###\", $div/@ORDER, \"###\", $div/@ORDERLABEL)";
	      Properties props = new Properties();
	      props.setProperty("method", "xml");
	      props.setProperty("encoding", "utf-8");
	      props.setProperty("indent", "yes");

	      XQResultSequence result = expr.executeQuery(query);
	      ArrayList<String> pagebreaks = new ArrayList<String>();

	      while (result.next()) {
	        // System.out.println(result.getItemType());

	        // result.writeItem(System.out, props);
	        if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          pagebreaks.add( result.getItemAsString(props));
	        } else {
	         pagebreaks.add( result.getAtomicValue());
	        }
	      }
	      return pagebreaks;
	    } catch (XQException e) {
	      e.printStackTrace();
	    }
	    return null;
		
	}
	
	
	public static ArrayList<String> extractMetsStructure(String toc_link) {
		XQDataSource xqds = new SaxonXQDataSource();
	    XQConnection conn;
	    try {
	      conn = xqds.getConnection();
	      XQStaticContext context = conn.getStaticContext();
	      conn.setStaticContext(nameSpacees12(context));
	      XQExpression expr = conn.createExpression();
	      String query = "let $physical := doc('" + toc_link + "/content')//toc:div[@ID='physical_0']\n";
	      query += "for $div in $physical/toc:div\n";
	      //query += "return data($physical/@TYPE)";
	     query += "return concat(data($div/toc:ptr[@LOCTYPE='URL']/@xlink:title), \"###\", $div/@ID, \"###\", $div/@ORDER, \"###\", $div/@ORDERLABEL)";
	      Properties props = new Properties();
	      props.setProperty("method", "xml");
	      props.setProperty("encoding", "utf-8");
	      props.setProperty("indent", "yes");

	      XQResultSequence result = expr.executeQuery(query);
	      ArrayList<String> image_urls = new ArrayList<String>();

	      while (result.next()) {
	        // System.out.println(result.getItemType());

	        // result.writeItem(System.out, props);
	        if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          image_urls.add(result.getItemAsString(props));
	        } else {
	          image_urls.add(result.getAtomicValue());
	        }
	      }
	      return image_urls;
	    } catch (XQException e) {
	      e.printStackTrace();
	    }
	    return null;
		
	}
	
	public static String extractXLink(String doc_string, String elements) {
		XQDataSource xqds = new SaxonXQDataSource();
	    XQConnection conn;
	    try {
	      conn = xqds.getConnection();
	      XQStaticContext context = conn.getStaticContext();
	      conn.setStaticContext(nameSpacees12(context));
	      XQExpression expr = conn.createExpression();
	      String query = "let $elems := doc('" + doc_string + "')//" + elements +"\n";
	      query += "return data($elems/@xlink:href)";
	      //query += "return count($elems)";
	     // query += "return concat(data($div/toc:ptr[@LOCTYPE='URL']/@xlink:title), \"###\", $div/@ID, \"###\", $div/@ORDER, \"###\", $div/@ORDERLABEL)";
	      Properties props = new Properties();
	      props.setProperty("method", "xml");
	      props.setProperty("encoding", "utf-8");
	      props.setProperty("indent", "yes");

	      XQResultSequence result = expr.executeQuery(query);
	      ArrayList<String> image_urls = new ArrayList<String>();

	      while (result.next()) {
	        // System.out.println(result.getItemType());

	        // result.writeItem(System.out, props);
	        if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          return result.getItemAsString(props);
	        } else {
	         return result.getAtomicValue();
	        }
	      }
	    } catch (XQException e) {
	      e.printStackTrace();
	    }
	    return null;
		
	}
	
	public static ArrayList<String> extractImageUrls(String mab_id) {
		
		String mab_id_in_search = null;
		if (mab_id.length() == 6) {
			mab_id_in_search = mab_id;
		} else if (mab_id.length() == 5) {		
			mab_id_in_search = "0" + mab_id;
		} else if (mab_id.length() == 4) {		
			mab_id_in_search = "00" + mab_id;
		}
		
		XQDataSource xqds = new SaxonXQDataSource();
	    XQConnection conn;
	    Properties props = new Properties();
	      props.setProperty("method", "xml");
	      props.setProperty("encoding", "utf-8");
	      props.setProperty("indent", "yes");
	      String search1 = "/srw/search/escidoc_all?query=escidoc.virrelement.mods.relatedItem.identifier=" + mab_id_in_search + "&amp;sortKeys=sort.escidoc.virrelement.mods.part.detail.number&amp;maximumRecords=1000";
	      String search2 = "/srw/search/escidoc_all?query=escidoc.virrelement.mods.relatedItem.identifier=" + mab_id_in_search + "&amp;sortKeys=sort.escidoc.virrelement.mods.part.detail.number&amp;maximumRecords=1000&amp;startRecord=1001";
	      ArrayList<String> image_urls = new ArrayList<String>();

	    try {
	      conn = xqds.getConnection();
	      XQStaticContext context = conn.getStaticContext();
	      conn.setStaticContext(nameSpacees12(context));
	      XQExpression expr = conn.createExpression();

	      String query = "for $item in doc('" + search1 + "')//item:item\n";
	      query += "for $comp in $item/comp:components/comp:component\n";
	      query += "return concat($comp/comp:properties/prop:file-name, \"###\", data($comp/comp:content/@xlink:href))";
	      
	      XQResultSequence result = expr.executeQuery(query);

	      while (result.next()) {
	        if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          image_urls.add(result.getItemAsString(props));
	        } else {
	          image_urls.add(result.getAtomicValue());
	        }
	      }
	      
	      String query2 = "for $item in doc('" + search2 + "')//item:item\n";
	      query2 += "for $comp in $item/comp:components/comp:component\n";
	      query2 += "return concat($comp/comp:properties/prop:file-name, \"###\", data($comp/comp:content/@xlink:href))";
	      
	      XQResultSequence result2 = expr.executeQuery(query2);

	      while (result2.next()) {
	        if (result2.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result2.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          image_urls.add(result2.getItemAsString(props));
	        } else {
	          image_urls.add(result2.getAtomicValue());
	        }
	      }
	      
	      
	      return image_urls;
	    } catch (XQException e) {
	      e.printStackTrace();
	    }
	    return null;
		
	}

	public static ArrayList<String> extractIds() {
	    XQDataSource xqds = new SaxonXQDataSource();
	    XQConnection conn;
	    try {
	      conn = xqds.getConnection();
	      XQStaticContext context = conn.getStaticContext();
	      conn.setStaticContext(nameSpacees12(context));
	      XQExpression expr = conn.createExpression();
	      String search = "/ir/containers?query=%22/properties/context/id%22=escidoc:20006";

	      String query = "for $container in doc('" + search + "')//container:container\n";
	      query += "let $id := $container/@xlink:href\n";
	      query += "let $mab:=$container/md:md-records/md:md-record/virr:virrelement/mods:mods/mods:recordInfo/mods:recordIdentifier[@source='mab001']\n";
	      query += "return concat(data($id), \"###\", $mab)";
	      Properties props = new Properties();
	      props.setProperty("method", "xml");
	      props.setProperty("encoding", "utf-8");
	      props.setProperty("indent", "yes");

	      XQResultSequence result = expr.executeQuery(query);
	      ArrayList<String> ids = new ArrayList<String>();

	      while (result.next()) {
	        // System.out.println(result.getItemType());

	        // result.writeItem(System.out, props);
	        if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          ids.add(result.getItemAsString(props));
	        } else {
	          ids.add(result.getAtomicValue());
	        }
	      }
	      return ids;
	    } catch (XQException e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
	
	public static ArrayList<String> extractTocURLs() {
	    XQDataSource xqds = new SaxonXQDataSource();
	    XQConnection conn;
	    try {
	      conn = xqds.getConnection();
	      XQStaticContext context = conn.getStaticContext();
	      conn.setStaticContext(nameSpacees12(context));
	      XQExpression expr = conn.createExpression();
	      String search = "/srw/search/escidoc_all?query=escidoc.property.content-model.objid=escidoc:toc&amp;maximumRecords=100";

	      String query = "for $item in doc('" + search + "')//item:item\n";
	      query += "let $url := $item/comp:components/comp:component/comp:content/@xlink:href\n";
	      query += "let $container_id := $item/md:md-records/md:md-record/virr:virrelement/mods:mods/mods:relatedItem/mods:identifier\n";
	      query += "return concat(data($url), \"###\", $container_id)";
	      Properties props = new Properties();
	      props.setProperty("method", "xml");
	      props.setProperty("encoding", "utf-8");
	      props.setProperty("indent", "yes");

	      XQResultSequence result = expr.executeQuery(query);
	      ArrayList<String> urls = new ArrayList<String>();

	      while (result.next()) {
	        // System.out.println(result.getItemType());

	        // result.writeItem(System.out, props);
	        if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE
	            || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
	          urls.add(result.getItemAsString(props));
	        } else {
	          urls.add(result.getAtomicValue());
	        }
	      }
	      return urls;
	    } catch (XQException e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
	
	public static XMLStreamReader extractMods(String container_id) {
		XQDataSource xqds = new SaxonXQDataSource();
		XQConnection conn;
		XMLStreamReader stream = null;
		try {
			conn = xqds.getConnection();
			XQStaticContext context = conn.getStaticContext();
			conn.setStaticContext(nameSpacees12(context));
			XQExpression expr = conn.createExpression();
			String search = "/ir/container/escidoc:" + container_id + "/md-records/md-record/escidoc";
			String query = "for $mods in doc('" + search + "')//virr:virrelement/mods:mods\n";
			query += "return $mods";
			Properties props = new Properties();
		      props.setProperty("method", "xml");
		      props.setProperty("encoding", "utf-8");
		      props.setProperty("indent", "yes");
			XQResultSequence result = expr.executeQuery(query);
			
			while (result.next()) {
				if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
					stream = result.getItemAsStream();
				} else {
					// authors.add(result.getAtomicValue());
				}
			}
			return stream;
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void getCompleteInfo() {
		
		XQDataSource xqds = new SaxonXQDataSource();
		XQConnection conn;
		XMLStreamReader stream = null;
		try {
			conn = xqds.getConnection();
			XQStaticContext context = conn.getStaticContext();
			conn.setStaticContext(nameSpacees12(context));
			XQExpression expr = conn.createExpression();
			String search = "/ir/containers?query=%22/properties/context/id%22=escidoc:20006";
			String query = "for $container in doc('" + search + "')//container:container\n";
			query += "let $date := $container/container:properties/prop:creation-date\n";
			query += "let $name := $container/container:properties/prop:name\n";
			query += "let $mab001 := $container/md:md-records/md:md-record/virr:virrelement/mods:mods/mods:recordInfo/mods:recordIdentifier[@source='mab001']\n";
			query += "let $mab720 := $container/md:md-records/md:md-record/virr:virrelement/mods:mods/mods:recordInfo/mods:recordIdentifier[@source='mab720']\n";
			query += "let $title := $container/md:md-records/md:md-record/virr:virrelement/mods:mods/mods:titleInfo[@ID='title331']/mods:title\n";
			query += "let $status := $container/container:properties/prop:public-status\n";
			query += "let $genre := $container/container:properties/srel:content-model/@xlink:title\n";
			query += "let $scans := $container/struct-map:struct-map/srel:item\n";

			query += "return concat($date, \"---\", $name, \"---\", $status, \"---\", $genre, \"---\", count($scans), \"---\", $mab001, \"---\", $mab720, \"---\", $title)";
			Properties props = new Properties();
		      props.setProperty("method", "xml");
		      props.setProperty("encoding", "utf-8");
		      props.setProperty("indent", "yes");
			XQResultSequence result = expr.executeQuery(query);
			
			while (result.next()) {
				if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
					stream = result.getItemAsStream();
				} else {
					System.out.println(result.getAtomicValue());
				}
			}
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
