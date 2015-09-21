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
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:mods="http://www.tei-c.org/ns/1.0" exclude-result-prefixes="mods">

	<xsl:output indent="no" method="xml" encoding="UTF-8"
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<xsl:key name="elements" match="*" use="name()" />

	<xsl:template match="/">
		<html>
			<head>
				<title>
					Bibliographic Metadata
        </title>
			</head>
			<body>
				<h1>
					Bibliographic Metadata
        </h1>
				<table>
					<tr>
						<td>label(s)</td>
						<td>(mab)</td>
						<td>value</td>
					</tr>
					<xsl:for-each select="//*[generate-id(.)=generate-id(key('elements',name())[1])]">
					<xsl:for-each select="key('elements', name())">
					<tr>
					<td>
					<!-- 
					<xsl:for-each select="ancestor-or-self::*">
						<xsl:value-of select="name()"/>
						<xsl:if test="position() != last()"> / </xsl:if>
					</xsl:for-each>
					 -->
					<xsl:for-each select="ancestor-or-self::*">
						<xsl:if test="position() = last()">
							<xsl:value-of select="name()"/>
						</xsl:if>
					</xsl:for-each>
					</td>
					<!-- 
					<xsl:variable name="ancestors" select="count(ancestor::*)"/>
						<tr>
						<xsl:if test="$ancestors=1">
							<td><xsl:value-of select="concat(name(./..), concat(' / ', name()))" /></td>
						</xsl:if>
						<xsl:if test="$ancestors=2">
							<td><xsl:value-of select="concat(name(./../..), (concat(name(./..), (concat(' / ', name())))))" /></td>
						</xsl:if>
						<xsl:if test="$ancestors=3">
							<td><xsl:value-of select="concat(name(./..), concat(' / ', name()))" /></td>
						</xsl:if>
						<xsl:if test="$ancestors=4">
							<td><xsl:value-of select="concat(name(./..), concat(' / ', name()))" /></td>
						</xsl:if>
						 -->
							<td>
								<xsl:if test="@ID">
									<xsl:value-of select="@ID" />
								</xsl:if>
								<xsl:if test="@displayLabel">
									<xsl:value-of select="@displayLabel" />
								</xsl:if>
								<xsl:if test="@source">
									<xsl:value-of select="@source" />
								</xsl:if>
							</td>
							<td>
								<xsl:value-of select="text()" />
							</td>
						</tr>
					</xsl:for-each>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>



</xsl:stylesheet>
