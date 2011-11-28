@XmlSchema(
		   elementFormDefault = XmlNsForm.QUALIFIED,
		   xmlns = {
				   @XmlNs (prefix = "mods", namespaceURI="http://www.loc.gov/mods/v3"),
				   @XmlNs (prefix = "xlink", namespaceURI = "http://www.w3.org/1999/xlink"),
				   @XmlNs (prefix = "mets", namespaceURI = "http://www.loc.gov/METS/")
		   }
)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value=StringAdapter.class, type=String.class)
package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.adapters.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNs;
import de.mpg.mpdl.dlc.vo.StringAdapter;