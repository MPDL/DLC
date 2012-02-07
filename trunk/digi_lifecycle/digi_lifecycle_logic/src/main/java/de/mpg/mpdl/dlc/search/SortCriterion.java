package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

public class SortCriterion {

	
	public enum SortIndex
	{
		
		TITLE("sort.dlc.title"),
		AUTHOR("sort.dlc.author"),
		YEAR("sort.dlc.year"),
		NEWEST("sort.escidoc.latest-release.date");
		
		private String indexName;
		
		SortIndex(String index)
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
	
	
	public enum SortOrder
	{
		
		ASCENDING("/sort.ascending"),
		DESCENDING("/sort.descending");
		
		private String orderName;
		
		SortOrder(String orderName)
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
	
	
	private SortIndex sortIndex;
	

	private SortOrder sortOrder;
	
	public SortCriterion(SortIndex sortIndex, SortOrder sortOrder)
	{
		this.sortIndex = sortIndex;
		this.sortOrder = sortOrder;
		
	}
	
	public SortCriterion(SortIndex sortIndex)
	{
		this.sortIndex = sortIndex;
		this.sortOrder = SortOrder.ASCENDING;
		
	}
	
	public SortIndex getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(SortIndex sortIndex) {
		this.sortIndex = sortIndex;
	}

	public void setSortIndexString(String sortIndex) {
		this.sortIndex = SortIndex.valueOf(sortIndex);
	}
	
	public String getSortIndexString() {
		return sortIndex.name();
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public static List<SortCriterion> getStandardSortCriteria()
	{
		
			List<SortCriterion> scList  = new ArrayList<SortCriterion>();
			scList.add(new SortCriterion(SortIndex.AUTHOR, SortOrder.ASCENDING));
			scList.add(new SortCriterion(SortIndex.TITLE, SortOrder.ASCENDING));
			scList.add(new SortCriterion(SortIndex.YEAR, SortOrder.DESCENDING));
			
			return scList;
		
	}
	
	

}
