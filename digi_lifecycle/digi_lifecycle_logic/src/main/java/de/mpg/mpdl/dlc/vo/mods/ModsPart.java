package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsPart {
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name = "order")
	private String order;
	
	@XmlPath("text()")
	private String value;
	
	@XmlPath("mods:detail/mods:number/text()")
	private String volumeDescriptive_089;
	
	@XmlPath("mods:detail/mods:title/text()")
	private String subseries_361;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getVolumeDescriptive_089() {
		return volumeDescriptive_089;
	}

	public void setVolumeDescriptive_089(String volumeDescriptive_089) {
		this.volumeDescriptive_089 = volumeDescriptive_089;
	}

	public String getSubseries_361() {
		return subseries_361;
	}

	public void setSubseries_361(String subseries_361) {
		this.subseries_361 = subseries_361;
	}








}
