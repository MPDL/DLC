package de.mpg.mpdl.dlc.vo.mets;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;


@XmlRootElement(name="mets", namespace="http://www.loc.gov/METS/")
@XmlAccessorType(XmlAccessType.FIELD)

public class Mets {
	
	@XmlPath("mets:structMap[@TYPE='physical']/mets:div[@DMDID='dmd_0']/mets:div")
	private List<Page> pages = new ArrayList<Page>();
	
	@XmlPath("mets:fileSec/mets:fileGrp[@USE='scans']/mets:file")
	private List<MetsFile> files = new ArrayList<MetsFile>();

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public List<MetsFile> getFiles() {
		return files;
	}

	public void setFiles(List<MetsFile> files) {
		this.files = files;
	}
	
/*
	
	@XmlPath("mets:structMap[@TYPE='logical']/mets:div")
	private List<MetsDiv> logicalStructure;
	*/
	
	//Doesn't currently work, see http://stackoverflow.com/questions/6882337/xmladapter-and-xmlidref-in-moxy-jaxb
	//@XmlJavaTypeAdapter(SmLinkAdapter.class)
	//@XmlElement(name="structLink", namespace="http://www.loc.gov/METS/")
	//private StructuralLinks structuralLinks;
	
	/*
	@XmlPath("mets:structLink")
	private MetsStructLink metsStructLink;
	*/
	
	/*
	public Map<MetsDiv, List<Page>> getPageMap() {
		if(pageMap==null)
		{

			pageMap = new HashMap<MetsDiv, List<Page>>();
			
			if(metsStructLink!=null)
			{
				for(MetsSmLink smLink : metsStructLink.getSmLinks())
				{

					if(pageMap.get(smLink.getFrom()) == null)
					{
						pageMap.put(smLink.getFrom(), new ArrayList<Page>());
					}
					
					List<Page> pageListForDiv = pageMap.get(smLink.getFrom());
					
					
					pageListForDiv.add(smLink.getTo());
					
				}
			}
			
		}
		
		return pageMap;
	}

	public void setPageMap(Map<MetsDiv, List<Page>> pageMap) {
		this.pageMap = pageMap;
	}

	public List<MetsDiv> getLogicalStructure() {
		return logicalStructure;
	}

	public void setLogicalStructure(List<MetsDiv> logicalStructure) {
		this.logicalStructure = logicalStructure;
	}
	
*/	

}
