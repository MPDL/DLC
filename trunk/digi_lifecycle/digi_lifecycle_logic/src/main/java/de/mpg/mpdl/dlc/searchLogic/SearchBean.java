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

import java.net.URL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;

import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeTypes;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;


public class SearchBean {
	
	private static Logger logger = Logger.getLogger(SearchBean.class);
	
	public static String dlcIndexName = "dlc_index";
	
	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	
	private static List<SearchCriterion> getStandardFilterCriterions(VolumeTypes[] volTypes)
	{
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		
		if(volTypes != null)
		{
			for(int i=0; i<volTypes.length; i++)
			{
				VolumeTypes volType = volTypes[i];
				
				//first with "and" and opening bracket
				if(i==0)
				{
					SearchCriterion scContentModel;
					if(volTypes.length >1)
						scContentModel = new SearchCriterion(Operator.AND, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 2, 0);
					else
						scContentModel = new SearchCriterion(Operator.AND, SearchType.CONTENT_MODEL_ID, volType.getContentModelId(), 1, 0);
						
					scList.add(scContentModel);
				}
				//last with "or" and closing bracket
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
		}
		else
		{
			SearchCriterion itemCriterion = new SearchCriterion(Operator.AND, SearchType.OBJECTTYPE, "item", 1, 1);
			scList.add(itemCriterion);
		}
		
		
		
		
		
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
	/*
	public VolumeSearchResult quickSearchVolumes(String query, int limit, int offset) throws Exception
	{
		
		VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.MULTIVOLUME, VolumeTypes.VOLUME};
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		SearchCriterion scFree = new SearchCriterion(Operator.AND, SearchType.FREE, query);
		scList.add(scFree);

		return search(volTypes, scList, SortCriterion.getStandardSortCriteria(), limit, offset);
	}
	*/
	/**
	 * Searches for an advanced query string in all metadata of monographs and multivolumes
	 * @param query
	 * @param limit
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public VolumeSearchResult advancedSearchVolumes(List<SearchCriterion> scList, List<SortCriterion> sortList, int limit, int offset) throws Exception
	{
		VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.MULTIVOLUME};
		return search(volTypes, scList, sortList, limit, offset);
	}
	
	
	public String getAdvancedSearchCQL(List<SearchCriterion> scList) throws Exception
	{
		//VolumeTypes[] volTypes = new VolumeTypes[]{VolumeTypes.MONOGRAPH, VolumeTypes.MULTIVOLUME, VolumeTypes.VOLUME};
		String cql =  SearchCriterion.toCql(getCompleteSearchCriterions(null, scList), false);
		return cql;
	}
	
	
	private List<SearchCriterion> getCompleteSearchCriterions(VolumeTypes[] volTypes, List<SearchCriterion> scList)
	{
		
		//Clone list, so original objects are not touched
		List<SearchCriterion> clonedScList = new ArrayList<SearchCriterion>();
		for(SearchCriterion sc : scList)
		{
			clonedScList.add(sc.clone());
		}
		
		
		List<SearchCriterion> scListStandard = getStandardFilterCriterions(volTypes);
		
		//throw out empty entries
		List<SearchCriterion> listWithoutEmptyEntries = new ArrayList<SearchCriterion>();
		for(SearchCriterion sc : clonedScList)
		{
			if(sc.getValue()!=null && !sc.getValue().isEmpty() && sc.getSearchIndexes()!=null)
			{
				listWithoutEmptyEntries.add(sc);
			}
		}
		
		//Add a bracket around the given searchcriterions
		if(listWithoutEmptyEntries!=null && listWithoutEmptyEntries.size()>=1)
		{
			listWithoutEmptyEntries.get(0).setOpenBracket(listWithoutEmptyEntries.get(0).getOpenBracket()+1);
			listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).setCloseBracket(listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).getCloseBracket()+1);
			//set first operator always to "AND"
			listWithoutEmptyEntries.get(0).setOperator(Operator.AND);
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
	public VolumeSearchResult search(VolumeTypes[] volTypes, List<SearchCriterion> scList, List<SortCriterion> sortList, int limit, int offset) throws Exception
	{
		boolean loadVolumeIntoMultivolumes = false;
		for(VolumeTypes volType : volTypes)
		{
			if(VolumeTypes.MULTIVOLUME.equals(volType))
			{
				loadVolumeIntoMultivolumes = true;
				break;
			}
		}
		
		return search(getCompleteSearchCriterions(volTypes, scList), sortList, limit, offset, dlcIndexName, loadVolumeIntoMultivolumes, null);
	}
	
	private VolumeSearchResult search(List<SearchCriterion> scList, List<SortCriterion> sortList, int limit, int offset, String index, boolean loadVolumesIntoMultivolumes, String userHandle) throws Exception
	{  
		
		//SearchCriterion itemCriterion = new SearchCriterion(SearchType.OBJECTTYPE, "item");
		//scList.add(0, itemCriterion);
		String cqlQuery = SearchCriterion.toCql(scList, false);
		
		return searchByCql(cqlQuery, sortList, limit, offset, index, loadVolumesIntoMultivolumes, userHandle);
	}
	
	
	
	public VolumeSearchResult searchByCql(String cql, List<SortCriterion> sortList, int limit, int offset, boolean loadVolumesIntoMultivolumes) throws Exception
	{
		return searchByCql(cql, sortList, limit, offset, dlcIndexName, loadVolumesIntoMultivolumes, null);
	}
	
	public VolumeSearchResult searchByCql(String cql, List<SortCriterion> sortList, int limit, int offset, String index, boolean loadVolumesIntoMultivolumes, String userHandle) throws Exception
	{
		long start = System.currentTimeMillis();
		
		
		if(sortList!=null && sortList.size()>0)
		{
			cql += " sortby";
			for(SortCriterion sc : sortList)
			{
				cql += " " + sc.getSortIndex().getSearchIndexName() + " " + sc.getSortOrder().getOrderName();
			}
		}
		
		
		
		SearchHandlerClient shc = new SearchHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));

		logger.info("Search Query: " + cql);
		
		SearchRetrieveResponse resp = shc.search(cql, offset, limit, null, index);
		List<Volume> volumeResult = new ArrayList<Volume>();		

		for(SearchResultRecord rec : resp.getRecords())
		{
			
			Item item = (Item)rec.getRecordData().getContent();

			Volume vol = VolumeServiceBean.createVolumeFromItem(item, null);
			vol.setSearchResultHighlight(rec.getRecordData().getHighlight());	
			volumeResult.add(vol);
		} 
		
		//Add volumes to multivolume
		
		volServiceBean.loadVolumesForMultivolume(volumeResult, null, false, null, null, !loadVolumesIntoMultivolumes);
		

		//long time = System.currentTimeMillis() - start;
		//System.out.println("Time search: " + time );

		return new VolumeSearchResult(volumeResult, resp.getNumberOfRecords());
	}
	
	
	
	


}
