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
	version="1.0" xmlns:tei="http://www.tei-c.org/ns/1.0">
	<xsl:output encoding="UTF-8" indent="yes" media-type="text/xml"
		method="xml" />

	<xsl:template match="tei:pb">
		<xsl:choose>
			<xsl:when
				test="(parent::tei:div or parent::tei:docTitle or parent::tei:titlePage) and not(preceding-sibling::*)" />
			
			<xsl:when test="(parent::tei:titlePage) and (preceding-sibling::tei:titlePart) and (following-sibling::tei:titlePart) " >
				<xsl:copy-of select="following-sibling::tei:titlePart" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="tei:titlePart">
		<xsl:choose>
			<xsl:when test="(parent::tei:titlePage) and (preceding-sibling::tei:pb) and not (following-sibling::tei:pb)" />
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tei:div">
		<xsl:choose>
			<xsl:when test="tei:pb[not(preceding-sibling::*)]">
				<xsl:copy-of select="tei:pb[1]" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tei:titlePage">
		<xsl:choose>
			<xsl:when test="tei:docTitle/tei:pb[not(preceding-sibling::*)]">
				<xsl:copy-of select="tei:docTitle/tei:pb[1]" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>
			<xsl:when test="tei:pb[not(preceding-sibling::*)]">
				<xsl:copy-of select="tei:pb[1]" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>

			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>

		</xsl:choose>
	</xsl:template>


	<xsl:template match="@*|*|processing-instruction()|comment()|text()">
		<xsl:copy>
			<xsl:apply-templates select="@*|*|processing-instruction()|comment()|text()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
