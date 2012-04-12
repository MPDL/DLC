package de.mpg.mpdl.dlc.vo.teisd;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class Body extends Div{
	
	
	public Body() {
		super(ElementType.BODY);
	}

	public Body(Body original) {
		super(original);
		this.setElementType(ElementType.BODY);
	}

}
