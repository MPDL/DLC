<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="tei html"
    version="2.0">
    <!-- import base conversion style -->

    <xsl:import href="../../../xhtml2/tei.xsl"/>

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>

         <p> This library is free software; you can redistribute it and/or
      modify it under the terms of the GNU Lesser General Public License as
      published by the Free Software Foundation; either version 2.1 of the
      License, or (at your option) any later version. This library is
      distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
      without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
      PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
      details. You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the Free Software
      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </p>
         <p>Author: See AUTHORS</p>
         <p>Id: $Id: to.xsl 10466 2012-06-08 18:47:50Z rahtz $</p>
         <p>Copyright: 2008, TEI Consortium</p>
      </desc>
   </doc>

   <xsl:output method="xhtml" omit-xml-declaration="yes"/>

<!-- set stylesheet parameters -->
   <xsl:param name="numberHeadings">false</xsl:param>
   <xsl:param name="numberFigures">false</xsl:param>
   <xsl:param name="numberTables">false</xsl:param>
  <xsl:param name="numberParagraphs">false</xsl:param>
  <xsl:param name="generateParagraphIDs">false</xsl:param>
  <xsl:param name="autoToc">false</xsl:param>
  <xsl:param name="cssInlineFile"/>
  <xsl:param name="cssFile"/>
  <xsl:param name="institution">DLC Project</xsl:param>
   <xsl:param name="bottomNavigationPanel">false</xsl:param>
  <xsl:param name="linkPanel">false</xsl:param>
   <xsl:param name="footnoteBackLink">false</xsl:param>
   <xsl:param name="homeURL"></xsl:param>
  <xsl:param name="feedbackURL"></xsl:param>
   <xsl:param name="homeWords"> </xsl:param>
 
  
  <xsl:param name="copyrightStatement">  <!-- AW 16.10.2012: tei-param.xsl copyrightStatement--> 
    <xsl:value-of select="tei:TEI/tei:teiHeader/tei:fileDesc/tei:publicationStmt/tei:availability/tei:p[1]"></xsl:value-of>
    <!-- This page is made available under the Creative Commons General Public License "Attribution, Non-Commercial, Share-Alike", version 3.0 (CCPL BY-NC-SA) --> 
  </xsl:param>
  
<!-- number paragraphs -->
  <xsl:template name="numberParagraph">
    <xsl:if test="ancestor::tei:body">
      <span class="numberParagraph">      
	<xsl:number level="any" from="tei:body"/>
      </span>
    </xsl:if>
  </xsl:template>

<!-- suppress pb -->
<xsl:template match="tei:pb"/>
    
<!-- deal with weird @rend values -->
<xsl:template match="tei:hi[@rend='del']">
<s><xsl:apply-templates/></s>
</xsl:template>
<xsl:template match="tei:hi[@rend='ul2']">
<u style="border-bottom: 1px double #000"><xsl:apply-templates/></u>
</xsl:template>

<xsl:template match="tei:hi[@rend='ulw']">
<u style="border-bottom: 1px dotted #000"><xsl:apply-templates/></u>
</xsl:template>

<xsl:template match="tei:hi[@rend='shadow']">
<u style="background-color: gray"><xsl:apply-templates/></u>
</xsl:template>

<xsl:template match="tei:lb[@rend='indent']">
<br/><xsl:text>    </xsl:text>
</xsl:template>

<!-- also weird list types -->

<xsl:template match="tei:list[@type='number']">
<ol>
          <xsl:apply-templates select="tei:item"/>
</ol></xsl:template>

<xsl:template match="tei:list[@type='simple']">
<ul style="list-style:none">
          <xsl:apply-templates select="tei:item"/>
</ul></xsl:template>

<!-- AW 24.01.2013: auskommentiert Links darstellen -->
<!--
<xsl:template match="tei:ref">
  <span class="ref"><xsl:apply-templates/></span>
  <span class="contextaRef"><xsl:value-of select="@cRef"/></span>
</xsl:template>
-->
<xsl:template match="tei:corr">
  <span class="corr"><xsl:apply-templates/></span>
</xsl:template>

<!-- add a space in front of surname inside author -->

<xsl:template
    match="tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:author/tei:surname">
<xsl:text> </xsl:text>
<xsl:apply-templates/>
</xsl:template>

  <xsl:template match="tei:div[@type='bibliography']">
<div class="refs">
<xsl:if test='not(tei:head)'>
<h2><span class="head">References</span></h2>
</xsl:if>
      <xsl:apply-templates/>
</div>  </xsl:template>

<!-- replace template for listbibl -->
<xsl:template match="tei:listBibl">
 <ul class="listBibl">
        <xsl:for-each select="tei:bibl|tei:biblItem">
          <li>
	    <xsl:choose>
	    <xsl:when test="@n">
	      <xsl:attribute name="id">
		<xsl:value-of select="@n"/>
</xsl:attribute></xsl:when>
<xsl:otherwise>
            <xsl:call-template name="makeAnchor">
              <xsl:with-param name="name">
                <xsl:apply-templates mode="ident" select="."/>
              </xsl:with-param>
            </xsl:call-template>
</xsl:otherwise></xsl:choose>
            <xsl:apply-templates select="."/>
          </li>
        </xsl:for-each>
      </ul>
</xsl:template>

  <xsl:template match="tei:div[@type='abstract']">
<div class="abstract">
<xsl:if test='not(tei:head)'>
<h2><span class="head">Abstract</span></h2>
</xsl:if>
      <xsl:apply-templates/>
</div>  </xsl:template>


<!-- these seem to be inherited -->
    <xsl:template match="html:*">
      <xsl:element name="{local-name()}">
	<xsl:copy-of select="@*"/>
	<xsl:apply-templates/>
      </xsl:element>
    </xsl:template>
    
    <xsl:template match="html:*/comment()">
      <xsl:copy-of select="."/>
    </xsl:template>

  <xsl:template match="tei:div[@type='frontispiece']">
      <xsl:apply-templates/>
  </xsl:template>


</xsl:stylesheet>
