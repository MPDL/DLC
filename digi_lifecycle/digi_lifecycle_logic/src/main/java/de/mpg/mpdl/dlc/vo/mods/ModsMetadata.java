package de.mpg.mpdl.dlc.vo.mods;

import gov.loc.mets.MetsDocument;

import java.io.File;
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

import de.mpg.mpdl.dlc.vo.MetsFile;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;


@XmlRootElement(name="mods", namespace="http://www.loc.gov/mods/v3")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModsMetadata {
	
	@XmlPath("recordInfo/recordIdentifier[@source='mab001']/text()")
	private String catalogueId_001;
	
	@XmlPath("relatedItem[@type='host']/identifier[@type='local']/text()")
	private String parentId_010;

	@XmlElement(name = "language")
	private ModsLanguage language_037;
	
	@XmlPath("part[@type='host']/detail/number/text()")
	private String volumeDescriptive_089;
	
	@XmlElement(name="name")
	private List<ModsName> names = new ArrayList<ModsName>();
	
	@XmlElement(name="titleInfo")
	private List<ModsTitle> titles = new ArrayList<ModsTitle>();
	

	@XmlPath("physicalDescription/form[@type=material]/text()")
	private String materialDesignation_334;
	
	@XmlPath("titleInfo/subTitle/text()")
	private String remainderTitle_334;
	
	@XmlElement(name = "note")
	private List<ModsNote> notes = new ArrayList<ModsNote>();
	
	
	@XmlPath("part[@type='constituent']/detail/title/text()")
	private String subseries_361;
	

	
	
	
	@XmlElement(name = "originInfo")
	List<ModsPublisher> publishers = new ArrayList<ModsPublisher>();

	
	
	@XmlPath("physicalDescription/extent/text()")
	private String extent_432;
	
	@XmlPath("physicalDescription/extent/text()")
	private String format_435;
	
	@XmlPath("relatedItem[@type='series']/titleInfo/title/text()")
	private String seriesTitle_451;
	
	
	@XmlElement(name = "identifier")
	private List<ModsIdentifier> identifiers = new ArrayList<ModsIdentifier>();
	
	
	@XmlPath("location/shelfLocator/text()")
	private String signature_544;
	

	
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



	public String getRemainderTitle_334() {
		return remainderTitle_334;
	}



	public void setRemainderTitle_334(String remainderTitle_334) {
		this.remainderTitle_334 = remainderTitle_334;
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



	public String getExtent_432() {
		return extent_432;
	}



	public void setExtent_432(String extent_432) {
		this.extent_432 = extent_432;
	}



	public String getFormat_435() {
		return format_435;
	}



	public void setFormat_435(String format_435) {
		this.format_435 = format_435;
	}



	public String getSeriesTitle_451() {
		return seriesTitle_451;
	}



	public void setSeriesTitle_451(String seriesTitle_451) {
		this.seriesTitle_451 = seriesTitle_451;
	}



	public String getSignature_544() {
		return signature_544;
	}



	public void setSignature_544(String signature_544) {
		this.signature_544 = signature_544;
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
		
		ModsTitle t = new ModsTitle();
		t.setTitle("Unifotm Title");
		t.setType("uniform");
		md.getTitles().add(t);
		
		ModsTitle t2 = new ModsTitle();
		t2.setTitle("Alternative Title");
		t2.setType("alternative");
		md.getTitles().add(t2);
		
		ModsTitle t3 = new ModsTitle();
		t3.setTitle("Title");
		md.getTitles().add(t3);

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
		
		

		Volume v = new Volume();
		v.setModsMetadata(md);
		
		
		
		for(int i=0; i<10; i++)
		{
			
			MetsFile f = new MetsFile();
			f.setID("img_" + i);
			f.setMimeType("image/jpg");
			f.setLocatorType("OTHER");
			f.setHref("dir/number" + i);
			v.getFiles().add(f);
			
			Page p = new Page();
			p.setID("page_" + i);
			p.setOrder(i);
			p.setOrderLabel("");
			p.setType("page");
			p.setFile(f);
			v.getPages().add(p);
		}
	
		
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
		Marshaller m = ctx.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		StringWriter sw = new StringWriter();
		m.marshal(v, sw);
		System.out.println(sw.toString());
		MetsDocument.Factory.parse(sw.toString());
		
		
		
		File example = new File("C:/Users/haarlae1/Documents/Digi Lifecycle/mods_example.xml");
		Unmarshaller um = ctx.createUnmarshaller();
		ModsMetadata unmarshalledMods = (ModsMetadata)um.unmarshal(example);
		System.out.println(unmarshalledMods.getCatalogueId_001());
		
		
	}


	


	

}
