package de.mpg.mpdl.dlc.vo.teisd;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pb", namespace = "http://www.tei-c.org/ns/1.0")
public class Pagebreak extends PbOrDiv{
	
	@XmlAttribute(name="facs")
	private String facs;

	public String getFacs() {
		return facs;
	}

	public void setFacs(String facs) {
		this.facs = facs;
	}
	
	

}
