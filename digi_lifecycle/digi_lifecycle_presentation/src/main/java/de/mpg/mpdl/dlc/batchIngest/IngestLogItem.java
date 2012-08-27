package de.mpg.mpdl.dlc.batchIngest;

public class IngestLogItem extends IngestLog{
	

	private String escidocId;
	private IngestLog parent;
	private String contentModel;
	
	public IngestLogItem()
	{
		
	}
	
	
	
	String getEscidocId() {
		return escidocId;
	}
	void setEscidocId(String escidocId) {
		this.escidocId = escidocId;
	}
	IngestLog getParent() {
		return parent;
	}
	void setParent(IngestLog parent) {
		this.parent = parent;
	}
	String getContentModel() {
		return contentModel;
	}
	void setContentModel(String contentModel) {
		this.contentModel = contentModel;
	}
	
	
	
	

}
