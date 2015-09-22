/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
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
	
	private String codicologicalFileName;
	
	private int volumes_nr;
	
	private int finished_volumes_nr;
	

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

	public int getVolumes_nr() {
		return volumes_nr;
	}

	public void setVolumes_nr(int volumes_nr) {
		this.volumes_nr = volumes_nr;
	}

	public int getFinished_volumes_nr() {
		return finished_volumes_nr;
	}

	public void setFinished_volumes_nr(int finished_volumes_nr) {
		this.finished_volumes_nr = finished_volumes_nr;
	}

	public String getCodicologicalFileName() {
		return codicologicalFileName;
	}

	public void setCodicologicalFileName(String codicologicalFileName) {
		this.codicologicalFileName = codicologicalFileName;
	}


	
	
	
}
