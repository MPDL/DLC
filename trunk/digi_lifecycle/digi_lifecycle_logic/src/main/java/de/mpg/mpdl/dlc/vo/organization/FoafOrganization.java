package de.mpg.mpdl.dlc.vo.organization;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="Organization",namespace="http://xmlns.com/foaf/0.1/")
public class FoafOrganization {
	
	@XmlPath("foaf:img/@rdf:resource")
	private String imgURL;
	
	@XmlPath("foaf:homepage/@rdf:resource")
	private String homePageURL;
	
	@XmlPath("foaf:mbox/@rdf:resource")
	private String email;
	
	public FoafOrganization()
	{
		
	}

	public String getImgURL() {
		return imgURL;
	}

	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}

	public String getHomePageURL() {
		return homePageURL;
	}

	public void setHomePageURL(String homePageURL) {
		this.homePageURL = homePageURL;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
