package de.mpg.mpdl.dlc.editor;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.dlc.editor.TeiElementWrapper.PositionType;
import de.mpg.mpdl.dlc.editor.TeiNode.Type;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.Pagebreak;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;
import de.mpg.mpdl.dlc.vo.teisd.TitlePage;

public class PagebreakWrapper {
	
	
	
	private Pagebreak pb;
	private List<TeiElementWrapper> teiElementWrapperList = new ArrayList<TeiElementWrapper>();

	
	public PagebreakWrapper()
	{
		this.setPb(new Pagebreak());
	}
	
	public PagebreakWrapper(Pagebreak pb)
	{
		this.setPb(pb);
	}

	public Pagebreak getPb() {
		return pb;
	}

	public void setPb(Pagebreak pb) {
		this.pb = pb;
	}

	public List<TeiElementWrapper> getTeiElementWrapperList() {
		return teiElementWrapperList;
	}

	public void setTeiElementWrapperList(List<TeiElementWrapper> teiElementWrapperList) {
		this.teiElementWrapperList = teiElementWrapperList;
	}
	
	
	
	


}
