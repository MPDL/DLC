package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsTitle {
	
	@XmlAttribute(name = "ID")
	private String mabId;
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name="displayLabel")
	private String displayLabel;
	
	@XmlPath("mods:title/text()")
	private String title;
	
	@XmlPath("mods:subTitle/text()")
	private String subTitle;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMabId() {
		return mabId;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public void setMabId(String mabId) {
		this.mabId = mabId;
	}
	
	

}
