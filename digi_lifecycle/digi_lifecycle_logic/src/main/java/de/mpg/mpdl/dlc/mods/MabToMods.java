package de.mpg.mpdl.dlc.mods;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import de.ddb.professionell.mabxml.mabxml1.DateiDocument;
import de.ddb.professionell.mabxml.mabxml1.DatensatzType;
import de.ddb.professionell.mabxml.mabxml1.FeldType;
import de.mpg.mpdl.dlc.escidoc.XBeanUtils;

import gov.loc.mods.v3.ModsDefinition;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsVersionAttributeDefinition;

public class MabToMods {
	
	private static ModsDocument modsDocument = null;
	private static ModsDefinition modsDefinition = null;
	private static DateiDocument mabDocument = null;
	private static DatensatzType[] mabRecords = null;
	
	public static ModsDocument fromMabXml(File mabXmlFile, String mab001Id)
	{
		modsDocument = ModsDocument.Factory.newInstance();
		modsDefinition = modsDocument.addNewMods();
		modsDefinition.setVersion(ModsVersionAttributeDefinition.X_3_4);
		try {
			mabDocument = DateiDocument.Factory.parse(mabXmlFile);
			mabRecords = mabDocument.getDatei().getDatensatzArray();
			for (DatensatzType mabRecord : mabRecords)
			{
				FeldType mab001Field = mabRecord.getFeldArray(0);
				if (mab001Field.xmlText().contains(mab001Id))
				{
					FeldType[] fields = mabRecord.getFeldArray();
					for (FeldType field : fields)
					{
						switch (field.getNr())
						{
						case 1:
							modsDefinition = RecordIdentifier.add(modsDefinition, getMABFieldValue(field), "mab001");
							break;
						}
					}
				}
			}
			return modsDocument;
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String getMABFieldValue(FeldType f)
    {
        int start = -1;
        int end = -1;
        String value = f.xmlText();
        if (f.getNsArray().length > 0)
        {
            String nonSort = f.getNsArray(0);
            start = value.indexOf(">") + 1;
            end = value.lastIndexOf("<mab:ns");
            value = value.substring(start, end);
            return value + nonSort;
        }
        else
        {
            start = value.indexOf(">") + 1;
            end = value.lastIndexOf("<");
            value = value.substring(start, end);
            return value;
        }
    }

}
