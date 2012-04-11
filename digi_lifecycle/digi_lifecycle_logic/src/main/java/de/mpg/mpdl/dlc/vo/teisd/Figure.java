package de.mpg.mpdl.dlc.vo.teisd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Figure extends PbOrDiv {
	
	
	private String figDesc;
	
	private List<String> head;

	public Figure() {
		super(ElementType.FIGURE);
	}

	public Figure(Figure original)
	{
		super(original);
		this.setFigDesc(original.getFigDesc());
		List<String> head = new ArrayList<String>();
		head.addAll(original.getHead());
		this.setHead(head);
	}
	
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
