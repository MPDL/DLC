package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.net.URL;

import com.sleepycat.db.Transaction;
import com.sleepycat.db.TransactionConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlInputStream;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlTransaction;
import com.sleepycat.dbxml.XmlUpdateContext;

public class DocumentStorage {
	
	private XmlManager manager = null;
	private XmlContainer container = null;
	private XmlTransaction transaction = null;
	private File file = null;
	private URL url = null;
	private String name = null;
	
	public DocumentStorage(XmlManager mgr, XmlContainer ctn, File f, String n)
	{
		this.manager = mgr;
		this.container = ctn;
		this.file = f;
		if (n != null)
		{
			this.name = n;
		}
	}
	
	public DocumentStorage(XmlManager mgr, XmlContainer ctn, URL u, String n)
	{
		this.manager = mgr;
		this.container = ctn;
		this.url = u;
		if (n != null)
		{
			this.name = n;
		}
	}
	
	public void store()
	{
		TransactionConfig tc = new TransactionConfig();
		tc.setReadCommitted(true);

		try {
			XmlUpdateContext ctx = manager.createUpdateContext();
			transaction = manager.createTransaction(null, tc);
			XmlInputStream stream = manager.createLocalFileInputStream(file.getAbsolutePath());
			if (name == null)
			{
				container.putDocument(transaction, file.getName(), stream,
					ctx);
			}
			else
			{
				container.putDocument(transaction, name, stream, ctx);
			}
			transaction.commit();
			transaction = null;
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				try {
					transaction.abort();
				} catch (Exception e) {
					System.err.println("Error aborting transaction: "
							+ e.toString());
					e.printStackTrace();
				}
			}
		}
	}

}
