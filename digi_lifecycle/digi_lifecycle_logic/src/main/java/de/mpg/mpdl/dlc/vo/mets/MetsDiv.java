package de.mpg.mpdl.dlc.vo.mets;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class MetsDiv {
	

	@XmlID
	@XmlAttribute(name = "ID")
	private String id;
	
	@XmlAttribute(name = "TYPE")
	private String type;
	
	@XmlAttribute(name = "ORDER")
	private int order;
	
	@XmlAttribute(name = "ORDERLABEL")
	private String orderLabel;
	
	@XmlAttribute(name = "LABEL")
	private String label;
	
	@XmlAttribute(name = "CONTENTIDS")
	private String contentIds;
	
	@XmlElement(name="div", namespace="http://www.loc.gov/METS/")
	private List<MetsDiv> divs = new ArrayList<MetsDiv>();
	
	@XmlTransient
	private MetsDiv parentDiv;
	
	//Sets the parent div after unmarshalling
	public void afterUnmarshal(Unmarshaller u, Object parent) {
	    this.parentDiv = (MetsDiv)parent;
	  }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getOrderLabel() {
		return orderLabel;
	}

	public void setOrderLabel(String orderLabel) {
		this.orderLabel = orderLabel;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<MetsDiv> getDivs() {
		return divs;
	}

	public void setDivs(List<MetsDiv> divs) {
		this.divs = divs;
	}

	public MetsDiv getParentDiv() {
		return parentDiv;
	}

	public void setParentDiv(MetsDiv parentDiv) {
		this.parentDiv = parentDiv;
	}
	
	
	
	
	//private List<MetsSmLink> smLinks = new ArrayList<MetsSmLink>();
	

}
