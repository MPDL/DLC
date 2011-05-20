package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.util.ArrayList;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;

public class XmlDBLoadTest {

	private static String envPath = "/home/frank/data/dlc/dbxml";
	private static String metsContainerName = "DLC_METS";
	private static String teiContainerName = "DLC_TEI";

	private static Environment environment = null;
	private static XmlManager manager = null;
	private static XmlContainer metsContainer = null;
	private static XmlContainer teiContainer = null;

	private static File metsFile = null;
	private static File teiFile = null;

	public static void main(String[] args) {

		load();
	}

	public static void load() {
		metsFile = new File(
				"/home/frank/data/digitization_lifecycle/mets_samples/MPIER_Journals_160");
		teiFile = new File(
				"/home/frank/data/digitization_lifecycle/tei_samples/TestOxygen.xml");

		try {
			environment = DBXMLDAO.getEnvironment(envPath);
			manager = DBXMLDAO.getManager(environment);
			// manager.removeContainer(metsContainerName);
			// manager.removeContainer(teiContainerName);
			metsContainer = DBXMLDAO.getContainer(manager, metsContainerName);
			teiContainer = DBXMLDAO.getContainer(manager, teiContainerName);
			FileStorage[] metsThreads;
			FileStorage[] teiThreads;
			metsThreads = new FileStorage[10];
			teiThreads = new FileStorage[10];
			for (int n = 0; n < 10; n++) {
				for (int i = 0; i < metsThreads.length; i++) {
					metsThreads[i] = new FileStorage(manager, metsContainer,
							metsFile, "dlc:32" + n+i);
					metsThreads[i].start();
					teiThreads[i] = new FileStorage(manager, teiContainer,
							teiFile, "dlc:32" + n+i);
					teiThreads[i].start();
				}
				for (int i = 0; i < metsThreads.length; i++) {
					metsThreads[i].join();
					teiThreads[i].join();
				}
			}

		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (metsContainer != null) {
				try {
					metsContainer.close();
				} catch (XmlException xe) {
					System.err.println("closing Environment: container: "
							+ xe.toString());
					xe.printStackTrace();
				}
			}
			if (teiContainer != null) {
				try {
					teiContainer.close();
				} catch (XmlException xe) {
					System.err.println("closing Environment: container: "
							+ xe.toString());
					xe.printStackTrace();
				}
			}
			if (manager != null) {
				try {
					manager.close();
				} catch (XmlException xe) {
					System.err.println("closing Environment: manager: "
							+ xe.toString());
					xe.printStackTrace();
				}
			}

		}

	}
}
