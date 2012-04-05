<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [<!ENTITY nbsp "&#160;">]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:tei="http://www.tei-c.org/ns/1.0" exclude-result-prefixes="tei">
	<xsl:output indent="no" method="html" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
	
	<xsl:template name="notessection">
		<xsl:if test="//tei:note">
			<hr class="subdivider" width="200" size="1" align="left"/>
			<div class="notes">
				<xsl:for-each select="//tei:note">
					<xsl:apply-templates select="." mode="shownotes"></xsl:apply-templates>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="structure">
		<html>
			<head>
				<title>
					<xsl:value-of select="normalize-space(//tei:titleStmt/tei:title)"/>
				</title>
				<link rel="stylesheet" type="text/css" href="test.css"/>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			</head>
			<body>
				<div id="header"></div>
				<div id="titel">
					<h1>
						<xsl:value-of select="normalize-space(//tei:titleStmt/tei:title)"/>
					</h1>
				</div>
				<div id="facs">
					<p>
						<xsl:value-of select="@facs"/>
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:value-of select="@facs"/>
							</xsl:attribute>
							<xsl:element name="img">
								<xsl:attribute name="src">
									<xsl:value-of select="@facs"/>
								</xsl:attribute>
								<xsl:attribute name="id">
									<xsl:text>bild</xsl:text>
								</xsl:attribute>
							</xsl:element>
						</xsl:element>
					</p>
				</div>
				<div id="transkription">
					<div id="text">
						<xsl:apply-templates select="//tei:TEI"></xsl:apply-templates>
						<br/>
						<xsl:call-template name="notessection"></xsl:call-template>
						<br/>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>

<!-- the result-document. -->
	<xsl:template match="/">
		<xsl:for-each select="//tei:pb">
			<xsl:call-template name="structure"></xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="text()">
		<xsl:value-of select="."/>
	</xsl:template>

    <!-- paragraphs...-->
	<xsl:template match="tei:p | tei:div">
		<p>
			<xsl:apply-templates/>
		</p>
	</xsl:template>

    <!-- linebreaks...-->
	<xsl:template match="tei:lb">
		<br/>
	</xsl:template>


    <!-- Notes of the document, for the moment, only a <p>. -->
	<xsl:template match="tei:note[@place='inline']">
		<p>
			<xsl:apply-templates/>
		</p>
	</xsl:template>
	
	
	<xsl:template match="tei:note[@place='foot']">
		<xsl:variable name="notenum" select="@n"/>
		<xsl:variable name="linklabel" select="normalize-space($notenum)"/>
		<sup>
			<a>
				<xsl:attribute name="name">refpoint-<xsl:value-of select="$linklabel"/>
				</xsl:attribute>
				<xsl:attribute name="href">#note-<xsl:value-of select="$linklabel"/>
				</xsl:attribute>
				<xsl:attribute name="title">Link to note <xsl:value-of select="$linklabel"/> 
        at the end of this document.</xsl:attribute>
				<xsl:value-of select="$linklabel"/>
			</a>
		</sup>
		<xsl:text></xsl:text>
	</xsl:template>
	
	<xsl:template match="tei:note" mode="shownotes">
		<xsl:variable name="notenum" select="@n"/>
		<xsl:variable name="linklabel" select="normalize-space($notenum)"/>
		<div class="footnote">
			<p>
				<a>
					<xsl:attribute name="id">note-<xsl:value-of select="$linklabel"/>
					</xsl:attribute>
					<xsl:attribute name="href">#refpoint-<xsl:value-of select="$linklabel"/>
					</xsl:attribute>
					<xsl:attribute name="title">
						<xsl:text>Link to insertion point of note in main 
       text.</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$notenum"/>
				</a>
				<xsl:text>. </xsl:text>
				<xsl:apply-templates mode="shownotes"/>
			</p>
			
		</div>
	</xsl:template>
	
	<xsl:template match="*" mode="shownotes">
		<xsl:copy>
			<xsl:apply-templates mode="shownotes"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="tei:lb" mode="shownotes">
		<br/>
	</xsl:template>


</xsl:stylesheet>
