package de.mpg.mpdl.dlc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQStaticContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.params.ConnConnectionParamBean;

import net.sf.saxon.xqj.SaxonXQDataSource;

public class HandleUpdate {

	public static void main(String[] args) {
		String testHandle = "11858/00-001M-0000-0023-9F70-3";
		String testUrl = "http://dlc.mpdl.mpg.de/dlc/viewMulti/escidoc:37079";
		xquery();
		// setNewUrl4Handle(testHandle, testUrl);

	}
	
	public static void xquery() {
		XQDataSource xqds = new SaxonXQDataSource();
		XQConnection conn;
		try {
			conn = xqds.getConnection();
			XQStaticContext context = conn.getStaticContext();
			context.setBaseURI("http://dlc.mpdl.mpg.de");
			context.declareNamespace("mets", "http://www.loc.gov/METS/");
			context.declareNamespace("mods", "http://www.loc.gov/mods/v3");
			context.declareNamespace("item", "http://www.escidoc.de/schemas/item/0.10");
			context.declareNamespace("md", "http://www.escidoc.de/schemas/metadatarecords/0.5");
			context.declareNamespace("comp", "http://www.escidoc.de/schemas/components/0.9");
			context.declareNamespace("prop", "http://escidoc.de/core/01/properties/");
			context.declareNamespace("srel", "http://escidoc.de/core/01/structural-relations/");
			context.declareNamespace("xlink", "http://www.w3.org/1999/xlink");
			context.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
			context.declareNamespace("ntei", "http://www.tei-c.org/ns/notTEI");
			conn.setStaticContext(context);
			XQExpression expr = conn.createExpression();
			String search = "/srw/search/dlc_index?query=escidoc.objecttype=item%20and%20escidoc.content-model.objid=escidoc:2&amp;maximumRecords=500";
			URI uri = new URI(search);
			String query = "for $item in doc('" + search + "')//item:item\n";
			/* query += "for $comp in $item//comp:component\n";
			query += "let $href := $comp/comp:content/@xlink:href\n";
			query += "where $comp/comp:properties/prop:content-category = 'tei'\n";
			query += "return ";
			query += "for $title in doc($href)//tei:titlePage/tei:docTitle/tei:titlePart\n";
			*/
			query += "return concat($item/@xlink:href, \" \", $item/item:properties/prop:pid)";
			
			Properties props = new Properties();
            props.setProperty("method", "xml");
            props.setProperty("encoding", "utf-8");
            props.setProperty("indent", "yes");
            
			XQResultSequence result = expr.executeQuery(query);
			String objectPid = null;
			String oldUrl = null;
			String rawValues = null;
			
			while (result.next()) {
				// System.out.println(result.getItemType());

				// result.writeItem(System.out, props);
				if (result.getItemType().getItemKind() == XQItemType.XQITEMKIND_NODE || result.getItemType().getItemKind() == XQItemType.XQITEMKIND_ELEMENT) {
					objectPid = result.getItem().getNode().getTextContent();
					System.out.println(objectPid);
					//oldUrl = getCurrentUrl4Handle(objectPid.substring(4));
					//System.out.println(oldUrl);
					//setNewUrl(oldUrl);
					//System.out.println(result.getItem().toString());
				} else {
					rawValues = result.getAtomicValue();
					setNewUrl4Handle(extractValues(rawValues)[1], extractValues(rawValues)[0]);
				}
			}
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getCurrentUrl4Handle(String handle) {
		HttpURLConnection connection = null;
	    BufferedReader reader  = null;
	    String line = null;
	    URL pidcache = null;
	    
	    try {
	    	pidcache = new URL("http://dlc.mpdl.mpg.de/pidcache/handle/read/view?pid=" + handle);
	    	
	    	connection = (HttpURLConnection)pidcache.openConnection();
	    	connection.setRequestMethod("GET");
	    	byte[] encoded = Base64.encodeBase64("pid_manager:Ghau672au72F".getBytes());
	    	connection.setRequestProperty("Authorization", "Basic " + new String(encoded));	    	
	    	connection.setDoOutput(true);
	    	
	    	connection.connect();
	    	
	    	reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    	String currentUrl = null;
	    	
	    	while ((line = reader.readLine()) != null) {
	            if (line.contains("<url>")) {
	            	currentUrl = line.substring(line.indexOf(">") + 1, line.length() - 6);
	            }
	          }
	        
	        reader.close();
	        return currentUrl;

	    } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally {
	    	connection.disconnect();
	    	reader = null;
	    }
		return null;

	}
	
	public static String setNewUrl4Handle(String handle, String url) {
		HttpURLConnection connection = null;
	    OutputStreamWriter writer = null;
	    BufferedReader reader  = null;
	    StringBuilder builder = null;
	    String line = null;
	    URL pidcache = null;
	    
	    try {
	    	pidcache = new URL("http://dlc.mpdl.mpg.de/pidcache/handle/write/modify?pid=" + handle);
	    	
	    	String urlParam = "url=" + url;
	    	byte[] urlParamBytes = urlParam.getBytes("UTF-8");
	    	connection = (HttpURLConnection)pidcache.openConnection();
	    	connection.setRequestMethod("POST");
	    	byte[] encoded = Base64.encodeBase64("pid_manager:Ghau672au72F".getBytes());
	    	connection.setRequestProperty("Authorization", "Basic " + new String(encoded));
	    	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        connection.setRequestProperty("Content-Length", String.valueOf(urlParamBytes.length));
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	
	    	connection.getOutputStream().write(urlParamBytes);
	    	
	    	System.out.println(connection.getResponseCode() + "  " + connection.getResponseMessage());
	    	
	    	reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
	    	builder = new StringBuilder();
	    	while ((line = reader.readLine()) != null) {
	            builder.append(line);
	          }
	        
	        reader.close();
	        return builder.toString();

	    } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally {
	    	connection.disconnect();
	    	reader = null;
	    	builder = null;
	    	writer = null;
	    }
		return null;

	}
	
	public static String[] extractValues(String raw) {
		String[] idAndPid = raw.split(" ");
		String url = "http://dlc.mpdl.mpg.de/dlc/viewMulti/" + idAndPid[0].substring(9);
		String pid = idAndPid[1].substring(4);
		return new String[]{url, pid};
	}
}
