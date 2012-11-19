package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsLocationSEC {
	
	@XmlPath("mods:physicalLocation/text()")
	private String sec_signature_646;
	
	@XmlElement(name = "url", namespace="http://www.loc.gov/mods/v3")
	private ModsURLSEC sec_url;

	public String getSec_signature_646() {
		return sec_signature_646;
	}

	public void setSec_signature_646(String sec_signature_646) {
		this.sec_signature_646 = sec_signature_646;
	}

	public ModsURLSEC getSec_url() {
		return sec_url;
	}

	public void setSec_url(ModsURLSEC sec_url) {
		this.sec_url = sec_url;
	} 

}
