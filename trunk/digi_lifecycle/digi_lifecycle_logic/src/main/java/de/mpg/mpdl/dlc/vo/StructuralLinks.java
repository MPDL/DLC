package de.mpg.mpdl.dlc.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructuralLinks {


	private Map<Page, List<MetsDiv>> divMap = new HashMap<Page, List<MetsDiv>>();
	
	private Map<MetsDiv, List<Page>> pageMap = new HashMap<MetsDiv, List<Page>>();

	public Map<Page, List<MetsDiv>> getDivMap() {
		return divMap;
	}

	public void setDivMap(Map<Page, List<MetsDiv>> divMap) {
		this.divMap = divMap;
	}

	public Map<MetsDiv, List<Page>> getPageMap() {
		return pageMap;
	}

	public void setPageMap(Map<MetsDiv, List<Page>> pageMap) {
		this.pageMap = pageMap;
	}

}
