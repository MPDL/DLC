package de.mpg.mpdl.dlc.mods;

import gov.loc.mods.v3.ModsDefinition;
import gov.loc.mods.v3.RecordIdentifierDefinition;
import gov.loc.mods.v3.RecordInfoDefinition;

public class RecordIdentifier {
	
	private static RecordInfoDefinition recordInfo = null;

	public static ModsDefinition add(ModsDefinition mods, String data, String source)
	{
		if (mods.sizeOfRecordInfoArray() == 0)
        {
            recordInfo = RecordInfoDefinition.Factory.newInstance();
            RecordIdentifierDefinition recordId = recordInfo.addNewRecordIdentifier();
            recordId.setSource(source);
            recordId.setStringValue(data);
            mods.insertNewRecordInfo(mods.sizeOfRecordInfoArray());
            mods.setRecordInfoArray(mods.sizeOfRecordInfoArray() - 1, recordInfo);
        }
        else
        {
            recordInfo = mods.getRecordInfoArray(0);
            RecordIdentifierDefinition recordId = recordInfo.insertNewRecordIdentifier(recordInfo.sizeOfRecordIdentifierArray());
            recordId.setSource(source);
            recordId.setStringValue(data);
        }
		return mods;
	}
}
