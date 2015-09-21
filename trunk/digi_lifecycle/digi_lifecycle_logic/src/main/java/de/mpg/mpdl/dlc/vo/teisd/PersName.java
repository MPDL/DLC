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
package de.mpg.mpdl.dlc.vo.teisd;

public class PersName {
	
	private String numeration;
	
	private String author;
	
	private String invertedAuthor;
	
	private String key;

	
	public PersName()
	{
		
	}
	
	public PersName(PersName original)
	{
		this.numeration = original.getNumeration();
		this.author = original.getAuthor();
		this.invertedAuthor = original.getInvertedAuthor();
		this.key = original.getKey();
	}
	
	public String getNumeration() {
		return numeration;
	}

	public void setNumeration(String numeration) {
		this.numeration = numeration;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getInvertedAuthor() {
		return invertedAuthor;
	}

	public void setInvertedAuthor(String invertedAuthor) {
		this.invertedAuthor = invertedAuthor;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public boolean isEmpty()
	{
		return ((numeration==null || numeration.trim().isEmpty()) &&
				(author==null || author.trim().isEmpty()) &&
				(invertedAuthor==null || invertedAuthor.trim().isEmpty()) &&
				(key==null || key.trim().isEmpty()));
		
	}
	
	public boolean isNotEmpty()
	{
		return !isEmpty();
		
	}

}
