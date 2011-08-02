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
package de.mpg.mpdl.dlc.vo;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
