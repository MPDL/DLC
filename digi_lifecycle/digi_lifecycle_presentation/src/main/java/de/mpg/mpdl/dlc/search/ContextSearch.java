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
package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * TODO
 * @author kleinfe1
 *
 */
public class ContextSearch {
	
	private String ouId;
	private String contextId;
	private List<SelectItem> contextList;
	
	
	public ContextSearch()
	{
		this.ouId = "";
		this.contextId = "";
		this.contextList = new ArrayList<SelectItem>();
	}

	public ContextSearch(String ouId, String contextId, List<SelectItem> contextList)
	{
		this.ouId = ouId;
		this.contextId = contextId;
		this.contextList = contextList;
	}
	
	
	public String getOuId() {
		return ouId;
	}
	public void setOuId(String ouId) {
		this.ouId = ouId;
	}
	public String getContextId() {
		return contextId;
	}
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}
	public List<SelectItem> getContextList() {
		return contextList;
	}
	public void setContextList(List<SelectItem> contextList) {
		this.contextList = contextList;
	}
}
