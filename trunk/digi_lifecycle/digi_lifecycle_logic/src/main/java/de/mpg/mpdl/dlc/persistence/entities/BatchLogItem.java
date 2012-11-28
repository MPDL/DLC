package de.mpg.mpdl.dlc.persistence.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.mpg.mpdl.dlc.persistence.entities.BatchLog.ErrorLevel;
import de.mpg.mpdl.dlc.persistence.entities.BatchLog.Step;

@Entity
@Table(name="batch_log_Item")
@NamedQueries({
    @NamedQuery(name="BatchLogItem.itemsByLogId", query="SELECT i FROM BatchLogItem i WHERE i.logId = :logId ORDER BY i.startDate DESC, i.id DESC"),
    @NamedQuery(name="BatchLogItem.itemById", query="SELECT i FROM BatchLogItem i WHERE i.id = :id")
})
public class BatchLogItem {
	
	public static String ITEMS_BY_LOG_ID = "BatchLogItem.itemsByLogId";
	public static String ITEM_BY_ID = "BatchLogItem.itemById";


	@Id
	@SequenceGenerator(name="LogItemIdGenerator")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="logItemIdGenerator")
	@Column(name="id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="logId", nullable=false)
	private BatchLog logId;
	
	@Column(name="name")
	private String name;
	
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Enumerated(EnumType.STRING)
	private Step step;
	
	@Enumerated(EnumType.STRING)
	private ErrorLevel errorLevel;
	
	private List<String> logs  = new ArrayList<String>();
		
	private String escidocId;
	
	@Column(columnDefinition="TEXT")
	private String shortTitle;
	
	@Column(columnDefinition="TEXT")
	private String subTitle;
	
	private String content_model;
		
	private int images_nr;
	
	private String fFileName;
	
	private String teiFileName;
	

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "logItemId",  orphanRemoval=true)
	private List<BatchLogItemVolume> batchItemVolumes = new ArrayList<BatchLogItemVolume>();

//	@PrePersist
//	@PreUpdate
//	public void updateTimeStamps() {
//	    //startTime = new Date();
//	    if (startDate==null) {
//	    	startDate = new Date();
//	    }
//	}
	
	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	public void setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
	}

	public BatchLogItemVolume addItemVolume(BatchLogItemVolume volume)
	{
		batchItemVolumes.add(volume);
		volume.setLogItemId(this);
		return volume;
		
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public BatchLog getLogId() {
		return logId;
	}


	public void setLogId(BatchLog logId) {
		this.logId = logId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}


	public void setErrorlevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEscidocId() {
		return escidocId;
	}


	public void setEscidocId(String escidocId) {
		this.escidocId = escidocId;
	}


	public String getContent_model() {
		return content_model;
	}


	public void setContent_model(String content_model) {
		this.content_model = content_model;
	}


	public int getImages_nr() {
		return images_nr;
	}


	public void setImages_nr(int images_nr) {
		this.images_nr = images_nr;
	}


	public List<String> getLogs() {
		return logs;
	}


	public void setLogs(List<String> logs) {
		this.logs = logs;
	}


	public String getfFileName() {
		return fFileName;
	}


	public void setfFileName(String fFileName) {
		this.fFileName = fFileName;
	}


	public String getTeiFileName() {
		return teiFileName;
	}


	public void setTeiFileName(String teiFileName) {
		this.teiFileName = teiFileName;
	}


	public List<BatchLogItemVolume> getBatchItemVolumes() {
		return batchItemVolumes;
	}


	public void setBatchItemVolumes(List<BatchLogItemVolume> batchItemVolumes) {
		this.batchItemVolumes = batchItemVolumes;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}


	
	
	
}
