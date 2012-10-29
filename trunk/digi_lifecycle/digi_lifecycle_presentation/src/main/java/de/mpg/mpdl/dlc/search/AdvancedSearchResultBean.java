package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

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
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
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
	
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	private List<SearchCriterion> searchCriterionList;

	//private List<SortCriterion> sortCriterionList = SortCriterion.getStandardSortCriteria();	
	
	private CombinedSortCriterion selectedSortCriterion = CombinedSortCriterion.AUTHOR_TITLE_ASC;
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	
	public AdvancedSearchResultBean()
	{
		
	}
	
	@Override
	public List<SortCriterion> getSortCriterionList()
	{
		return selectedSortCriterion.getScList();
	}

	
	@Override
	public List<SelectItem> getSortIndicesMenu() {
		List<SelectItem> scMenuList = new ArrayList<SelectItem>();
		scMenuList.add(new SelectItem(CombinedSortCriterion.AUTHOR_TITLE_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_author")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.TITLE_YEAR_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_title")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.YEAR_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_year")));
		//scMenuList.add(new SelectItem(CombinedSortCriterion.NEWEST_DESC.name(), internationalizationHelper.getLabel("sort_criterion_newest")));
		return scMenuList;
	}
	
	
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
			this.getSessionBean().setLatestCql(query);
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

	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}

	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public CombinedSortCriterion getSelectedSortCriterion() {
		return selectedSortCriterion;
	}

	public void setSelectedSortCriterion(CombinedSortCriterion selectedSortCriterion) {
		this.selectedSortCriterion = selectedSortCriterion;
	}


	
	
}
