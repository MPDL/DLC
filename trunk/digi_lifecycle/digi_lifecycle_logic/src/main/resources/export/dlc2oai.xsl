<!--
	CDDL HEADER START The contents of this file are subject to the terms
	of the Common Development and Distribution License, Version 1.0 only
	(the "License"). You may not use this file except in compliance with
	the License. You can obtain a copy of the license at
	license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the
	License for the specific language governing permissions and
	limitations under the License. When distributing Covered Code, include
	this CDDL HEADER in each file and include the License file at
	license/ESCIDOC.LICENSE. If applicable, add the following below this
	CDDL HEADER, with the fields enclosed by brackets "[]" replaced with
	your own identifying information: Portions Copyright [yyyy] [name of
	copyright owner] CDDL HEADER END Copyright 2006-2010
	Fachinformationszentrum Karlsruhe Gesellschaft für
	wissenschaftlich-technische Information mbH and Max-Planck-
	Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved.
	Use is subject to license terms.
-->
<!--
	Transformation from DLC METS/MODS metadata to OAI DC (!OpenAire compliant):
	Author: Matthias Walter (initial creation) $Author: $ (last changed)
	$Revision: $ $LastChangedDate: $
-->
<xsl:stylesheet version="2.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:dlc="http://dlc.mpdl.mpg.de/v1"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    exclude-result-prefixes="dcterms mods mets dlc xsi xsl xlink"
    >

    <xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
    <xsl:preserve-space elements="*"/>
    
    <xsl:template match="node()|@*" >
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods">
        <oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <!-- dc:type -->
            <dc:type>
                <xsl:value-of select="mods:extension/dlc:cModel" />
            </dc:type>

            
            <!-- CREATORS -->
            <xsl:for-each select="./mods:name">

                <xsl:choose>
                    <xsl:when test="mods:role/mods:roleTerm = 'aut'">
                        <dc:creator>
                            <xsl:value-of select="mods:namePart" />
                        </dc:creator>
                    </xsl:when>
                    <xsl:otherwise>
                        <dc:contributor>
                            <xsl:value-of select="mods:namePart" />
                        </dc:contributor>
                    </xsl:otherwise>
                </xsl:choose>

            </xsl:for-each>

            <!-- dc:title -->
            <xsl:variable name="title" select="normalize-space(./mods:titleInfo[@displayLabel='mainTitle']/mods:title)"/>
            <xsl:if test="$title != ''">
                <dc:title>
                    <xsl:value-of select="$title"/>
                </dc:title>
            </xsl:if>

            <!-- dc:language -->
            <xsl:for-each select="./mods:language/mods:languageTerm">
                <xsl:if test=". != ''">
                    <dc:language>
                        <xsl:value-of select="normalize-space(.)"/>
                    </dc:language>
                </xsl:if>
            </xsl:for-each>

            <!-- dc:identifiers -->
            <xsl:for-each select="./mods:identifier">
                <dc:identifier>
                    <xsl:value-of select="." />
                </dc:identifier>
            </xsl:for-each>
            
            <!-- dc:publisher -->
            <xsl:variable name="publisher" select="normalize-space(./mods:originInfo/mods:publisher)"/>
            <xsl:if test="$publisher != ''">
                <dc:publisher>
                    <xsl:value-of select="$publisher"/>
                </dc:publisher>
            </xsl:if>

            <!-- dc:date -->
            <xsl:variable name="date" select="./mods:originInfo/mods:dateIssued" />
            <xsl:if test="$date != ''">
                <dc:date>
                    <xsl:value-of select="$date" />
                </dc:date>
            </xsl:if>

            <!-- dc:relation -->
            <xsl:variable name="relatedItem" select="./mods:relatedItem[@type='host']/mods:recordIdentifier" />
            <xsl:if test="$relatedItem != ''">
	        <dc:relation>
	            <xsl:value-of select="$relatedItem" />
	        </dc:relation>
            </xsl:if>
        
            <!-- dc:description -->
            <xsl:for-each select="./mods:note">
                <xsl:if test=". != ''">
                    <dc:description>
                        <xsl:value-of select="normalize-space(.)" />
                    </dc:description>
                </xsl:if>
            </xsl:for-each>
            
            <!-- dc:subject -->
            <xsl:for-each select="./mods:subject/mods:topic">
                <xsl:if test=". != ''">
                    <dc:subject>
                        <xsl:value-of select="normalize-space(.)" />
                    </dc:subject>
                </xsl:if>
            </xsl:for-each>

        </oai_dc:dc>
    </xsl:template>

</xsl:stylesheet>

