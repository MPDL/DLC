package de.mpg.mpdl.dlc.batchIngest;

public class IngestLogItem extends IngestLog{
	
	
	private String escidocId;

	private String message;
	private String contentModel;
	private Integer imagesNr;
	private boolean hasFooter;
	private boolean hasTEI;
	private String esciDocStatus;
	public String getEscidocId() {
		return escidocId;
	}
	public void setEscidocId(String escidocId) {
		this.escidocId = escidocId;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getContentModel() {
		return contentModel;
	}
	public void setContentModel(String contentModel) {
		this.contentModel = contentModel;
	}
	public Integer getImagesNr() {
		return imagesNr;
	}
	public void setImagesNr(Integer imagesNr) {
		this.imagesNr = imagesNr;
	}
	public boolean isHasFooter() {
		return hasFooter;
	}
	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}
	public boolean isHasTEI() {
		return hasTEI;
	}
	public void setHasTEI(boolean hasTEI) {
		this.hasTEI = hasTEI;
	}
	public String getEsciDocStatus() {
		return esciDocStatus;
	}
	public void setEsciDocStatus(String esciDocStatus) {
		this.esciDocStatus = esciDocStatus;
	}
	

	

}
