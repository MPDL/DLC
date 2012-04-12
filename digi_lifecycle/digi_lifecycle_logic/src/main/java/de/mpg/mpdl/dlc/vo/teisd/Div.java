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
		this.setAuthor1(original.getAuthor1());
		this.setAuthor2(original.getAuthor2());
		this.setAuthor3(original.getAuthor3());
		
		this.setAuthor1inv(original.getAuthor1inv());
		this.setAuthor2inv(original.getAuthor2inv());
		this.setAuthor3inv(original.getAuthor3inv());

		List<String> head = new ArrayList<String>();
		head.addAll(original.getHead());
		this.setHead(head);
	}

	
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
	
	
	



	
	

	
}
