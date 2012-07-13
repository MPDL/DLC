package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsRelatedItem {
	
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "displayLabel")
	private String displayLabel;

	@XmlPath("text()")
	private String value;
	
	@XmlPath("mods:recordInfo/mods:recordIdentifier[@source='local']/text()")
	private String parentId_010;
	
	@XmlPath("mods:titleInfo/mods:title/text()")
	private String title;

	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getParentId_010() {
		return parentId_010;
	}


	public void setParentId_010(String parentId_010) {
		this.parentId_010 = parentId_010;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
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


	public String getDisplayLabel() {
		return displayLabel;
	}


	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

}
