package de.mpg.mpdl.dlc.viewer;

import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import de.escidoc.core.resources.common.Relation;
import de.mpg.mpdl.dlc.beans.ApplicationBean;
import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.util.MessageHelper;
import de.mpg.mpdl.dlc.vo.Volume;


public abstract class VolumeLoaderBean {
	
	private static Logger logger = Logger.getLogger(VolumeLoaderBean.class);
	
	@ManagedProperty("#{loginBean}")
	protected LoginBean loginBean;
	
	@ManagedProperty("#{applicationBean}")
	protected ApplicationBean appBean;
	
	protected VolumeServiceBean volServiceBean = new VolumeServiceBean();
	
	protected String volumeId;
	
	protected Volume volume;
	
	protected List<Volume> relatedVolumes = new ArrayList<Volume>();

	protected abstract void volumeLoaded();

	
	
	public void loadVolume()
	{
		try { 

			
			if(volume==null || !volumeId.equals(volume.getItem().getObjid()))
			{   
				logger.info("Load new book " + volumeId);
				this.volume = volServiceBean.retrieveVolume(volumeId, null);
				if(volume.getItem().getProperties().getContentModel().getObjid().equals(appBean.getCmMono()))
				{
					volServiceBean.loadTeiSd(volume, null);
					volServiceBean.loadTei(volume, null);
					volServiceBean.loadPagedTei(volume, null);					
				}
				else if(volume.getItem().getProperties().getContentModel().getObjid().equals(appBean.getCmMultiVol()))
				{
					for(Relation rel : volume.getItem().getRelations())
					{
						Volume child = null;
						try {
							child= volServiceBean.retrieveVolume(rel.getObjid(), loginBean.getUserHandle());
						} catch (Exception e) {
							logger.error("cannot retrieve child Volume" + e.getMessage());
						}
						relatedVolumes.add(child);
					}
				}
				else
				{
					try {
						Volume parent = null;
						Relation rel = volume.getItem().getRelations().get(0);
						parent = volServiceBean.retrieveVolume(rel.getObjid(), null);
						relatedVolumes.add(parent);
					} catch (Exception e) {
						logger.error("cannot retrieve parent Volume" + e.getMessage());
					}
					volServiceBean.loadTeiSd(volume, null);
					volServiceBean.loadTei(volume, null);
					volServiceBean.loadPagedTei(volume, null);
				}
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



	public ApplicationBean getAppBean() {
		return appBean;
	}



	public void setAppBean(ApplicationBean appBean) {
		this.appBean = appBean;
	}



	public List<Volume> getRelatedVolumes() {
		return relatedVolumes;
	}



	public void setRelatedVolumes(List<Volume> relatedVolumes) {
		this.relatedVolumes = relatedVolumes;
	}



	
	

}
