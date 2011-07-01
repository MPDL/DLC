package de.mpg.mpdl.dlc.vo;

public class Page {
	
	private String pagination;
	private String path;
	
	
	
	
	public Page(String pagination, String path) {
		this.pagination = pagination;
		this.path = path;
	}
	public void setPagination(String pagination) {
		this.pagination = pagination;
	}
	public String getPagination() {
		return pagination;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}

}
