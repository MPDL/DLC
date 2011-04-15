package de.mpg.mpdl.dlc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.faces.el.ValueBinding;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlDocumentConfig;
import com.sleepycat.dbxml.XmlEventReader;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlIndexDeclaration;
import com.sleepycat.dbxml.XmlIndexSpecification;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlMetaData;
import com.sleepycat.dbxml.XmlMetaDataIterator;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlUpdateContext;
import com.sleepycat.dbxml.XmlValue;

public class TXNTest {

	private static String src_dir = "/home/frank/data/digitization_lifecycle/mets_samples";

	private static String envPath = "/home/frank/data/dlc/dbxml";
	private static String containerName = "DLC_All";
	private static Environment environment = null;
	private static XmlManager manager = null;
	private static XmlContainer container = null;
	private static ArrayList<File> files = null;

	public static void main(String[] args) {

		//prepare();
		//load();
		query();
		//addMD();
		//System.setProperty("sun.arch.data.model", "32");
		//System.out.println(System.getProperty("sun.arch.data.model"));
	}

	public static void load() {
		try {
			environment = DBXMLDAO.getEnvironment(envPath);
			manager = DBXMLDAO.getManager(environment);
			//manager.removeContainer(containerName);
			container = DBXMLDAO.getContainer(manager, containerName);
			File dir = new File(src_dir);
			FilenameFilter metsFiles = new FilenameFilter() {
				public boolean accept(File file, String name) {
					String regex = ".*xml";
					return name.matches(regex);
				}
			};
			File[] mets_files = dir.listFiles();
			List<File> list = Arrays.asList(mets_files);
			files = new ArrayList<File>();
			files.addAll(list);
			FileStorage[] threads;
			threads = new FileStorage[files.size()];
			for (int i = 0; i < files.size(); i++) {
				threads[i] = new FileStorage(manager, container, files.get(i));
				threads[i].start();
			}
			for (int i = 0; i < files.size(); i++) {
				threads[i].join();
			}

		} catch (XmlException xe) {
			xe.printStackTrace();
		} catch (DatabaseException de) {
			de.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBXMLDAO.close(container, manager);
		}
	}

	public static void query() {
		try {
			environment = DBXMLDAO.getEnvironment(envPath);
			manager = DBXMLDAO.getManager(environment);
			container = DBXMLDAO.getContainer(manager, "DLC_All");
			System.out.println(container.getName() + " contains "
					+ container.getNumDocuments() + " documents.");
			/*
			XmlResults docs = container
					.getAllDocuments(new XmlDocumentConfig());
			while (docs.hasNext()) {
				XmlDocument doc = docs.next().asDocument();
				System.out.println(doc.getName());
			}
			docs.delete();
			*/
			XmlQueryContext qctx = manager.createQueryContext();
			qctx.setNamespace("mets", "http://www.loc.gov/METS/");
			qctx.setNamespace("xlink", "http://www.w3.org/1999/xlink");
			qctx.setNamespace("", "http://www.tei-c.org/ns/1.0");
			qctx.setNamespace("srel", "http://escidoc.de/core/01/structural-relations/");
			// qctx.setDefaultCollection("TEI_samples");
			//String query = "for $num in (100001 to 100561) return\n";
			String query = "for $doc in collection('DLC_All')/mets:mets[dbxml:metadata('srel:context')='/ir/context/dlc:ctx_KHI']\n";
			query += "let $id := $doc/@ID\n";
			//query += "where contains($href, \"escidoc:110765\")\n";
			//query += "replace value of node $id with 'dlc: + string($num)'";
			//query += "return distinct-values($id)";
			query += "return $id";
			String tei_query = "for $title in collection('TEI_samples')/TEI/teiHeader/fileDesc/publicationStmt/availability\n";
			tei_query += "return distinct-values($title)";
			String q = getQueryFromFile(new File("src/main/resources/queries/defaultURLsFromMETS.qry"));
			q = q.replace("###collection###", "DLC_All");
			q = q.replace("###objid###", "escidoc:109310");
			String qwf = getQueryFromFile(new File("src/main/resources/queries/changeValue.qry"));
			String test = "for $n in (\"A\", \"B\", \"C\") for $m in (\"i\", \"ii\", \"iii\", \"iv\") return concat($n, $m)";
			XmlQueryExpression expr = manager.prepare(query, qctx);
			// System.out.println(expr.getQueryPlan());

			XmlResults results = expr.execute(qctx);
			System.out.println(results.size());
			while (results.hasNext()) {
				XmlValue value = results.next();
				//System.out.println(value.asDocument().getName() + "   "
				//		+ value.getNodeValue());
				//System.out.println(value.asDocument().getName());
				//System.out.println(value.asDocument().getName() + "   " + value.getNodeValue());
				System.out.println(value.asString());
				/*
				if (value.isType(XmlValue.TEXT_NODE)) {
					System.out.println("it's a TEXT_NODE");
				} else {
					System.out.println(value.getType() + " "
							+ value.getTypeName() + " " + value.getTypeURI());
				}
				*/
				/*
				XmlEventReader reader = value.asEventReader();
				while (reader.hasNext()) {
					int type = reader.next();
					if (type == XmlEventReader.StartElement) {
						System.out.print(reader.getLocalName());
					} else {
						if (type == XmlEventReader.EndElement) {
							System.out.println("/" + reader.getLocalName());
						} else {
							if (type == XmlEventReader.Characters) {
								System.out.print(reader.getValue().trim());
							} else {
								System.out.println(reader.getEventType());
							}
						}
					}
				}
				*/
			}
			results.delete();
			expr.delete();
			qctx.delete();
		} catch (XmlException xe) {
			xe.printStackTrace();
		} catch (DatabaseException de) {
			de.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBXMLDAO.close(container, manager);
		}
	}

