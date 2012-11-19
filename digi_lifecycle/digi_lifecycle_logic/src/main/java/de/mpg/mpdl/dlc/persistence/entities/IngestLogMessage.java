package de.mpg.mpdl.dlc.persistence.entities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem.IngestStatus;

@Entity
@Table(name="logMessages")
public class IngestLogMessage {

	
	public enum ActivityType
	{
		CREATE_ITEM, 
		SUBMIT_ITEM,
		RELEASE_ITEM,
		PROCESS_IMAGE,
		UPDATE_ITEM,
		DELETE_ITEM,
		WITHDRAW_ITEM
		;
	}
	
	
	@Id
	@SequenceGenerator(name="logMessageIdGenerator")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="logMessageIdGenerator")
	@Column(name="id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="ingestProcess", nullable=false)
	private DatabaseItem ingestProcess;
	
	
	@Column(name = "dateCreated", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	
	private String message;


	@Enumerated(EnumType.STRING)
	private ActivityType type;
	
	@Enumerated(EnumType.STRING)
	private IngestStatus ingestStatus = IngestStatus.RUNNING;
	
	private String error;
	
	
	@PrePersist
	@PreUpdate
	public void updateTimeStamps() {
	    //startTime = new Date();
	    if (getDateCreated()==null) {
	    	setDateCreated(new Date());
	    }
	}
	
	public IngestLogMessage()
	{
		
	}
	
	public IngestLogMessage(ActivityType type, String msg)
	{
		this.message = msg;
		this.setType(type);
	}

	public IngestLogMessage(ActivityType type)
	{
		this.setType(type);
	}
	
	


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DatabaseItem getIngestProcess() {
		return ingestProcess;
	}

	public void setIngestProcess(DatabaseItem ingestProcess) {
		this.ingestProcess = ingestProcess;
	}

	

	public IngestStatus getIngestStatus() {
		return ingestStatus;
	}

	public void setIngestStatus(IngestStatus ingestStatus) {
		this.ingestStatus = ingestStatus;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public void setError(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		this.error = sw.toString(); // 
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	
	
}
