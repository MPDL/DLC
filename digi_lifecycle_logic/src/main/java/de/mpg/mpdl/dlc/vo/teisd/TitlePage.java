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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;

@XmlRootElement(name = "div", namespace = "http://www.tei-c.org/ns/1.0")
public class TitlePage extends Div {
	
	
	@XmlPath("/tei:docTitle/tei:titlePart")
	private List<DocTitle> docTitles;
	
	private List<DocTitle> titleParts;


	public TitlePage()
	{
		super(ElementType.TITLE_PAGE);
	}
	
	
	public TitlePage(Div original)
	{
		super(original);
		this.setElementType(ElementType.TITLE_PAGE);
		
	}
	
	public TitlePage(TitlePage original)
	{
		super(original);
		this.setElementType(ElementType.TITLE_PAGE);
		List<DocTitle> clonedDocTitles = new ArrayList<DocTitle>();
		List<DocTitle> clonedTitleParts = new ArrayList<DocTitle>();
		if(original.getDocTitles() != null)
		{
			for(DocTitle originalDt : original.getDocTitles())
			{
				DocTitle clonedDt = new DocTitle(originalDt);
				clonedDocTitles.add(clonedDt);
			}
		}
		
		if(original.getTitleParts()!=null)
		{
			for(DocTitle originalDt : original.getTitleParts())
			{
				DocTitle clonedDt = new DocTitle(originalDt);
				clonedTitleParts.add(clonedDt);
			}
		}
		
		
		
		this.setDocTitles(clonedDocTitles);
		this.setTitleParts(clonedTitleParts);
	}
	
	public List<DocTitle> getDocTitles() {
		return docTitles;
	}


	public void setDocTitles(List<DocTitle> docTitles) {
		this.docTitles = docTitles;
	}


	public List<DocTitle> getTitleParts() {
		return titleParts;
	}


	public void setTitleParts(List<DocTitle> titleParts) {
		this.titleParts = titleParts;
	}


	public static List<DocTitle> docTitleListFactory()
	{
		return new ArrayList<DocTitle>();
	}
	
	

	


	
}
