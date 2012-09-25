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

import org.eclipse.persistence.oxm.annotations.XmlPath;

import de.mpg.mpdl.dlc.beans.VolumeServiceBean;
import de.mpg.mpdl.dlc.vo.Volume;


@XmlRootElement(name="mods:mods", namespace="http://www.loc.gov/mods/v3")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModsMetadata {
	
	@XmlPath("mods:recordInfo/mods:recordIdentifier[@source='mab001']/text()")
	private String catalogueId_001;
	
	/**
	 * for 010, 590, 451, 451a,621
	 */
	@XmlElement(name = "mods:relatedItem", namespace="http://www.loc.gov/mods/v3")
	private List<ModsRelatedItem> relatedItems = new ArrayList<ModsRelatedItem>();
	
	@XmlElement(name = "mods:language", namespace="http://www.loc.gov/mods/v3")
	private ModsLanguage language_037;
	
	/**
	 * for 089, 090, 361
	 */
	@XmlElement(name = "mods:part", namespace ="http://www.loc.gov/mods/v3")
	private List<ModsPart> parts = new ArrayList<ModsPart>();
	

//	@XmlPath("mods:part[@type='host']/text()")
//	private String sortField_090;
//	
//	@XmlPath("mods:part[@type='host']/mods:detail/mods:number/text()")
//	private String volumeDescriptive_089;
//	
//	@XmlPath("mods:part[@type='constituent']/mods:detail/mods:title/text()")
//	private String subseries_361;
		


	@XmlElement(name="mods:titleInfo", namespace="http://www.loc.gov/mods/v3")
	private List<ModsTitle> titles = new ArrayList<ModsTitle>();
	
	/**
	 * for 334, 433, 434, 435
	 */
	@XmlElement(name = "mods:physicalDescription", namespace ="http://www.loc.gov/mods/v3")
	private List<ModsPhysicalDescription> physicalDescriptions = new ArrayList<ModsPhysicalDescription>();

	
	
	@XmlElement(name="mods:name", namespace="http://www.loc.gov/mods/v3")
	private List<ModsName> names = new ArrayList<ModsName>();	
	
	@XmlElement(name = "mods:note", namespace="http://www.loc.gov/mods/v3")
	private List<ModsNote> notes = new ArrayList<ModsNote>();

	@XmlElement(name = "mods:originInfo", namespace="http://www.loc.gov/mods/v3")
	List<ModsPublisher> publishers = new ArrayList<ModsPublisher>();
	
	@XmlElement(name = "mods:identifier", namespace="http://www.loc.gov/mods/v3")
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
		String mets = VolumeServiceBean.transformTeiToMets(new FileInputStream(example));
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
