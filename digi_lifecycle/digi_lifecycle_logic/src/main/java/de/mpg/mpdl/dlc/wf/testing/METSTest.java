package de.mpg.mpdl.dlc.wf.testing;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import gov.loc.mets.MetsDocument;
import gov.loc.mets.MetsDocument.Mets;
import gov.loc.mets.MetsType.FileSec;

public class METSTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			MetsDocument md = MetsDocument.Factory.parse(new File("/home/frank/data/digitization_lifecycle/mets_samples/MPIER_Journals_112"));
			Mets mets = md.getMets();
			FileSec files = mets.getFileSec();
			System.out.println(files.getFileGrpArray(0).getFileArray(0).getFLocatArray(0).getHref());
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
