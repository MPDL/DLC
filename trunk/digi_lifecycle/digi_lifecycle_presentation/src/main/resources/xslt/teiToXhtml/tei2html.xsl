<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:te="http://www.tei-c.org/ns/1.0"
    xmlns:xi="http://www.w3.org/2001/XInclude" version="1.0" exclude-result-prefixes="te xi">
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
        <desc>
            <p>Creating simpler HTML from generic TEI-P5 input</p>
            <p>Klaus E. Werner, MPI Rom</p>
        </desc>
    </doc>
    <xsl:output encoding="UTF-8" indent="yes" method="xml" xml:space="default"
        media-type="application/xhtml+xml" omit-xml-declaration="yes" />
    <xsl:variable name="title" select="te:TEI/te:teiHeader/te:fileDesc/te:titleStmt/te:title[1]" />
    <xsl:variable name="copyright"
        select="te:TEI/te:teiHeader/te:fileDesc/te:publicationStmt/te:publisher/te:orgName" />
    <xsl:variable name="lang" select="te:TEI/@xml:lang" />
    <!-- building the basic html -->
    <xsl:template match="te:TEI">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html></xsl:text>
        <html lang="{$lang}">
            <head>
                <meta charset="utf-8" />
                <title>
                    <xsl:value-of select="$title" />
                </title>
                <meta name="viewport" content="width=device-width" />
                <style type="text/css">
                    ins{
                        text-align:right;
                        font-style:italic;
                        display:inline-block;
                        float:right;
                        text-decoration:none;
                    }</style>
            </head>
            <body>
                <header>
                    <xsl:value-of select="$title" />
                </header>
                <div role="main">
                    <xsl:apply-templates />
                </div>
                <footer>
                    <xsl:value-of select="$copyright" />
                </footer>
            </body>
        </html>
    </xsl:template>
    <!-- elements we ignore completely (for now) -->
    <xsl:template match="te:teiHeader|te:figDesc" />
    <!-- elements we ignore as such but proceed to other nodes further down -->
    <xsl:template match="te:tei|te:text|te:front|te:body|te:back">
        <xsl:apply-templates />
    </xsl:template>
    <!-- elements we want to represent as blocks -->
    <xsl:template
        match="te:divGen|te:p|te:titlePage|te:docTitle|te:docImprint|te:publisher|te:pubPlace|te:titlePart|te:div|te:epigraph|te:quote|te:bibl|te:lg|te:l|te:list|te:item|te:figDesc">
        <xsl:element name="div">
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    <!-- elements we want to represent inline -->
    <xsl:template match="te:hi">
        <em>
            <xsl:apply-templates />
        </em>
    </xsl:template>
    <!-- some special elements -->
    <xsl:template match="te:head">
        <br />
        <xsl:element name="div">
            <xsl:apply-templates />
        </xsl:element>
        <br />
    </xsl:template>
    <xsl:template match="te:ref">
        <a href="{@target}">
            <xsl:apply-templates />
        </a>
    </xsl:template>
    <xsl:template match="te:lb">
        <br />
    </xsl:template>
    <xsl:template match="te:pb">
        <hr />
        <xsl:if test="number(@n)">
            <ins id="{@xml:id}">
                <xsl:value-of select="@n" />
            </ins>
        </xsl:if>
    </xsl:template>
    <xsl:template match="te:table">
        <table>
            <xsl:apply-templates />
        </table>
    </xsl:template>
    <xsl:template match="te:row">
        <tr>
            <xsl:apply-templates />
        </tr>
    </xsl:template>
    <xsl:template match="te:cell">
        <td>
            <xsl:apply-templates />
        </td>
    </xsl:template>
    <!-- rules for figures -->
    <xsl:template match="te:figure">
        <object type="image/jpeg">
            <xsl:if test="te:head">
                <xsl:apply-templates select="te:head" />
            </xsl:if>
            <img src="{concat(te:graphic/@url,'.jpg')}" />
            <xsl:apply-templates select="te:p" />
        </object>
    </xsl:template>
</xsl:stylesheet>
