package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class ModsNote {
	
	@XmlAttribute(name="ID")
	private String mabId;
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlValue
	private String note;

	public String getMabId() {
		return mabId;
	}

	public void setMabId(String mabId) {
		this.mabId = mabId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	

}
