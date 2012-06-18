package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;


@ManagedBean
@ViewScoped
@URLMapping(id = "advancedSearch", pattern = "/search", viewId = "/advancedSearch.xhtml")
public class AdvancedSearchBean {

	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);
	
	private List<SearchCriterion> searchCriterionList;
	
	private List<SearchCriterion> contextList;

	private String selectedContextId="";
	private List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
	
	private String selectedOuId ="";
	private List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();
	
	private String freeSearch ="";
	
	private String yearFrom ="";
	private String yearTo ="";
	
	private String fulltextSearch= "";

	private ContextServiceBean contextServiceBean = new ContextServiceBean();	
	private OrganizationalUnitServiceBean ouServiceBean =  new OrganizationalUnitServiceBean();
	
	private SearchBean searchBean = new SearchBean();
	
	@ManagedProperty("#{advancedSearchResultBean}")
	private AdvancedSearchResultBean advancedSearchResultBean;
	
	public AdvancedSearchBean()
	{
		this.searchCriterionList = new ArrayList<SearchCriterion>();
		this.searchCriterionList.add(new SearchCriterion(SearchType.FREE, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.AUTHOR, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.TITLE, ""));
			
		this.contextList = new ArrayList<SearchCriterion>();
		this.contextList.add(new SearchCriterion(SearchType.CONTEXT_ID, ""));
		
		this.init();
	}
	
	public void init()
	{
		this.refreshLists();
	}
	
	public void refreshLists()
	{
		this.ouSelectItems.clear();
		this.ouSelectItems.add(new SelectItem("",ApplicationBean.getResource("Label", "sc_allLib")));
		for(OrganizationalUnit ou : ouServiceBean.retrieveOUs())
		{
			this.ouSelectItems.add(new SelectItem(ou.getObjid(),ou.getProperties().getName()));
		}
		
		this.contextSelectItems.clear();
		this.contextSelectItems.add(new SelectItem("",ApplicationBean.getResource("Label", "sc_allCon")));
		if (!selectedOuId.equals(""))
		{
			for(Context c : contextServiceBean.retrieveOUContexts(selectedOuId))
			{
				this.contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
			}
		}
		else
		{
			for(Context c : contextServiceBean.retrieveAllcontexts())
			{
				this.contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
			}
		}
	}
	
	public String startSearch()
	{		
		try {
			//Set context id
//			for(int i = 0; i < contextList.size(); i ++)
//			{
//				SearchCriterion scCon = new SearchCriterion(SearchType.CONTEXT_ID,contextList.get(i).getValue());
//				this.searchCriterionList.add(scCon);
//			}
			if (!this.selectedContextId.equals(""))
			{
				SearchCriterion scCon = new SearchCriterion(SearchType.CONTEXT_ID, this.selectedContextId);
				this.searchCriterionList.add(scCon);
			}
			//All context for a ou
			else
				if (this.selectedContextId.equals("") && this.contextSelectItems.size() > 0)
				{
					for (int i = 0; i < this.contextSelectItems.size(); i ++)
					{
						SearchCriterion scCon = new SearchCriterion(SearchType.CONTEXT_ID, this.contextSelectItems.get(i).getValue().toString());
						this.searchCriterionList.add(scCon);
					}			
				}
			
			
			if (!this.freeSearch.equals(""))
			{
				//Set free search
				SearchCriterion scFree = new SearchCriterion(SearchType.FREE, this.freeSearch);
				this.searchCriterionList.add(scFree);
			}
			
			//Set year search
			if (!this.yearFrom.equals("") || (!this.yearTo.equals("")))
			{
				this.setYearSearch();
			}
			
			//Set fulltext search
			if(!this.fulltextSearch.equals(""))
			{
				SearchCriterion ftSc = new SearchCriterion(SearchType.FULLTEXT, this.fulltextSearch);
				this.searchCriterionList.add(ftSc);
			}
			
			advancedSearchResultBean.setSearchCriterionList(searchCriterionList);
			String cql = searchBean.getAdvancedSearchCQL(searchCriterionList);
			advancedSearchResultBean.setCqlQuery(cql);
			return "pretty:searchResult";
		} catch (Exception e) {
			logger.error("Error while creating CQL query", e);
			return "";
		}
	}
	
	/**
	 * TODO, spec not sufficient!!!
	 */
	private void setYearSearch()
	{
		if (!this.yearFrom.equals(""))
		{
			if (this.yearTo.equals(""))
			{
				
			}
		}
	}

	
	public String resetSearch()
	{
		this.searchCriterionList = new ArrayList<SearchCriterion>();
		this.searchCriterionList.add(new SearchCriterion(SearchType.FREE, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.AUTHOR, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.TITLE, ""));
		
		this.freeSearch = "";
		this.fulltextSearch = "";
		this.yearFrom = "";
		this.yearTo ="";
		
		this.selectedOuId = "";
		this.selectedContextId = "";
		
		this.refreshLists();
		return"";
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

	public String getSelectedContextId() {
		return selectedContextId;
	}

	public void setSelectedContextId(String selectedContextId) {
		this.selectedContextId = selectedContextId;
	}
	
	public List<SelectItem> getContextSelectItems() {
		return contextSelectItems;
	}

	public void setContextSelectItems(List<SelectItem> contextSelectItems) {
		this.contextSelectItems = contextSelectItems;
	}
	

	public String getSelectedOuId() {
		return selectedOuId;
	}

	public void setSelectedOuId(String selectedOuId) {
		this.selectedOuId = selectedOuId;
	}

	public List<SelectItem> getOuSelectItems() {
		return ouSelectItems;
	}

	public void setOuSelectItems(List<SelectItem> ouSelectItems) {
		this.ouSelectItems = ouSelectItems;
	}
	

	public String getFreeSearch() {
		return freeSearch;
	}

	public void setFreeSearch(String freeSearch) {
		this.freeSearch = freeSearch;
	}
	
	public String getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(String yearFrom) {
		this.yearFrom = yearFrom;
	}

	public String getYearTo() {
		return yearTo;
	}

	public void setYearTo(String yearTo) {
		this.yearTo = yearTo;
	}
	

	public String getFulltextSearch() {
		return fulltextSearch;
	}

	public void setFulltextSearch(String fulltextSearch) {
		this.fulltextSearch = fulltextSearch;
	}
	
	
	public List<SearchCriterion> getContextList() {
		return contextList;
	}

	public void setContextList(List<SearchCriterion> contextList) {
		this.contextList = contextList;
	}
	
}
