@XmlSchema(
		   elementFormDefault = XmlNsForm.QUALIFIED,
		   xmlns = {
				   @XmlNs (prefix = "mods", namespaceURI="http://www.loc.gov/mods/v3"),
				   @XmlNs (prefix = "xlink", namespaceURI = "http://www.w3.org/1999/xlink"),
				   @XmlNs (prefix = "mets", namespaceURI = "http://www.loc.gov/METS/")
		   }
)
@XmlAccessorType(XmlAccessType.FIELD)
package de.mpg.mpdl.dlc.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNs;