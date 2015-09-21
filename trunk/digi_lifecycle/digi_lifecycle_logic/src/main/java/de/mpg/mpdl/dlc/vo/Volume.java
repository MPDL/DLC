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
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft f�r wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur F�rderung der Wissenschaft e.V.
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
package de.mpg.mpdl.dlc.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Document;

import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.escidoc.core.resources.sb.search.Highlight;
import de.escidoc.core.resources.sb.search.SearchHit;
import de.escidoc.core.resources.sb.search.Type;
import de.mpg.mpdl.dlc.vo.mets.Mets;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;

public class Volume {

	// @XmlPath("mets:dmdSec[@ID='dmd_0']/mets:mdWrap[@MDTYPE='MODS']/mets:xmlData/mods:mods")
	private ModsMetadata modsMetadata;

	/*
	 * @XmlPath("mets:structMap[@TYPE='physical']/mets:div[@DMDID='dmd_0']/mets:div"
	 * ) private List<Page> pages = new ArrayList<Page>();
	 * 
	 * @XmlPath("mets:fileSec/mets:fileGrp[@USE='scans']/mets:file") private
	 * List<MetsFile> files = new ArrayList<MetsFile>();
	 */

	private Mets mets;

	@XmlTransient
	private ItemProperties properties;

	@XmlTransient
	private Item item;

	@XmlTransient
	private String tei;

	@XmlTransient
	private String codicological;

	// private TeiSd teiSd;

	@XmlTransient
	private Document teiSdXml;

	@XmlTransient
	private String pagedTei;

	@XmlTransient
	private List<String> relatedVolumes;

	@XmlTransient
	private List<Volume> relatedChildVolumes;

	@XmlTransient
	private Volume relatedParentVolume;

	@XmlTransient
	private Highlight searchResultHighlight;

	private boolean reversePagination;
	private boolean directionRTL;

	public Volume() {

	}

	/*
	 * public Volume (Item item) throws Exception {
	 * 
	 * this.item = item; this.properties = item.getProperties();
	 * 
	 * MetadataRecord mdRec = item.getMetadataRecords().get("escidoc");
	 * MetsDocument metsDoc = MetsDocument.Factory.parse(mdRec.getContent());
	 * 
	 * Node child =
	 * metsDoc.getMets().getDmdSecArray(0).getMdWrap().getXmlData().
	 * getDomNode().getFirstChild();
	 * 
	 * XmlCursor xmlDataCursor =
	 * metsDoc.getMets().getDmdSecArray(0).getMdWrap().getXmlData().newCursor();
	 * xmlDataCursor.toChild(0); ModsDocument modsDoc =
	 * ModsDocument.Factory.parse(xmlDataCursor.getDomNode());
	 * xmlDataCursor.dispose();
	 * 
	 * JAXBContext ctx = JAXBContext.newInstance(new Class[] {
	 * ModsMetadata.class }); Unmarshaller um = ctx.createUnmarshaller();
	 * this.setModsMetadata((ModsMetadata)um.unmarshal(modsDoc.getDomNode()));
	 * 
	 * 
	 * Map<String, FileType> fileMap = new HashMap<String, FileType>();
	 * 
	 * for(FileGrp fileGroup : metsDoc.getMets().getFileSec().getFileGrpArray())
	 * { if(fileGroup.getUSE().equals("scans")) { for(FileType fileType :
	 * fileGroup.getFileArray()) { fileMap.put(fileType.getID(), fileType); }
	 * 
	 * } }
	 * 
	 * for(StructMapType structMap : metsDoc.getMets().getStructMapArray()) {
	 * if(structMap.getTYPE().equals("physical")) { DivType mainDiv =
	 * structMap.getDiv(); for(DivType pageDiv : mainDiv.getDivArray()) {
	 * FileType fileType = fileMap.get(pageDiv.getFptrArray(0).getFILEID());
	 * Page p = new Page(pageDiv.getORDERLABEL(),
	 * fileType.getFLocatArray(0).getHref()); getPages().add(p); }
	 * 
	 * } }
	 * 
	 * 
	 * }
	 */

	public void setProperties(ItemProperties properties) {
		this.properties = properties;
	}

	public ItemProperties getProperties() {
		return properties;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item getItem() {
		this.item.getProperties().getContentModel().getObjid();
		return item;
	}

	public void setModsMetadata(ModsMetadata modsMetadata) {
		this.modsMetadata = modsMetadata;
	}

	public ModsMetadata getModsMetadata() {
		return modsMetadata;
	}

	public String getTei() {
		return this.tei;
	}

	public void setTei(String tei) {
		this.tei = tei;

	}

	public void setPagedTei(String pagedTei) {
		this.pagedTei = pagedTei;

	}

	public String getPagedTei() {
		return pagedTei;
	}

	/*
	 * public TeiSd getTeiSd() { return teiSd; }
	 * 
	 * public void setTeiSd(TeiSd teiSd) { this.teiSd = teiSd; }
	 */

	public Document getTeiSdXml() {
		return teiSdXml;
	}

	public void setTeiSdXml(Document teiSdXml) {
		this.teiSdXml = teiSdXml;
	}

	public List<String> getRelatedVolumes() {
		return relatedVolumes;
	}

	public void setRelatedVolumes(List<String> relatedVolumes) {
		this.relatedVolumes = relatedVolumes;
	}

	public Highlight getSearchResultHighlight() {
		return searchResultHighlight;
	}

	public void setSearchResultHighlight(Highlight searchResultHighlight) {
		this.searchResultHighlight = searchResultHighlight;
	}

	public int getSearchResultHighlightSize() {
		int size = 0;
		if (this.searchResultHighlight != null) {
			for (SearchHit hit : this.searchResultHighlight) {
				if (hit.getTextFragments() != null
						&& hit.getType().equals(Type.FULLTEXT)) {
					size += hit.getTextFragments().size();
				}
			}
		}
		return size;
	}

	public Mets getMets() {
		return mets;
	}

	public void setMets(Mets mets) {
		this.mets = mets;
	}

	public List<Page> getPages() {
		return getMets().getPages();
	}

	public List<Volume> getRelatedChildVolumes() {
		return relatedChildVolumes;
	}

	public void setRelatedChildVolumes(List<Volume> relatedChildVolumes) {
		this.relatedChildVolumes = relatedChildVolumes;
	}

	public Volume getRelatedParentVolume() {
		return relatedParentVolume;
	}

	public void setRelatedParentVolume(Volume relatedParentVolume) {
		this.relatedParentVolume = relatedParentVolume;
	}

	public String getObjidAndVersion() {
		return this.getItem().getOriginObjid() + ":"
				+ this.getItem().getProperties().getVersion().getNumber();
	}

	public String getCodicological() {
		return codicological;
	}

	public void setCodicological(String codicological) {
		this.codicological = codicological;
	}

	public boolean isDirectionRTL() {
		return this.directionRTL;
	}

	public void setDirectionRTL(boolean directionRTL) {
		this.directionRTL = directionRTL;
	}

	public boolean isReversePagination() {
		return reversePagination;
	}

	public void setReversePagination(boolean reversePagination) {
		this.reversePagination = reversePagination;
	}

}
