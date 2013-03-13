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
package de.mpg.mpdl.dlc.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortOrders;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

public abstract class SortableVolumePaginatorBean extends BasePaginatorBean<Volume> {

	
	//private List<SortCriterion> sortCriterionList; //= SortCriterion.getStandardSortCriteria();
	//private List<SortCriterion> sortCriterionFilterList = SortCriterion.getStandardFilterSortCriteria();

	
	public abstract List<SortCriterion> getSortCriterionList();

	
	
	/*
	public List<SortCriterion> getSortCriterionFilterList() {
		return sortCriterionFilterList ;
	}

	public void setSortCriterionFilterList(List<SortCriterion> sortCriterionList) {
		this.sortCriterionFilterList = sortCriterionList;
	}
	*/
	
	public abstract List<SelectItem> getSortIndicesMenu();
	
		
	
	
	public String changeSortCriteria()
	{
		setCurrentPageNumber(1);
		return getNavigationString();
	}
	
	public String changeSortOrder(SortCriterion sc)
	{
	
		if(SortOrders.ASCENDING.equals(sc.getSortOrder()))
		{
			sc.setSortOrder(SortOrders.DESCENDING);
		}
		else
		{
			sc.setSortOrder(SortOrders.ASCENDING);
		}
			
		return changeSortCriteria();
	}
	
	
	
	

	

	
}

