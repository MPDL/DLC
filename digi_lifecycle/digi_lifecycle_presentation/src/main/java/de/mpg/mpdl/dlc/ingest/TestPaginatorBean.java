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
package de.mpg.mpdl.dlc.ingest;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.mpg.mpdl.jsf.components.paginator.BasePaginatorBean;

@ManagedBean
@ViewScoped
public class TestPaginatorBean extends BasePaginatorBean<String> {

	private List<String> list = new ArrayList<String>();
	
	public TestPaginatorBean()
	{
		super();
		
		for (int i=0; i<2000; i++)
		{
			list.add("test " + i);
		}
		
	}
	
	@Override
	public List<String> retrieveList(int offset, int limit) throws Exception {
		return list.subList(offset, offset+limit); 
		
	}

	@Override
	public int getTotalNumberOfRecords() {
		return list.size();
	}

	@Override
	public String getNavigationString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentPageNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

}
