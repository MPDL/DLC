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
	
	@ManagedProperty("#{structuralEditorBean}")
	private StructuralEditorBean structuralEditorBean;
	
	private TeiElementWrapper partnerElement;
	
	private PositionType positionType;

	private PbOrDiv teiElement;
	
	private TreeWrapperNode treeWrapperNode;
	
	
	
	//The pagebreak this element belongs to
	private TeiElementWrapper pagebreakWrapper;
	
	//The pagebreak id this element should belong to
	private String pagebreakIdToMoveTo;
	
	//For Pagebreaks:
	//Link to METS page element, if a pagebreak 
	private Page page;
	
	private TeiElementWrapper nextPagebreak;
	


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
	

	/*
	public List<TeiElementWrapper> getElementsToNextPb() {
		return elementsToNextPb;
	}


	public void setElementsToNextPb(List<TeiElementWrapper> elementsToNextPb) {
		this.elementsToNextPb = elementsToNextPb;
	}
	*/
	
	
	
}
