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
package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedProperty;
import javax.faces.component.html.HtmlPanelGroup;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;

public class TeiElementWrapper {

	public enum PositionType
	{
		START, END, EMPTY;
	}
	
	
	private TeiElementWrapper partnerElement;
	
	private PositionType positionType;

	private PbOrDiv teiElement;
	
	private TreeWrapperNode treeWrapperNode;
	
	
	
	//The pagebreak this element belongs to
	private TeiElementWrapper pagebreakWrapper;
	
	//The pagebreak id this element should belong to, automatically set when pagebreak wrapper is set
	private String pagebreakIdToMoveTo;
	
	//For Pagebreaks:
	//Link to METS page element, if a pagebreak 
	private Page page;
	
	private TeiElementWrapper nextPagebreak;
	private TeiElementWrapper lastPagebreak;
	
	
	

	public TeiElementWrapper()
	{
		
	}
	
	public TeiElementWrapper(TeiElementWrapper original)
	{
		this.setLastPagebreak(original.getLastPagebreak());
		this.setNextPagebreak(original.getNextPagebreak());
		this.setPage(original.getPage());
		this.setPagebreakIdToMoveTo(original.getPagebreakIdToMoveTo());
		this.setPagebreakWrapper(original.getPagebreakWrapper());
		this.setPartnerElement(original.getPartnerElement());
		this.setPositionType(original.getPositionType());
		this.setTeiElement(original.getTeiElement());
		this.setTreeWrapperNode(original.getTreeWrapperNode());
		
	}
	
	
	
	public PositionType getPositionType() {
		return positionType;
	}


	public void setPositionType(PositionType positionType) {
		this.positionType = positionType;
	}


	public TeiElementWrapper getPartnerElement() {
		return partnerElement;
	}


	public void setPartnerElement(TeiElementWrapper partnerElement) {
		this.partnerElement = partnerElement;
	}


	public PbOrDiv getTeiElement() {
		return teiElement;
	}


	public void setTeiElement(PbOrDiv teiElement) {
		this.teiElement = teiElement;
	}



	public Page getPage() {
		return page;
	}


	public void setPage(Page page) {
		this.page = page;
	}


	public TreeWrapperNode getTreeWrapperNode() {
		return treeWrapperNode;
	}


	public void setTreeWrapperNode(TreeWrapperNode treeWrapperNode) {
		this.treeWrapperNode = treeWrapperNode;
	}


	public TeiElementWrapper getPagebreakWrapper() {
		return pagebreakWrapper;
	}


	public void setPagebreakWrapper(TeiElementWrapper pagebreakWrapper) {
		this.pagebreakWrapper = pagebreakWrapper;
		this.pagebreakIdToMoveTo = pagebreakWrapper.getTeiElement().getId();
		
	}


	public TeiElementWrapper getNextPagebreak() {
		return nextPagebreak;
	}


	public void setNextPagebreak(TeiElementWrapper nextPagebreak) {
		this.nextPagebreak = nextPagebreak;
	}



	


	public String getPagebreakIdToMoveTo() {
		return pagebreakIdToMoveTo;
	}


	public void setPagebreakIdToMoveTo(String pagebreakId) {
		this.pagebreakIdToMoveTo = pagebreakId;
		/*
		try {
			System.out.println("Page: " + getPagebreakWrapper().getTeiElement().getId() + " set to " + pagebreakId);
		} catch (Exception e) {
			System.out.println("NullPointerException");
		}
		*/
	}


	public TeiElementWrapper getLastPagebreak() {
		return lastPagebreak;
	}


	public void setLastPagebreak(TeiElementWrapper lastPagebreak) {
		this.lastPagebreak = lastPagebreak;
	}


	/*
	public List<TeiElementWrapper> getElementsToNextPb() {
		return elementsToNextPb;
	}


	public void setElementsToNextPb(List<TeiElementWrapper> elementsToNextPb) {
		this.elementsToNextPb = elementsToNextPb;
	}
	*/
	
	
	
}
