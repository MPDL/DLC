<!--
  CDDL HEADER START
  The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
  
  You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
  See the License for the specific language governing permissions and limitations under the License.
  
  When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  CDDL HEADER END
  
  Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
  All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:tei="http://www.tei-c.org/ns/1.0"
		xmlns:mets="http://www.loc.gov/METS/"
		xmlns:exsl="http://exslt.org/common">
		
		<xsl:output indent="yes"/>
		
		<xsl:param name="mets"/>

		
		<xsl:template match="tei:pb">
			<xsl:variable name="pbId" select="@xml:id"/>
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:attribute name="facs">
					<xsl:value-of select="$mets//mets:div[@ID=$pbId]/@CONTENTIDS"/>
				</xsl:attribute>
			</xsl:copy>
		</xsl:template>
		
		<xsl:template match="*|@*|text()">
			<xsl:copy>
				<xsl:apply-templates select="*|@*|text()"/>
			</xsl:copy>
		</xsl:template>
		
		
		
		
		
		
		
		
</xsl:stylesheet>
