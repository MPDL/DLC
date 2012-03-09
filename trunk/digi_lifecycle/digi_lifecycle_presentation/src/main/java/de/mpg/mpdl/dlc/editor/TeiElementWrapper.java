package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.List;

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
	
	//Link to METS page element, if a pagebreak 
	private Page page;


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


	/*
	public List<TeiElementWrapper> getElementsToNextPb() {
		return elementsToNextPb;
	}


	public void setElementsToNextPb(List<TeiElementWrapper> elementsToNextPb) {
		this.elementsToNextPb = elementsToNextPb;
	}
	*/
	
	
	
}
