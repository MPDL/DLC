package de.mpg.mpdl.dlc.vo.teisd;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class Group extends Div{
	
	public Group() {
		super(ElementType.GROUP);
	}
	
	public Group(Group original) {
		super(original);
		this.setElementType(ElementType.GROUP);
	}

}
