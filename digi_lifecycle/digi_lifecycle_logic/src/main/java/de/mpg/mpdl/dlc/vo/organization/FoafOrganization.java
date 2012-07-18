package de.mpg.mpdl.dlc.vo.organization;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="Organization",namespace="http://xmlns.com/foaf/0.1/")
public class FoafOrganization {
	
	@XmlPath("foaf:img/@rdf:resource")
	private String imgURL;
	
	@XmlPath("foaf:homepage/@rdf:resource")
	private String homePageURL;
	
	@XmlPath("foaf:cataloguePrefix/@rdf:resource")
	private String cataloguePrefix;
	
	@XmlPath("foaf:contact/@rdf:resource")
	private String contact;
	
	@XmlPath("foaf:imprint/@rdf:resource")
	private String imprint;
	
	@XmlPath("foaf:support/@rdf:resource")
	private String support;
	
	@XmlPath("foaf:usageRequirement/@rdf:resource")
	private String usageRequirement;
	
	
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



	public String getCataloguePrefix() {
		return cataloguePrefix;
	}



	public void setCataloguePrefix(String cataloguePrefix) {
		this.cataloguePrefix = cataloguePrefix;
	}



	public String getContact() {
		return contact;
	}



	public void setContact(String contact) {
		this.contact = contact;
	}



	public String getImprint() {
		return imprint;
	}



	public void setImprint(String imprint) {
		this.imprint = imprint;
	}



	public String getSupport() {
		return support;
	}



	public void setSupport(String support) {
		this.support = support;
	}



	public String getUsageRequirement() {
		return usageRequirement;
	}



	public void setUsageRequirement(String usageRequirement) {
		this.usageRequirement = usageRequirement;
	}








	
}
