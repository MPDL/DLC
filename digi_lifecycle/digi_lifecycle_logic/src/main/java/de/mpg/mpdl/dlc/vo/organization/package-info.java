/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
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

import de.mpg.mpdl.dlc.vo.util.StringAdapter;
