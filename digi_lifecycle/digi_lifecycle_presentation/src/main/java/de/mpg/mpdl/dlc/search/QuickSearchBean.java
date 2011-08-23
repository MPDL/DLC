package de.mpg.mpdl.dlc.search;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;


@ManagedBean
@RequestScoped
public class QuickSearchBean {

	
	private String searchString;
	
	@ManagedProperty("#{quickSearchResultBean}")
	private QuickSearchResultBean quickSearchResultBean;
	
	public String startSearch()
	{
		quickSearchResultBean.setQuery(searchString);
		return "pretty:quickSearchResult";
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
}
