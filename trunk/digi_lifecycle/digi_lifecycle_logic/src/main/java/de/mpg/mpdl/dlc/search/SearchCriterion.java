package de.mpg.mpdl.dlc.search;

import java.util.List;


import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLRelation;
import org.z3950.zing.cql.CQLTermNode;
import org.z3950.zing.cql.ModifierSet;

import de.mpg.mpdl.dlc.search.Criterion.Operator;



public class SearchCriterion extends Criterion{

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
		
		//TODO: structmd title and author
		
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
		};

	}
	
	private SearchType searchType;


	
	public SearchCriterion(Operator op, SearchType searchType, String value) 
	{
		this.operator = op;
		this.searchType = searchType;
		this.value = value;
	}
	
	public SearchCriterion(Operator op, SearchType searchType, String value, int openBracket, int closeBracket)
	{
		this.operator = op;
		this.searchType = searchType;
		this.value = value;
		this.openBracket = openBracket;
		this.setCloseBracket(closeBracket);
	}
	
	public SearchCriterion(SearchType searchType, String value)
	{
		this.operator = Operator.AND;
		this.searchType = searchType;
		this.value = value;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}
	public static String toCql(List<SearchCriterion> cList) {
		{
			  
			String cql = "";  
			for(int i=0; i<cList.size(); i++)
			{
				SearchCriterion sc = cList.get(i);
				if(sc.getValue()!= null && !sc.getValue().trim().isEmpty())
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
						cql += indexName + "=\"" + sc.getValue() + "\"";
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
