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
package de.mpg.mpdl.dlc.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.mpg.mpdl.dlc.vo.mets.Page;


@Entity
@Table(name="carousel_items")
@NamedQueries({
    @NamedQuery(name=CarouselItem.ALL_ITEMS_BY_CONTEXT_ID, query="SELECT i FROM CarouselItem i WHERE i.contextId = :contextId")
    //@NamedQuery(name=CarouselItem.ALL_ITEMS_BY_OU_ID, query="SELECT i FROM CarouselItem i WHERE i.ouId = :ouId")
    })
@NamedNativeQuery(name=CarouselItem.ALL_ITEMS_BY_OU_ID, query="SELECT * FROM carousel_items i WHERE ouId = ? ORDER BY RANDOM()", resultClass=CarouselItem.class)
public class CarouselItem {

	public static final String ALL_ITEMS_BY_CONTEXT_ID = "CarouselItem.itemsByContextId";
	public static final String ALL_ITEMS_BY_OU_ID = "CarouselItem.itemsByOuId";
	
	/*
	@Id
	@SequenceGenerator(name="carouselItemIdGenerator")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="carouselItemIdGenerator")
	@Column(name="id")
	private Long id;
	*/
	
	@Id
	private String itemId;
	
	@Column(columnDefinition="TEXT")
	private String imageUrl;
	
	private String contextId;
	
	private String ouId;

	/*
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	 */
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getOuId() {
		return ouId;
	}

	public void setOuId(String ouId) {
		this.ouId = ouId;
	}
	
	/*
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof CarouselItem)
		{
			return this.getItemId().equals(((CarouselItem)o).getItemId());
		}
		else
		{
			return this.equals(o);
		}
			
	}
	*/
	
}
