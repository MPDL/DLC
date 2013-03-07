/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.mods;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import de.ddb.professionell.mabxml.mabxml1.DateiDocument;
import de.ddb.professionell.mabxml.mabxml1.DatensatzType;
import de.ddb.professionell.mabxml.mabxml1.FeldType;


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
