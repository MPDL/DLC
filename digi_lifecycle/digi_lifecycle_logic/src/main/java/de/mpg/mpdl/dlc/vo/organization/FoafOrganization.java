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
package de.mpg.mpdl.dlc.vo.organization;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="Organization",namespace="http://xmlns.com/foaf/0.1/")
public class FoafOrganization {
	
	@XmlPath("foaf:img/@rdf:resource")
	private String imgURL;
	
	@XmlPath("foaf:homepage/@rdf:resource")
	private String homePageURL;
	
	@XmlPath("foaf:cataloguePrefix/@rdf:resource")
	private String cataloguePrefix;
	
	@XmlPath("foaf:contact/@rdf:resource")
	private String contact;
	
	@XmlPath("foaf:imprint/@rdf:resource")
	private String imprint;
	
	@XmlPath("foaf:support/@rdf:resource")
	private String support;
	
	@XmlPath("foaf:usageRequirement/@rdf:resource")
	private String usageRequirement;
	
	
	public FoafOrganization()
	{
		
	}



	public String getImgURL() {
		return imgURL;
	}



	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}



	public String getHomePageURL() {
		return homePageURL;
	}



	public void setHomePageURL(String homePageURL) {
		this.homePageURL = homePageURL;
	}



	public String getCataloguePrefix() {
		return cataloguePrefix;
	}



	public void setCataloguePrefix(String cataloguePrefix) {
		this.cataloguePrefix = cataloguePrefix;
	}



	public String getContact() {
		return contact;
	}



	public void setContact(String contact) {
		this.contact = contact;
	}



	public String getImprint() {
		return imprint;
	}



	public void setImprint(String imprint) {
		this.imprint = imprint;
	}



	public String getSupport() {
		return support;
	}



	public void setSupport(String support) {
		this.support = support;
	}



	public String getUsageRequirement() {
		return usageRequirement;
	}



	public void setUsageRequirement(String usageRequirement) {
		this.usageRequirement = usageRequirement;
	}








	
}
