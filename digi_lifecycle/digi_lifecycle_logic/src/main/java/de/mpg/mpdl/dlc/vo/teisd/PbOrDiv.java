package de.mpg.mpdl.dlc.vo.teisd;

import javax.xml.bind.annotation.XmlAttribute;

public class PbOrDiv {
	
	

	@XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
	private String id;
	
	@XmlAttribute(name = "n")
	private String numeration;
	
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

}
