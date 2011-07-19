package de.mpg.mpdl.dlc.vo.mods;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsPublisher {

	@XmlAttribute(name="displayLabel")
	private String mabId;

	@XmlPath("place/placeTerm[@type='text']/text()")
	private String place;
	
	@XmlPath("publisher/text()")
	private String publisher;
	
	@XmlElement(name = "edition")
	private String editionStatement;
	
	@XmlElement(name = "dateIssued")
	private Date dateIssued_425;
	
	@XmlPath("dateIssued/@encoding")
	private String dateEncoding = "w3cdtf";
	
	@XmlPath("dateIssued/@keyDate")
	private String dateKeyDate = "yes";
	
	
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

	public void setEditionStatement(String editionStatement) {
		this.editionStatement = editionStatement;
	}

	public String getEditionStatement() {
		return editionStatement;
	}

	public Date getDateIssued_425() {
		return dateIssued_425;
	}

	public void setDateIssued_425(Date dateIssued_425) {
		this.dateIssued_425 = dateIssued_425;
	}

	public String getDateEncoding() {
		return dateEncoding;
	}

	public void setDateEncoding(String dateEncoding) {
		this.dateEncoding = dateEncoding;
	}

	public String getDateKeyDate() {
		return dateKeyDate;
	}

	public void setDateKeyDate(String dateKeyDate) {
		this.dateKeyDate = dateKeyDate;
	}
}
