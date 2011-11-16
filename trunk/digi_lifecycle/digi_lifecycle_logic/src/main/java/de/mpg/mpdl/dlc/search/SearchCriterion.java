package de.mpg.mpdl.dlc.search;

import java.util.List;


public class SearchCriterion {

	public enum Operator
	{
		AND, OR
	}
	
	public enum SearchType
	{
		FREE(new String[]{"escidoc.metadata"}), 
		AUTHOR(new String[]{"escidoc.namePart"}), 
		TITLE(new String[]{"escidoc.title"}), 
		PLACE(new String[]{"escidoc.placeTerm"}), 
		PUBLISHER(new String[]{"escidoc.publisher"}),
		YEAR(new String[]{"escidoc.dateIssued"}), 
		KEYWORD(new String[]{"escidoc.note"}), 
		ID(new String[]{"escidoc.identifier"}), 
		FULLTEXT(new String[]{"escidoc.fulltext"}),
		
		CONTEXT_ID(new String[]{"escidoc.context.objid"}),
		CONTENT_MODEL_ID(new String[]{"escidoc.content-model.objid"}),
		OBJECTTYPE(new String[]{"escidoc.objecttype"});
		
		private String[] indexNames;
		
		SearchType(String[] indexNames)
		{
			this.indexNames = indexNames;
		}

		public String[] getIndexNames() {
			return indexNames;
		}

		public void setIndexes(String[] indexNames) {
			this.indexNames = indexNames;
		}
	}
	
	private SearchType searchType;
	
	private String text;
	
	private Operator operator = Operator.AND;
	
	private int openBracket = 0;
	
	private int closeBracket = 0;

	
	public SearchCriterion(Operator op, SearchType searchType, String text)
	{
		this.operator = op;
		this.searchType = searchType;
		this.text = text;
	}
	
	public SearchCriterion(Operator op, SearchType searchType, String text, int openBracket, int closeBracket)
	{
		this.operator = op;
		this.searchType = searchType;
		this.text = text;
		this.openBracket = openBracket;
		this.setCloseBracket(closeBracket);
	}
	
	public SearchCriterion(SearchType searchType, String text)
	{
		this.operator = Operator.AND;
		this.searchType = searchType;
		this.text = text;
	}
	
	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public static String toCql(List<SearchCriterion> scList)
	{
		
		String cql = "";
		for(int i=0; i<scList.size(); i++)
		{
			SearchCriterion sc = scList.get(i);
			if(sc.getText()!= null && !sc.getText().trim().isEmpty())
			{
				
				
				if(i!=0)
				{
					cql += " " + sc.getOperator().name() + " ";
				}
				
				
				for(int j=0; j<sc.getOpenBracket();j++)
				{
					cql+="(";
				}
				
				if(sc.getSearchType().getIndexNames().length > 1)
				{
					cql += "(";
				}
				
				for(int j=0; j<sc.getSearchType().getIndexNames().length; j++)
				{
					String indexName = sc.getSearchType().getIndexNames()[j];
					if(j!=0)
					{
						cql += " OR ";
					}
					cql += indexName + "=" + sc.getText();
				}
				
				if(sc.getSearchType().getIndexNames().length > 1)
				{
					cql += ")";
				}
				

				for(int j=0; j<sc.getCloseBracket();j++)
				{
					cql+=")";
				}
			}
			
		}
		return cql;
	}

	public int getOpenBracket() {
		return openBracket;
	}

	public void setOpenBracket(int openBracket) {
		this.openBracket = openBracket;
	}

	public int getCloseBracket() {
		return closeBracket;
	}

	public void setCloseBracket(int closeBracket) {
		this.closeBracket = closeBracket;
	}

	
	
	
}
