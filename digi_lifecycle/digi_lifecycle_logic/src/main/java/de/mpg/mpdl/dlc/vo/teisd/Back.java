package de.mpg.mpdl.dlc.vo.teisd;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class Back extends Div{
	
	public Back() {
		super(ElementType.BACK);
	}
	
	public Back(Back original) {
		super(original);
		this.setElementType(ElementType.BACK);
	}

}
