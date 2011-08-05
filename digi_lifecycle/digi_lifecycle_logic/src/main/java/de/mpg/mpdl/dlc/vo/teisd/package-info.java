@XmlSchema(
		   elementFormDefault = XmlNsForm.QUALIFIED,
		   xmlns = {
				   @XmlNs (prefix = "xlink", namespaceURI="http://www.w3.org/1999/xlink"),
				   @XmlNs (prefix = "tei", namespaceURI = "http://www.tei-c.org/ns/1.0")
		   }
)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value=StringAdapter.class, type=String.class)
package de.mpg.mpdl.dlc.vo.teisd;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import de.mpg.mpdl.dlc.vo.*;
