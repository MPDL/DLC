<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xml="http://www.w3.org/XML/1998/namespace" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.tei-c.org/ns/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:ntei="http://www.tei-c.org/ns/notTEI" exclude-result-prefixes="tei">
	<xsl:output indent="yes" method="xml" encoding="UTF-8"/>
	
	<xsl:param name="pageId"></xsl:param>
	
	<xsl:template name="structure">
		<TEI>
			<teiHeader/>
			<text>
			<body>
				<xsl:apply-templates select="."/>
				</body>
			</text>
		</TEI>
	</xsl:template>
	
	<xsl:template match="/">
		<xsl:for-each select="//ntei:page[@xml:id=$pageId]">
			<xsl:call-template name="structure"></xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="node()">
		<xsl:copy-of select="./*"/>
	</xsl:template>

</xsl:stylesheet>
