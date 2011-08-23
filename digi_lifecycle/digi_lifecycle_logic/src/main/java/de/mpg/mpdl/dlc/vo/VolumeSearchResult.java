package de.mpg.mpdl.dlc.vo;

import java.util.ArrayList;
import java.util.List;

public class VolumeSearchResult {
	
	private List<Volume> volumes = new ArrayList<Volume>();
	
	private int numberOfRecords;
	
	public VolumeSearchResult(List<Volume> volumes, int numberOfRecords) {
		super();
		this.volumes = volumes;
		this.numberOfRecords = numberOfRecords;
	}
	

	public List<Volume> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}

	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	

	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}
	
	

}
