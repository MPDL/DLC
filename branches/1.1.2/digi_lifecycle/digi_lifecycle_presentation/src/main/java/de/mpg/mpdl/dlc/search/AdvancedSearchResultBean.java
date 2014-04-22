/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.SessionBean;
import de.mpg.mpdl.dlc.beans.SortableVolumePaginatorBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean.VolumeStatus;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.util.InternationalizationHelper;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;

@ManagedBean
@SessionScoped
@URLMapping(id = "searchResult", viewId = "/advancedSearchResult.xhtml", pattern = "/searchresult")
public class AdvancedSearchResultBean extends SortableVolumePaginatorBean {
	
	private static Logger logger = Logger.getLogger(AdvancedSearchResultBean.class);
	
	private int totalNumberOfRecords;
	
	@URLQueryParameter("q")
	private String cqlQuery;
	
	
	private SearchBean searchBean = new SearchBean();
	
	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;
	
	private List<SearchCriterion> searchCriterionList;

	//private List<SortCriterion> sortCriterionList = SortCriterion.getStandardSortCriteria();	
	
	private CombinedSortCriterion selectedSortCriterion = CombinedSortCriterion.AUTHOR_TITLE_ASC;
	
	@ManagedProperty("#{internationalizationHelper}")
	private InternationalizationHelper internationalizationHelper;
	
	
	public AdvancedSearchResultBean()
	{
		
	}
	
	@Override
	public List<SortCriterion> getSortCriterionList()
	{
		return selectedSortCriterion.getScList();
	}

	
	@Override
	public List<SelectItem> getSortIndicesMenu() {
		List<SelectItem> scMenuList = new ArrayList<SelectItem>();
		scMenuList.add(new SelectItem(CombinedSortCriterion.AUTHOR_TITLE_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_author")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.AUTHOR_TITLE_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_author_desc")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.TITLE_YEAR_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_title")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.TITLE_YEAR_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_title_desc")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.YEAR_ASC.name(), InternationalizationHelper.getLabel("sort_criterion_year")));
		scMenuList.add(new SelectItem(CombinedSortCriterion.YEAR_DESC.name(), InternationalizationHelper.getLabel("sort_criterion_year_desc")));
		//scMenuList.add(new SelectItem(CombinedSortCriterion.NEWEST_DESC.name(), internationalizationHelper.getLabel("sort_criterion_newest")));
		return scMenuList;
	}
	
	
	@Override
	public List<Volume> retrieveList(int offset, int limit) throws Exception {
		VolumeSearchResult res = searchBean.searchByCql(cqlQuery, getSortCriterionList(), limit, offset, false);
		this.totalNumberOfRecords = res.getNumberOfRecords();

		return res.getVolumes();
	}
	
	
	@Override
	public void loadSubvolumes(Volume v) {
		
		try {
			VolumeServiceBean volServiceBean = new VolumeServiceBean();
			List<Volume> volList = new ArrayList<Volume>();
			volList.add(v);
			volServiceBean.loadVolumesForMultivolume(volList, null, false, null,null, false);
		} catch (Exception e) {
			logger.error("could not load volumes for multivolume", e);
		}
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() {
		// TODO Auto-generated method stub
		return "pretty:searchResult";
	}

	public String getCqlQuery() {
		return cqlQuery;
	}

	public void setCqlQuery(String query) 
	{
//		if (this.totalNumberOfRecords > 0) an empty search result list is also a result
//		{
			sessionBean.setLatestCql(cqlQuery);
//		}
		//this.setCurrentPageNumber(1);
		this.cqlQuery = query;
	}

	public List<SearchCriterion> getSearchCriterionList() {
		return searchCriterionList;
	}

	public void setSearchCriterionList(List<SearchCriterion> searchCriterionList) {
		this.searchCriterionList = searchCriterionList;
	}
	
	public String getFulltextSearchStringsAsQueryParam()
	{
		StringBuffer fulltextStrings = new StringBuffer();
		
		CQLParser cqlp = new CQLParser();
		try {
			CQLNode root = cqlp.parse(cqlQuery);
			createFulltextString(root, fulltextStrings);
			
		}catch (Exception e) {
			logger.error("Could not parse cql query for fulltext hits", e);
		}
		
		
		/*
		
		if(searchCriterionList!=null)
		{
			for(SearchCriterion sc : searchCriterionList)
			{
				if(SearchType.FULLTEXT.equals(sc.getSearchType()))
				{
					fulltextStrings.append(sc.getText() + ",");
				}
			}
		}
		
		*/
		return fulltextStrings.toString();
	}
	
	private StringBuffer createFulltextString(CQLNode node, StringBuffer buffer)
	{
		if(node!=null)
		{
			if(node instanceof CQLTermNode)
			{
				CQLTermNode termNode = (CQLTermNode) node;
				if(termNode.getIndex().equals("escidoc.fulltext"))
				buffer.append(termNode.getTerm()+",");
			}
			else if (node instanceof CQLBooleanNode)
			{
				createFulltextString(((CQLBooleanNode) node).left, buffer);
				createFulltextString(((CQLBooleanNode) node).right, buffer);
			}
		}
		
		
		return buffer;
		
	}

	public InternationalizationHelper getInternationalizationHelper() {
		return internationalizationHelper;
	}

	public void setInternationalizationHelper(InternationalizationHelper internationalizationHelper) {
		this.internationalizationHelper = internationalizationHelper;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public CombinedSortCriterion getSelectedSortCriterion() {
		return selectedSortCriterion;
	}

	public void setSelectedSortCriterion(CombinedSortCriterion selectedSortCriterion) {
		this.selectedSortCriterion = selectedSortCriterion;
	}


	
	
}
