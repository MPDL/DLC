<?xml version="1.0"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="text"/>
	<xsl:variable name="newline">
		<xsl:text></xsl:text>
	</xsl:variable>
	<xsl:key name="elements" match="*" use="name()"/>
	<xsl:template match="/">
		<xsl:value-of select="$newline"/>
		<xsl:text>Summary of Elements</xsl:text>
		<xsl:value-of select="$newline"/>
		<xsl:value-of select="$newline"/>
		<xsl:for-each select="//*[generate-id(.)=generate-id(key('elements',name())[1])]">
			<xsl:sort select="name()"/>
			<xsl:for-each select="key('elements', name())">
				<xsl:if test="position()=1">
					<xsl:text>Element </xsl:text>
					<xsl:value-of select="name()"/>
					<xsl:text> occurs </xsl:text>
					<xsl:value-of select="count(//*[name()=name(current())])"/>
					<xsl:text> times.</xsl:text>
					<xsl:value-of select="$newline"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:value-of select="$newline"/>
		<xsl:text>There are </xsl:text>
		<xsl:value-of select="count(//*)"/>
		<xsl:text> elements in all.</xsl:text>
	</xsl:template>
</xsl:stylesheet>
