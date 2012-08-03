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
import de.mpg.mpdl.dlc.search.Criterion.Operator;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;


@ManagedBean
@ViewScoped
@URLMapping(id = "advancedSearch", pattern = "/search", viewId = "/advancedSearch.xhtml")
public class AdvancedSearchBean {

	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);
	
	private List<SearchCriterion> searchCriterionList;	
	
	private String freeSearch ="";
	
	private SearchCriterion yearFrom = null;
	private SearchCriterion yearTo = null;
	
	private String fulltextSearch= "";

	private ContextServiceBean contextServiceBean = new ContextServiceBean();	
	private OrganizationalUnitServiceBean ouServiceBean =  new OrganizationalUnitServiceBean();	
	private SearchBean searchBean = new SearchBean();
	
	@ManagedProperty("#{advancedSearchResultBean}")
	private AdvancedSearchResultBean advancedSearchResultBean;
	
	//Variables for the Library/Context Search
	private List<SelectItem> allLibItems;
	private List<SelectItem> allConItems;
	//private ContextSearch contextSearchItem;
	private List<ContextSearch> contextScElements = new ArrayList<ContextSearch>();
	
	
	public AdvancedSearchBean()
	{
		this.searchCriterionList = new ArrayList<SearchCriterion>();
		this.searchCriterionList.add(new SearchCriterion(SearchType.FREE, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.AUTHOR, ""));
		this.searchCriterionList.add(new SearchCriterion(SearchType.TITLE, ""));
		
		this.yearFrom = new SearchCriterion(SearchType.YEAR, "");
		this.yearTo = new SearchCriterion(SearchType.YEAR, "");
		
		//this.contextSearchItem = new ContextSearch();
		
		this.init();
	}
	
	public void init()
	{
		//Set the libraries list
		List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();
		ouSelectItems.add(new SelectItem("",ApplicationBean.getResource("Label", "sc_allLib")));
		for(OrganizationalUnit ou : ouServiceBean.retrieveOUs())
		{
			ouSelectItems.add(new SelectItem(ou.getObjid(),ou.getProperties().getName()));
		}
		this.setAllLibItems(ouSelectItems);
		
		//Set the contexts list
		List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
		contextSelectItems.add(new SelectItem("",ApplicationBean.getResource("Label", "sc_allCon")));
		for(Context c : contextServiceBean.retrieveAllcontexts())
		{
			contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
		}
		this.setAllConItems(contextSelectItems);
		
		ContextSearch cs = new ContextSearch();
		refreshContextList(cs);
		this.contextScElements.add(cs);
	}
	
	public void refreshContextList(ContextSearch cs)
	
	{				
				
		//Prepare context list (depends on library selection)
		String ouId = cs.getOuId();
		List<SelectItem> contextSelectItems= new ArrayList<SelectItem>();
		contextSelectItems.add(new SelectItem("",ApplicationBean.getResource("Label", "sc_allCon")));			
		if (ouId != null && !ouId.equals(""))
		{
			for(Context c : contextServiceBean.retrieveOUContexts(ouId))
			{
				contextSelectItems.add(new SelectItem(c.getObjid(),c.getProperties().getName()));
			}
		}
		
		cs.setContextList(contextSelectItems);
		cs.setOuId(ouId);
	}
	
	public String startSearch()
	{		
		try {

			this.setCollectionSearch();
			this.setYearSearch();
			
			if (!this.freeSearch.equals(""))
			{
				//Set free search
				SearchCriterion scFree = new SearchCriterion(SearchType.FREE_AND_FULLTEXT, this.freeSearch);
				this.searchCriterionList.add(scFree);
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
	 * Set the collection search criterion
	 */
	private void setCollectionSearch()
	{		
		//Set context id
		boolean start = false;
		boolean end = false;
		for(int i = 0; i < contextScElements.size(); i ++)
		{
			SearchCriterion scCon;
			ContextSearch contextSearch = contextScElements.get(i);
			if (i == contextScElements.size()-1) end = true;
			
			//Means, a specific context was selected
			if (!contextSearch.getContextId().equals(""))
			{
				if (i==0 && !start)
				{
					scCon = new SearchCriterion(Operator.AND, SearchType.CONTEXT_ID, contextSearch.getContextId(),1,0);
					this.searchCriterionList.add(scCon);
					start = true;
				}
				if (i == contextScElements.size()-1 && end)
				{
					scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, contextSearch.getContextId(),0,1);
					this.searchCriterionList.add(scCon);
				}
				else
				{
					scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, contextSearch.getContextId());
					this.searchCriterionList.add(scCon);
				}
			}

			//Select all context of a ou
			else
			{
				if (contextSearch.getContextId().equals("") && contextSearch.getContextList().size() > 0)
				{
					//First elem is 'All collections'
					for (int y = 1; y < contextSearch.getContextList().size(); y ++)
					{
						String currentContextId = contextSearch.getContextList().get(y).getValue().toString();
						if (y == 1 && !start)
						{
							scCon = new SearchCriterion(Operator.AND, SearchType.CONTEXT_ID, currentContextId,1,0);
							this.searchCriterionList.add(scCon);
							start = true;
						}
						else
						{
							//last element
							if (y == contextSearch.getContextList().size()-1 && end)
							{
								scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, currentContextId,0,1);
								this.searchCriterionList.add(scCon);
							}
							else
							{
								scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, currentContextId);
								this.searchCriterionList.add(scCon);
							}
						}
					}			
				}
			}
		}

	}

	/**
	 * Set the year search criterion
	 */
	private void setYearSearch()
	{
		if (this.yearFrom != null)
		{
			if (this.yearTo == null)
			{
				this.searchCriterionList.add(this.yearFrom);
			}
			
			else
			{ 
				this.yearFrom.setConnector(">=");
				this.yearFrom.setOpenBracket(1);
				this.yearFrom.setCloseBracket(0);
				this.searchCriterionList.add(this.yearFrom);
				this.yearTo.setConnector("<=");
				this.yearTo.setOpenBracket(0);
				this.yearTo.setCloseBracket(1);
				this.searchCriterionList.add(yearTo);
			}
		}
		else
		{
			if (!this.yearTo.equals(""))
			{
				//TODO: messaging
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
		this.yearFrom = new SearchCriterion(SearchType.YEAR, "");
		this.yearTo = new SearchCriterion(SearchType.YEAR, "");
		
		this.contextScElements.clear();
		this.contextScElements.add(new ContextSearch());
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

	public String getFreeSearch() {
		return freeSearch;
	}

	public void setFreeSearch(String freeSearch) {
		this.freeSearch = freeSearch;
	}
	
	public SearchCriterion getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(SearchCriterion yearFrom) {
		this.yearFrom = yearFrom;
	}
	
	public SearchCriterion getYearTo() {
		return yearTo;
	}

	public void setYearTo(SearchCriterion yearTo) {
		this.yearTo = yearTo;
	}

	public String getFulltextSearch() {
		return fulltextSearch;
	}

	public void setFulltextSearch(String fulltextSearch) {
		this.fulltextSearch = fulltextSearch;
	}
	
	public void newContextScElement()
	{	
		ContextSearch cs = new ContextSearch();		
		refreshContextList(cs);
		
		this.contextScElements.add(cs);
	}
		
	public List<ContextSearch> getContextScElements() {
		return contextScElements;
	}

	public void setContextScElements(List<ContextSearch> contextScElements) {
		this.contextScElements = contextScElements;
	}
	public List<SelectItem> getAllLibItems() {
		return allLibItems;
	}

	public void setAllLibItems(List<SelectItem> allLibItems) {
		this.allLibItems = allLibItems;
	}

	public List<SelectItem> getAllConItems() {
		return allConItems;
	}

	public void setAllConItems(List<SelectItem> allConItems) {
		this.allConItems = allConItems;
	}
	
}
