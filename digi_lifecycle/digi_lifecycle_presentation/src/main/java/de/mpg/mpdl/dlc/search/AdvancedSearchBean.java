package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;


@ManagedBean
@RequestScoped
@URLMapping(id = "advancedSearch", pattern = "/search", viewId = "/advancedSearch.xhtml")
public class AdvancedSearchBean {

	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);
	
	private List<SearchCriterion> searchCriterionList;
	
	@EJB
	private SearchBean searchBean;
	
	@ManagedProperty("#{advancedSearchResultBean}")
	private AdvancedSearchResultBean advancedSearchResultBean;
	
	public AdvancedSearchBean()
	{
		this.searchCriterionList = new ArrayList<SearchCriterion>();
		this.searchCriterionList.add(new SearchCriterion(SearchType.FREE, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.AUTHOR, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.TITLE, ""));
		
	}
	
	public String startSearch()
	{
		
		try {
			
			
			
			advancedSearchResultBean.setSearchCriterionList(searchCriterionList);
			String cql = searchBean.getAdvancedSearchCQL(searchCriterionList);
			advancedSearchResultBean.setCqlQuery(cql);
			return "pretty:searchResult";
		} catch (Exception e) {
			logger.error("Error while creating CQL query", e);
			return "";
		}
	}

	public AdvancedSearchResultBean getAdvancedSearchResultBean() {
		return advancedSearchResultBean;
	}

	public void setAdvancedSearchResultBean(AdvancedSearchResultBean advancedSearchResultBean) {
		this.advancedSearchResultBean = advancedSearchResultBean;
	}

	public List<SearchCriterion> getSearchCriterionList() {
		return searchCriterionList;
	}

	public void setSearchCriterionList(List<SearchCriterion> searchCriterionList) {
		this.searchCriterionList = searchCriterionList;
	}

	
}
