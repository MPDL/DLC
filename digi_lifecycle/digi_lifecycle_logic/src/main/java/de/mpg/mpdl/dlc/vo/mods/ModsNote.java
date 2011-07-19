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

}
