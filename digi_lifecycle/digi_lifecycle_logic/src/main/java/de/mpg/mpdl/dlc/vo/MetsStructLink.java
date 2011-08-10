package de.mpg.mpdl.dlc.vo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class MetsStructLink {

	
	@XmlPath("mets:smLink")
	private List<MetsSmLink> smLinks = new ArrayList<MetsSmLink>();

	public List<MetsSmLink> getSmLinks() {
		return smLinks;
	}

	public void setSmLinks(List<MetsSmLink> smLinks) {
		this.smLinks = smLinks;
	}

}
