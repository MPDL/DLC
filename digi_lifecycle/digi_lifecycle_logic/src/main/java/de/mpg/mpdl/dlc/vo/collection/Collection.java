package de.mpg.mpdl.dlc.vo.collection;

import de.mpg.mpdl.dlc.vo.organization.Organization;


public class Collection {
	private String id;
	
	private String name;
	
	private String description;
	
	private String orgaId;
	
	private DLCAdminDescriptor dlcAD;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrgaId() {
		return orgaId;
	}

	public void setOrgaId(String orgaId) {
		this.orgaId = orgaId;
	}

	public DLCAdminDescriptor getDlcAD() {
		return dlcAD;
	}

	public void setDlcAD(DLCAdminDescriptor dlcAD) {
		this.dlcAD = dlcAD;
	}
	
	

}
