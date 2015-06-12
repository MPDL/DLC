<?xml version="1.0" encoding="UTF-8"?>
<!-- CDDL HEADER START The contents of this file are subject to the terms 
	of the Common Development and Distribution License, Version 1.0 only (the 
	"License"). You may not use this file except in compliance with the License. 
	You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
	See the License for the specific language governing permissions and limitations 
	under the License. When distributing Covered Code, include this CDDL HEADER 
	in each file and include the License file at license/ESCIDOC.LICENSE. If 
	applicable, add the following below this CDDL HEADER, with the fields enclosed 
	by brackets "[]" replaced with your own identifying information: Portions 
	Copyright [yyyy] [name of copyright owner] CDDL HEADER END Copyright 2006-2012 
	Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische 
	Information mbH and Max-Planck- Gesellschaft zur Förderung der Wissenschaft 
	e.V. All rights reserved. Use is subject to license terms. -->
<!-- Transformation from a dlc item xml to a zvdd conform mets xml Author: 
	Friederike Kleinfercher (initial creation) -->

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:mods="http://www.loc.gov/mods/v3" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
	xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:dv="http://dfg-viewer.de/"
	xmlns:dlc="http://dlc.mpdl.mpg.de/v1"
	xmlns:map="xalan://java.util.Map"
	extension-element-prefixes="map"
	exclude-result-prefixes="#all">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />


	<xsl:param name="itemId" select="'not defined'" />
	<xsl:param name="itemHdl" select="'not defined'" />
	<xsl:param name="teiUrl" select="'not defined'" />
	<xsl:param name="metsUrl" select="'not defined'" />
	<xsl:param name="imageUrl" select="'not defined'" />
	<xsl:param name="parentUrl" select="'not defined'" />
	<!-- DLC internal shit -->
	<xsl:param name="cModelId" select="'not defined'" />
	<xsl:param name="cModelName" select="'not defined'" />
	<xsl:param name="ctxId" select="'not defined'" />
	<xsl:param name="ctxName" select="'not defined'" />
	<xsl:param name="ouId" select="'not defined'" />
	<xsl:param name="ouName" select="'not defined'" />
	
	<xsl:variable name="teiXml" select="document($teiUrl)" />
	<xsl:variable name="metsXml" select="document($metsUrl)" />
	<xsl:variable name="parentModsXml" select="document($parentUrl)" />

	<xsl:template match="/">
		<xsl:element name="mets:mets">
			<xsl:call-template name="dmdsec" />
			<xsl:call-template name="amdsec" />
			<xsl:call-template name="files" />
			<xsl:call-template name="logic" />
			<xsl:call-template name="phys" />
			<xsl:call-template name="link" />
		</xsl:element>
	</xsl:template>

	<!-- The descriptive metadata of a dlc item -->
	<xsl:template name="dmdsec">
		<xsl:element name="mets:dmdSec">
			<xsl:attribute name="ID"><xsl:value-of select="'dmd0'" /></xsl:attribute>
			<xsl:element name="mets:mdWrap">
				<xsl:attribute name="MDTYPE">MODS</xsl:attribute>
				<xsl:element name="mets:xmlData">
					<xsl:apply-templates select="escidocMetadataRecords:md-record/mods:mods"
						mode="zvdd" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<!-- start of zvdd mode -->

	<xsl:template match="@*|node()" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />
		</xsl:copy>
	</xsl:template>

	<!-- this should fix the invalid date format as well ... -->
	<xsl:template match="text()" mode="zvdd" priority="0">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<!-- do not copy empty elements, 'cause ZVDD will complain about -->
	<xsl:template
		match="*[not(node())] | *[not(node()[2]) and node()/self::text() and not(normalize-space()) ] "
		mode="zvdd" />

	<!-- add title of multivolume to all volumes -->
	
	<xsl:template match="mods:relatedItem[@type = 'host']" priority="4" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />
			<xsl:if test="not(mods:titleInfo)">
				<mods:titleInfo>
					<mods:title>
						<xsl:value-of select="$parentModsXml/escidocMetadataRecords:md-record/mods:mods/mods:titleInfo[@displayLabel = 'mainTitle']/mods:title" />
					</mods:title>
				</mods:titleInfo>
			</xsl:if>
		</xsl:copy>
	</xsl:template>
	
	<!-- ZVDD expects dateIssued in originInfo -->
	<xsl:template match="mods:originInfo" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />

			<xsl:if test="not(mods:dateIssued)">
				<mods:dateIssued>
					<xsl:attribute name="encoding">
						<xsl:value-of select="'w3cdtf'" />
					</xsl:attribute>
					<xsl:value-of select="'2000'" />
				</mods:dateIssued>
			</xsl:if>
		</xsl:copy>
	</xsl:template>

	<!-- subsequent titleInfo elements require a type attribute and a title 
		sub-element -->
	<xsl:template match="mods:titleInfo[position()>1]" mode="zvdd">
		<xsl:copy>
			<xsl:if test="not(@type)">
				<xsl:attribute name="type">
					<xsl:value-of select="'alternatve'" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="@*" mode="zvdd" />

			<xsl:if test="not(mods:title)">
				<mods:title>
					<xsl:value-of select="'4 the sake of zvdd'" />
				</mods:title>
			</xsl:if>
			<xsl:apply-templates select="node()" mode="zvdd" />
		</xsl:copy>
	</xsl:template>

	<!-- identifier element must have type attribute -->
	<xsl:template match="mods:identifier" mode="zvdd">
		<xsl:copy>
			<xsl:if test="not(@type)">
				<xsl:attribute name="type">
					<xsl:value-of select="'dlc'" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="mods:namePart" mode="zvdd">
		<xsl:copy>
			<xsl:attribute name="type">
				<xsl:value-of select="'given'" />
			</xsl:attribute>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="mods:languageTerm/@authority" mode="zvdd">
		<xsl:attribute name="authority">
			<xsl:value-of select="'iso639-2b'" />
		</xsl:attribute>
	</xsl:template>

	<!-- copy only the 1st occurrence of physicalDescription, 'cause ZVDD allows 
		only one -->
	<!-- in addition physicalDescription must contain either extent or digitalOrigin -->
	<!-- changed 2015/05/26: copy only the first physicalDescription that contains an extent 
		subeöement -->
	<xsl:template match="mods:physicalDescription[mods:extent][1]" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="mods:physicalDescription" mode="zvdd" />
	
	<!-- finally adding DLC specific shit in order to be able to define sets for the OAI provider -->
	<xsl:template match="mods:mods[not(mods:extension)]" mode="zvdd">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="zvdd" />
			<mods:identifier>
				<xsl:attribute name="type">
					<xsl:value-of select="'uri'" />
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="$cModelId = 'escidoc:2'">
						<xsl:value-of select="concat('http://dlc.mpdl.mpg.de/dlc/viewMulti/', replace($itemId,'_',':'))" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat('http://dlc.mpdl.mpg.de/dlc/view/', replace($itemId,'_',':'), '/recto-verso')" />
					</xsl:otherwise>
				</xsl:choose>
			</mods:identifier>
			<mods:identifier>
				<xsl:attribute name="type">
					<xsl:value-of select="'hdl'" />
				</xsl:attribute>
				<xsl:value-of select="$itemHdl" />
			</mods:identifier>
			<mods:extension>
			<dlc:cModel>
				<xsl:attribute name="id">
					<xsl:value-of select="$cModelId" />
				</xsl:attribute>
				<xsl:value-of select="$cModelName" />
			</dlc:cModel>
			<dlc:context>
				<xsl:attribute name="id">
					<xsl:value-of select="$ctxId" />
				</xsl:attribute>
				<xsl:value-of select="$ctxName" />
			</dlc:context>
			<dlc:ou>
				<xsl:attribute name="id">
					<xsl:value-of select="$ouId" />
				</xsl:attribute>
				<xsl:value-of select="$ouName" />
			</dlc:ou>
		</mods:extension>
		</xsl:copy>
	</xsl:template>
	<!-- end of zvdd mode -->


	<!-- The administrative metadata of a dlc item -->
	<xsl:template name="amdsec">
		<xsl:element name="mets:amdSec">
			<xsl:attribute name="ID"><xsl:value-of select="'amd0'" /></xsl:attribute>
			<xsl:element name="mets:rightsMD">
				<xsl:element name="mets:mdWrap">
					<xsl:attribute name="MDTYPE">OTHER</xsl:attribute>
					<xsl:attribute name="OTHERMDTYPE">DVRIGHTS</xsl:attribute>
					<xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
					<xsl:element name="mets:xmlData">
						<xsl:element name="dv:rights">
							<xsl:element name="dv:owner">
								DLC - Digital Libraries Connected
							</xsl:element>
							<xsl:element name="dv:ownerContact">
								http://dlcproject.wordpress.com/contact/
							</xsl:element>
							<xsl:element name="dv:ownerLogo">
								http://dlc.mpdl.mpg.de/dlc/resources/images/logo-dlc_150x286.png
							</xsl:element>
							<xsl:element name="dv:ownerSiteURL">
								http://dlc.mpdl.mpg.de/dlc/
							</xsl:element>
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
							<xsl:element name="dv:reference">
								http://dlc.mpdl.mpg.de/dlc/
							</xsl:element>
							<xsl:choose>
								<xsl:when test="$cModelId = 'escidoc:2'">
									<xsl:element name="dv:presentation">
								http://dlc.mpdl.mpg.de/dlc/viewMulti/
								<xsl:value-of select="replace($itemId,'_',':')" />
							</xsl:element>
								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="dv:presentation">
								http://dlc.mpdl.mpg.de/dlc/view/
								<xsl:value-of select="replace($itemId,'_',':')" />
								/recto-verso
							</xsl:element>
								</xsl:otherwise>
							</xsl:choose>
							
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<!-- The file section of a dlc item -->
	<xsl:template name="files">
		<xsl:element name="mets:fileSec">
			<xsl:choose>
				<xsl:when test="$teiXml"> <!-- Elements from tei file -->
					<!-- All files in webresolution -->
					<xsl:element name="mets:fileGrp">
						<xsl:attribute name="USE">DEFAULT</xsl:attribute>
						<xsl:for-each select="$teiXml/tei:TEI//tei:pb">
							<xsl:element name="mets:file">
								<xsl:attribute name="ID">file_default_<xsl:value-of
									select="@xml:id" /></xsl:attribute>
								<xsl:attribute name="MIMETYPE"><xsl:value-of
									select="'image/jpeg'" /></xsl:attribute>
								<xsl:element name="mets:FLocat">
									<xsl:attribute name="LOCTYPE">URL</xsl:attribute>
									<xsl:attribute name="xlink:href"><xsl:value-of
										select="$imageUrl" />web/<xsl:value-of select="@facs" /></xsl:attribute>
								</xsl:element>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
					<!-- All files as thumbnails -->
					<xsl:element name="mets:fileGrp">
						<xsl:attribute name="USE">MIN</xsl:attribute>
						<xsl:for-each select="$teiXml/tei:TEI//tei:pb">
							<xsl:element name="mets:file">
								<xsl:attribute name="ID">file_min_<xsl:value-of
									select="@xml:id" /></xsl:attribute>
								<xsl:attribute name="MIMETYPE"><xsl:value-of
									select="'image/jpeg'" /></xsl:attribute>
								<xsl:element name="mets:FLocat">
									<xsl:attribute name="LOCTYPE">URL</xsl:attribute>
									<xsl:attribute name="xlink:href"><xsl:value-of
										select="$imageUrl" />thumbnails/<xsl:value-of
										select="@facs" /></xsl:attribute>
								</xsl:element>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise> <!-- Elements from mets file -->
					<!-- All files in webresolution -->
					<xsl:element name="mets:fileGrp">
						<xsl:attribute name="USE">DEFAULT</xsl:attribute>
						<xsl:for-each
							select="$metsXml/escidocMetadataRecords:md-record/mets:mets/mets:structMap/mets:div/mets:div">
							<xsl:element name="mets:file">
								<xsl:attribute name="ID">file_default_<xsl:value-of
									select="@ID" /></xsl:attribute>
								<xsl:attribute name="MIMETYPE"><xsl:value-of
									select="'image/jpeg'" /></xsl:attribute>
								<xsl:element name="mets:FLocat">
									<xsl:attribute name="LOCTYPE">URL</xsl:attribute>
									<xsl:attribute name="xlink:href"><xsl:value-of
										select="$imageUrl" />web/<xsl:value-of select="@CONTENTIDS" /></xsl:attribute>
								</xsl:element>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
					<!-- All files as thumbnails -->
					<xsl:element name="mets:fileGrp">
						<xsl:attribute name="USE">MIN</xsl:attribute>
						<xsl:for-each
							select="$metsXml/escidocMetadataRecords:md-record/mets:mets/mets:structMap/mets:div/mets:div">
							<xsl:element name="mets:file">
								<xsl:attribute name="ID">file_min_<xsl:value-of
									select="@ID" /></xsl:attribute>
								<xsl:attribute name="MIMETYPE"><xsl:value-of
									select="'image/jpeg'" /></xsl:attribute>
								<xsl:element name="mets:FLocat">
									<xsl:attribute name="LOCTYPE">URL</xsl:attribute>
									<xsl:attribute name="xlink:href"><xsl:value-of
										select="$imageUrl" />thumbnails/<xsl:value-of
										select="@CONTENTIDS" /></xsl:attribute>
								</xsl:element>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<!-- The physical structmap of a dlc item -->
	<xsl:template name="phys">

		<xsl:element name="mets:structMap">
			<xsl:attribute name="TYPE">PHYSICAL</xsl:attribute>
			<xsl:element name="mets:div">
				<xsl:attribute name="ID"><xsl:value-of select="'phys0'" /></xsl:attribute>
				<xsl:attribute name="TYPE"><xsl:value-of select="'physSequence'" /></xsl:attribute>
				<xsl:attribute name="DMDID"><xsl:value-of select="'dmd0'" /></xsl:attribute>
				<xsl:attribute name="ADMID"><xsl:value-of select="'amd0'" /></xsl:attribute>
				<xsl:choose>
					<xsl:when test="$teiXml">
						<xsl:for-each select="$teiXml/tei:TEI//tei:pb">
							<xsl:element name="mets:div">
								<xsl:variable name="vID">
									<xsl:value-of select="@xml:id" />
								</xsl:variable>
								<xsl:attribute name="ID"><xsl:value-of
									select="$vID" /></xsl:attribute>
								<xsl:attribute name="CONTENTIDS"><xsl:value-of
									select="@facs" /></xsl:attribute>
								<!-- <xsl:attribute name="ORDERLABEL"><xsl:value-of select="'???'"/></xsl:attribute> -->
								<xsl:attribute name="ORDER">
									<xsl:value-of select="position()" />				
							</xsl:attribute>

								<xsl:attribute name="ORDERLABEL">
								<xsl:value-of select="@n" />			
							</xsl:attribute>
								<xsl:attribute name="TYPE"><xsl:value-of
									select="'page'" /></xsl:attribute>
								<xsl:element name="mets:fptr">
									<xsl:attribute name="FILEID">file_min_<xsl:value-of
										select="@xml:id" /></xsl:attribute>
								</xsl:element>
								<xsl:element name="mets:fptr">
									<xsl:attribute name="FILEID">file_default_<xsl:value-of
										select="@xml:id" /></xsl:attribute>
								</xsl:element>
							</xsl:element>

						</xsl:for-each>
					</xsl:when>

					<xsl:otherwise>
						<!-- <xsl:copy-of select="$metsXml/escidocMetadataRecords:md-record/mets:mets/mets:structMap"/> -->
						<xsl:for-each
							select="$metsXml/escidocMetadataRecords:md-record/mets:mets/mets:structMap/mets:div/mets:div">
							<xsl:element name="mets:div">
								<xsl:variable name="vID">
									<xsl:value-of select="@ID" />
								</xsl:variable>
								<xsl:attribute name="ID"><xsl:value-of
									select="$vID" /></xsl:attribute>
								<xsl:attribute name="CONTENTIDS"><xsl:value-of
									select="@CONTENTIDS" /></xsl:attribute>
								<!-- <xsl:attribute name="ORDERLABEL"><xsl:value-of select="'???'"/></xsl:attribute> -->
								<xsl:attribute name="ORDER">
								<xsl:choose>
									<xsl:when test="@ORDER">
										<xsl:value-of select="@ORDER" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="0" />
									</xsl:otherwise>
								</xsl:choose>						
							</xsl:attribute>
								<xsl:attribute name="TYPE"><xsl:value-of
									select="'page'" /></xsl:attribute>
								<xsl:element name="mets:fptr">
									<xsl:attribute name="FILEID">file_min_<xsl:value-of
										select="@ID" /></xsl:attribute>
								</xsl:element>
								<xsl:element name="mets:fptr">
									<xsl:attribute name="FILEID">file_default_<xsl:value-of
										select="@ID" /></xsl:attribute>
								</xsl:element>
							</xsl:element>
						</xsl:for-each>
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
				<xsl:attribute name="ID"><xsl:value-of select="'log0'" /></xsl:attribute>
				<xsl:attribute name="DMDID"><xsl:value-of select="'dmd0'" /></xsl:attribute>
				<xsl:attribute name="ADMID"><xsl:value-of select="'amd0'" /></xsl:attribute>
				<xsl:attribute name="TYPE"><xsl:value-of select="'Monograph'" /></xsl:attribute>
				<!-- All logical elements with its original ids, labels and structural 
					types -->
				<xsl:for-each select="$teiXml/tei:TEI//(tei:div|tei:titlePage)">
					<xsl:element name="mets:div">
						<xsl:attribute name="ID">log_<xsl:value-of
							select="@xml:id" /></xsl:attribute>

						<xsl:if test="self::tei:div">
							<xsl:attribute name="LABEL"><xsl:value-of
								select="tei:head" /></xsl:attribute>
							<xsl:if test="@type">
								<xsl:attribute name="TYPE"><xsl:value-of
									select="@type" /></xsl:attribute>
							</xsl:if>
							<xsl:if test="not(@type)">
								<xsl:attribute name="TYPE"><xsl:value-of
									select="'chapter'" /></xsl:attribute>
							</xsl:if>
						</xsl:if>

						<xsl:if test="self::tei:titlePage">
							<xsl:attribute name="TYPE"><xsl:value-of
								select="'title_page'" /></xsl:attribute>
							<xsl:attribute name="LABEL"><xsl:value-of
								select="//tei:docTitle" /></xsl:attribute>
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
				<xsl:variable name="currentPb" select="." />
				<!-- Select parent div and all divs/titlePage until next pagebreak -->
				<xsl:variable name="divsForPb"
					select="(ancestor::tei:div|ancestor::tei:titlePage)[1]|((following::tei:div|following::tei:titlePage) except (following::tei:pb[1]/(following::tei:div|following::tei:titlePage)))" />
				<xsl:for-each select="$divsForPb">
					<xsl:element name="mets:smLink">
						<xsl:attribute name="xlink:from">log_<xsl:value-of
							select="@xml:id" /></xsl:attribute>
						<xsl:attribute name="xlink:to"><xsl:value-of
							select="$currentPb/@xml:id" /></xsl:attribute>
					</xsl:element>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>