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
package de.mpg.mpdl.dlc.vo.mods;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;


@XmlRootElement(name="mods", namespace="http://www.loc.gov/mods/v3")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModsMetadata {
	
	@XmlPath("mods:recordInfo/mods:recordIdentifier[@source='mab001']/text()")
	private String catalogueId_001;
	
	/**
	 * for 010, 590, 451, 451a,621
	 */
	@XmlElement(name = "relatedItem", namespace="http://www.loc.gov/mods/v3")
	private List<ModsRelatedItem> relatedItems = new ArrayList<ModsRelatedItem>();
	
	@XmlElement(name = "language", namespace="http://www.loc.gov/mods/v3")
	private ModsLanguage language_037;
	
	/**
	 * for 089, 090, 361
	 */
	@XmlElement(name = "part", namespace ="http://www.loc.gov/mods/v3")
	private List<ModsPart> parts = new ArrayList<ModsPart>();
	

//	@XmlPath("mods:part[@type='host']/text()")
//	private String sortField_090;
//	
//	@XmlPath("mods:part[@type='host']/mods:detail/mods:number/text()")
//	private String volumeDescriptive_089;
//	
//	@XmlPath("mods:part[@type='constituent']/mods:detail/mods:title/text()")
//	private String subseries_361;
		


	@XmlElement(name="titleInfo", namespace="http://www.loc.gov/mods/v3")
	private List<ModsTitle> titles = new ArrayList<ModsTitle>();
	
	/**
	 * for 334, 433, 434, 435
	 */
	@XmlElement(name = "physicalDescription", namespace ="http://www.loc.gov/mods/v3")
	private List<ModsPhysicalDescription> physicalDescriptions = new ArrayList<ModsPhysicalDescription>();

	
	
	@XmlElement(name="name", namespace="http://www.loc.gov/mods/v3")
	private List<ModsName> names = new ArrayList<ModsName>();	
	
	@XmlElement(name = "note", namespace="http://www.loc.gov/mods/v3")
	private List<ModsNote> notes = new ArrayList<ModsNote>();

	@XmlElement(name = "originInfo", namespace="http://www.loc.gov/mods/v3")
	List<ModsPublisher> publishers = new ArrayList<ModsPublisher>();
	
	@XmlElement(name = "identifier", namespace="http://www.loc.gov/mods/v3")
	private List<ModsIdentifier> identifiers = new ArrayList<ModsIdentifier>();
	
	@XmlPath("mods:location/mods:physicalLocation/text()")
	private String signature_544;
	
	
	
	@XmlPath("mods:subject[@authority='rswk']/mods:topic/text()")
	private List<String> keywords = new ArrayList<String>();
	
	@XmlPath("mods:abstract/text()")
	private String freeText;


	public List<ModsRelatedItem> getRelatedItems() {
		return relatedItems;
	}


	public void setRelatedItems(List<ModsRelatedItem> relatedItems) {
		this.relatedItems = relatedItems;
	}


	public List<ModsPart> getParts() {
		return parts;
	}

	public void setParts(List<ModsPart> parts) {
		this.parts = parts;
	}

	public List<ModsPhysicalDescription> getPhysicalDescriptions() {
		return physicalDescriptions;
	}




	public void setPhysicalDescriptions(
			List<ModsPhysicalDescription> physicalDescriptions) {
		this.physicalDescriptions = physicalDescriptions;

	}


	

	public ModsMetadata()
	{
		
	}
	
	
	public String getCatalogueId_001() {
		return catalogueId_001;
	}

	public void setCatalogueId_001(String catalogueId_001) {
		this.catalogueId_001 = catalogueId_001;
	}


	public List<ModsName> getNames() {
		return names;
	}


	public void setNames(List<ModsName> names) {
		this.names = names;
	}


	public void setIdentifiers(List<ModsIdentifier> identifiers) {
		this.identifiers = identifiers;
	}



	public List<ModsIdentifier> getIdentifiers() {
		return identifiers;
	}



	public void setLanguage_037(ModsLanguage language_037) {
		this.language_037 = language_037;
	}



	public ModsLanguage getLanguage_037() {
		return language_037;
	}





	public List<ModsTitle> getTitles() {
		return titles;
	}



	public void setTitles(List<ModsTitle> titles) {
		this.titles = titles;
	}






	public List<ModsNote> getNotes() {
		return notes;
	}



	public void setNotes(List<ModsNote> notes) {
		this.notes = notes;
	}




	public List<ModsPublisher> getPublishers() {
		return publishers;
	}



	public void setPublishers(List<ModsPublisher> publishers) {
		this.publishers = publishers;
	}





	public String getSignature_544() {
		return signature_544;
	}



	public void setSignature_544(String signature_544) {
		this.signature_544 = signature_544;
	}






	public List<String> getKeywords() {
		return keywords;
	}


	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}


	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}
	
	

	public static void main(String[] args) throws Exception
	{
		new ModsMetadata().printExample();
		//ModsDocument modsDoc = ModsDocument.Factory.newInstance();
		//RecordIdentifier ric = modsDoc.addNewMods().addNewLocation().addNewShelfLocator()
		//ric.setSource("test");
		
		//modsDoc.save(System.out);
		
		
		
		
		
		
		/*
		File example = new File("C:/Users/haarlae1/Documents/Digi Lifecycle/mods_example.xml");
		Unmarshaller um = ctx.createUnmarshaller();
		ModsMetadata unmarshalledMods = (ModsMetadata)um.unmarshal(example);
		System.out.println(unmarshalledMods.getCatalogueId_001());
		*/
		 
	}

	public void printExample() throws Exception
	{
		ModsMetadata md = new ModsMetadata();
		
		md.setCatalogueId_001("testCatId");
		/*
		ModsTitle t = new ModsTitle();
		t.setTitle("Unifotm Title");
		t.setType("uniform");
		md.getTitles().add(t);
		
		ModsTitle t2 = new ModsTitle();
		t2.setTitle("Alternative Title");
		t2.setType("alternative");
		md.getTitles().add(t2);
		*/
		/*
		ModsTitle t3 = new ModsTitle();
		t3.setTitle("Title");
		md.getTitles().add(t3);
*/
		/*
		ModsName name1 = new ModsName();
		name1.setName("Test Author 1");
		name1.setRole("aut");
		
		ModsName name2 = new ModsName();
		name2.setName("Test Editor 1");
		name2.setRole("asn");
		md.getNames().add(name1);
		md.getNames().add(name2);
		

		ModsIdentifier id1 = new ModsIdentifier();
		id1.setInvalid("yes");
		id1.setValue("invalidisbn");
		id1.setType("ISBN");
		md.getIdentifiers().add(id1);
		*/
		

		
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
		/*
		Marshaller m = ctx.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		m.marshal(md, sw);
		System.out.println(sw.toString());
		
		Volume v = new Volume();
		v.setModsMetadata(md);
		
		
		
		for(int i=0; i<10; i++)
		{
			
			MetsFile f = new MetsFile();
			f.setId("img_" + i);
			f.setMimeType("image/jpg");
			f.setLocatorType("OTHER");
			f.setHref("dir/number" + i);
			v.getFiles().add(f);
			
			Page p = new Page();
			p.setId("page_" + i);
			p.setOrder(i);
			p.setOrderLabel("");
			p.setType("page");
			p.setFile(f);
			v.getPages().add(p);
		}
	
		
		ctx = JAXBContext.newInstance(new Class[] { Volume.class });
		m = ctx.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		StringWriter sw2 = new StringWriter();
		m.marshal(v, sw2);
		System.out.println(sw2.toString());
		MetsDocument.Factory.parse(sw2.toString());
		
		*/
		
		File example = new File("/home/frank/data/digitization_lifecycle/tei_samples/ernstcurtius_v02.xml");
		//File teiFileWithIds = VolumeServiceBean.addIdsToTei(new FileInputStream(example));
		String mets = VolumeServiceBean.transformTeiToMets(new StreamSource(example));
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		Volume vol = (Volume)unmarshaller.unmarshal(new ByteArrayInputStream(mets.getBytes("UTF-8")));
		
		System.out.println(mets);
		
		System.out.println("------------------------------------------------");
		StringWriter sw = new StringWriter();
		Marshaller m = ctx.createMarshaller();
		m.marshal(vol, sw);
		System.out.println(sw.toString());
		//System.out.println(unmarshalledMets.getModsMetadata().getTitles().size());
		
		
	}
	
	

	


	

}
