<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [<!ENTITY nbsp "&#160;">]>
<xsl:stylesheet version="2.0" xmlns:xml="http://www.w3.org/XML/1998/namespace" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:tei="http://www.tei-c.org/ns/1.0" exclude-result-prefixes="tei">
	<xsl:output indent="no" method="html" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
	
	<xsl:param name="pbid"></xsl:param>
	
	<xsl:template name="notessection">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:if test="//tei:note[@place='foot']">
			<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
				<!-- 
				<hr class="subdivider" width="200" size="1" align="left"/>
				 -->
				 <br/>
				<div class="notes">
					<xsl:for-each select="//tei:note">
						<xsl:apply-templates select="." mode="shownotes">
							<xsl:with-param name="start" select="$start"/>
							<xsl:with-param name="end" select="$end"/>
						</xsl:apply-templates>
					</xsl:for-each>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<xsl:template name="structure">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<html>
			<head>
			<!-- 
				<title>
					<xsl:value-of select="normalize-space(//tei:titleStmt/tei:title[@type='main'])"/>
				</title>
				<link rel="stylesheet" type="text/css" href="test.css"/>
			 -->
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			</head>
			<body>
			<!-- 
				<div id="header"></div>
				<div id="titel">
					<h1>
						<xsl:value-of select="normalize-space(//tei:titleStmt/tei:title[@type='main'])"/>
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
									<xsl:text>image</xsl:text>
								</xsl:attribute>
							</xsl:element>
						</xsl:element>
					</p>
				</div>
			 -->
				<div id="transkription">
					<div id="text">
						<xsl:apply-templates select="/tei:TEI/tei:text">
							<xsl:with-param name="start" select="."/>
							<xsl:with-param name="end" select="following::tei:pb[1]"/>
						</xsl:apply-templates>
						
						<br/>
						<xsl:call-template name="notessection">
							<xsl:with-param name="start" select="."/>
							<xsl:with-param name="end" select="following::tei:pb[1]"/>
						</xsl:call-template>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	<!--
	<xsl:template match="/">
		<xsl:for-each select="//tei:pb">
			<xsl:if test="@xml:id=$pbid">
				<xsl:call-template name="structure"></xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	-->
	<xsl:template match="/">
		<xsl:for-each select="//tei:pb[@xml:id=$pbid]">
				<xsl:call-template name="structure"></xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="text()">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
			<xsl:value-of select="."/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="tei:p">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
			<p>
				<xsl:apply-templates>
					<xsl:with-param name="start" select="$start"/>
					<xsl:with-param name="end" select="$end"/>
				</xsl:apply-templates>
			</p>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="tei:lb">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
			<br/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="tei:div">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
			<p>
				<xsl:apply-templates>
					<xsl:with-param name="start" select="$start"/>
					<xsl:with-param name="end" select="$end"/>
				</xsl:apply-templates>
			</p>
		</xsl:if>
	</xsl:template>

	
	<xsl:template match="tei:note[@place='inline']">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
			<p>
				<xsl:apply-templates>
					<xsl:with-param name="start" select="$start"/>
					<xsl:with-param name="end" select="$end"/>
				</xsl:apply-templates>
			</p>
		</xsl:if>
	</xsl:template>

	<xsl:template match="tei:note[@resp]">
		
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:variable name="monRef" select="@xml:id"/>
		<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
			<a class="info_anmerkung" href="#">
				<sup>
					<xsl:value-of select="count(preceding::tei:note) + 1"/>
				</sup>
				<span>
					<xsl:value-of select="normalize-space(//tei:note[@xml:id=$monRef]/text())"/>
				</span>
			</a>
		</xsl:if>
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
				</xsl:attribute>
				<xsl:value-of select="$linklabel"/>
			</a>
		</sup>
		<xsl:text></xsl:text>
	</xsl:template>
	
	<xsl:template match="tei:note" mode="shownotes">
		<xsl:param name="start"/>
		<xsl:param name="end"/>
		<xsl:variable name="notenum" select="@n"/>
		<xsl:variable name="linklabel" select="normalize-space($notenum)"/>
		<xsl:if test="not(following::tei:pb[@xml:id=$start/@xml:id]) and not(preceding::tei:pb[@xml:id=$end/@xml:id])">
			<div class="footnote">
				<p>
					<a>
						<xsl:attribute name="id">note-<xsl:value-of select="$linklabel"/>
						</xsl:attribute>
						<xsl:attribute name="href">#refpoint-<xsl:value-of select="$linklabel"/>
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:text>Link to insertion point.</xsl:text>
						</xsl:attribute>
						<xsl:value-of select="$notenum"/>
					</a>
					<xsl:text>. </xsl:text>
					<xsl:apply-templates mode="shownotes"/>
				</p>
			</div>
		</xsl:if>
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
