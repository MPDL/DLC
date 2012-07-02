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


@XmlRootElement(name="mods", namespace="http://www.loc.gov/mods/v3")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModsMetadata {
	
	@XmlPath("mods:recordInfo/mods:recordIdentifier[@source='mab001']/text()")
	private String catalogueId_001;
	
	@XmlPath("mods:relatedItem[@type='host']/mods:identifier[@type='local']/text()")
	private String parentId_010;

	@XmlElement(name = "language", namespace="http://www.loc.gov/mods/v3")
	private ModsLanguage language_037;
	
	@XmlPath("mods:part[@type='host']/text()")
	private String sortField_090;
	
	@XmlPath("mods:part[@type='host']/mods:detail/mods:number/text()")
	private String volumeDescriptive_089;
	
	@XmlElement(name="name", namespace="http://www.loc.gov/mods/v3")
	private List<ModsName> names = new ArrayList<ModsName>();
	
	@XmlElement(name="titleInfo", namespace="http://www.loc.gov/mods/v3")
	private List<ModsTitle> titles = new ArrayList<ModsTitle>();
	
	@XmlPath("mods:physicalDescription/mods:form[@type=material]/text()")
	private String materialDesignation_334;
	
	@XmlPath("mods:titleInfo/mods:subTitle/text()")
	private String remainderTitle_335;
	
	@XmlElement(name = "note", namespace="http://www.loc.gov/mods/v3")
	private List<ModsNote> notes = new ArrayList<ModsNote>();
	
	@XmlPath("mods:part[@type='constituent']/mods:detail/mods:title/text()")
	private String subseries_361;

	@XmlElement(name = "originInfo", namespace="http://www.loc.gov/mods/v3")
	List<ModsPublisher> publishers = new ArrayList<ModsPublisher>();
	
	@XmlElement(name = "relatedItem", namespace="http://www.loc.gov/mods/v3")
	private ModsRelatedItem relatedItem;

	
//	@XmlPath("mods:physicalDescription/mods:extent/text()")
//	private String extent_433;


	//	@XmlPath("mods:physicalDescription/mods:extent/text()")
//	private String format_435;
	@XmlPath("mods:physicalDescription/mods:extent/text()")
	private List<String> extents = new ArrayList<String>();
	
//	@XmlPath("mods:relatedItem[@type='series']/mods:titleInfo/mods:title/text()")
//	private String seriesTitle_451;
	
	@XmlElement(name = "relatedItem", namespace="http://www.loc.gov/mods/v3")
	private List<ModsSeries> series = new ArrayList<ModsSeries>();	
	
	@XmlElement(name = "identifier", namespace="http://www.loc.gov/mods/v3")
	private List<ModsIdentifier> identifiers = new ArrayList<ModsIdentifier>();
	
	@XmlPath("mods:location/mods:shelfLocator/text()")
	private String signature_544;
	
	@XmlPath("mods:relatedItem[@type='host']/mods:titleInfo/mods:title/text()")
	private String source_590;	
	
	@XmlPath("mods:subject[@authority='rswk']/mods:topic/text()")
	private List<String> keywords = new ArrayList<String>();
	
	@XmlPath("mods:abtract/text()")
	private String freeText;
	

	public ModsMetadata()
	{
		
	}
	
	
	public String getCatalogueId_001() {
		return catalogueId_001;
	}

	public void setCatalogueId_001(String catalogueId_001) {
		this.catalogueId_001 = catalogueId_001;
	}

	public String getParentId_010() {
		return parentId_010;
	}



	public void setParentId_010(String parentId_010) {
		this.parentId_010 = parentId_010;
	}


	
	public String getVolumeDescriptive_089() {
		return volumeDescriptive_089;
	}



	public void setVolumeDescriptive_089(String volumeDescriptive_089) {
		this.volumeDescriptive_089 = volumeDescriptive_089;
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



	public String getSortField_090() {
		return sortField_090;
	}


	public void setSortField_090(String sortField_090) {
		this.sortField_090 = sortField_090;
	}


	public List<ModsTitle> getTitles() {
		return titles;
	}



	public void setTitles(List<ModsTitle> titles) {
		this.titles = titles;
	}



	public String getMaterialDesignation_334() {
		return materialDesignation_334;
	}



	public void setMaterialDesignation_334(String materialDesignation_334) {
		this.materialDesignation_334 = materialDesignation_334;
	}



	public String getRemainderTitle_335() {
		return remainderTitle_335;
	}



	public void setRemainderTitle_335(String remainderTitle_335) {
		this.remainderTitle_335 = remainderTitle_335;
	}



	public List<ModsNote> getNotes() {
		return notes;
	}



	public void setNotes(List<ModsNote> notes) {
		this.notes = notes;
	}



	public String getSubseries_361() {
		return subseries_361;
	}



	public void setSubseries_361(String subseries_361) {
		this.subseries_361 = subseries_361;
	}


	public List<ModsPublisher> getPublishers() {
		return publishers;
	}



	public void setPublishers(List<ModsPublisher> publishers) {
		this.publishers = publishers;
	}



	public List<String> getExtents() {
		return extents;
	}


	public void setExtents(List<String> extents) {
		this.extents = extents;
	}


	public List<ModsSeries> getSeries() {
		return series;
	}


	public void setSeries(List<ModsSeries> series) {
		this.series = series;
	}


	public String getSignature_544() {
		return signature_544;
	}



	public void setSignature_544(String signature_544) {
		this.signature_544 = signature_544;
	}



	public String getSource_590() {
		return source_590;
	}


	public void setSource_590(String source_590) {
		this.source_590 = source_590;
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
	
	
	public ModsRelatedItem getRelatedItem() {
		return relatedItem;
	}


	public void setRelatedItem(ModsRelatedItem relatedItem) {
		this.relatedItem = relatedItem;
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