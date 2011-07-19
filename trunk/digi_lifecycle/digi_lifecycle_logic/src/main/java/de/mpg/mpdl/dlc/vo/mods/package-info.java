@XmlSchema(
		   elementFormDefault = XmlNsForm.QUALIFIED,
		   xmlns = {
				   @XmlNs (prefix = "mods", namespaceURI="http://www.loc.gov/mods/v3"),
				   @XmlNs (prefix = "xlink", namespaceURI = "http://www.w3.org/1999/xlink")
		   }
)
package de.mpg.mpdl.dlc.vo.mods;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNs;