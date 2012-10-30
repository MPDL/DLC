<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:tei="http://www.tei-c.org/ns/1.0">
		
		<xsl:output indent="yes"/>
		
		<!-- Copy these elements into the TEI SD, ignore others -->
		<xsl:template match="/tei:TEI|tei:text|tei:front|tei:body|tei:back|tei:div|tei:pb|tei:titlePage|tei:docTitle|tei:figure">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates />
			</xsl:copy>
		</xsl:template>
		
		<xsl:template match="tei:figure"/>
		
		
		
		<!--for head and title tags: copy tag with all attributes, copy the inner text content, replace <lb/> with a blank -->
		<xsl:template match="tei:head|tei:titlePart|tei:figDesc|tei:caption">
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
		
		<xsl:template match="*" mode="textContent">
			<xsl:apply-templates mode="textContent"/>
		</xsl:template>
		
		<!--Ignore all other text content -->
		<xsl:template match="text()">
		</xsl:template>

		
</xsl:stylesheet>
