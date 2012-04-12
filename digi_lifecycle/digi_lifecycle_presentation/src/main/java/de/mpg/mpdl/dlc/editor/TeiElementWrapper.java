package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedProperty;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;

public class TeiElementWrapper {

	public enum PositionType
	{
		START, END, EMPTY;
	}
	
	
	private TeiElementWrapper partnerElement;
	
	private PositionType positionType;

	private PbOrDiv teiElement;
	
	private TreeWrapperNode treeWrapperNode;
	
	
	
	//The pagebreak this element belongs to
	private TeiElementWrapper pagebreakWrapper;
	
	//The pagebreak id this element should belong to, automatically set when pagebreak wrapper is set
	private String pagebreakIdToMoveTo;
	
	//For Pagebreaks:
	//Link to METS page element, if a pagebreak 
	private Page page;
	
	private TeiElementWrapper nextPagebreak;
	private TeiElementWrapper lastPagebreak;
	

	public TeiElementWrapper()
	{
		
	}
	
	public TeiElementWrapper(TeiElementWrapper original)
	{
		this.setLastPagebreak(original.getLastPagebreak());
		this.setNextPagebreak(original.getNextPagebreak());
		this.setPage(original.getPage());
		this.setPagebreakIdToMoveTo(original.getPagebreakIdToMoveTo());
		this.setPagebreakWrapper(original.getPagebreakWrapper());
		this.setPartnerElement(original.getPartnerElement());
		this.setPositionType(original.getPositionType());
		this.setTeiElement(original.getTeiElement());
		this.setTreeWrapperNode(original.getTreeWrapperNode());
		
	}
	
	
	
	public PositionType getPositionType() {
		return positionType;
	}


	public void setPositionType(PositionType positionType) {
		this.positionType = positionType;
	}


	public TeiElementWrapper getPartnerElement() {
		return partnerElement;
	}


	public void setPartnerElement(TeiElementWrapper partnerElement) {
		this.partnerElement = partnerElement;
	}


	public PbOrDiv getTeiElement() {
		return teiElement;
	}


	public void setTeiElement(PbOrDiv teiElement) {
		this.teiElement = teiElement;
	}



	public Page getPage() {
		return page;
	}


	public void setPage(Page page) {
		this.page = page;
	}


	public TreeWrapperNode getTreeWrapperNode() {
		return treeWrapperNode;
	}


	public void setTreeWrapperNode(TreeWrapperNode treeWrapperNode) {
		this.treeWrapperNode = treeWrapperNode;
	}


	public TeiElementWrapper getPagebreakWrapper() {
		return pagebreakWrapper;
	}


	public void setPagebreakWrapper(TeiElementWrapper pagebreakWrapper) {
		this.pagebreakWrapper = pagebreakWrapper;
		this.pagebreakIdToMoveTo = pagebreakWrapper.getTeiElement().getId();
		
	}


	public TeiElementWrapper getNextPagebreak() {
		return nextPagebreak;
	}


	public void setNextPagebreak(TeiElementWrapper nextPagebreak) {
		this.nextPagebreak = nextPagebreak;
	}



	


	public String getPagebreakIdToMoveTo() {
		return pagebreakIdToMoveTo;
	}


	public void setPagebreakIdToMoveTo(String pagebreakId) {
		this.pagebreakIdToMoveTo = pagebreakId;
		/*
		try {
			System.out.println("Page: " + getPagebreakWrapper().getTeiElement().getId() + " set to " + pagebreakId);
		} catch (Exception e) {
			System.out.println("NullPointerException");
		}
		*/
	}


	public TeiElementWrapper getLastPagebreak() {
		return lastPagebreak;
	}


	public void setLastPagebreak(TeiElementWrapper lastPagebreak) {
		this.lastPagebreak = lastPagebreak;
	}
	

	/*
	public List<TeiElementWrapper> getElementsToNextPb() {
		return elementsToNextPb;
	}


	public void setElementsToNextPb(List<TeiElementWrapper> elementsToNextPb) {
		this.elementsToNextPb = elementsToNextPb;
	}
	*/
	
	
	
}
