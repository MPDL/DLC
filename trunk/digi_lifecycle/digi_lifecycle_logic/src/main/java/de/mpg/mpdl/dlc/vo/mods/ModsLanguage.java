package de.mpg.mpdl.dlc.vo.mods;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ModsLanguage {

	@XmlPath("languageTerm[@type='code']/text()")
	private String language;
	
	/**
	 * Workaround
	 */
	@XmlPath("languageTerm[@type='code']/@authority")
	private String languageAuthority = "rfc4646";

	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguageAuthority() {
		return languageAuthority;
	}

	public void setLanguageAuthority(String languageAuthority) {
		this.languageAuthority = languageAuthority;
	}
	
}
