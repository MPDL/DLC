package de.mpg.mpdl.dlc.vo.teisd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import de.mpg.mpdl.dlc.vo.MetsDiv;

public class PbOrDiv {

	@XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
	private String id;
	
	@XmlAttribute(name = "n")
	private String numeration;
	
	@XmlAttribute(name="type")
	private String type;
	
	@XmlElements
	(value = {
			@XmlElement(name="div", namespace="http://www.tei-c.org/ns/1.0", type=de.mpg.mpdl.dlc.vo.teisd.Div.class),
			@XmlElement(name="pb", namespace="http://www.tei-c.org/ns/1.0", type=de.mpg.mpdl.dlc.vo.teisd.Pagebreak.class),
			@XmlElement(name="titlePage", namespace="http://www.tei-c.org/ns/1.0", type=de.mpg.mpdl.dlc.vo.teisd.TitlePage.class)
	})
	private List<PbOrDiv> pbOrDiv = new ArrayList<PbOrDiv>();
	
	@XmlTransient
	private PbOrDiv parent;
	
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
	
	
	public List<PbOrDiv> getPbOrDiv() {
		return pbOrDiv;
	}

	public void setPbOrDiv(List<PbOrDiv> pbOrDiv) {
		this.pbOrDiv = pbOrDiv;
	} 

	
	//Sets the parent div after unmarshalling
	public void afterUnmarshal(Unmarshaller u, Object parent) {
	    this.setParent((PbOrDiv)parent);
	  }

	public PbOrDiv getParent() {
		return parent;
	}

	public void setParent(PbOrDiv parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

}
