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
package de.mpg.mpdl.dlc.vo.teisd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class Figure extends Div {
	
	
	private String figDesc;
	
	private String caption;
	
	
	private List<PersName> persNames = new ArrayList<PersName>();
	

	public Figure() {
		super(ElementType.FIGURE);
	}
	
	public Figure(Div original)
	{
		super(original);
		this.setElementType(ElementType.FIGURE);
	}

	public Figure(Figure original)
	{
		super(original);
		this.setElementType(ElementType.FIGURE);
		this.setFigDesc(original.getFigDesc());
		List<String> head = new ArrayList<String>();
		head.addAll(original.getHead());
		this.setHead(head);
		
		List<PersName> persNameList = new ArrayList<PersName>();
		
		for(PersName originalPersName : original.getPersNames())
		{
			persNameList.add(new PersName(originalPersName));
		}
		this.setPersNames(persNameList);
		
		this.setCaption(original.getCaption());
	}
	
	public String getFigDesc() {
		return figDesc;
	}

	public void setFigDesc(String figureDesc) {
		this.figDesc = figureDesc;
	}

	public List<PersName> getPersNames() {
		return persNames;
	}

	public void setPersNames(List<PersName> persNames) {
		this.persNames = persNames;
	}

	public static List<PersName> persNameListFactory()
	{
		return new ArrayList<PersName>();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
	public boolean isNotEmptyFigDesc()
	{
		
		
		return (!(figDesc==null || figDesc.trim().isEmpty())) || isNotEmptyPersNames();
	}
	
	public boolean isNotEmptyPersNames()
	{
		boolean notEmptyPersNames = false;
		if(persNames!=null)
		{
			for(PersName persName : persNames)
			{
				if(!persName.isEmpty())
				{
					notEmptyPersNames = true;
				}
			}
		}
		
		return notEmptyPersNames;
	}

	

}
