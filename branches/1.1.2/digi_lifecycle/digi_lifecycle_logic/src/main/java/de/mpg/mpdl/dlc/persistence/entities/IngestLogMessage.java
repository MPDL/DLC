/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
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
@Table(name="manualingest_logmessages")
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
	
	@Column(columnDefinition="TEXT")
	private String message;


	@Enumerated(EnumType.STRING)
	private ActivityType type;
	
	@Enumerated(EnumType.STRING)
	private IngestStatus ingestStatus = IngestStatus.RUNNING;
	
	@Column(columnDefinition="TEXT")
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
