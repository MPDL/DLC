package de.mpg.mpdl.dlc.vo.teisd;

import gov.loc.mets.DivType;
import gov.loc.mets.FileType;
import gov.loc.mets.MetsDocument;
import gov.loc.mets.MetsType.FileSec.FileGrp;
import gov.loc.mets.StructMapType;
import gov.loc.mods.v3.ModsDocument;
import gov.loc.mods.v3.ModsType;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.xmlbeans.XmlCursor;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;
import org.w3c.dom.Node;


import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.Properties;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;

@XmlRootElement(name="TEI", namespace="http://www.tei-c.org/ns/1.0")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeiSd {

	
	@XmlElements
	(value = {
			@XmlElement(type=de.mpg.mpdl.dlc.vo.teisd.Front.class),
			@XmlElement(type=de.mpg.mpdl.dlc.vo.teisd.Body.class),
			@XmlElement(type=de.mpg.mpdl.dlc.vo.teisd.Back.class),
			@XmlElement(type=de.mpg.mpdl.dlc.vo.teisd.Div.class),
			@XmlElement(type=de.mpg.mpdl.dlc.vo.teisd.Pagebreak.class)
	})
	@XmlPaths
	(value = {
			@XmlPath("tei:text/tei:front"),
			@XmlPath("tei:text/tei:body"),
			@XmlPath("tei:text/tei:back"),
			@XmlPath("tei:text/tei:front/tei:div"),
			@XmlPath("tei:text/tei:front/tei:pb")
	})
	
	private List<PbOrDiv> pbOrDiv = new ArrayList<PbOrDiv>();
	

	/**
	 * Helper Maps for working with structural Links
	 */
	@XmlTransient
	private Map<String, PbOrDiv> divMap;

	public TeiSd ()
	{
	}
	
	
	public Map<String, PbOrDiv> getDivMap() {
		
		if(divMap==null)
		{
			divMap = new HashMap<String, PbOrDiv>();
			createDivMap(getPbOrDiv());
		}
		return divMap;
	}
	
	
	private void createDivMap(List<PbOrDiv> currentList)
	{
		for(PbOrDiv current : currentList)
		{
			divMap.put(current.getId(), current);
			if(current.getPbOrDiv()!=null)
			{	
				createDivMap(current.getPbOrDiv());
			}
		}
	}

	
	public static List<PbOrDiv> pbOrDivListFactory()
	{
		return new ArrayList<PbOrDiv>();
	}


	public static void main (String[] args) throws Exception
	{
		
		
		File example = new File("C:/Users/haarlae1/Documents/Digi Lifecycle/teisd_example.xml");
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { TeiSd.class });
		
		Unmarshaller um = ctx.createUnmarshaller();
		TeiSd tei = (TeiSd)um.unmarshal(example);
		System.out.println(tei.getPbOrDiv().size());
		
		
		TeiSd teiSd = new TeiSd();
		
		Front front = new Front();
		Body body = new Body();
		
		
		Div div = new Div();
		//div.setHead("test");
		div.setId("id");
		div.setNumeration("1");
		
		Div div2 = new Div();
		//div2.setHead("test2");
		div2.setId("id");
		div2.setNumeration("2");
		
		div.getPbOrDiv().add(div2);
		
		Pagebreak pb = new Pagebreak();
		pb.setId("pbid");
		pb.setFacs("facsnumber");
		
		body.getPbOrDiv().add(pb);
		body.getPbOrDiv().add(div);
		front.getPbOrDiv().add(div);
		
		teiSd.getPbOrDiv().add(front);
		teiSd.getPbOrDiv().add(body);
		
		Marshaller m = ctx.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		m.marshal(teiSd, sw);
		
		System.out.println(sw.toString());

		
	}




	public List<PbOrDiv> getPbOrDiv() {
		return pbOrDiv;
	}




	public void setPageOrDiv(List<PbOrDiv> pageOrDiv) {
		this.pbOrDiv = pageOrDiv;
	}
	

	
	
}
