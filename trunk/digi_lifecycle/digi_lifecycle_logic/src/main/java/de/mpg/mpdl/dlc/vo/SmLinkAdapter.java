package de.mpg.mpdl.dlc.vo;


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