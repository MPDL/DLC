<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:tei="http://www.tei-c.org/ns/1.0">
		
		<xsl:output indent="yes"/>
		
		<!-- Copy these elements into the TEI SD, ignore others -->
		<xsl:template match="/tei:TEI|tei:text|tei:front|tei:body|tei:back|tei:div|tei:pb|tei:titlePage|tei:titlePage/tei:docTitle|tei:figure|tei:div/tei:byline">
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates />
			</xsl:copy>
		</xsl:template>
		
		
		
		<!--for head and title tags: copy tag with all attributes, copy the inner text content, replace <lb/> with a blank -->
		<xsl:template match="tei:div/tei:head|tei:div/tei:byline/tei:docAuthor|tei:docTitle/tei:titlePart|tei:titlePage/tei:titlePart|tei:figure/tei:head|tei:figure/tei:figDesc|tei:figure/tei:caption|tei:figure/tei:persName">
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
		
		<!--Copy pbs -->
		<!--
		<xsl:template match="tei:pb" mode="textContent">
			<xsl:apply-templates select="."/>
		</xsl:template>
		-->
		
		<xsl:template match="*" mode="textContent">
			<xsl:apply-templates mode="textContent"/>
		</xsl:template>
		
		<!--Go deeper for all other content -->
		<xsl:template match="*">
			<xsl:apply-templates/>
		</xsl:template>
		
		<!--Ignore all other text content -->
		<xsl:template match="text()">
		</xsl:template>

		
</xsl:stylesheet>
