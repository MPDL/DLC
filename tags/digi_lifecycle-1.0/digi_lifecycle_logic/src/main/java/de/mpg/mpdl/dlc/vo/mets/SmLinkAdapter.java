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
package de.mpg.mpdl.dlc.vo.mets;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import javax.xml.bind.annotation.adapters.XmlAdapter;


public class SmLinkAdapter extends XmlAdapter<MetsStructLink, StructuralLinks> {

	
	
	@Override
	public StructuralLinks unmarshal(MetsStructLink metsStructLink) throws Exception {
		StructuralLinks structLink = new StructuralLinks();
		
		
		for(MetsSmLink smLink : metsStructLink.getSmLinks())
		{
			if(structLink.getDivMap().get(smLink.getTo()) == null)
			{
				structLink.getDivMap().put(smLink.getTo(), new ArrayList<MetsDiv>());
			}
			
			List<MetsDiv> divListForPage = structLink.getDivMap().get(smLink.getTo());
			
			
			divListForPage.add(smLink.getFrom());
			
			
			

			if(structLink.getPageMap().get(smLink.getFrom()) == null)
			{
				structLink.getPageMap().put(smLink.getFrom(), new ArrayList<Page>());
			}
			
			List<Page> pageListForDiv = structLink.getPageMap().get(smLink.getFrom());
			
			
			pageListForDiv.add(smLink.getTo());
			
		}
		return structLink;
	}

	@Override
	public MetsStructLink marshal(StructuralLinks structLinks) throws Exception {
		
		MetsStructLink metsStructLink = new MetsStructLink();
		for(Entry<MetsDiv, List<Page>> entry : structLinks.getPageMap().entrySet())
		{
			for(Page p : entry.getValue())
			{
				MetsSmLink smLink = new MetsSmLink();
				smLink.setFrom(entry.getKey());
				smLink.setTo(p);
				metsStructLink.getSmLinks().add(smLink);
			}
			
			
		}
		return metsStructLink;
	}

	

}