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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsRelatedItem {
	
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name = "displayLabel")
	private String displayLabel;
	
	@XmlPath("mods:recordInfo/mods:recordIdentifier[@source='local']/text()")
	private String parentId_010;
	
	@XmlPath("mods:titleInfo/mods:title/text()")
	private String title;
	
	@XmlElement(name = "originInfo", namespace="http://www.loc.gov/mods/v3")
	private List<ModsPublisher> sec_Publisher = new ArrayList<ModsPublisher>();
	
	@XmlElement(name = "identifier", namespace="http://www.loc.gov/mods/v3")
	private List<ModsIdentifier> sec_identifiers = new ArrayList<ModsIdentifier>();

	@XmlElement(name = "location", namespace="http://www.loc.gov/mods/v3")
	private List<ModsLocationSEC> sec_location = new ArrayList<ModsLocationSEC>();
	
	@XmlElement(name = "note", namespace="http://www.loc.gov/mods/v3")
	private ModsNote sec_notes;
	
	@XmlElement(name = "relatedItem", namespace="http://www.loc.gov/mods/v3")
	private ModsRelatedItem sec_relatedItems;
	
	@XmlPath("mods:accessCondition/text()")
	private String sec_copyright;
	
	@XmlElement(name = "physicalDescription", namespace ="http://www.loc.gov/mods/v3")
	private List<ModsPhysicalDescription> sec_physicalDescriptions = new ArrayList<ModsPhysicalDescription>();


	public ModsRelatedItem getSec_relatedItems() {

		return sec_relatedItems;
	}


	public void setSec_relatedItems(ModsRelatedItem sec_relatedItems) {
		this.sec_relatedItems = sec_relatedItems;
	}


	public String getSec_copyright() {
		return sec_copyright;
	}


	public void setSec_copyright(String sec_copyright) {
		this.sec_copyright = sec_copyright;
	}


	public List<ModsPhysicalDescription> getSec_physicalDescriptions() {
		return sec_physicalDescriptions;
	}


	public void setSec_physicalDescriptions(
			List<ModsPhysicalDescription> sec_physicalDescriptions) {
		this.sec_physicalDescriptions = sec_physicalDescriptions;
	}


	public String getParentId_010() {
		return parentId_010;
	}


	public void setParentId_010(String parentId_010) {
		this.parentId_010 = parentId_010;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDisplayLabel() {
		return displayLabel;
	}


	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}


	public List<ModsPublisher> getSec_Publisher() {
		return sec_Publisher;
	}


	public void setSec_Publisher(List<ModsPublisher> sec_Publisher) {
		this.sec_Publisher = sec_Publisher;
	}


	public List<ModsIdentifier> getSec_identifiers() {
		return sec_identifiers;
	}


	public void setSec_identifiers(List<ModsIdentifier> sec_identifiers) {
		this.sec_identifiers = sec_identifiers;
	}

	public List<ModsLocationSEC> getSec_location() {
		return sec_location;
	}


	public void setSec_location(List<ModsLocationSEC> sec_location) {
		this.sec_location = sec_location;
	}


	public ModsNote getSec_notes() {
		return sec_notes;
	}


	public void setSec_notes(ModsNote sec_notes) {
		this.sec_notes = sec_notes;
	}
	

}
