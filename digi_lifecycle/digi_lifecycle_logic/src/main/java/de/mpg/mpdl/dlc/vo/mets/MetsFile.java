package de.mpg.mpdl.dlc.vo.mets;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class MetsFile {
	
	@XmlAttribute(name = "ID")
	@XmlID
	private String id;
	
	@XmlAttribute(name = "MIMETYPE")
	private String mimeType;
	
	@XmlPath("mets:FLocat/@LOCTYPE")
	private String locatorType;
	
	@XmlPath("mets:FLocat/@xlink:href")
	private String href;



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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
