package de.mpg.mpdl.dlc.vo.teisd;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class Front extends Div{

	
	public Front() {
		super(ElementType.FRONT);
	}

	public Front(Front original) {
		super(original);
		this.setElementType(ElementType.FRONT);
	}
}
