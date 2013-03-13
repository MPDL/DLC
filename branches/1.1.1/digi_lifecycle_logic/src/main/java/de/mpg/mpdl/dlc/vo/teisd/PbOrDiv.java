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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import de.mpg.mpdl.dlc.vo.mets.MetsDiv;

public abstract class PbOrDiv {

	@XmlTransient
	public enum ElementType
	{
		PB, DIV, FRONT, BODY, BACK, FIGURE, TITLE_PAGE, TEXT, GROUP
	}
	
	@XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
	protected String id;
	
	@XmlAttribute(name = "n")
	protected String numeration;
	
	@XmlAttribute(name="type")
	protected String type;
	
	@XmlAttribute(name="rend")
	protected String rend;
	
	@XmlElements
	(value = {
			@XmlElement(name="div", namespace="http://www.tei-c.org/ns/1.0", type=de.mpg.mpdl.dlc.vo.teisd.Div.class),
			@XmlElement(name="pb", namespace="http://www.tei-c.org/ns/1.0", type=de.mpg.mpdl.dlc.vo.teisd.Pagebreak.class),
			@XmlElement(name="titlePage", namespace="http://www.tei-c.org/ns/1.0", type=de.mpg.mpdl.dlc.vo.teisd.TitlePage.class)
	})
	private List<PbOrDiv> pbOrDiv = new ArrayList<PbOrDiv>();
	
	@XmlTransient
	private PbOrDiv parent;
	
	@XmlTransient
	private ElementType elementType;
	
	
	public PbOrDiv(ElementType type)
	{
		this.elementType = type;
	}
	
	public PbOrDiv(PbOrDiv original)
	{
		this.setElementType(original.getElementType());
		this.setId(original.getId());
		this.setNumeration(original.getNumeration());
		this.setParent(original.getParent());
		this.setPbOrDiv(original.getPbOrDiv());
		this.setType(original.getType());
		this.setRend(original.getRend());
	}
	
	public PbOrDiv()
	{
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumeration() {
		return numeration;
	}

	public void setNumeration(String numeration) {
		this.numeration = numeration;
	}
	
	
	public List<PbOrDiv> getPbOrDiv() {
		return pbOrDiv;
	}

	public void setPbOrDiv(List<PbOrDiv> pbOrDiv) {
		this.pbOrDiv = pbOrDiv;
	} 

	
	//Sets the parent div after unmarshalling
	public void afterUnmarshal(Unmarshaller u, Object parent) {
	    this.setParent((PbOrDiv)parent);
	  }

	public PbOrDiv getParent() {
		return parent;
	}

	public void setParent(PbOrDiv parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}
	
	/*
	 * Workaround for a4j:repeat
	 */
	public List<PersName> getPersNames()
	{
		return null;
	}

	public String getRend() {
		return rend;
	}

	public void setRend(String rend) {
		this.rend = rend;
	}
	
	

}
