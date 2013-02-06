/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
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
