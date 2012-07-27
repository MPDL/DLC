package de.mpg.mpdl.dlc.vo.mods;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsPublisher {


	@XmlPath("mods:place/mods:placeTerm[@type='text']/text()")
	private String place;
	
	@XmlPath("mods:publisher/text()")
	private String publisher;
	
	@XmlElement(name = "edition", namespace="http://www.loc.gov/mods/v3")
	private String edition;
	
	@XmlPath("mods:dateIssued")
	private ModsDate dateIssued_425;
	
	@XmlPath("mods:dateCaptured[@encoding='w3cdtf']/text()")
	private String dateCaptured;
	
	@XmlAttribute(name="displayLabel")
	private String displayLabel;
	
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

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEdition() {
		return edition;
	}

	public ModsDate getDateIssued_425() {
		return dateIssued_425;
	}

	public void setDateIssued_425(ModsDate dateIssued_425) {
		this.dateIssued_425 = dateIssued_425;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getDateCaptured() {
		return dateCaptured;
	}

	public void setDateCaptured(String dateCaptured) {
		this.dateCaptured = dateCaptured;
	}
	

}
