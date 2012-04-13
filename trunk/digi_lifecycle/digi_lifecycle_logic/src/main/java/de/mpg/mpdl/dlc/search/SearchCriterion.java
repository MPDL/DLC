package de.mpg.mpdl.dlc.search;

import java.util.List;

import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLRelation;
import org.z3950.zing.cql.CQLTermNode;
import org.z3950.zing.cql.ModifierSet;


public class SearchCriterion {

	public enum Operator
	{
		AND, OR
	}
	
	public enum SearchType
	{
		FREE(new String[]{"escidoc.metadata"}), 
		AUTHOR(new String[]{"dlc.author"}), 
		TITLE(new String[]{"dlc.title"}), 
		PLACE(new String[]{"dlc.place"}), 
		PUBLISHER(new String[]{"dlc.publisher"}),
		YEAR(new String[]{"dlc.year"}), 
		KEYWORD(new String[]{"dlc.subject"}), 
		ID(new String[]{"dlc.identifier"}), 
		FULLTEXT(new String[]{"escidoc.fulltext"}),
		
		CONTEXT_ID(new String[]{"escidoc.context.objid"}),
		CONTENT_MODEL_ID(new String[]{"escidoc.content-model.objid"}),
		OBJECTTYPE(new String[]{"escidoc.objecttype"}),
	
		CREATEDBY(new String[]{"escidoc.created-by.name"});
		
		
		
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
					cql += indexName + "=\"" + sc.getText() + "\"";
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
	
	public static void main(String[] args)

	{
		// Building a parse-tree by hand
//		CQLNode n1 = new CQLTermNode("dc.author", new CQLRelation("="),
//					     "kernighan");
//		CQLNode n2 = new CQLTermNode("dc.title", new CQLRelation("all"),
//					     "elements style");
//		CQLNode root = new CQLAndNode(n1, n2, new ModifierSet("and"));
//		CQL
//		System.out.println(root.toCQL());

		// Parsing a CQL query
//		CQLParser parser = new CQLParser();
//		CQLNode root = parser.parse("title=dinosaur");
//		System.out.print(root.toXCQL(0));
//		System.out.println(root.toCQL());
//		System.out.println(root.toPQF(config));
		// ... where `config' specifies CQL-qualfier => Z-attr mapping
	}
	
	
	
}
