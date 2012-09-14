package de.mpg.mpdl.dlc.vo.mets;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;


public class Page{
	
	@XmlAttribute(name = "ID")
	@XmlID
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
	
	/*
	 * viewType not in Xml, just setted by viewPage
	 */
	private String viewType;
	
	/*
	@XmlPath("mets:fptr[1]/@FILEID")
	@XmlIDREF
	private MetsFile thumbnailFile = new MetsFile();
	
	@XmlPath("mets:fptr[2]/@FILEID")
	@XmlIDREF
	private MetsFile defaultFile = new MetsFile();
	
	@XmlPath("mets:fptr[3]/@FILEID")
	@XmlIDREF
	private MetsFile maxFile = new MetsFile();
	
	@XmlPath("mets:fptr[4]/@FILEID")
	@XmlIDREF
	private MetsFile digilibFile = new MetsFile();

	*/
	
	
	/*
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		
		this.setParent((PbOrDiv)parent);
	  }
	*/
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getContentIds() {
		return contentIds;
	}

	public void setContentIds(String contentIds) {
		this.contentIds = contentIds;
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

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	
	/*
	public MetsFile getThumbnailFile() {
		return thumbnailFile;
	}

	public void setThumbnailFile(MetsFile thumbnailFile) {
		this.thumbnailFile = thumbnailFile;
	}

	public MetsFile getDefaultFile() {
		return defaultFile;
	}

	public void setDefaultFile(MetsFile defaultFile) {
		this.defaultFile = defaultFile;
	}

	public MetsFile getMaxFile() {
		return maxFile;
	}

	public void setMaxFile(MetsFile maxFile) {
		this.maxFile = maxFile;
	}

	public MetsFile getDigilibFile() {
		return digilibFile;
	}

	public void setDigilibFile(MetsFile digilibFile) {
		this.digilibFile = digilibFile;
	}
	*/


}
