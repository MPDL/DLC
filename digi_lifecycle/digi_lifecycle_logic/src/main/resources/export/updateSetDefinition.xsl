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
	exclude-result-prefixes="#all">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:strip-space elements="all" />


	<xsl:param name="ctxId" select="'not defined'" />
	<xsl:param name="ctxName" select="'not defined'" />
	<xsl:variable name="ctx" select="replace($ctxId, 'escidoc:', 'ctx_')" />
	<xsl:variable name="luceneQuery"
		select="concat('/text//mets/dmdSec/mdWrap/xmlData/mods/extension/context/@id:&quot;', $ctxId, '&quot;')" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="text()" priority="0">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="ListSets[not(set/setSpec[text() = $ctx])]">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
			<set>
				<setSpec>
					<xsl:value-of select="$ctx" />
				</setSpec>
				<setName>
					<xsl:value-of select="$ctxName" />
				</setName>
				<virtualSearchField>
					<xsl:attribute name="field">
						<xsl:value-of select="'setSpec'" />
					</xsl:attribute>
					<virtualSearchTermDefinition>
						<xsl:attribute name="term">
						<xsl:value-of select="$ctx" />
					</xsl:attribute>
						<Query>
							<booleanQuery>
								<xsl:attribute name="type">
						<xsl:value-of select="'OR'" />
					</xsl:attribute>
								<textQuery>
									<xsl:attribute name="field">
						<xsl:value-of select="'xmlFormat'" />
					</xsl:attribute>
									<xsl:attribute name="type">
						<xsl:value-of select="'matchKeyword'" />
					</xsl:attribute>
									<xsl:attribute name="excludeOrRequire">
						<xsl:value-of select="'require'" />
					</xsl:attribute>
									<xsl:value-of select="'mets'" />
								</textQuery>
								<luceneQuery>
									<xsl:value-of select="$luceneQuery" />
								</luceneQuery>
							</booleanQuery>
						</Query>
					</virtualSearchTermDefinition>
				</virtualSearchField>
			</set>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>