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
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="batch_log")
@NamedQueries({
    @NamedQuery(name="BatchLog.itemsByUserId", query="SELECT i FROM BatchLog i WHERE i.userId = :userId ORDER BY i.startDate DESC, i.id DESC"),
    @NamedQuery(name="BatchLog.itemById", query="SELECT i FROM BatchLog i WHERE i.id = :id"),
    @NamedQuery(name="BatchLog.itemsByContextId", query="SELECT i FROM BatchLog i WHERE i.contextId = :contextId ORDER BY i.startDate DESC, i.id DESC")
})
public class BatchLog {
	
	public static String ITEMS_BY_USER_ID = "BatchLog.itemsByUserId";
	public static String ITEMS_BY_CONTEXT_ID = "BatchLog.itemsByContextId";
	public static String ITEM_BY_ID = "BatchLog.ItemById";

	
    /**
     * enum to describe if something went wrong with this element.
     * 
     * - CHECK:     check the batch ingest data
     * - STOPPED:   ingest can not be started because validation failed
     * - STARTED:   data correct, ingest started, in progress
     * - FINISHED:  data correct, ingest completed

     */
	public enum Step
	{
		CHECK, STOPPED, STARTED, FINISHED, INTERRPTED
	}
	
    /**
     * enum to describe the general status of the log.
     */
	public enum Status
	{
		PENDING, SUBMITTED, RELEASED, ROLLBACK, DELETED, WITHDRAW
	}
	
    /**
     * enum to describe if something went wrong with this element.
     * 
     * - FINE:      everything is alright
     * - PROBLEM:   import worked, but something could have been done better
     * - ERROR:   items can not be imported because of validation failure
     * - Exception: item can not be ingested because of internal exception, ingest data correct
     * - FATAL:     data correct, because of interruption due to system errors can not be completed
     */
    public enum ErrorLevel
    {
        FINE, PROBLEM, ERROR, EXCEPTION, FATAL
    }
    
	@Id
	@SequenceGenerator(name="logIdGenerator")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="logIdGenerator")
	@Column(name="id")
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Enumerated(EnumType.STRING)
	private Step step = Step.CHECK;
	
	@Enumerated(EnumType.STRING)
	private ErrorLevel errorLevel;
	
	private List<String> logs = new ArrayList<String>();
	
	@Enumerated(EnumType.STRING)
	private Status status;

	private int totalItems;
	
	private int finishedItems;
	
	private String userId;
	
	private String userName;
	
	private String contextId;
	
	@Column(columnDefinition="TEXT")
	private String contextName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "logId",  orphanRemoval=true)
	private List<BatchLogItem> batchItems = new ArrayList<BatchLogItem>();

	
	@PrePersist
	@PreUpdate
	public void updateTimeStamps() {
	    //startTime = new Date();
	    if (startDate==null) {
	    	startDate = new Date();
	    }
	}
	
	public BatchLogItem addItem(BatchLogItem item)
	{
		batchItems.add(item);
		item.setLogId(this);
		return item;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}

	public void setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
	}

	public Status getStatus() {
		return status;
	}

	public void setStuatus(Status status) {
		this.status = status;
	}

	public List<String> getLogs() {
		return logs;
	}

	public void setLogs(List<String> logs) {
		this.logs = logs;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public int getFinishedItems() {
		return finishedItems;
	}

	public void setFinishedItems(int finishedItems) {
		this.finishedItems = finishedItems;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public List<BatchLogItem> getBatchItems() {
		return batchItems;
	}

	public void setBatchItems(List<BatchLogItem> batchItems) {
		this.batchItems = batchItems;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	


}
