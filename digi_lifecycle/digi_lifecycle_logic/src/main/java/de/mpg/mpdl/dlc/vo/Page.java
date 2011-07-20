package de.mpg.mpdl.dlc.vo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Page {
	
	@XmlAttribute(name = "ID")
	@XmlID
	private String ID;
	
	@XmlAttribute(name = "TYPE")
	private String type = "page";
	
	@XmlAttribute(name = "ORDER")
	private int order;
	
	@XmlAttribute(name = "ORDERLABEL")
	private String orderLabel;
	
	@XmlPath("fptr/@FILEID")
	@XmlIDREF
	private MetsFile file;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
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

	public MetsFile getFile() {
		return file;
	}

	public void setFile(MetsFile file) {
		this.file = file;
	}
	
	
	
	


}
