/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.vo.mods;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsPublisher {


	@XmlPath("mods:place/mods:placeTerm[@type='text']/text()")
	private String place;
	
	@XmlPath("mods:publisher/text()")
	private String publisher;
	
	@XmlElement(name = "edition", namespace="http://www.loc.gov/mods/v3")
	private String edition;
	
	@XmlPath("mods:dateIssued")
	private ModsDate dateIssued_425;
	
	@XmlPath("mods:dateCaptured[@encoding='w3cdtf']/text()")
	private String dateCaptured;
	
	@XmlAttribute(name="displayLabel")
	private String displayLabel;
	
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {

		this.place = place;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEdition() {
		return edition;
	}

	public ModsDate getDateIssued_425() {
		return dateIssued_425;
	}

	public void setDateIssued_425(ModsDate dateIssued_425) {
		this.dateIssued_425 = dateIssued_425;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getDateCaptured() {
		return dateCaptured;
	}

	public void setDateCaptured(String dateCaptured) {
		this.dateCaptured = dateCaptured;
	}
	

}
