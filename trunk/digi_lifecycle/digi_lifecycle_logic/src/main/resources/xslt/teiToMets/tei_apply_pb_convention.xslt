<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" 
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:xi="http://www.w3.org/2001/XInclude"
    exclude-result-prefixes="xd">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>For use in <xd:b>TEI</xd:b>: Putting PB-element in front of DIV-element</xd:p>
            <xd:p><xd:b>Created on:</xd:b> Sep 25, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> kew</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:output encoding="UTF-8" indent="yes" media-type="text/xml" method="xml"/>
    <xsl:template match="tei:TEI">
        <xsl:element name="TEI" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:apply-templates select="*"></xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*[not(local-name()='pb' or local-name()='div' or local-name()='titlePage')]">
        <xsl:call-template name="copy"/>
    </xsl:template>

<xsl:template name="copy">
    <xsl:element name="{name(.)}" xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:for-each select="attribute::*">
            <xsl:attribute name="{name(.)}">
                <xsl:value-of select="."/>
            </xsl:attribute>
        </xsl:for-each>
        <xsl:apply-templates/>
    </xsl:element>
    </xsl:template>
 
    <xsl:template match="tei:pb">
        <xsl:choose>
            <xsl:when test="parent::tei:div or parent::tei:docTitle and not(preceding-sibling::*)"/>
            <xsl:otherwise>
                <xsl:call-template name="copy"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="tei:div">
        <xsl:choose>
            <xsl:when test="tei:pb[not(preceding-sibling::*)]">
                <xsl:element name="pb" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:for-each select="tei:pb[1]/attribute::*">
                        <xsl:attribute name="{name(.)}">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                    </xsl:for-each>
                </xsl:element>
                <xsl:element name="div" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:for-each select="attribute::*">
                        <xsl:attribute name="{name(.)}">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                    </xsl:for-each>
                    <xsl:apply-templates select="*"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="copy"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="tei:titlePage">
        <xsl:choose>
            <xsl:when test="tei:docTitle/tei:pb[not(preceding-sibling::*)]">
                <xsl:element name="pb" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:for-each select="tei:docTitle/tei:pb[1]/attribute::*">
                        <xsl:attribute name="{name(.)}">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                    </xsl:for-each>
                </xsl:element>
                <xsl:element name="titlePage" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:for-each select="attribute::*">
                        <xsl:attribute name="{name(.)}">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                    </xsl:for-each>
                    <xsl:apply-templates select="*"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="copy"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    

</xsl:stylesheet>