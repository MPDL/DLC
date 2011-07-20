package de.mpg.mpdl.dlc.vo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class MetsFile {
	
	@XmlAttribute(name = "ID")
	@XmlID
	private String ID;
	
	@XmlAttribute(name = "MIMETYPE")
	private String mimeType;
	
	@XmlPath("FLocat/@LOCTYPE")
	private String locatorType;
	
	@XmlPath("FLocat/@xlink:href")
	private String href;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getLocatorType() {
		return locatorType;
	}

	public void setLocatorType(String locatorType) {
		this.locatorType = locatorType;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
