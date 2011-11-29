package de.mpg.mpdl.dlc.vo.collection;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="dlc-admin-descriptor")
public class DLCAdminDescriptor {


	@XmlElement(name="page")
	private List<PageDescriptor> pds = new ArrayList<PageDescriptor>();

	public List<PageDescriptor> getPds() {
		return pds;
	}

	public void setPds(List<PageDescriptor> pds) {
		this.pds = pds;
	}
	
	
	
	
	

}
