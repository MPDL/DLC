package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsURLSEC {
	@XmlAttribute(name = "usage")
	private String usage;
	
	@XmlAttribute(name = "displayLabel")
	private String displayLabel;

	@XmlPath("text()")
	private String value;

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
