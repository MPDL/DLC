package de.mpg.mpdl.dlc.vo.teisd;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class PbOrDiv {
	
	

	@XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
	private String id;
	
	@XmlAttribute(name = "n")
	private String numeration;
	
	@XmlTransient
	protected String type;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumeration() {
		return numeration;
	}

	public void setNumeration(String numeration) {
		this.numeration = numeration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
