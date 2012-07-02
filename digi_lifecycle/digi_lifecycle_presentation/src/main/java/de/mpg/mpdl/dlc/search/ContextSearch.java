package de.mpg.mpdl.dlc.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * TODO
 * @author kleinfe1
 *
 */
public class ContextSearch {
	
	private String ouId;
	private String contextId;
	private List<SelectItem> contextList;
	
	
	public ContextSearch()
	{
		this.ouId = "";
		this.contextId = "";
		this.contextList = new ArrayList<SelectItem>();
	}

	public ContextSearch(String ouId, String contextId, List<SelectItem> contextList)
	{
		this.ouId = ouId;
		this.contextId = contextId;
		this.contextList = contextList;
	}
	
	
	public String getOuId() {
		return ouId;
	}
	public void setOuId(String ouId) {
		this.ouId = ouId;
	}
	public String getContextId() {
		return contextId;
	}
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}
	public List<SelectItem> getContextList() {
		return contextList;
	}
	public void setContextList(List<SelectItem> contextList) {
		this.contextList = contextList;
	}
}
