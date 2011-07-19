package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsPublisher {

	@XmlAttribute(name="displayLabel")
	private String mabId;

	@XmlPath("place/placeTerm[@type='text']/text()")
	private String place;
	
	@XmlPath("publisher/text()")
	private String publisher;
	
	
	public String getMabId() {
		return mabId;
	}

	public void setMabId(String mabId) {
		this.mabId = mabId;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
}
