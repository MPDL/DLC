package de.mpg.mpdl.dlc.vo.organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="organizational-unit", namespace="http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit")
@XmlAccessorType(XmlAccessType.FIELD)
public class EscidocMetadata {

	@XmlPath("dc:title/text()")
	private String title;
	
	@XmlPath("dc:description/text()")
	private String description;
	
	@XmlPath("eterms:country/text()")	
	private String country;
	
	@XmlPath("eterms:city/text()")
	private String city;
	
	@XmlPath("dcterms:alternative/text()")
	private String alternativeTitle;
	
	@XmlPath("kml:coordinates/text()")
	private String coordinates;
	
	@XmlPath("eterms:organization-type/text()")
	private String type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAlternativeTitle() {
		return alternativeTitle;
	}

	public void setAlternativeTitle(String alternativeTitle) {
		this.alternativeTitle = alternativeTitle;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
	
	
	
}
