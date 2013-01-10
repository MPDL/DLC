package de.mpg.mpdl.dlc.vo.teisd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;

@XmlRootElement(name = "div", namespace = "http://www.tei-c.org/ns/1.0")
public class Div extends PbOrDiv {
	
	
	/*
	@XmlAttribute(name="author1")
	private String author1;
	
	@XmlAttribute(name="author2")
	private String author2;
	
	@XmlAttribute(name="author3")
	private String author3;
	
	
	
	@XmlAttribute(name="author1inv")
	private String author1inv;
	
	@XmlAttribute(name="author2inv")
	private String author2inv;
	
	@XmlAttribute(name="author3inv")
	private String author3inv;
	
	
	@XmlAttribute(name="author1pnd")
	private String author1pnd;
	
	@XmlAttribute(name="author2pnd")
	private String author2pnd;
	
	@XmlAttribute(name="author3pnd")
	private String author3pnd;
	
	*/
	
	private List<DocAuthor> docAuthors = new ArrayList<DocAuthor>();
	
	@XmlPath("tei:head/text()")
	private List<String> head = new ArrayList<String>();
	
	
	public Div() {
		super(ElementType.DIV);
		// TODO Auto-generated constructor stub
	}
	
	public Div(ElementType elType) {
		super(elType);
		// TODO Auto-generated constructor stub
	}
	
	public Div(Div original)
	{
		super(original);
		this.setElementType(ElementType.DIV);
		
		/*
		this.setAuthor1(original.getAuthor1());
		this.setAuthor2(original.getAuthor2());
		this.setAuthor3(original.getAuthor3());
		
		this.setAuthor1inv(original.getAuthor1inv());
		this.setAuthor2inv(original.getAuthor2inv());
		this.setAuthor3inv(original.getAuthor3inv());
		
		this.setAuthor1pnd(original.getAuthor1pnd());
		this.setAuthor2pnd(original.getAuthor2pnd());
		this.setAuthor3pnd(original.getAuthor3pnd());

*/
		
		List<DocAuthor> docAuthorList = new ArrayList<DocAuthor>();
		
		for(DocAuthor originalDocAuthor : original.getDocAuthors())
		{
			docAuthorList.add(new DocAuthor(originalDocAuthor));
		}
		this.setDocAuthors(docAuthorList);
		
		List<String> head = new ArrayList<String>();
		head.addAll(original.getHead());
		this.setHead(head);
	}

	/*
	public String getAuthor1() {
		return author1;
	}

	public void setAuthor1(String author1) {
		this.author1 = author1;
	}

	public String getAuthor2() {
		return author2;
	}

	public void setAuthor2(String author2) {
		this.author2 = author2;
	}

	public String getAuthor3() {
		return author3;
	}

	public void setAuthor3(String author3) {
		this.author3 = author3;
	}

	public String getAuthor1inv() {
		return author1inv;
	}

	public void setAuthor1inv(String author1inv) {
		this.author1inv = author1inv;
	}

	public String getAuthor2inv() {
		return author2inv;
	}

	public void setAuthor2inv(String author2inv) {
		this.author2inv = author2inv;
	}

	public String getAuthor3inv() {
		return author3inv;
	}

	public void setAuthor3inv(String author3inv) {
		this.author3inv = author3inv;
	}

	public String getAuthor1pnd() {
		return author1pnd;
	}

	public void setAuthor1pnd(String author1pnd) {
		this.author1pnd = author1pnd;
	}

	public String getAuthor2pnd() {
		return author2pnd;
	}

	public void setAuthor2pnd(String author2pnd) {
		this.author2pnd = author2pnd;
	}

	public String getAuthor3pnd() {
		return author3pnd;
	}

	public void setAuthor3pnd(String author3pnd) {
		this.author3pnd = author3pnd;
	}

*/
	public List<String> getHead() {
		return head;
	}

	public void setHead(List<String> head) {
		this.head = head;
	}

	public static List<String> headListFactory()
	{
		return new ArrayList<String>();
	}
	
	public static List<DocAuthor> docAuthorListFactory()
	{
		return new ArrayList<DocAuthor>();
	}

	public List<DocAuthor> getDocAuthors() {
		return docAuthors;
	}

	public void setDocAuthors(List<DocAuthor> docAuthors) {
		this.docAuthors = docAuthors;
	}
	
	
	
	public boolean isNotEmptyDocAuthors()
	{
		if(docAuthors!=null)
		{
			for(DocAuthor docAuthor: docAuthors)
			{
				if(!docAuthor.isEmpty())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isNotEmptyHead()
	{
		if(head!=null)
		{
			for(String h: head)
			{
				if(!h.trim().isEmpty())
				{
					System.out.println("isNotEmptyhead true");
					return true;
				}
			}
		}
		System.out.println("isNotEmptyhead false");
		return false;
	}


	
	

	
}