	public static void addMD() {
		XmlResults docs = null;
		XmlUpdateContext xuctx = null;
		try {
			environment = DBXMLDAO.getEnvironment(envPath);
			manager = DBXMLDAO.getManager(environment);
			container = DBXMLDAO.getContainer(manager, containerName);
			xuctx = manager.createUpdateContext();
			docs = container.getAllDocuments(new XmlDocumentConfig());
			String message;
			System.out.println(container.getName() + ": ");
			while (docs.hasNext()) {
				XmlDocument doc = docs.next().asDocument();
				String docname = doc.getName();
				String ctxname = docname.substring(0, docname.indexOf("_"));
				byte[] name = doc.getMetaData(
						"http://www.sleepycat.com/2002/dbxml", "name");
				// doc.setMetaData("http://www.sleepycat.com/2002/dbxml",
				// "name", new XmlValue(new String("Oertel_Bd_" + number)));
				byte[] ctx = doc.getMetaData(
						"http://escidoc.de/core/01/structural-relations/",
						"context");
				// doc.removeMetaData("http://escidoc.de/core/01/properties/",
				// "context");
				doc.setMetaData("http://escidoc.de/core/01/structural-relations/",
				"context", new XmlValue(new
				String("/ir/context/dlc:ctx_" + ctxname)));
				doc.setMetaData("http://escidoc.de/core/01/structural-relations/",
				"content-model", new XmlValue(new
				String("/cmm/content-model/dlc:20007")));
				doc.setMetaData("http://escidoc.de/core/01/structural-relations/",
				"created-by", new XmlValue(new
				String("/aa/user-account/dlc:user_" + ctxname)));

				if (ctx != null) {
					System.out.println(new String(name) + " is in "
							+ new String(ctx));
				} else {
					System.out.println(new String(name) + " is in " + null);
				}
				XmlMetaDataIterator mit = doc.getMetaDataIterator();
				XmlMetaData md = mit.next();
				while (md != null) {
					message = "\tURI: " + md.get_uri();
					message += ", attribute name: " + md.get_name();
					message += ", value: " + md.get_value().asString() + "\n";
					System.out.println(message);

					md = mit.next();
				}
				container.updateDocument(doc, xuctx);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			docs.delete();
			DBXMLDAO.close(container, manager);
		}
	}

	public static void indexes() {
		try {
			environment = DBXMLDAO.getEnvironment(envPath);
			manager = DBXMLDAO.getManager(environment);
			container = DBXMLDAO.getContainer(manager, containerName);
			XmlIndexSpecification ispec = container.getIndexSpecification();
			int count = 0;
			XmlIndexDeclaration idxDecl = null;
			while ((idxDecl = (ispec.next())) != null) {
				System.out.println("For node '" + idxDecl.name + "', "
						+ idxDecl.uri + ": '" + idxDecl.index + "'.");
				count++;
			}
			System.out.println(count + " indices found.");
			ispec.delete();

		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			DBXMLDAO.close(container, manager);
		}
	}
	
	public static void prepare()
	{
		File dir = new File(src_dir);
		File[] mets = dir.listFiles();
		for (File src : mets)
		{
			File renamed = new File(src.getAbsolutePath().replace("VIRR", "Journals"));
			src.renameTo(renamed);
			/*
			for (int i = 0; i < 10; i++)
			{
				try {
					InputStream in = new FileInputStream(src);
					OutputStream out = new FileOutputStream(src_dir + "/" + src.getName() + i);
					byte[] buf = new byte[1024];
				      int len;
				      while ((len = in.read(buf)) > 0){
				        out.write(buf, 0, len);
				      }
				      in.close();
				      out.close();
				      System.out.println("File copied.");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
		}
	}
	
	public static String getQueryFromFile(File file)
	{
		 byte[] buffer = new byte[(int)file.length()];
		    BufferedInputStream f = null;
		    try {
		        f = new BufferedInputStream(new FileInputStream(file));
		        f.read(buffer);
		    } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
		        if (f != null) try { f.close(); } catch (IOException ignored) { }
		    }
		    return new String(buffer);

	}
}
