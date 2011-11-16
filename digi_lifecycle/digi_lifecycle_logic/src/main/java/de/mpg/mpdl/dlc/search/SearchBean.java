package de.mpg.mpdl.dlc.search;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.search.SearchCriterion.Operator;
import de.mpg.mpdl.dlc.search.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;

@Stateless
public class SearchBean {
	
	private static Logger logger = Logger.getLogger(SearchBean.class);
	
	private static String dlcIndexName = "dlc_index";
	
	
	
	
	
	
	private static List<SearchCriterion> getStandardFilterCriterions(VolumeTypes[] volTypes)
	{
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		
		
		for(int i=0; i<volTypes.length; i++)
		{
			VolumeTypes volType = volTypes[i];
			
			//first with "and" and opening bracket
			if(i==0)
			{
				SearchCriterion scContentModel = new SearchCriterion(Operator.AND, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 2, 0);
				scList.add(scContentModel);
			}
			//last with "or" and closing brakcket
			else if(i==volTypes.length-1)
			{
				SearchCriterion scContentModel = new SearchCriterion(Operator.OR, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 1);
				scList.add(scContentModel);
			}
			//others with "or" and without brackets
			else
			{
				SearchCriterion scContentModel = new SearchCriterion(Operator.OR, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 0, 0);
				scList.add(scContentModel);
			}
			
			
			
		}
		
		SearchCriterion itemCriterion = new SearchCriterion(Operator.AND, SearchType.OBJECTTYPE, "item", 0, 1);
		scList.add(itemCriterion);
		
		
		return scList;
	}
	
	
	/**
	 * Searches for an arbitrary string in all metadata of monographs and multivolumes
	 * @param query
	 * @param limit
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public VolumeSearchResult quickSearchVolumes(String query, int limit, int offset) throws Exception
	{
		
		VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.MULTIVOLUME};
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		SearchCriterion scFree = new SearchCriterion(Operator.AND, SearchType.FREE, query);
		scList.add(scFree);

		return search(volTypes, scList, limit, offset);
	}
	
	/**
	 * Searches for an advanced query string in all metadata of monographs and multivolumes
	 * @param query
	 * @param limit
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public VolumeSearchResult advancedSearchVolumes(List<SearchCriterion> scList, int limit, int offset) throws Exception
	{
		VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.MULTIVOLUME};
		
		return search(volTypes, scList, limit, offset);
	}
	
	
	public String getAdvancedSearchCQL(List<SearchCriterion> scList) throws Exception
	{
		VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.MULTIVOLUME};
		String cql =  SearchCriterion.toCql(getCompleteSearchCriterions(volTypes, scList));
		return cql;
	}
	
	
	private List<SearchCriterion> getCompleteSearchCriterions(VolumeTypes[] volTypes, List<SearchCriterion> scList)
	{
		List<SearchCriterion> scListStandard = getStandardFilterCriterions(volTypes);
		
		//throw out empty entries
		List<SearchCriterion> listWithoutEmptyEntries = new ArrayList<SearchCriterion>();
		for(SearchCriterion sc : scList)
		{
			if(sc.getText()!=null && !sc.getText().isEmpty() && sc.getSearchType()!=null)
			{
				listWithoutEmptyEntries.add(sc);
			}
		}
		
		//Add a bracket around the given searchcriterions
		if(listWithoutEmptyEntries!=null && listWithoutEmptyEntries.size()>=1)
		{
			listWithoutEmptyEntries.get(0).setOpenBracket(listWithoutEmptyEntries.get(0).getOpenBracket()+1);
			listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).setCloseBracket(listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).getCloseBracket()+1);
		}
		
		scListStandard.addAll(listWithoutEmptyEntries);
		return scListStandard;
		
	}
	
	/**
	 * Searches in all items, content model can be defined in volume types
	 * @param scList
	 * @param limit
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public VolumeSearchResult search(VolumeTypes[] volTypes, List<SearchCriterion> scList, int limit, int offset) throws Exception
	{
		
		return search(getCompleteSearchCriterions(volTypes, scList), limit, offset, dlcIndexName, null);
	}
	
	private VolumeSearchResult search(List<SearchCriterion> scList, int limit, int offset, String index, String userHandle) throws Exception
	{
		
		//SearchCriterion itemCriterion = new SearchCriterion(SearchType.OBJECTTYPE, "item");
		//scList.add(0, itemCriterion);
		String cqlQuery = SearchCriterion.toCql(scList);
		return searchByCql(cqlQuery, limit, offset, index, userHandle);
	}
	
	
	
	public VolumeSearchResult searchByCql(String cql, int limit, int offset) throws Exception
	{
		return searchByCql(cql, limit, offset, dlcIndexName, null);
	}
	
	public VolumeSearchResult searchByCql(String cql, int limit, int offset, String index, String userHandle) throws Exception
	{
		SearchHandlerClient shc = new SearchHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));

		logger.info("Search Query: " + cql);
		
		SearchRetrieveResponse resp = shc.search(cql, offset, limit, null, index);
		
		List<Volume> volumeResult = new ArrayList<Volume>();
		
		for(SearchResultRecord rec : resp.getRecords())
		{
			Item item = (Item)rec.getRecordData().getContent();
			volumeResult.add(VolumeServiceBean.createVolumeFromItem(item, null));
		}
		return new VolumeSearchResult(volumeResult, resp.getNumberOfRecords());
	}

}
