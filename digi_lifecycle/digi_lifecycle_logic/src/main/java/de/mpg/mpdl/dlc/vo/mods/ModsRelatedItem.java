package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsRelatedItem {
	
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name = "name")
	private String name;
	
	@XmlValue
	private String value;

	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}
