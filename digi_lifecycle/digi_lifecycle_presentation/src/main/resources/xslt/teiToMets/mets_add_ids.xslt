<?xml version="1.0" encoding="UTF-8"?>
<!--
  CDDL HEADER START
  The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
  
  You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
  See the License for the specific language governing permissions and limitations under the License.
  
  When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  CDDL HEADER END
  
  Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
  All rights reserved. Use is subject to license terms.

  Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
  institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
  (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
  for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
  Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
  DLC-Software are requested to include a "powered by DLC" on their webpage,
  linking to the DLC documentation (http://dlcproject.wordpress.com/).
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:mets="http://www.loc.gov/METS/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">

    
    <xsl:output indent="yes"/>
    
   <xsl:template match="@*|*|processing-instruction()|comment()|text()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|processing-instruction()|comment()|text()"/>
    </xsl:copy>
  </xsl:template>
    
   <xsl:template match="mets:div">
    <xsl:copy>
      <xsl:if test="not(@ID) or @ID=''">
        <xsl:attribute name="ID">
          <xsl:value-of select="generate-id()"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="@*|*|processing-instruction()|comment()|text()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
