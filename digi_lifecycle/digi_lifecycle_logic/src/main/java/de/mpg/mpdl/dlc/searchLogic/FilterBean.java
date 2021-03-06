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
		if(userHandle!=null)
		{
			client.setHandle(userHandle);
		}
		
		
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
		logger.info("Filter query:" + query);
		request.setQuery(query);
		if(limit >0)
		{
			request.setMaximumRecords(new NonNegativeInteger(String.valueOf(limit)));
		}
		if(offset >0)
		{
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
		
		//Clone list, so original objects are not touched
		List<SearchCriterion> clonedFcList = new ArrayList<SearchCriterion>();
		for(SearchCriterion sc : fcList)
		{
			clonedFcList.add(sc.clone());
		}
		
		List<SearchCriterion> fcStandard = getStandardFilterCriterions(volTypes, volStatus, publicStatus);
		//throw out empty entries
		List<SearchCriterion> listWithoutEmptyEntries = new ArrayList<SearchCriterion>();
		for(SearchCriterion fc : clonedFcList)
		{
			if(fc.getValue()!=null && !fc.getValue().isEmpty() && (fc.getSearchIndexes()!=null) || fc.getFilterIndexes() != null)
			{
				listWithoutEmptyEntries.add(fc);
			}
		}
		
		//Add a bracket around the given searchcriterions
		if(listWithoutEmptyEntries!=null && listWithoutEmptyEntries.size()>=1)
		{
			listWithoutEmptyEntries.get(0).setOpenBracket(listWithoutEmptyEntries.get(0).getOpenBracket()+1);
			listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).setCloseBracket(listWithoutEmptyEntries.get(listWithoutEmptyEntries.size()-1).getCloseBracket()+1);
			listWithoutEmptyEntries.get(0).setOperator(Operator.AND);
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
		

		if(volVersionStatus!=null)
		{
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
		}
		
		
		if(publicStatus!=null)
		{
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
		}
		
		
		return fcList;
	}
	

	



	
}
