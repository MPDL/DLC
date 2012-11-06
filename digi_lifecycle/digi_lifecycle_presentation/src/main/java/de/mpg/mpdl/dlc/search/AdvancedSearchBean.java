package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.ContextServiceBean;
import de.mpg.mpdl.dlc.beans.OrganizationalUnitServiceBean;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.util.VolumeUtilBean;


@ManagedBean
@SessionScoped
@URLMapping(id = "advancedSearch", pattern = "/search", viewId = "/advancedSearch.xhtml")
public class AdvancedSearchBean {

	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);
	
	private List<SearchCriterion> searchCriterionList;	
	
	private String freeSearch ="";
	
	private SearchCriterion yearFrom = null;
	private SearchCriterion yearTo = null;
	
	private SearchCriterion fulltextSearch= null;
	
	private SearchCriterion cdcSearch= null;

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
		
		this.fulltextSearch = new SearchCriterion(SearchType.FULLTEXT, "");
		
		this.cdcSearch = new SearchCriterion(SearchType.CODICOLOGICAL, "");
		//this.contextSearchItem = new ContextSearch();
		
		this.init();
	}
	
	public void init()
	{
		//Set the libraries list
		List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();
		ouSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allLib")));
		for(OrganizationalUnit ou : ouServiceBean.retrieveOUs())
		{
			//Only add an ou if it has a context otherwise there is no collection which we can search in
			if (contextServiceBean.retrieveOUContexts(ou.getObjid()).size() > 0)
			{
				ouSelectItems.add(new SelectItem(ou.getObjid(),ou.getProperties().getName()));
			}
		}
		this.setAllLibItems(ouSelectItems);
		
		//Set the contexts list
		List<SelectItem> contextSelectItems = new ArrayList<SelectItem>();
		contextSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allCon")));
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
		contextSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allCon")));			
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
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

			scList.addAll(this.searchCriterionList);
			
			this.setCollectionSearch(scList);
			this.setYearSearch(scList);
			
			if (!this.freeSearch.trim().equals(""))
			{
				//Set free search
				SearchCriterion scFree = new SearchCriterion(SearchType.FREE, this.freeSearch);
				scList.add(scFree);
			}
			
			//Set fulltext search
			if(!this.fulltextSearch.getValue().trim().isEmpty())
			{
				
				scList.add(this.fulltextSearch);
			}
			
			//Set cdc search
			if(!this.cdcSearch.getValue().trim().isEmpty())
			{
				
				scList.add(this.cdcSearch);
			}
			
		
			advancedSearchResultBean.setSearchCriterionList(scList);
			String cql = searchBean.getAdvancedSearchCQL(scList);
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
	private void setCollectionSearch(List<SearchCriterion> scList)
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
				if(contextScElements.size()==1)
				{
					scCon = new SearchCriterion(Operator.AND, SearchType.CONTEXT_ID, contextSearch.getContextId(),1,1);
					scList.add(scCon);
				}
				else if (i==0 && !start)
				{
					scCon = new SearchCriterion(Operator.AND, SearchType.CONTEXT_ID, contextSearch.getContextId(),1,0);
					scList.add(scCon);
					start = true;
				}
				else if (i == contextScElements.size()-1 && end)
				{
					scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, contextSearch.getContextId(),0,1);
					scList.add(scCon);
				}
				else
				{
					scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, contextSearch.getContextId());
					scList.add(scCon);
				}
			}

			//Select all context of a ou
			else if(contextSearch.getOuId()!=null && !contextSearch.getOuId().equals(""))
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
							scList.add(scCon);
							start = true;
						}
						else
						{
							//last element
							if (y == contextSearch.getContextList().size()-1 && end)
							{
								scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, currentContextId,0,1);
								scList.add(scCon);
							}
							else
							{
								scCon = new SearchCriterion(Operator.OR, SearchType.CONTEXT_ID, currentContextId);
								scList.add(scCon);
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
	private void setYearSearch(List<SearchCriterion> scList) throws RuntimeException
	{
		if (!this.yearFrom.getValue().trim().isEmpty())
		{
			if (this.yearTo.getValue().trim().isEmpty())
			{
				scList.add(this.yearFrom);
			}
			
			else
			{ 
				this.yearFrom.setConnector(">=");
				this.yearFrom.setOpenBracket(1);
				this.yearFrom.setCloseBracket(0);
				scList.add(this.yearFrom);
				this.yearTo.setConnector("<=");
				this.yearTo.setOpenBracket(0);
				this.yearTo.setCloseBracket(1);
				scList.add(yearTo);
			}
		}
		else
		{
			if (!this.yearTo.getValue().trim().isEmpty())
			{
				MessageHelper.errorMessage("Please provide a value for \"year from\"");
				throw new RuntimeException("\"Year from\" not provided");
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

		this.fulltextSearch = new SearchCriterion(SearchType.FULLTEXT, "");
		this.yearFrom = new SearchCriterion(SearchType.YEAR, "");
		this.yearTo = new SearchCriterion(SearchType.YEAR, "");
		this.cdcSearch = new SearchCriterion(SearchType.CODICOLOGICAL, "");
		
		//Reset context
		this.contextScElements.clear();
		ContextSearch cs = new ContextSearch();
		refreshContextList(cs);
		this.contextScElements.add(cs);
		
		//Reset ou
		List<SelectItem> ouSelectItems = new ArrayList<SelectItem>();
		ouSelectItems.add(new SelectItem("",InternationalizationHelper.getLabel("sc_allLib")));
		for(OrganizationalUnit ou : ouServiceBean.retrieveOUs())
		{
			ouSelectItems.add(new SelectItem(ou.getObjid(),ou.getProperties().getName()));
		}
		this.setAllLibItems(ouSelectItems);
		
		
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

	public SearchCriterion getFulltextSearch() {
		return fulltextSearch;
	}

	public void setFulltextSearch(SearchCriterion fulltextSearch) {
		this.fulltextSearch = fulltextSearch;
	}

	public SearchCriterion getCdcSearch() {
		return cdcSearch;
	}

	public void setCdcSearch(SearchCriterion cdcSearch) {
		this.cdcSearch = cdcSearch;
	}
	
}
