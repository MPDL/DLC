package de.mpg.mpdl.dlc.viewer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;

@ManagedBean
@ViewScoped
@URLMapping(id = "viewMultiVol", pattern = "/viewMmulti/#{viewMultiVol.volumeId}", viewId = "/viewMultiVol.xhtml")
public class ViewMultiVol{

	private VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	private String volumeId;
	
	

	private Volume volume;
	
	@ManagedProperty("#{loginBean}")
	private LoginBean loginBean;
	
	enum ViewType
	{
		LIST, ISBD
	}
	
	private ViewType viewType = ViewType.LIST;
	
	
	@URLAction(onPostback=false)
	public void loadVolume()
	{
		if(volume==null || !volumeId.equals(volume.getObjidAndVersion()))
		{   
			try {
				this.volume = volServiceBean.loadCompleteVolume(volumeId, loginBean.getUserHandle());
				
			} catch (Exception e) {
				MessageHelper.errorMessage("Problem while loading volume");
			}
		}
		volumeLoaded();
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
	
	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	
	

}
