package de.mpg.mpdl.dlc.vo.mets;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;



public class MetsSmLink {

	//@XmlPath("@xlink:from/text()")
	//private String fromId;
	
	@XmlPath("@xlink:from/text()")
	@XmlIDREF
	private MetsDiv from;
	
	@XmlPath("@xlink:to/text()")
	@XmlIDREF
	private Page to;

	public MetsDiv getFrom() {
		return from;
	}

	public void setFrom(MetsDiv from) {
		this.from = from;
	}

	public Page getTo() {
		return to;
	}

	public void setTo(Page to) {
		this.to = to;
	}
}
