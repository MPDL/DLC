package de.mpg.mpdl.dlc.vo.teisd;

import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv.ElementType;

public class Text extends Div{
	
	public Text() {
		super(ElementType.TEXT);
	}
	
	public Text(Text original) {
		super(original);
		this.setElementType(ElementType.TEXT);
	}

}
