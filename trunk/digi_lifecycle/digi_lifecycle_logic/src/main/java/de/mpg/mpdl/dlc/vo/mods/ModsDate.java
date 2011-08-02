package de.mpg.mpdl.dlc.vo.mods;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsDate {
	
	@XmlValue
	@XmlSchemaType(name = "dateTime")
	private Date date;
	
	@XmlAttribute(name = "encoding")
	private String dateEncoding = "w3cdtf";
	
	@XmlAttribute(name = "keyDate")
	private String dateKeyDate = "yes";

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
