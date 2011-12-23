package de.mpg.mpdl.dlc.vo.collection;

public class Collection {
	private String id;
	
	private String name;
	
	private String description;
	
	private String ouId;
	
	private String type;
	
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


	public String getOuId() {
		return ouId;
	}

	public void setOuId(String ouId) {
		this.ouId = ouId;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DLCAdminDescriptor getDlcAD() {
		return dlcAD;
	}

	public void setDlcAD(DLCAdminDescriptor dlcAD) {
		this.dlcAD = dlcAD;
	}
	
	

}
