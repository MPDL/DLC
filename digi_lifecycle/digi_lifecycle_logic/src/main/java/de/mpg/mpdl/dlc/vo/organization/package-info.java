@XmlSchema(
		   elementFormDefault = XmlNsForm.QUALIFIED,
		   xmlns = {
				   @XmlNs (prefix = "rdf", namespaceURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
				   @XmlNs (prefix = "foaf", namespaceURI = "http://xmlns.com/foaf/0.1/"),
				   @XmlNs (prefix = "mdou", namespaceURI = "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit"),
				   @XmlNs (prefix = "dc", namespaceURI="http://purl.org/dc/elements/1.1/"),
				   @XmlNs (prefix = "eterms", namespaceURI = "http://purl.org/escidoc/metadata/terms/0.1/"),
				   @XmlNs (prefix = "dcterms", namespaceURI = "http://purl.org/dc/terms/"),
				   @XmlNs (prefix = "kml", namespaceURI = "http://www.opengis.net/kml/2.2")
		   }
)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value=StringAdapter.class, type=String.class)
package de.mpg.mpdl.dlc.vo.organization;

import javax.xml.bind.annotation.XmlAccessType;


import javax.xml.bind.annotation.adapters.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNs;
import de.mpg.mpdl.dlc.vo.StringAdapter;