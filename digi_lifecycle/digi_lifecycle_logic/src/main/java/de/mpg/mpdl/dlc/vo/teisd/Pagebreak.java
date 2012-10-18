package de.mpg.mpdl.dlc.vo.teisd;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

@XmlRootElement(name = "pb", namespace = "http://www.tei-c.org/ns/1.0")
public class Pagebreak extends PbOrDiv{
	
	
	
	

	@XmlAttribute(name="facs")
	private String facs;
	
	@XmlAttribute(name="next")
	private String next;
	
	@XmlAttribute(name="prev")
	private String prev;
	
	@XmlAttribute(name="subtype")
	private String subtype;
	


	
	public Pagebreak() {
		super(ElementType.PB);
		// TODO Auto-generated constructor stub
	}
	
	public Pagebreak(Pagebreak original) {
		super(original);
		this.setElementType(ElementType.PB);
		this.setFacs(original.getFacs());
		this.setNext(original.getNext());
		this.setPrev(original.getPrev());
		this.setSubtype(original.getSubtype());
	}
	
	public String getFacs() {
		return facs;
	}

	public void setFacs(String facs) {
		this.facs = facs;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getPrev() {
		return prev;
	}

	public void setPrev(String prev) {
		this.prev = prev;
	}


	

}
