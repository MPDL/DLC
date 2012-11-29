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
