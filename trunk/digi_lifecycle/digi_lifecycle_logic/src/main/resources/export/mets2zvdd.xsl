<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:mets="http://www.loc.gov/METS/" xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:dv="http://dfg-viewer.de/" xmlns:dlc="http://dlc.mpdl.mpg.de/v1" exclude-result-prefixes="#all">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:strip-space elements="*"/>

	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods">
	<!-- 
		<xsl:variable name="related_title" select="normalize-space(./mods:relatedItem[@type='host']/mods:titleInfo/mods:title)"/>
		<xsl:variable name="part_detail" select="normalize-space(./mods:part/mods:detail/mods:number)"/>
	 -->
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />
			<!-- 
			<xsl:apply-templates select="@*|node()" mode="zvdd">
				<xsl:with-param name="related_title" select="$related_title" tunnel="yes"/>
				<xsl:with-param name="part_detail" select="$part_detail" tunnel="yes"/>
			</xsl:apply-templates>
			 -->
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="@*|node()" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="*[not(node())] | *[not(node()[2]) and node()/self::text() and not(normalize-space()) ]" priority="1" mode="zvdd"/>
	
	<xsl:template match="*[normalize-space() = '']" priority="0" mode="zvdd"/>
	 
	<xsl:template match="mods:relatedItem[not(@type = 'host')]" priority="4" mode="zvdd"/>
	
	<xsl:template match="mods:relatedItem[@type = 'host']" mode="zvdd">
		<!-- 
		<xsl:param name="related_title" tunnel="yes"/>
		<xsl:param name="part_detail" tunnel="yes"/>
		 -->
		<xsl:variable name="title" select="normalize-space(./mods:titleInfo/mods:title)"/>
		<xsl:variable name="part" select="normalize-space(../mods:part/mods:detail/mods:number)"/>

		<mods:titleInfo>
			<xsl:attribute name="type">
				<xsl:value-of select="'alternative'"/>
			</xsl:attribute>
			<mods:title>
				<xsl:value-of select="concat($title, ' - ', $part)"/>
			</mods:title>
		</mods:titleInfo>
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd"/>
		</xsl:copy>	
	</xsl:template>
	
	<xsl:template match="mods:dateIssued[not(@keyDate)]" mode="zvdd">
		<xsl:copy>
			<xsl:attribute name="keyDate">
				<xsl:value-of select="'yes'"/>
			</xsl:attribute>
			<xsl:apply-templates select="@*|node()" mode="zvdd"/>
		</xsl:copy>
	</xsl:template>
	
	

</xsl:stylesheet>


