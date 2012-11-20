package de.mpg.mpdl.dlc.searchLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortCriterion {

	

	public enum CombinedSortCriterion
	{
		AUTHOR_TITLE_ASC(new SortCriterion[]{
				new SortCriterion(SortIndices.AUTHOR, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.TITLE, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)}),
		AUTHOR_TITLE_DESC(new SortCriterion[]{
				new SortCriterion(SortIndices.AUTHOR, SortOrders.DESCENDING),
				new SortCriterion(SortIndices.TITLE, SortOrders.ASCENDING)}),
		TITLE_YEAR_ASC(new SortCriterion[]{
				new SortCriterion(SortIndices.TITLE, SortOrders.ASCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)}),
		TITLE_YEAR_DESC(new SortCriterion[]{
				new SortCriterion(SortIndices.TITLE, SortOrders.DESCENDING),
				new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)}),
		YEAR_DESC(new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING)),
		YEAR_ASC(new SortCriterion(SortIndices.YEAR, SortOrders.ASCENDING)),
		LAST_MODIFIED_DESC(new SortCriterion(SortIndices.LAST_MODIFIED, SortOrders.DESCENDING)),
		NEWEST_DESC(new SortCriterion(SortIndices.NEWEST, SortOrders.DESCENDING)),
		VOLUME(new SortCriterion[]{
				new SortCriterion(SortIndices.VOLUME_ORDER, SortOrders.ASCENDING),
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
		NEWEST("sort.escidoc.creation-date", "/sort/properties/creation-date"),
		STATUS("", "/properties/public-status"),
		LAST_MODIFIED("", "/sort/last-modification-date"),
		
		VOLUME_ORDER("sort.escidoc.part", "/sort/md-records/md-record/mods/part/order");
		
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
