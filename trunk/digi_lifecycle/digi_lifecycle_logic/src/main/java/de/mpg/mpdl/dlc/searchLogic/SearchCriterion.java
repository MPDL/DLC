package de.mpg.mpdl.dlc.searchLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SearchCriterion extends Criterion{

	public enum SearchType
	{
		FREE(new String[]{"escidoc.metadata"}), 
		FREE_AND_FULLTEXT(new String[]{"escidoc.metadata","escidoc.fulltext"}), 
		AUTHOR(new String[]{"dlc.author"}), 
		TITLE(new String[]{"dlc.title"}), 
		PLACE(new String[]{"dlc.place"}), 
		PUBLISHER(new String[]{"dlc.publisher"}),
		YEAR(new String[]{"dlc.year"}), 
		KEYWORD(new String[]{"dlc.subject"}), 
		ID(new String[]{"dlc.identifier"}), 
		FULLTEXT(new String[]{"escidoc.fulltext"}),
		CORPORATE(new String[]{"dlc.corporate"}),
		SHELFMARK(new String[]{"dlc.shelfmark"}), //TODO check
		
		//TODO: structmd title and author
		
		CONTEXT_ID(new String[]{"escidoc.context.objid"}),
		CONTENT_MODEL_ID(new String[]{"escidoc.content-model.objid"}),
		OBJECTTYPE(new String[]{"escidoc.objecttype"}),
	
		CREATEDBY(new String[]{"escidoc.created-by.name"}),
		
		CODICOLOGICAL(new String[]{"dlc.cdc.metadata"});
		
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

	public SearchCriterion(String con, SearchType searchType, String value, int openBracket, int closeBracket) 
	{
		this.connector = con;
		this.searchType = searchType;
		this.value = value;
		this.openBracket = openBracket;
		this.setCloseBracket(closeBracket);
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
					
					
					/*
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
						cql += indexName + sc.getConnector() + "\"" + sc.getValue() + "\"";
					}
					
					if(sc.getSearchType().getIndexNames().length > 1)
					{
						cql += ")";
					}
					*/
					
					cql += baseCqlBuilder(sc.getSearchType().getIndexNames(), sc.getValue(), sc.getConnector());

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




	/**
	 * Creates a cql string out of one or several search indexes and an search string. The search string is splitted into single words, except they are in quotes.
	 * The special characters of the search string parts are escaped.
	 * 
	 * Example:
	 * cqlIndexes={escidoc.title, escidoc.fulltext}
	 * searchString = book "john grisham"
	 * 
	 * Resulting cql string:
	 * escidoc.title=("book" and "john grisham") or escioc.fulltext=("book" and "john grisham")
	 * 
	 * @param cqlIndexes
	 * @param searchString
	 * @return the cql string or null, if no search string or indexes are given
	 */
	protected static String baseCqlBuilder(String[] cqlIndexes, String searchString, String connector)
	{

		if(searchString!=null && !searchString.trim().isEmpty())
		{

			//split the search string into single words, except if they are in quotes
			List<String> splittedSearchStrings = new ArrayList<String>();
			
			Pattern pattern = Pattern.compile("(?<=\\s|^)\"(.*?)\"(?=\\s|$)|(\\S+)");
			Matcher m = pattern.matcher(searchString);
			
			while(m.find())
			{
				String subSearchString = m.group();
				
				if(subSearchString!=null && !subSearchString.trim().isEmpty())
				{
					subSearchString = subSearchString.trim();
					
					//Remove quotes at beginning and end
					if(subSearchString.startsWith("\""))
					{
						subSearchString = subSearchString.substring(1, subSearchString.length());
					}
					
					if(subSearchString.endsWith("\""))
					{
						subSearchString = subSearchString.substring(0, subSearchString.length()-1);
					}
				}
				if(!subSearchString.trim().isEmpty())
				{
					splittedSearchStrings.add(subSearchString.trim());
				}
				
				
			}
			
			
			StringBuilder cqlStringBuilder = new StringBuilder();

			if(cqlIndexes.length > 1)
			{
				cqlStringBuilder.append("(");
			}
			
			for(int j=0; j< cqlIndexes.length; j++)
			{
				cqlStringBuilder.append(cqlIndexes[j]);
				cqlStringBuilder.append(connector);
				
				if(splittedSearchStrings.size()>1)
				{
					cqlStringBuilder.append("(");
				}
				
				for(int i =0; i<splittedSearchStrings.size(); i++)
				{
					String subSearchString = splittedSearchStrings.get(i);
					cqlStringBuilder.append("\"");
					cqlStringBuilder.append(escapeForCql(subSearchString));
					cqlStringBuilder.append("\"");
					
					if(splittedSearchStrings.size() > i+1)
					{
						cqlStringBuilder.append(" and ");
					}
					
				}
				if(splittedSearchStrings.size()>1)
				{
					cqlStringBuilder.append(")");
				}

				
				
				if(cqlIndexes.length > j+1)
				{
					cqlStringBuilder.append(" or ");
				}
			}
			
			if(cqlIndexes.length > 1)
			{
				cqlStringBuilder.append(")");
			}

			return cqlStringBuilder.toString();
		}
		
		return null;
		
		
	}
	
	protected static String escapeForCql(String escapeMe) {
	  	String result = escapeMe.replace( "<", "\\<" );
	  	result = result.replace( ">", "\\>" );
	  	result = result.replace( "+", "\\+" );
	  	result = result.replace( "-", "\\-" );
	  	result = result.replace( "&", "\\&" );
	  	result = result.replace( "%", "\\%" );
	  	result = result.replace( "|", "\\|" );
	  	result = result.replace( "(", "\\(" );
	  	result = result.replace( ")", "\\)" );
	  	result = result.replace( "[", "\\[" );
	  	result = result.replace( "]", "\\]" );
	  	result = result.replace( "^", "\\^" );
	  	result = result.replace( "~", "\\~" );
	  	result = result.replace( "!", "\\!" );
	  	result = result.replace( "{", "\\{" );
	  	result = result.replace( "}", "\\}" );
		result = result.replace( "\"", "\\\"" );
	  	return result;
	  }




	
	
	
}
