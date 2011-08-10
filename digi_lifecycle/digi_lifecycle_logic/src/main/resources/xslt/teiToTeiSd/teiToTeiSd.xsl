<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:tei="http://www.tei-c.org/ns/1.0">
		
		
		<xsl:template match="/tei:TEI|tei:text|tei:front|tei:body|tei:back|tei:div|tei:pb|tei:titlePage|tei:docTitle">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates />
			</xsl:copy>
		</xsl:template>
		
		<xsl:template match="tei:head|tei:titlePart">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:value-of select="."/>
			</xsl:copy>
		</xsl:template>
		
		<xsl:template match="text()">
		</xsl:template>

		
</xsl:stylesheet>
