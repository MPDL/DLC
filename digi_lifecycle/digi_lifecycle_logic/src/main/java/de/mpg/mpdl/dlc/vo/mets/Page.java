package de.mpg.mpdl.dlc.vo.mets;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlPath;


public class Page{
	
	@XmlAttribute(name = "ID")
	@XmlID
	private String id;
	
	@XmlAttribute(name = "TYPE")
	private String type = "page";
	
	@XmlAttribute(name = "ORDER")
	private int order;
	
	@XmlAttribute(name = "ORDERLABEL")
	private String orderLabel;
	
	@XmlPath("mets:fptr/@FILEID")
	@XmlIDREF
	private MetsFile file;

	

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		
		if(o instanceof Page)
		{
			return this.getId().equals(((Page)o).getId());
		}
		else
		{
			return this.equals(o);
		}
			
	}
	
	
	
	


}
