package de.mpg.mpdl.dlc.vo.mods;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;


public class ModsPhysicalDescription {

//	@XmlPath("mods:form[@type='material']/text()")
//	private String materialDesignation_334;
//	
//	@XmlPath("mods:form/text()")
//	private String sec_technicalIndication;
	
	@XmlElement(name = "form", namespace="http://www.loc.gov/mods/v3")	
	private ModsPhysicalDescriptionForm pdForm;
	
	@XmlPath("mods:extent/text()")
	private String extent;



	public String getExtent() {
		return extent;
	}

	public void setExtent(String extent) {
		this.extent = extent;
	}

	public ModsPhysicalDescriptionForm getPdForm() {
		return pdForm;
	}

	public void setPdForm(ModsPhysicalDescriptionForm pdForm) {
		this.pdForm = pdForm;
	}


	
}
