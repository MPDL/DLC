package de.mpg.mpdl.dlc.vo.teisd;

import java.util.List;

public class Figure extends PbOrDiv {
	
	private String figDesc;
	
	private List<String> head;

	public String getFigDesc() {
		return figDesc;
	}

	public void setFigDesc(String figureDesc) {
		this.figDesc = figureDesc;
	}

	public List<String> getHead() {
		return head;
	}

	public void setHead(List<String> head) {
		this.head = head;
	}

	

}
