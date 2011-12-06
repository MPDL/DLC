package de.mpg.mpdl.dlc.vo.collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import de.mpg.mpdl.dlc.vo.mets.MetsFile;
import de.mpg.mpdl.dlc.vo.mets.Page;

@XmlRootElement(name="page")
public class PageDescriptor {
	
	@XmlPath("mets:div")
	private Page page;
	
	@XmlPath("mets:file")
	private MetsFile mets;

	public Page getPage() {
		return page;
	}

	public void setPages(Page page) {
		this.page = page;
	}

	public MetsFile getMets() {
		return mets;
	}

	public void setMets(MetsFile mets) {
		this.mets = mets;
	}
	
}
