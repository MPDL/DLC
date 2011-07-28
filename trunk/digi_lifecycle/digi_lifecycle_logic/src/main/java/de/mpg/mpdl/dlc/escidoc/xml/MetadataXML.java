package de.mpg.mpdl.dlc.escidoc.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import gov.loc.mods.v3.ModsDefinition;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsVersionAttributeDefinition;
import gov.loc.mods.v3.TitleInfoDefinition;
import gov.loc.mods.v3.TitleInfoTypeAttributeDefinition;
import org.apache.axis.encoding.Base64;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;
import org.purl.escidoc.metadata.profiles.x01.file.FileDocument;
import org.purl.escidoc.metadata.profiles.x01.file.FileType;

public class MetadataXML {
	
	private static SimpleLiteral sl = null;
	
	private MetadataXML() {}
	
	public static FileDocument component()
	{
		FileDocument fd = FileDocument.Factory.newInstance();
		FileType ft = fd.addNewFile();
		sl = ft.addNewTitle();
		sl.set(setValue("Title of the file"));
		sl.setLang("en");
		sl = ft.addNewDescription();
		sl.set(setValue("Description of the file"));
		sl.setLang("en");
		sl = ft.addNewIdentifier();
		sl.set(setValue("Identifier of the file"));
		sl = ft.addNewContentCategory();
		sl.set(setValue("Category of the file"));
		sl = ft.addNewFormat();
		sl.set(setValue("Format of the file"));
		sl = ft.addNewAvailable();
		sl.set(setValue("Availability of the file"));
		sl = ft.addNewDateCopyrighted();
		sl.set(setValue("Copywrite date of the file"));
		sl = ft.addNewRights();
		sl.set(setValue("Rights statement of the file"));
		return fd;
	}
	
	private static XmlString setValue(String value)
	{
		XmlString stringValue = XmlString.Factory.newInstance();
		stringValue.setStringValue(value);
		return stringValue;
	}
	
	public static ModsDocument mods()
	{
		ModsDocument modsDoc = ModsDocument.Factory.newInstance();
		ModsDefinition mods = modsDoc.addNewMods();
		mods.setVersion(ModsVersionAttributeDefinition.X_3_4);
		TitleInfoDefinition tid = mods.addNewTitleInfo();
		tid.setID("mab_331");
		tid.setDisplayLabel("title derived from MAB 331");
		tid.setType2(TitleInfoTypeAttributeDefinition.UNIFORM);
		tid.addNewTitle().setStringValue("Title of the book");
		return modsDoc;
	}
	
	public static XmlString encodedMAB(String mabId, File mabFile)
	{
		XmlString encodedMABRecord = null;
		String mabString = null;
		 BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(mabFile));
	        String line;
	        String idPrefix = "001 ";
	        while ((line = br.readLine()) != null)
	        {
	            if (line.contains(idPrefix + mabId))
	            {
	                mabString = line;
	            }
	        }
	        String encoded = Base64.encode(mabString.getBytes());
	        encodedMABRecord = setValue(encoded);
	        return encodedMABRecord;
	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
