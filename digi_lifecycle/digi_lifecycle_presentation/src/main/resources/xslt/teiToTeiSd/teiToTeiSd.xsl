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
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:tei="http://www.tei-c.org/ns/1.0">
		
		<xsl:output indent="yes"/>
		
		<!-- Copy these elements into the TEI SD, ignore others -->
		
		<xsl:template match="/tei:TEI">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates select="tei:text"/>
			</xsl:copy>
		</xsl:template>
		
		
		
		<xsl:template match="tei:group|tei:text|tei:front|tei:body|tei:back|tei:div|tei:pb|tei:titlePage|tei:titlePage/tei:docTitle|tei:figure|tei:div/tei:byline">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates />
			</xsl:copy>
		</xsl:template>
		
		
		
		<!--for head and title tags: copy tag with all attributes, copy the inner text content, replace <lb/> with a blank -->
		<xsl:template match="tei:div/tei:head|tei:div/tei:byline/tei:docAuthor|tei:docTitle/tei:titlePart|tei:titlePage/tei:titlePart|tei:figure/tei:head|tei:figure/tei:figDesc|tei:figure/tei:caption|tei:figure/tei:persName">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates mode="textContent"/>
			</xsl:copy>
		</xsl:template>
		
		<xsl:template match="text()" mode="textContent">
			<xsl:value-of select="."/>
		</xsl:template>
		
		<xsl:template match="tei:lb" mode="textContent">
			<xsl:value-of select="' '"/>
		</xsl:template>
		
		<!--Copy pbs -->
		<!--
		<xsl:template match="tei:pb" mode="textContent">
			<xsl:apply-templates select="."/>
		</xsl:template>
		-->
		
		<xsl:template match="*" mode="textContent">
			<xsl:apply-templates mode="textContent"/>
		</xsl:template>
		
		<!--Go deeper for all other content -->
		<xsl:template match="*">
			<xsl:apply-templates/>
		</xsl:template>
		
		<!--Ignore all other text content -->
		<xsl:template match="text()">
		</xsl:template>

		
</xsl:stylesheet>
