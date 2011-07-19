package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsTitle {
	
	@XmlAttribute(name = "ID")
	private String mabId;
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlPath("title/text()")
	private String title;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
