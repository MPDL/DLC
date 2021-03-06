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
package de.mpg.mpdl.dlc.searchLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortCriterion {

	

	public enum CombinedSortCriterion
	{
		
		
		AUTHOR_TITLE_ASC(new SortCriterion[]{
				new SortCriterion(SortIndices.AUTHOR_TITLE_COMPOUND, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)}),
		AUTHOR_TITLE_DESC(new SortCriterion[]{
				new SortCriterion(SortIndices.AUTHOR_TITLE_COMPOUND, SortOrders.DESCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)}),
		TITLE_YEAR_ASC(new SortCriterion[]{
				new SortCriterion(SortIndices.TITLE, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)}),
		TITLE_YEAR_DESC(new SortCriterion[]{
				new SortCriterion(SortIndices.TITLE, SortOrders.DESCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)}),
		YEAR_DESC(new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)),
		YEAR_ASC(new SortCriterion(SortIndices.YEAR, SortOrders.ASCENDING)),
		SIGNATURE_ASC(new SortCriterion(SortIndices.SIGNATURE, SortOrders.ASCENDING)),
		LAST_MODIFIED_DESC(new SortCriterion(SortIndices.LAST_MODIFIED, SortOrders.DESCENDING)),
		NEWEST_DESC(new SortCriterion(SortIndices.NEWEST, SortOrders.DESCENDING)),
		VOLUME(new SortCriterion[]{
				new SortCriterion(SortIndices.VOLUME_ORDER, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.VOLUME_PARTNUMBER, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.VOLUME_PARTTITLE, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.AUTHOR, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.TITLE, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)
				});
		
		private List<SortCriterion> scList;
		
		
		private CombinedSortCriterion(SortCriterion[] scList) {
			this.scList = Arrays.asList(scList);
		}
		
		private CombinedSortCriterion(List<SortCriterion> scList) {
			this.scList = scList;
		}
		private CombinedSortCriterion(SortCriterion sc) {
			this.scList = new ArrayList<SortCriterion>();
			this.scList.add(sc);
		}

		public List<SortCriterion> getScList() {
			return scList;
		}

		public void setScList(List<SortCriterion> scList) {
			this.scList = scList;
		}
	}
	
	
	public enum SortIndices
	{
		
		TITLE("sort.dlc.title", "/sort/dlc/title"),
		AUTHOR("sort.dlc.author", "/sort/dlc/author"),
		YEAR("sort.dlc.year", "/sort/dlc/year"),
		SIGNATURE("sort.escidoc.physicalLocation", "/sort/md-records/md-record/mods/location/physicalLocation"),
		NEWEST("sort.escidoc.creation-date", "/sort/properties/creation-date"),
		STATUS("", "/properties/public-status"),
		LAST_MODIFIED("", "/sort/last-modification-date"),
		AUTHOR_TITLE_COMPOUND("sort.dlc.compound.author-title", "/sort/dlc/compound/author-title"),
		
		VOLUME_ORDER("sort.escidoc.part.order", "/sort/md-records/md-record/mods/part/order"),
		VOLUME_PARTNUMBER("sort.escidoc.number", "/sort/md-records/md-record/mods/part/detail/number"),
		VOLUME_PARTTITLE("sort.escidoc.part", "/sort/md-records/md-record/mods/part/detail/title");
		
		private String searchIndexName;
		private String filterIndexName;
		
		SortIndices(String searchIndexName, String filterIndexName)
		{
			this.searchIndexName = searchIndexName;
			this.filterIndexName = filterIndexName;
		}

		public String getSearchIndexName() {
			return searchIndexName;
		}

		public void setSearchIndexName(String searchIndexName) {
			this.searchIndexName = searchIndexName;
		}

		public String getFilterIndexName() {
			return filterIndexName;
		}

		public void setFilterIndexName(String filterIndexName) {
			this.filterIndexName = filterIndexName;
		}

		
	}
	
	
	public enum SortOrders
	{
		
		ASCENDING("/sort.ascending"),
		DESCENDING("/sort.descending");
		
		private String orderName;
		
		SortOrders(String orderName)
		{
			this.setOrderName(orderName);
		}

		public String getOrderName() {
			return orderName;
		}

		public void setOrderName(String orderName) {
			this.orderName = orderName;
		}
	}
	
	
	private SortIndices sortIndex;
	

	private SortOrders sortOrder;
	
	public SortCriterion(SortIndices sortIndex, SortOrders sortOrder)
	{
		this.sortIndex = sortIndex;
		this.sortOrder = sortOrder;
		
	}
	
	public SortCriterion(SortIndices sortIndex)
	{
		this.sortIndex = sortIndex;
		this.sortOrder = SortOrders.ASCENDING;
		
	}
	
	public SortIndices getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(SortIndices sortIndex) {
		this.sortIndex = sortIndex;
	}

	

	public SortOrders getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrders sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	
	
	
	
	

}
