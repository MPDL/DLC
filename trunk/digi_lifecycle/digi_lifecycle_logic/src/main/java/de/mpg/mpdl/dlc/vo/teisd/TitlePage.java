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
	
	
	@XmlPath("/tei:docTitle/tei:titlePart/text()")
	private String head;
	
	

	
	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	
}
