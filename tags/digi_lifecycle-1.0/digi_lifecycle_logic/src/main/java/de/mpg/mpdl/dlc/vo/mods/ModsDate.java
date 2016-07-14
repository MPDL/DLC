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

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsDate {
	
	@XmlValue
	//@XmlSchemaType(name = "dateTime")
	private String date;
	
	@XmlAttribute(name = "encoding")
	private String dateEncoding = "w3cdtf";
	
	@XmlAttribute(name = "keyDate")
	private String dateKeyDate = "yes";

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDateEncoding() {
		return dateEncoding;
	}

	public void setDateEncoding(String dateEncoding) {
		this.dateEncoding = dateEncoding;
	}

	public String getDateKeyDate() {
		return dateKeyDate;
	}

	public void setDateKeyDate(String dateKeyDate) {
		this.dateKeyDate = dateKeyDate;
	}

}