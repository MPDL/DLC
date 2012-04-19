package de.mpg.mpdl.dlc.search;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.search.Criterion.Operator;
import de.mpg.mpdl.dlc.search.FilterCriterion.FilterParam;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;

public class FilterBean {
	private static Logger logger = Logger.getLogger(FilterBean.class);
	
	
	public VolumeSearchResult itemFilter(VolumeTypes[] volTypes, VolumeStatus[] volStatus, List<FilterCriterion> fcList, List<SortCriterion> sortList, int limit, int offset, String userHandle) throws Exception
	{
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		SearchRetrieveRequestType request = new SearchRetrieveRequestType();
		request.setQuery(getCompleteFilterQueries(volTypes, volStatus, fcList));
		
		SearchRetrieveResponse response = null;
		response = client.retrieveItems(request);
		
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
	
	
	private String getCompleteFilterQueries(VolumeTypes[] volTypes, VolumeStatus[] volStatus, List<FilterCriterion> fcList)
	{
		List<FilterCriterion> fcStandard = getStandardFilterCriterions(volTypes, volStatus);
		//throw out empty entries
		List<FilterCriterion> listWithoutEmptyEntries = new ArrayList<FilterCriterion>();
		for(FilterCriterion fc : fcList)
		{
			if(fc.getValue()!=null && !fc.getValue().isEmpty() && fc.getFilterParam()!=null)
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
		
		return FilterCriterion.toCql(fcStandard);
	}
	
	
	private static List<FilterCriterion> getStandardFilterCriterions(VolumeTypes[] volTypes, VolumeStatus[] volStatus)
	{
		List<FilterCriterion> fcList = new ArrayList<FilterCriterion>();
		

		
		for(int i=0; i<volTypes.length; i++)
		{
			VolumeTypes volType = volTypes[i];
			
			//first with "and" and opening bracket
			if(i==0)
			{
				FilterCriterion fcContentModel;
				if(volTypes.length >1)
					fcContentModel = new FilterCriterion(Operator.AND, FilterParam.CONTENT_MODEL_ID, volType.getContentModelId(), 1, 0);
				else
					fcContentModel = new FilterCriterion(Operator.AND, FilterParam.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 0);
					
				fcList.add(fcContentModel);
			}
			//last with "or" and closing brakcket
			else if(i==volTypes.length-1)
			{
				FilterCriterion fcContentModel = new FilterCriterion(Operator.OR, FilterParam.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 1);
				fcList.add(fcContentModel);
			}
			//others with "or" and without brackets
			else
			{
				FilterCriterion fcContentModel= new FilterCriterion(Operator.OR, FilterParam.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 0);
				fcList.add(fcContentModel);
			}
		}
		
		
		for(int i = 0; i<volStatus.length; i++)
		{
			VolumeStatus status = volStatus[i];
			if(i==0)
			{
				FilterCriterion fcStatus;
				if(volStatus.length >1)
					fcStatus = new FilterCriterion(Operator.AND, FilterParam.STATUS,status.getStatus(), 1, 0);
				else
					fcStatus = new FilterCriterion(Operator.AND, FilterParam.STATUS,status.getStatus(), 0, 0);
				
				fcList.add(fcStatus);
			}
			else if( i == volStatus.length -1)
			{
				FilterCriterion fcStatus = new FilterCriterion(Operator.OR, FilterParam.STATUS,status.getStatus(), 0, 1);
				fcList.add(fcStatus);
			}
			else
			{
				FilterCriterion fcStatus = new FilterCriterion(Operator.OR, FilterParam.STATUS,status.getStatus(), 0, 0);
				fcList.add(fcStatus);
			}
				
		}
		
		return fcList;
	}
	

	



	
}
