package de.mpg.mpdl.dlc.vo.teisd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;

@XmlRootElement(name = "div", namespace = "http://www.tei-c.org/ns/1.0")
public class TitlePage extends Div {
	
	
	@XmlPath("/tei:docTitle/tei:titlePart")
	private List<DocTitle> docTitles;
	
	private List<DocTitle> titleParts;


	public TitlePage()
	{
		super(ElementType.TITLE_PAGE);
	}
	
	public TitlePage(TitlePage original)
	{
		super(original);
		
		List<DocTitle> clonedDocTitles = new ArrayList<DocTitle>();
		List<DocTitle> clonedTitleParts = new ArrayList<DocTitle>();
		
		for(DocTitle originalDt : original.getDocTitles())
		{
			DocTitle clonedDt = new DocTitle(originalDt);
			clonedDocTitles.add(clonedDt);
		}
		
		for(DocTitle originalDt : original.getTitleParts())
		{
			DocTitle clonedDt = new DocTitle(originalDt);
			clonedTitleParts.add(clonedDt);
		}
		
		this.setDocTitles(clonedDocTitles);
		this.setTitleParts(clonedTitleParts);
	}
	
	public List<DocTitle> getDocTitles() {
		return docTitles;
	}


	public void setDocTitles(List<DocTitle> docTitles) {
		this.docTitles = docTitles;
	}


	public List<DocTitle> getTitleParts() {
		return titleParts;
	}


	public void setTitleParts(List<DocTitle> titleParts) {
		this.titleParts = titleParts;
	}


	public static List<DocTitle> docTitleListFactory()
	{
		return new ArrayList<DocTitle>();
	}
	
	

	


	
}
