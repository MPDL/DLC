package de.mpg.mpdl.dlc.vo.organization;



public class Organization {
	
	private String id;
	
	private EscidocMetadata escidocMd;
	
	private DLCMetadata dlcMd;
	
	private boolean editable;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EscidocMetadata getEscidocMd() {
		return escidocMd;
	}

	public void setEscidocMd(EscidocMetadata escidocMd) {
		this.escidocMd = escidocMd;
	}

	public DLCMetadata getDlcMd() {
		return dlcMd;
	}

	public void setDlcMd(DLCMetadata dlcMd) {
		this.dlcMd = dlcMd;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	

	
	


}
