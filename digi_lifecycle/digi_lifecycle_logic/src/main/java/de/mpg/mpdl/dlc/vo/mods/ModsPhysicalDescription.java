package de.mpg.mpdl.dlc.vo.mods;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsPhysicalDescription {
	@XmlPath("mods:form[@type='material']/text()")
	private String materialDesignation_334;
	
	@XmlPath("mods:form/text()")
	private String sec_technicalIndication;
	
	@XmlPath("mods:extent/text()")
	private String extent;

	public String getMaterialDesignation_334() {
		return materialDesignation_334;
	}

	public void setMaterialDesignation_334(String materialDesignation_334) {
		this.materialDesignation_334 = materialDesignation_334;
	}

	public String getExtent() {
		return extent;
	}

	public void setExtent(String extent) {
		this.extent = extent;
	}

	public String getSec_technicalIndication() {
		return sec_technicalIndication;
	}

	public void setSec_technicalIndication(String sec_technicalIndication) {
		this.sec_technicalIndication = sec_technicalIndication;
	}
	
	
}
