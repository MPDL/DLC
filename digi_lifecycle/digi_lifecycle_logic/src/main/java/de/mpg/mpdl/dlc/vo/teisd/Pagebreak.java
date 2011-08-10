package de.mpg.mpdl.dlc.vo.teisd;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "pb", namespace = "http://www.tei-c.org/ns/1.0")
public class Pagebreak extends PbOrDiv{
	
	@XmlTransient
	protected String type = "pb";
	
	@XmlAttribute(name="facs")
	private String facs;

	public String getFacs() {
		return facs;
	}

	public void setFacs(String facs) {
		this.facs = facs;
	}
	
	

}
