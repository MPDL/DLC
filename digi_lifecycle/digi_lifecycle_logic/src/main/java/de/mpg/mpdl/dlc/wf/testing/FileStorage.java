package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import com.sleepycat.db.TransactionConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlInputStream;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlTransaction;
import com.sleepycat.dbxml.XmlUpdateContext;

public class FileStorage extends Thread {

	private int MAX_RETRY = 20;
	private XmlManager manager = null;
	private XmlContainer container = null;
	private File file = null;
	private URL url = null;
	private String name = null;

	FileStorage(XmlManager xmgr, XmlContainer xcon, File f, String name) {
		this.manager = xmgr;
		this.container = xcon;
		this.file = f;
		if (name != null)
		{
			this.name = name;
		}
	}

	FileStorage(XmlManager xmgr, XmlContainer xcon, URL u) {
		this.manager = xmgr;
		this.container = xcon;
		this.url = u;
	}

	public void run() {
		TransactionConfig tc = new TransactionConfig();
		tc.setReadCommitted(true);
		XmlTransaction transaction = null;
		boolean retry = true;
		int count = 0;
		int deadlocks = 0;
		while (retry) {
			try {
				XmlUpdateContext context = manager.createUpdateContext();
				transaction = manager.createTransaction(null, tc);
				XmlInputStream stream = manager.createLocalFileInputStream(file
						.getAbsolutePath());
				if (name == null)
				{
					container.putDocument(transaction, file.getName(), stream,
						context);
				}
				else
				{
					container.putDocument(transaction, name, stream, context);
					System.out.println(name + " was successfully stored in container " + container.getName());
				}
				transaction.commit();
				transaction = null;
				retry = false;
			} catch (XmlException xe) {
				retry = false;
				if (xe.getDatabaseException() instanceof com.sleepycat.db.DeadlockException) {
					System.out.println("got DeadLockException!");
					deadlocks++;
					if (count < MAX_RETRY) {
						System.err
								.println(getName() + " : Retrying operation.");
						retry = true;
						count++;

					} else {
						System.err.println(getName()
								+ " : out of retries. Giving up.");
					}

				} else {
					System.err.println("Error on transaction commit: "
							+ xe.toString());
					xe.printStackTrace();
				}
			} catch (Exception e) {
				System.err.println(getName() + " got exception : "
						+ e.toString());
				retry = false;
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
}
