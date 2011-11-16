package de.mpg.mpdl.dlc.search;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.management.Query;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
@URLMapping(id = "searchResult", viewId = "/advancedSearchResult.xhtml", pattern = "/searchresult")
public class AdvancedSearchResultBean extends BasePaginatorBean<Volume> {

	private int totalNumberOfRecords;
	
	@URLQueryParameter("q")
	private String cqlQuery;
	
	@EJB
	private SearchBean searchBean;
	
	private List<SearchCriterion> searchCriterionList;	
	
	
	@Override
	public List<Volume> retrieveList(int offset, int limit) throws Exception {
		VolumeSearchResult res = searchBean.searchByCql(cqlQuery, limit, offset);
		this.totalNumberOfRecords = res.getNumberOfRecords();
		return res.getVolumes();
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCqlQuery() {
		return cqlQuery;
	}

	public void setCqlQuery(String query) {
		this.cqlQuery = query;
	}

	public List<SearchCriterion> getSearchCriterionList() {
		return searchCriterionList;
	}

	public void setSearchCriterionList(List<SearchCriterion> searchCriterionList) {
		this.searchCriterionList = searchCriterionList;
	}

	
}
