
package de.mpg.mpdl.dlc.vo.organization;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


 
@XmlRootElement(name="RDF", namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class DLCMetadata {
	

	@XmlElement(name="Organization",namespace="http://xmlns.com/foaf/0.1/")
	private FoafOrganization foafOrganization;
	
	public DLCMetadata()
	{
		
	}
	
	public FoafOrganization getFoafOrganization() {
		return foafOrganization;
	}

	public void setFoafOrganization(FoafOrganization foafOrganization) {
		this.foafOrganization = foafOrganization;
	}
	
	

}
