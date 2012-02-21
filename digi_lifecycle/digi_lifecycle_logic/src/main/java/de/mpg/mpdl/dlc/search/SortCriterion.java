package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

public class SortCriterion {

	
	public enum SortIndices
	{
		
		TITLE("sort.dlc.title"),
		AUTHOR("sort.dlc.author"),
		YEAR("sort.dlc.year"),
		NEWEST("sort.escidoc.latest-release.date");
		
		private String indexName;
		
		SortIndices(String index)
		{
			this.setIndexName(index);
		}

		public String getIndexName() {
			return indexName;
		}

		public void setIndexName(String indexName) {
			this.indexName = indexName;
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
	
	public static List<SortCriterion> getStandardSortCriteria()
	{
		
			List<SortCriterion> scList  = new ArrayList<SortCriterion>();
			scList.add(new SortCriterion(SortIndices.AUTHOR, SortOrders.ASCENDING));
			scList.add(new SortCriterion(SortIndices.TITLE, SortOrders.ASCENDING));
			scList.add(new SortCriterion(SortIndices.YEAR, SortOrders.DESCENDING));
			
			return scList;
		
	}
	
	

}
