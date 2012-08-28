package de.mpg.mpdl.dlc.search;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.SortableVolumePaginatorBean;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;

@ManagedBean
@SessionScoped
@URLMapping(id = "searchResult", viewId = "/advancedSearchResult.xhtml", pattern = "/searchresult")
public class AdvancedSearchResultBean extends SortableVolumePaginatorBean {
	
	private static Logger logger = Logger.getLogger(AdvancedSearchResultBean.class);
	
	private int totalNumberOfRecords;
	
	@URLQueryParameter("q")
	private String cqlQuery;
	
	
	private SearchBean searchBean = new SearchBean();
	private ApplicationBean appBean = new ApplicationBean();
	private SessionBean sessionBean = new SessionBean();
	
	private List<SearchCriterion> searchCriterionList;	
	
	
	@Override
	public List<Volume> retrieveList(int offset, int limit) throws Exception {
		VolumeSearchResult res = searchBean.searchByCql(cqlQuery, getSortCriterionList(), limit, offset);
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
		return "pretty:searchResult";
	}

	public String getCqlQuery() {
		return cqlQuery;
	}

	public void setCqlQuery(String query) {
		if (this.getCurrentPartList().size() > 0)
		{
			this.sessionBean.setLatestCql(query);
		}
		this.cqlQuery = query;
	}

	public List<SearchCriterion> getSearchCriterionList() {
		return searchCriterionList;
	}

	public void setSearchCriterionList(List<SearchCriterion> searchCriterionList) {
		this.searchCriterionList = searchCriterionList;
	}
	
	public String getFulltextSearchStringsAsQueryParam()
	{
		StringBuffer fulltextStrings = new StringBuffer();
		
		CQLParser cqlp = new CQLParser();
		try {
			CQLNode root = cqlp.parse(cqlQuery);
			createFulltextString(root, fulltextStrings);
			
		}catch (Exception e) {
			logger.error("Could not parse cql query for fulltext hits", e);
		}
		
		
		/*
		
		if(searchCriterionList!=null)
		{
			for(SearchCriterion sc : searchCriterionList)
			{
				if(SearchType.FULLTEXT.equals(sc.getSearchType()))
				{
					fulltextStrings.append(sc.getText() + ",");
				}
			}
		}
		
		*/
		return fulltextStrings.toString();
	}
	
	private StringBuffer createFulltextString(CQLNode node, StringBuffer buffer)
	{
		if(node!=null)
		{
			if(node instanceof CQLTermNode)
			{
				CQLTermNode termNode = (CQLTermNode) node;
				if(termNode.getIndex().equals("escidoc.fulltext"))
				buffer.append(termNode.getTerm()+",");
			}
			else if (node instanceof CQLBooleanNode)
			{
				createFulltextString(((CQLBooleanNode) node).left, buffer);
				createFulltextString(((CQLBooleanNode) node).right, buffer);
			}
		}
		
		
		return buffer;
		
	}
	
}
