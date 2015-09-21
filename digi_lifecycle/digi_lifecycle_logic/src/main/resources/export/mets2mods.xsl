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
<!-- Transformation from a dlc item xml to a zvdd conform mets xml Author: 
	Friederike Kleinfercher (initial creation) -->

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:mods="http://www.loc.gov/mods/v3" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:dv="http://dfg-viewer.de/"
	exclude-result-prefixes="#all">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />


	<xsl:param name="itemId" select="'not defined'" />
	<xsl:param name="teiUrl" select="'not defined'" />
	<xsl:param name="metsUrl" select="'not defined'" />
	<xsl:param name="imageUrl" select="'not defined'" />
	<xsl:variable name="teiXml" select="document($teiUrl)" />
	<xsl:variable name="metsXml" select="document($metsUrl)" />

	<xsl:template match="/">
		<xsl:copy-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/node()"/>
	</xsl:template>


	<!-- start of zvdd mode -->

	<xsl:template match="@*|node()" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="#current" />
		</xsl:copy>
	</xsl:template>

	<!-- this should fix the invalid date format as well ... -->
	<xsl:template match="text()" mode="zvdd" priority="0">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<!-- do not copy empty elements, 'cause ZVDD will complain about -->
	<xsl:template
		match="*[not(node())] | *[not(node()[2]) and node()/self::text() and not(normalize-space()) ] "
		mode="zvdd" />

	<!-- ZVDD expects dateIssued in originInfo -->
	<xsl:template match="mods:originInfo" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="#current" />

			<xsl:if test="not(mods:dateIssued)">
				<mods:dateIssued>
					<xsl:attribute name="encoding">
						<xsl:value-of select="'w3cdtf'" />
					</xsl:attribute>
					<xsl:value-of select="'2000'" />
				</mods:dateIssued>
			</xsl:if>
		</xsl:copy>
	</xsl:template>

	<!-- subsequent titleInfo elements require a type attribute and a title 
		sub-element -->
	<xsl:template match="mods:titleInfo[position()>1]" mode="zvdd">
		<xsl:copy>
			<xsl:if test="not(@type)">
				<xsl:attribute name="type">
					<xsl:value-of select="'alternatve'" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="@*" mode="#current" />

			<xsl:if test="not(mods:title)">
				<mods:title>
					<xsl:value-of select="'4 the sake of zvdd'" />
				</mods:title>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="#current" />
		</xsl:copy>
	</xsl:template>

	<!-- identifier element must have type attribute -->
	<xsl:template match="mods:identifier" mode="zvdd">
		<xsl:copy>
			<xsl:if test="not(@type)">
				<xsl:attribute name="type">
					<xsl:value-of select="'dlc'" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="@*|node()" mode="#current" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="mods:namePart" mode="zvdd">
		<xsl:copy>
			<xsl:attribute name="type">
				<xsl:value-of select="'given'" />
			</xsl:attribute>
			<xsl:apply-templates select="@*|node()" mode="#current" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="mods:languageTerm/@authority" mode="zvdd">
		<xsl:attribute name="authority">
			<xsl:value-of select="'iso639-2b'" />
		</xsl:attribute>
	</xsl:template>

	<!-- copy only the 1st occurrence of physicalDescription, 'cause ZVDD allows 
		only one -->
	<!-- in addition physicalDescription must contain either extent or digitalOrigin -->
	<!-- changed 2015/05/26: copy only if physicalDescription contains extent 
		eöement -->
	<xsl:template match="mods:physicalDescription" mode="zvdd">
		<xsl:copy>
			<xsl:if test="mods:extent">
				<xsl:apply-templates select="@*|node()" mode="#current" />
			</xsl:if>
		</xsl:copy>
	</xsl:template>
	<!-- <xsl:template match="mods:physicalDescription[position()>1]" mode="zvdd" 
		/> -->
	<!-- end of zvdd mode -->


</xsl:stylesheet>