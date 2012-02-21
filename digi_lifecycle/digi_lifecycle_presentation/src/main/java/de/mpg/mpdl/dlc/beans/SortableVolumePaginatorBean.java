package de.mpg.mpdl.dlc.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.search.SearchBean;
import de.mpg.mpdl.dlc.search.SortCriterion;
import de.mpg.mpdl.dlc.search.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.search.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

public abstract class SortableVolumePaginatorBean extends BasePaginatorBean<Volume> {

	
	private List<SortCriterion> sortCriterionList = SortCriterion.getStandardSortCriteria();

	public List<SortCriterion> getSortCriterionList() {
		return sortCriterionList;
	}

	public void setSortCriterionList(List<SortCriterion> sortCriterionList) {
		this.sortCriterionList = sortCriterionList;
	}
	
	public List<SelectItem> getSortIndicesMenu()
	{
		List<SelectItem> scMenuList = new ArrayList<SelectItem>();
		for(SortIndices si : SortIndices.values())
		{
			scMenuList.add(new SelectItem(si.name(), si.name()));
			
		}
		return scMenuList;
	}
	
	public String changeSortCriteria()
	{
		setCurrentPageNumber(1);
		return getNavigationString();
	}
	
	public String changeSortOrder(SortCriterion sc)
	{
	
		if(SortOrders.ASCENDING.equals(sc.getSortOrder()))
		{
			sc.setSortOrder(SortOrders.DESCENDING);
		}
		else
		{
			sc.setSortOrder(SortOrders.ASCENDING);
		}
			
		return changeSortCriteria();
	}
	
	
	
	

	

	
}

