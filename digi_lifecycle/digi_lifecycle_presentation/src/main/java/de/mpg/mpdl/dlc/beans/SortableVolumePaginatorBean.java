package de.mpg.mpdl.dlc.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

public abstract class SortableVolumePaginatorBean extends BasePaginatorBean<Volume> {

	
	//private List<SortCriterion> sortCriterionList; //= SortCriterion.getStandardSortCriteria();
	//private List<SortCriterion> sortCriterionFilterList = SortCriterion.getStandardFilterSortCriteria();

	
	public abstract List<SortCriterion> getSortCriterionList();

	
	
	/*
	public List<SortCriterion> getSortCriterionFilterList() {
		return sortCriterionFilterList ;
	}

	public void setSortCriterionFilterList(List<SortCriterion> sortCriterionList) {
		this.sortCriterionFilterList = sortCriterionList;
	}
	*/
	
	public abstract List<SelectItem> getSortIndicesMenu();
	
		
	
	
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

