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
package de.mpg.mpdl.dlc.vo.teisd;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

@XmlRootElement(name = "pb", namespace = "http://www.tei-c.org/ns/1.0")
public class Pagebreak extends PbOrDiv{
	
	
	
	

	@XmlAttribute(name="facs")
	private String facs;
	
	@XmlAttribute(name="next")
	private String next;
	
	@XmlAttribute(name="prev")
	private String prev;
	
	@XmlAttribute(name="subtype")
	private String subtype;
	


	
	public Pagebreak() {
		super(ElementType.PB);
		// TODO Auto-generated constructor stub
	}
	
	public Pagebreak(Pagebreak original) {
		super(original);
		this.setElementType(ElementType.PB);
		this.setFacs(original.getFacs());
		this.setNext(original.getNext());
		this.setPrev(original.getPrev());
		this.setSubtype(original.getSubtype());
	}
	
	public String getFacs() {
		return facs;
	}

	public void setFacs(String facs) {
		this.facs = facs;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getPrev() {
		return prev;
	}

	public void setPrev(String prev) {
		this.prev = prev;
	}


	

}
