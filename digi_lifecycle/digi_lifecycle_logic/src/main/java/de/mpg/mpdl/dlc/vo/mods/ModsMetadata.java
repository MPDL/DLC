package de.mpg.mpdl.dlc.vo.mods;

import gov.loc.mods.v3.ModsDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;


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
	

	@XmlPath("originInfo/edition/text()")
	private String editionStatement_403;
	
	
	@XmlElement(name = "originInfo")
	List<ModsPublisher> publishers = new ArrayList<ModsPublisher>();
	
	
	@XmlPath("originInfo/dateIssued[@encoding='w3cdtf']/text()")
	private Date dateIssued_425;
	
	
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


/*
	public String getZdbId_025z() {
		return zdbId_025z;
	}



	public void setZdbId_025z(String zdbId_025z) {
		this.zdbId_025z = zdbId_025z;
	}
*/
/*

	public String getParentId_010() {
		return parentId_010;
	}



	public void setParentId_010(String parentId_010) {
		this.parentId_010 = parentId_010;
	}



	public String getUniformTitle_304() {
		return uniformTitle_304;
	}



	public void setUniformTitle_304(String uniformTitle_304) {
		this.uniformTitle_304 = uniformTitle_304;
	}



	public String getAlternativeTitle_310() {
		return alternativeTitle_310;
	}



	public void setAlternativeTitle_310(String titleProper_310) {
		this.alternativeTitle_310 = titleProper_310;
	}



	public String getTitleProper_331() {
		return titleProper_331;
	}



	public void setTitleProper_331(String titleProper_331) {
		this.titleProper_331 = titleProper_331;
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


	public void setDateIssued_425(Date dateIssued_425) {
		this.dateIssued_425 = dateIssued_425;
	}



	public Date getDateIssued_425() {
		return dateIssued_425;
	}

	


	public void setIdentifiers(List<ModsIdentifier> identifiers) {
		this.identifiers = identifiers;
	}



	public List<ModsIdentifier> getIdentifiers() {
		return identifiers;
	}

*/

	public void setLanguage_037(ModsLanguage language_037) {
		this.language_037 = language_037;
	}



	public ModsLanguage getLanguage_037() {
		return language_037;
	}



	public static void main(String[] args) throws Exception
	{
		
		ModsDocument modsDoc = ModsDocument.Factory.newInstance();
		//RecordIdentifier ric = modsDoc.addNewMods().addNewLocation().addNewShelfLocator()
		//ric.setSource("test");
		
		modsDoc.save(System.out);
		
		/*
		ModsMetadata md = new ModsMetadata();
		md.setUniformTitle_304("Unifotm Title");
		md.setAlternativeTitle_310("Proper 310");
		md.setTitleProper_331("Proper 331");
		md.setLanguage_037("en");
		
		
		
		ModsName name1 = new ModsName();
		name1.setName("Test Author 1");
		name1.setRole("aut");
		
		ModsName name2 = new ModsName();
		name2.setName("Test Editor 1");
		name2.setRole("asn");
		md.getNames().add(name1);
		md.getNames().add(name2);
		
		md.setDateIssued_425(new Date());
		

		
		ModsIdentifier id1 = new ModsIdentifier();
		id1.setInvalid("yes");
		id1.setValue("invalidisbn");
		id1.setType("ISBN");
		md.getIdentifiers().add(id1);
		
		*/

		
		
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { ModsMetadata.class });
		//Marshaller m = ctx.createMarshaller();
		//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//m.marshal(md, System.out);
		
		
		File example = new File("C:/Users/haarlae1/Documents/Digi Lifecycle/mods_example.xml");
		Unmarshaller um = ctx.createUnmarshaller();
		ModsMetadata unmarshalledMods = (ModsMetadata)um.unmarshal(example);
		System.out.println(unmarshalledMods.getCatalogueId_001());
		 
	}



	


	

}
