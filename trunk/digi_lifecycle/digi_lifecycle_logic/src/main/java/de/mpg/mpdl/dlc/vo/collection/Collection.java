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
package de.mpg.mpdl.dlc.vo.collection;

public class Collection {
	private String id;
	
	private String name;
	
	private String description;
	
	private String ouId;
	
	private String ouTitle;
	
	private String type;
	
	private DLCAdminDescriptor dlcAD;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getOuId() {
		return ouId;
	}

	public void setOuId(String ouId) {
		this.ouId = ouId;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DLCAdminDescriptor getDlcAD() {
		return dlcAD;
	}

	public void setDlcAD(DLCAdminDescriptor dlcAD) {
		this.dlcAD = dlcAD;
	}

	public String getOuTitle() {
		return ouTitle;
	}

	public void setOuTitle(String ouTitle) {
		this.ouTitle = ouTitle;
	}
	
	

}
