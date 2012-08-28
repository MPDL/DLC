package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;




@ManagedBean
@RequestScoped
public class QuickSearchBean {

	private static Logger logger = Logger.getLogger(QuickSearchBean.class);
	
	private String searchString;
	
	@ManagedProperty("#{quickSearchResultBean}")
	private QuickSearchResultBean quickSearchResultBean;
	
	@ManagedProperty("#{advancedSearchResultBean}")
	private AdvancedSearchResultBean advancedSearchResultBean;
	
	public String startSearch()
	{
		try {
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
			SearchCriterion scFree = new SearchCriterion(SearchType.FREE, this.searchString);
			scList.add(scFree);


			//Set fulltext search


			advancedSearchResultBean.setSearchCriterionList(scList);
			String cql = new SearchBean().getAdvancedSearchCQL(scList);
			advancedSearchResultBean.setCqlQuery(cql);
			return "pretty:searchResult";
		} catch (Exception e) {
			logger.error("Error in quicksearch", e);
			return "";
		}
		
		
		
		/*
		quickSearchResultBean.setQuery(searchString);
		return "pretty:quickSearchResult";
		*/
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public QuickSearchResultBean getQuickSearchResultBean() {
		return quickSearchResultBean;
	}

	public void setQuickSearchResultBean(QuickSearchResultBean quickSearchResultBean) {
		this.quickSearchResultBean = quickSearchResultBean;
	}

	public AdvancedSearchResultBean getAdvancedSearchResultBean() {
		return advancedSearchResultBean;
	}

	public void setAdvancedSearchResultBean(AdvancedSearchResultBean advancedSearchResultBean) {
		this.advancedSearchResultBean = advancedSearchResultBean;
	}
	
}
