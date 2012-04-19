package de.mpg.mpdl.dlc.search;

import java.util.List;


public class FilterCriterion extends Criterion{
	
	public enum FilterParam
	{
		CREATED_BY(new String[]{"/properties/created-by/id"}),
		CONTENT_MODEL_ID(new String[]{"/properties/content-model/id"}),
		CONTEXT_ID(new String[]{"/properties/context/id"}),
		STATUS(new String[]{"/properties/public-status"});
		
		private String[] params;
		
		private FilterParam(String[] params)
		{
			this.params = params;
		}
		public String[] getParams() {
			return params;
		}
		public void setParam(String[] params) {
			this.params = params;
		}
			
	}

	private FilterParam filterParam;
	
	
	public FilterCriterion(Operator op, FilterParam filterParam, String value)
	{
		this.operator = op;
		this.filterParam = filterParam;
		this.value = value;
	}
	
	public FilterCriterion(Operator op, FilterParam filterParam, String value, int openBracket, int closeBracket)
	{
		this.operator = op;
		this.filterParam = filterParam;
		this.value = value;
		this.openBracket = openBracket;
		this.closeBracket = closeBracket;
	}
	
	public FilterCriterion (FilterParam filterParam, String value)
	{
		this.operator = Operator.AND;
		this.filterParam = filterParam;
		this.value = value;
		
	}

	public FilterParam getFilterParam() {
		return filterParam;
	}

	public void setFilterParam(FilterParam filterParam) {
		this.filterParam = filterParam;
	}

	public static String toCql(List<FilterCriterion> fcList) {
		{
			  
			String cql = "";  
			for(int i=0; i<fcList.size(); i++)
			{
				FilterCriterion fc = fcList.get(i);
				if(fc.getValue()!= null && !fc.getValue().trim().isEmpty())
				{
					
					
					if(i!=0)
					{
						cql += " " + fc.getOperator().name() + " ";
					}
					
					
					for(int j=0; j<fc.getOpenBracket();j++)
					{
						cql+="(";
					}
					
					if(fc.getFilterParam().getParams().length > 1)
					{
						cql += "(";
					}
					
					for(int j=0; j<fc.getFilterParam().getParams().length; j++)
					{
						String indexName = fc.getFilterParam().getParams()[j];
						if(j!=0)
						{
							cql += " OR ";
						}
						cql += "\"" + indexName + "\"=\"" + fc.getValue()+"\"";
					}
					
					if(fc.getFilterParam().getParams().length > 1)
					{
						cql += ")";
					}
					

					for(int j=0; j<fc.getCloseBracket();j++)
					{
						cql+=")";
					}
				}
				
			}
			
			
			System.out.println(cql);
		
			return cql;
		}
	}

	
	
	
}
