package de.mpg.mpdl.dlc.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.mpg.mpdl.dlc.vo.mets.Page;


@Entity
@Table(name="carousel_items")
@NamedQueries({
    @NamedQuery(name=CarouselItem.ALL_ITEMS_BY_CONTEXT_ID, query="SELECT i FROM CarouselItem i WHERE i.contextId = :contextId"),
    @NamedQuery(name=CarouselItem.ALL_ITEMS_BY_OU_ID, query="SELECT i FROM CarouselItem i WHERE i.ouId = :ouId")
    })
public class CarouselItem {

	public static final String ALL_ITEMS_BY_CONTEXT_ID = "CarouselItem.itemsByContextId";
	public static final String ALL_ITEMS_BY_OU_ID = "CarouselItem.itemsByOuId";
	
	/*
	@Id
	@SequenceGenerator(name="carouselItemIdGenerator")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="carouselItemIdGenerator")
	@Column(name="id")
	private Long id;
	*/
	
	@Id
	private String itemId;
	
	@Column(columnDefinition="TEXT")
	private String imageUrl;
	
	private String contextId;
	
	private String ouId;

	/*
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	 */
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getOuId() {
		return ouId;
	}

	public void setOuId(String ouId) {
		this.ouId = ouId;
	}
	
	/*
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof CarouselItem)
		{
			return this.getItemId().equals(((CarouselItem)o).getItemId());
		}
		else
		{
			return this.equals(o);
		}
			
	}
	*/
	
}
