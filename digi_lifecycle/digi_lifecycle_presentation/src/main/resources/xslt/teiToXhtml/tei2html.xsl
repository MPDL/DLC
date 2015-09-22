<?xml version="1.0" encoding="UTF-8"?>
<!--
  CDDL HEADER START
  The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
  
  You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
  See the License for the specific language governing permissions and limitations under the License.
  
  When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  CDDL HEADER END
  
  Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
  All rights reserved. Use is subject to license terms.

  Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
  institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
  (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
  for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
  Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
  DLC-Software are requested to include a "powered by DLC" on their webpage,
  linking to the DLC documentation (http://dlcproject.wordpress.com/).
-->

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
       
        <html xmlns="http://www.w3.org/1999/xhtml">
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
