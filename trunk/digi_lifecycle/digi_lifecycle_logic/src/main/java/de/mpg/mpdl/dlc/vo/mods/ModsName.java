package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlAccessorType(XmlAccessType.FIELD)
public class ModsName {
	
	@XmlAttribute(name = "ID")
	private String mabId;
	
	@XmlAttribute(name = "type")
	private String type = "personal";
	
	@XmlAttribute(name = "authority")
	private String authority = "pnd";
	
	@XmlPath("mods:role/mods:roleTerm[@type='code']/@authority")
	private String roleTermAuthority = "marcrelator";
	
	@XmlPath("mods:role/mods:roleTerm[@type='code']/text()")
	private String role;
	
	@XmlPath("mods:namePart/text()")
	private String name;
	
	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getAuthority() {
		return authority;
	}


	public void setAuthority(String authority) {
		this.authority = authority;
	}


	public String getRoleTermAuthority() {
		return roleTermAuthority;
	}


	public void setRoleTermAuthority(String roleTermAuthority) {
		this.roleTermAuthority = roleTermAuthority;
	}


	public String getMabId() {
		return mabId;
	}


	public void setMabId(String mabId) {
		this.mabId = mabId;
	}




	
}
