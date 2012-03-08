package de.mpg.mpdl.dlc.editor;

import java.util.List;

import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;

public class PagebreakWrapperBean {
	
	private TeiNode frontBodyBack;
	
	private TeiNode pbTeiNode;
	
	private List<TeiNode> teiNodes;
	
	
	
	public PagebreakWrapperBean()
	{
		
	}
	
	

	

	public List<TeiNode> getTeiNodes() {
		return teiNodes;
	}

	public void setTeiNodes(List<TeiNode> teiNodes) {
		this.teiNodes = teiNodes;
	}

	public TeiNode getFrontBodyBack() {
		return frontBodyBack;
	}

	public void setFrontBodyBack(TeiNode frontBodyBack) {
		this.frontBodyBack = frontBodyBack;
	}

	public TeiNode getPbTeiNode() {
		return pbTeiNode;
	}

	public void setPbTeiNode(TeiNode pbTeiNode) {
		this.pbTeiNode = pbTeiNode;
	}
	
	public Pagebreak getPb()
	{
		return (Pagebreak)getPbTeiNode().getPbOrDiv();
	}

	

	
}
