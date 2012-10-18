package de.mpg.mpdl.dlc.searchLogic;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;

import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;

public class FilterBean {
	private static Logger logger = Logger.getLogger(FilterBean.class);
	
	
	public VolumeSearchResult itemFilter(VolumeTypes[] volTypes, VolumeStatus[] volStatus, VolumeStatus[] publicStatus, List<SearchCriterion> fcList, List<SortCriterion> sortList, int limit, int offset, String userHandle) throws Exception
	{
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		SearchRetrieveRequestType request = new SearchRetrieveRequestType();
		String query = getCompleteFilterQueries(volTypes, volStatus, publicStatus, fcList);
		
		if(sortList !=null && sortList.size()>0)
		{
			query += " sortby";
			for(SortCriterion sc : sortList)
			{
				query += " \"" + sc.getSortIndex().getFilterIndexName() + "\"" + sc.getSortOrder().getOrderName(); 
			}
		}
		System.out.println("Filter query:" + query);
		request.setQuery(query);
		if(limit >0 && offset >0)
		{
		request.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));
		request.setStartRecord(new PositiveInteger(String.valueOf(offset)));
		
		}
		SearchRetrieveResponse response  = client.retrieveItems(request);
		
		List<Volume> volumeResult = new ArrayList<Volume>();
		
		for(SearchResultRecord rec : response.getRecords())
		{
			Item item = (Item) rec.getRecordData().getContent();
			Volume vol = VolumeServiceBean.createVolumeFromItem(item, null);
			vol.setSearchResultHighlight(rec.getRecordData().getHighlight());
			volumeResult.add(vol);
		}
		return new VolumeSearchResult(volumeResult, response.getNumberOfRecords());

	}
	
	
	private String getCompleteFilterQueries(VolumeTypes[] volTypes, VolumeStatus[] volStatus, VolumeStatus[] publicStatus, List<SearchCriterion> fcList)
	{
		List<SearchCriterion> fcStandard = getStandardFilterCriterions(volTypes, volStatus, publicStatus);
		//throw out empty entries
		List<SearchCriterion> listWithoutEmptyEntries = new ArrayList<SearchCriterion>();
		for(SearchCriterion fc : fcList)
		{
			if(fc.getValue()!=null && !fc.getValue().isEmpty() && fc.getSearchType()!=null)
			{
				listWithoutEmptyEntries.add(fc);
			}
		}
		
		//Add a bracket around the given searchcriterions
		if(listWithoutEmptyEntries!=null && listWithoutEmptyEntries.size()>=1)
		{
			listWithoutEmptyEntries.get(0).setOpenBracket(listWithoutEmptyEntries.get(0).getOpenBracket()+1);
			listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).setCloseBracket(listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).getCloseBracket()+1);
		}
		
		fcStandard.addAll(listWithoutEmptyEntries);
		
		return SearchCriterion.toCql(fcStandard, true);
	}
	
	
	private static List<SearchCriterion> getStandardFilterCriterions(VolumeTypes[] volTypes, VolumeStatus[] volVersionStatus, VolumeStatus[] publicStatus)
	{
		List<SearchCriterion> fcList = new ArrayList<SearchCriterion>();
		

		
		for(int i=0; i<volTypes.length; i++)
		{
			VolumeTypes volType = volTypes[i];
			
			//first with "and" and opening bracket
			if(i==0)
			{
				SearchCriterion fcContentModel;
				if(volTypes.length >1)
					fcContentModel = new SearchCriterion(Operator.AND, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 1, 0);
				else
					fcContentModel = new SearchCriterion(Operator.AND, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 0);
					
				fcList.add(fcContentModel);
			}
			//last with "or" and closing brakcket
			else if(i==volTypes.length-1)
			{
				SearchCriterion fcContentModel = new SearchCriterion(Operator.OR, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 1);
				fcList.add(fcContentModel);
			}
			//others with "or" and without brackets
			else
			{
				SearchCriterion fcContentModel= new SearchCriterion(Operator.OR, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 0);
				fcList.add(fcContentModel);
			}
		}
		
		
		for(int i = 0; i<volVersionStatus.length; i++)
		{
			VolumeStatus status = volVersionStatus[i];
			if(i==0)
			{
				SearchCriterion fcStatus;
				if(volVersionStatus.length >1)
					fcStatus = new SearchCriterion(Operator.AND, SearchType.VERSION_STATUS,status.getStatus(), 1, 0);
				else
					fcStatus = new SearchCriterion(Operator.AND, SearchType.VERSION_STATUS,status.getStatus(), 0, 0);
				
				fcList.add(fcStatus);
			}
			else if( i == volVersionStatus.length -1)
			{
				SearchCriterion fcStatus = new SearchCriterion(Operator.OR, SearchType.VERSION_STATUS,status.getStatus(), 0, 1);
				fcList.add(fcStatus);
			}
			else
			{
				SearchCriterion fcStatus = new SearchCriterion(Operator.OR, SearchType.VERSION_STATUS,status.getStatus(), 0, 0);
				fcList.add(fcStatus);
			}
				
		}
		
		for(int i = 0; i<publicStatus.length; i++)
		{
			VolumeStatus status = publicStatus[i];
			if(i==0)
			{
				SearchCriterion fcStatus;
				if(publicStatus.length >1)
					fcStatus = new SearchCriterion(Operator.AND, SearchType.PUBLIC_STATUS,status.getStatus(), 1, 0);
				else
					fcStatus = new SearchCriterion(Operator.AND, SearchType.PUBLIC_STATUS,status.getStatus(), 0, 0);
				
				fcList.add(fcStatus);
			}
			else if( i == publicStatus.length -1)
			{
				SearchCriterion fcStatus = new SearchCriterion(Operator.OR, SearchType.PUBLIC_STATUS,status.getStatus(), 0, 1);
				fcList.add(fcStatus);
			}
			else
			{
				SearchCriterion fcStatus = new SearchCriterion(Operator.OR, SearchType.PUBLIC_STATUS,status.getStatus(), 0, 0);
				fcList.add(fcStatus);
			}
				
		}
		
		return fcList;
	}
	

	



	
}
