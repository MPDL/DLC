/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.searchLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class SearchCriterion extends Criterion{

	
	
	public enum SearchType
	{
		FREE(new String[]{"escidoc.metadata"}, null), 
		FREE_AND_FULLTEXT(new String[]{"escidoc.metadata","escidoc.fulltext"}, null), 
		AUTHOR(new String[]{"dlc.author"}, new String[]{"/dlc/author"}), 
		TITLE(new String[]{"dlc.title"}, new String[]{"/dlc/title"}), 
		PLACE(new String[]{"dlc.place"}, null), 
		PUBLISHER(new String[]{"dlc.publisher"}, null),
		YEAR(new String[]{"dlc.year"}, null), 
		KEYWORD(new String[]{"dlc.subject"}, null), 
		ID(new String[]{"dlc.identifier"}, null), 
		FULLTEXT(new String[]{"escidoc.fulltext"}, null),
		CORPORATE(new String[]{"dlc.corporate"}, null),
		SHELFMARK(new String[]{"escidoc.physicalLocation"}, new String[] {"/md-records/md-record/mods/location/physicalLocation"}),
		CONTENT_CATEGORY(new String[]{"escidoc.component.content-category"}, new String[] {"/components/component/properties/content-category"}),
		
		CONTEXT_ID(new String[]{"escidoc.context.objid"}, new String[]{"/properties/context/id"}),
		CONTENT_MODEL_ID(new String[]{"escidoc.content-model.objid"}, new String[]{"/properties/content-model/id"}),
		OBJECTTYPE(new String[]{"escidoc.objecttype"}, null),
		OBJECT_ID(new String[]{"escidoc.objid"}, new String[]{"/id"}),
	
		CREATED_BY(new String[]{"escidoc.created-by.name"}, new String[]{"/properties/created-by/id"}),
		
		CODICOLOGICAL(new String[]{"dlc.cdc.metadata"}, null),
		CDC_OBJECT_TYPE(new String[]{"dlc.cdc.objectType"}, null),
		CDC_LEAF_MARKER(new String[]{"dlc.cdc.leafMarker"}, null),
		CDC_BINDING(new String[]{"dlc.cdc.binding"}, null),
		CDC_BOOK_COVER_DECORATION(new String[]{"dlc.cdc.bookCoverDecoration"}, null),
		CDC_TIPPED_IN(new String[]{"dlc.cdc.tippedIn"}, null),
		CDC_ENDPAPER(new String[]{"dlc.cdc.endpaper"}, null),
		CDC_MARGINALIA(new String[]{"dlc.cdc.marginalia"}, null),
		CDC_EDGE(new String[]{"dlc.cdc.edge"}, null),
		
		
		
		PUBLIC_STATUS(null, new String[]{"/properties/public-status"}),
		VERSION_STATUS(null, new String[]{"/properties/version/status"}),
		RELATION_ID(null, new String[]{"/relations/relation/id"});
		
		private String[] searchIndexNames;
		private String[] filterIndexNames;
		
		SearchType(String[] searchIndexNames, String[] filterIndexNames)
		{
			this.searchIndexNames = searchIndexNames;
			this.filterIndexNames = filterIndexNames;
		}

		public String[] getSearchIndexNames() {
			return searchIndexNames;
		}

		public void setSearchIndexNames(String[] searchIndexNames) {
			this.searchIndexNames = searchIndexNames;
		}

		public String[] getFilterIndexNames() {
			return filterIndexNames;
		}

		public void setFilterIndexNames(String[] filterIndexNames) {
			this.filterIndexNames = filterIndexNames;
		}

	

	}
	
	private SearchType searchType;


	public SearchCriterion()
	{
		this.operator = Operator.AND;
		//this.searchType = toBeCloned.getSearchType();
		
	}
	
	public SearchCriterion(SearchCriterion toBeCloned)
	{
		super(toBeCloned);
		this.searchType = toBeCloned.getSearchType();
		
	}
	
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
	
	
	@Override
	public SearchCriterion clone()
	{
		return new SearchCriterion(this);
	}
	
	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}
	
	
	public String[] getSearchIndexes()
	{
		if(getSearchType()!=null)
		{
			return getSearchType().getSearchIndexNames();
		}
		else return null;
				
	}
	
	public String[] getFilterIndexes()
	{
		if(getSearchType() != null)
		{
			return getSearchType().getFilterIndexNames();
		}
		else return null;
	}
	
	public static String toCql(List<SearchCriterion> cList, boolean filter ) {
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
					
					//Set connector to ">" if value equals * in order to search if this field exists or not
					if("*".equals(sc.getValue()))
					{
						sc.setConnector(">");
					}
					
					if(!filter)
					{
						cql += baseCqlBuilder(sc.getSearchIndexes(), sc.getValue(), sc.getConnector());
					}
					else
					{
						cql += baseCqlBuilder(sc.getSearchType().getFilterIndexNames(), sc.getValue(), sc.getConnector());
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
				
				cqlStringBuilder.append("\""+cqlIndexes[j]+"\"");
				
				
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
