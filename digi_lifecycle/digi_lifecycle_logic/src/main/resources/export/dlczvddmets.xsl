<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformation from a dlc item xml to a zvdd conform mets xml
	Author: Friederike Kleinfercher (initial creation) 
-->

<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:mods="http://www.loc.gov/mods/v3" 
	xmlns:mets="http://www.loc.gov/METS/" 
	xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
	xmlns:tei="http://www.tei-c.org/ns/1.0" 
	xmlns:dv="http://dfg-viewer.de/"
>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:param name="itemId" select="'not defined'"/>
	<xsl:param name="teiUrl" select="'not defined'"/>
	<xsl:param name="metsUrl" select="'not defined'"/>
	<xsl:param name="imageUrl" select="'not defined'"/>
	<xsl:variable name="teiXml" select="document($teiUrl)"/>
	<xsl:variable name="metsXml" select="document($metsUrl)"/>
	
	<xsl:template match="/">
		<xsl:element name="mets:mets">
			<xsl:call-template name="dmdsec"/>
			<xsl:call-template name="amdsec"/>
			<xsl:call-template name="files"/>
			<xsl:call-template name="logic"/>
			<xsl:call-template name="phys"/>
			<xsl:call-template name="link"/>
		</xsl:element>					
	</xsl:template>	
	
	<!-- The descriptive metadata of a dlc item -->
	<xsl:template name="dmdsec">
		<xsl:element name="mets:dmdSec">
			<xsl:attribute name="ID"><xsl:value-of select="'dmd0'"/></xsl:attribute>
			<xsl:element name="mets:mdWrap">
				<xsl:attribute name="MDTYPE">MODS</xsl:attribute>
				<xsl:element name="mets:xmlData">					
					<xsl:copy-of select="escidocMetadataRecords:md-record/mods:mods"/>
				</xsl:element>
			</xsl:element>
		</xsl:element>					
	</xsl:template>	
	
	<!-- The administrative metadata of a dlc item -->
	<xsl:template name="amdsec">
		<xsl:element name="mets:amdSec">
			<xsl:attribute name="ID"><xsl:value-of select="'amd0'"/></xsl:attribute>
			<xsl:element name="mets:rightsMD">
				<xsl:element name="mets:mdWrap">
					<xsl:attribute name="MDTYPE">OTHER</xsl:attribute>
					<xsl:attribute name="OTHERMDTYPE">DVRIGHTS</xsl:attribute>
					<xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
					<xsl:element name="mets:xmlData">
						<xsl:element name="dv:rights">
							<xsl:element name="dv:owner"></xsl:element>	
							<xsl:element name="dv:ownerContact"></xsl:element>	
							<xsl:element name="dv:ownerLogo">http://dlc.mpdl.mpg.de/dlc/resources/images/solution_logo_216_75.png</xsl:element>	
							<xsl:element name="dv:ownerSiteURL"></xsl:element>	
						</xsl:element>	
					</xsl:element>
				</xsl:element>					
			</xsl:element>
			<xsl:element name="mets:digiprovMD">
				<xsl:element name="mets:mdWrap">
					<xsl:attribute name="MDTYPE">OTHER</xsl:attribute>
					<xsl:attribute name="OTHERMDTYPE">DVLINKS</xsl:attribute>
					<xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
					<xsl:element name="mets:xmlData">
						<xsl:element name="dv:links">
							<xsl:element name="dv:reference">http://dlc.mpdl.mpg.de/dlc/</xsl:element>
							<xsl:element name="dv:presentation">http://dlc.mpdl.mpg.de/dlc/view/<xsl:value-of select="replace($itemId,'_',':')"/>/recto-verso</xsl:element>
						</xsl:element>
					</xsl:element>
				</xsl:element>				
			</xsl:element>					
		</xsl:element>					
	</xsl:template>	
	
	<!-- The file section of a dlc item -->
	<xsl:template name="files">
		<xsl:element name="mets:fileSec">
			<!-- All files in webresolution -->
			<xsl:element name="mets:fileGrp">
				<xsl:attribute name="USE">DEFAULT</xsl:attribute>
				<xsl:for-each select="$teiXml/tei:TEI//tei:pb">
					<xsl:element name="mets:file">
						<xsl:attribute name="ID">div<xsl:value-of select="@xml:id"/></xsl:attribute>
						<xsl:attribute name="MIMETYPE"><xsl:value-of select="'image/jpeg'"/></xsl:attribute>
						<xsl:element name="mets:FLocat">
							<xsl:attribute name="LOCTYPE">URL</xsl:attribute>
							<xsl:attribute name="xlink:href"><xsl:value-of select="$imageUrl"/>web/<xsl:value-of select="@facs"/></xsl:attribute>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
			<!-- All files as thumbnails -->
			<xsl:element name="mets:fileGrp">
				<xsl:attribute name="USE">MIN</xsl:attribute>
				<xsl:for-each select="$teiXml/tei:TEI//tei:pb">
					<xsl:element name="mets:file">
						<xsl:attribute name="ID">div<xsl:value-of select="@xml:id"/></xsl:attribute>
						<xsl:attribute name="MIMETYPE"><xsl:value-of select="'image/jpeg'"/></xsl:attribute>
						<xsl:element name="mets:FLocat">
							<xsl:attribute name="LOCTYPE">URL</xsl:attribute>
							<xsl:attribute name="xlink:href"><xsl:value-of select="$imageUrl"/>thumbnails/<xsl:value-of select="@facs"/></xsl:attribute>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>					
	</xsl:template>	
	
	<!-- The physical structmap of a dlc item -->
	<xsl:template name="phys">
		<xsl:element name="mets:structMap">
			<xsl:attribute name="TYPE">PHYSICAL</xsl:attribute>
			<xsl:element name="mets:div">
				<xsl:attribute name="ID"><xsl:value-of select="'phys0'"/></xsl:attribute>
				<xsl:attribute name="TYPE"><xsl:value-of select="'physSequence'"/></xsl:attribute>
				<xsl:attribute name="DMDID"><xsl:value-of select="'dmd0'"/></xsl:attribute>
				<xsl:attribute name="ADMID"><xsl:value-of select="'amd0'"/></xsl:attribute>
				<xsl:choose>
					<xsl:when test="$teiXml">
						<xsl:for-each select="$teiXml/tei:TEI//tei:pb">
							<xsl:element name="mets:div">
								<xsl:variable name="vID"><xsl:value-of select="@xml:id"/></xsl:variable>
							    <xsl:attribute name="ID"><xsl:value-of select="$vID"/></xsl:attribute>
								<xsl:attribute name="CONTENTIDS"><xsl:value-of select="@facs"/></xsl:attribute>
								<!--  <xsl:attribute name="ORDERLABEL"><xsl:value-of select="'???'"/></xsl:attribute> -->
								<xsl:attribute name="ORDER">
									<xsl:choose>
										<xsl:when test="@n">
											<xsl:value-of select="@n"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$metsXml/escidocMetadataRecords:md-record/mets:mets/mets:structMap/mets:div/mets:div[@ID=$vID]/@ORDER"/>
										</xsl:otherwise>
									</xsl:choose>						
								</xsl:attribute>
								<xsl:attribute name="TYPE"><xsl:value-of select="'page'"/></xsl:attribute>  
								<xsl:element name="mets:fptr">
									<xsl:attribute name="FILEID">div<xsl:value-of select="@xml:id"/></xsl:attribute>
								</xsl:element> 
							</xsl:element>			
		      			</xsl:for-each>
    				</xsl:when>
      				<xsl:otherwise>
						<xsl:copy-of select="$metsXml/escidocMetadataRecords:md-record/mets:mets/mets:structMap"/>
					</xsl:otherwise>
				</xsl:choose>     			
      		</xsl:element>
		</xsl:element>					
	</xsl:template>		
	
	<!-- The logical structmap of a dlc item -->
	<xsl:template name="logic">
		<xsl:element name="mets:structMap">
			<xsl:attribute name="TYPE">LOGICAL</xsl:attribute>
			<xsl:element name="mets:div">
				<xsl:attribute name="ID"><xsl:value-of select="'log0'"/></xsl:attribute>
				<xsl:attribute name="DMDID"><xsl:value-of select="'dmd0'"/></xsl:attribute>
				<xsl:attribute name="ADMID"><xsl:value-of select="'amd0'"/></xsl:attribute>
				<xsl:attribute name="TYPE"><xsl:value-of select="'Monograph'"/></xsl:attribute>
				<!-- All logical elements with its original ids, labels and structural types -->
				<xsl:for-each select="$teiXml/tei:TEI//tei:head">
					<xsl:element name="mets:div">
						<xsl:attribute name="ID"><xsl:value-of select="../@xml:id"/></xsl:attribute>
						<xsl:attribute name="LABEL"><xsl:value-of select="."/></xsl:attribute>
						<xsl:if test="../@type!=''">
							<xsl:attribute name="TYPE"><xsl:value-of select="../@type"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="../@type=''">
							<xsl:attribute name="TYPE"><xsl:value-of select="'chapter'"/></xsl:attribute>
						</xsl:if>
		      		</xsl:element>
	      		</xsl:for-each>
      		</xsl:element>
		</xsl:element>					
	</xsl:template>	
	
	<!-- Linking of the logical and the physical structmap -->
	<xsl:template name="link">
		<xsl:element name="mets:structLink">
			<xsl:for-each select="$teiXml/tei:TEI//tei:pb">
				<xsl:element name="mets:smLink">
					<xsl:attribute name="xlink:from"><xsl:value-of select="../@xml:id"/></xsl:attribute>
					<xsl:attribute name="xlink:to"><xsl:value-of select="@xml:id"/></xsl:attribute>				
	      		</xsl:element>
      		</xsl:for-each>
		</xsl:element>					
	</xsl:template>		
	
</xsl:stylesheet>