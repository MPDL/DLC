<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:tei="http://www.tei-c.org/ns/1.0">
	<xsl:output encoding="UTF-8" indent="yes" media-type="text/xml"
		method="xml" />

	<xsl:template match="tei:pb">
		<xsl:choose>
			<xsl:when
				test="(parent::tei:div or parent::tei:docTitle or parent::tei:titlePage) and not(preceding-sibling::*)" />
			
			<xsl:when test="(parent::tei:titlePage) and (preceding-sibling::tei:titlePart) and (following-sibling::tei:titlePart) " >
				<xsl:copy-of select="following-sibling::tei:titlePart" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="tei:titlePart">
		<xsl:choose>
			<xsl:when test="(parent::tei:titlePage) and (preceding-sibling::tei:pb) and not (following-sibling::tei:pb)" />
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tei:div">
		<xsl:choose>
			<xsl:when test="tei:pb[not(preceding-sibling::*)]">
				<xsl:copy-of select="tei:pb[1]" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tei:titlePage">
		<xsl:choose>
			<xsl:when test="tei:docTitle/tei:pb[not(preceding-sibling::*)]">
				<xsl:copy-of select="tei:docTitle/tei:pb[1]" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>
			<xsl:when test="tei:pb[not(preceding-sibling::*)]">
				<xsl:copy-of select="tei:pb[1]" />
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:when>

			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates
						select="@*|*|processing-instruction()|comment()|text()" />
				</xsl:copy>
			</xsl:otherwise>

		</xsl:choose>
	</xsl:template>


	<xsl:template match="@*|*|processing-instruction()|comment()|text()">
		<xsl:copy>
			<xsl:apply-templates select="@*|*|processing-instruction()|comment()|text()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>