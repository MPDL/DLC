package de.mpg.mpdl.dlc.viewer;

import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;


public abstract class VolumeLoaderBean {
	
	private static Logger logger = Logger.getLogger(VolumeLoaderBean.class);
	
	@ManagedProperty("#{loginBean}")
	protected LoginBean loginBean;
	
	@EJB
	protected VolumeServiceBean volServiceBean;
	
	protected String volumeId;
	
	protected Volume volume;

	protected abstract void volumeLoaded();

	
	
	public void loadVolume()
	{
		try { 

			
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{   
				logger.info("Load new book " + volumeId);
				this.volume = volServiceBean.retrieveVolume(volumeId, null);
				volServiceBean.loadTei(volume, null);
				volServiceBean.loadPagedTei(volume, null);
			}
			volumeLoaded();
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			MessageHelper.errorMessage("Problem while loading volume");
		}
		
	}
	

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}
	

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public String getVolumeId() {
		return volumeId;
	}

}
