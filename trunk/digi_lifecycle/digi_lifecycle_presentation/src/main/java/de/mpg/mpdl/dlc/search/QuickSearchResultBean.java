package de.mpg.mpdl.dlc.search;

import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@SessionScoped
@URLMapping(id = "quickSearchResult", viewId = "/quickSearchResult.xhtml", pattern = "/quicksearch")
public class QuickSearchResultBean extends BasePaginatorBean<Volume> {

	private int totalNumberOfRecords;
	
	@URLQueryParameter("q")
	private String query;
	
	
	private SearchBean searchBean = new SearchBean();
	private ApplicationBean appBean = new ApplicationBean();
	
	@Override
	public List<Volume> retrieveList(int offset, int limit) throws Exception {
		VolumeSearchResult res = searchBean.quickSearchVolumes(query, limit, offset);
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
		return "pretty:quickSearchResult";
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.appBean.setLatestCql(query);
		this.query = query;
	}
	
	
}
