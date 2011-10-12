package de.mpg.mpdl.dlc.viewer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "viewMultiVol", pattern = "/viewMmulti/#{viewMultiVol.volumeId}", viewId = "/viewMultiVol.xhtml")
public class ViewMultiVol extends VolumeLoaderBean{

	enum ViewType
	{
		LIST, ISBD
	}
	
	private ViewType viewType = ViewType.LIST;
	
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		super.loadVolume();
	}
	
	protected void volumeLoaded()
	{
	}

	public ViewType getViewType() {
		return viewType;
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}
	
	

}
