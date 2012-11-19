package de.mpg.mpdl.dlc.persistence.entities;

import java.sql.Timestamp;
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

import de.mpg.mpdl.dlc.persistence.entities.IngestLogMessage.ActivityType;

@Entity
@Table(name="item")
@NamedQueries({
    @NamedQuery(name="DatabaseItem.itemsByUser", query="SELECT i FROM DatabaseItem i WHERE i.userId = :userId ORDER BY i.dateCreated DESC"),
    @NamedQuery(name="DatabaseItem.itemsById", query="SELECT i FROM DatabaseItem i WHERE i.id = :id")
})
public class DatabaseItem {

	public static String ALL_ITEMS_BY_USER_ID = "DatabaseItem.itemsByUser";
	public static String ITEMS_BY_ID = "DatabaseItem.itemsById";
	
	public enum IngestStatus{
		RUNNING, READY, ERROR
	}
	
	public enum ItemStatus{
		PENDING, SUBMITTED, RELEASED, DELETED;
	}
	
	@Id
	@SequenceGenerator(name="processIdGenerator")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="processIdGenerator")
	@Column(name="id")
	private Long id;
	
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	@Enumerated(EnumType.STRING)
	private IngestStatus ingestStatus = IngestStatus.RUNNING;
	
	@Enumerated(EnumType.STRING)
	private  ItemStatus itemStatus = ItemStatus.PENDING;
	
	private String contentModelId;
	
	private String contextId;
	
	@Column(columnDefinition="TEXT")
	private String contextName;
	
	private String userId;
	
	private String userName;
	
	private String itemId;
	
	@Column(columnDefinition="TEXT")
	private String shortTitle;
	
	@Column(columnDefinition="TEXT")
	private String subTitle;
	
	private String multivolumeId;
	
	private int numberOfImages;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ingestProcess",  orphanRemoval=true)
	private List<IngestLogMessage> messages = new ArrayList<IngestLogMessage>();
	
	
	
	
	@PrePersist
	@PreUpdate
	public void updateTimeStamps() {
	    //startTime = new Date();
	    if (dateCreated==null) {
	    	dateCreated = new Date();
	    }
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<IngestLogMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<IngestLogMessage> messages) {
		this.messages = messages;
	}

	public String getContentModelId() {
		return contentModelId;
	}

	public void setContentModelId(String contentModelId) {
		this.contentModelId = contentModelId;
	}

	
	
	public IngestLogMessage addMessage(IngestLogMessage ilm)
	{
		//IngestLogMessage ilm = new IngestLogMessage(type, msg);
		messages.add(ilm);
		ilm.setIngestProcess(this);
		return ilm;
		
	}

	

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getMultivolumeId() {
		return multivolumeId;
	}

	public void setMultivolumeId(String multivolumeId) {
		this.multivolumeId = multivolumeId;
	}

	public ItemStatus getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(ItemStatus itemStatus) {
		this.itemStatus = itemStatus;
	}

	public IngestStatus getIngestStatus() {
		return ingestStatus;
	}

	public void setIngestStatus(IngestStatus ingestStatus) {
		this.ingestStatus = ingestStatus;
	}

	public int getNumberOfImages() {
		return numberOfImages;
	}

	public void setNumberOfImages(int numberOfImages) {
		this.numberOfImages = numberOfImages;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
}
