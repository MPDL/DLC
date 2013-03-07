<?xml version="1.0" encoding="UTF-8"?>
<!--
  CDDL HEADER START
  The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
  
  You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
  See the License for the specific language governing permissions and limitations under the License.
  
  When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  CDDL HEADER END
  
  Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
  All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:mab="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd" xmlns="http://www.loc.gov/mods/v3" exclude-result-prefixes="mab">
	<xsl:output indent="yes" method="xml" encoding="UTF-8"/>
	
	<xsl:variable name="IDPREFIX" select="'mab'"/>
	<xsl:variable name="UNIFORM_TITLE" select="'uniform'"/>
	<xsl:variable name="ALTERNATIVE_TITLE" select="'alternative'"/>
	<xsl:variable name="ABBREVIATED_TITLE" select="'abbreviated'"/>
	<xsl:variable name="TRANSLATED_TITLE" select="'translated'"/>
	
	
	
	
	
	
	<xsl:key match="mab:feld" name="fields" use="@nr"/>

	 <xsl:template match="/">
		<xsl:element name="mods:mods">
			<xsl:attribute name="version"><xsl:value-of select="'3.4'"/></xsl:attribute>
				<xsl:for-each select="key('fields', '001')">
					<xsl:call-template name="mab001ToRecordIdentifier"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '010')">
					<xsl:call-template name="mab010ToRelatedItem"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '025')">
					<xsl:call-template name="mab025ToIdentifier"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '037')">
					<xsl:call-template name="mab037ToLanguage"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '089')">
					<xsl:call-template name="mab089ToPart"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '090')">
					<xsl:call-template name="mab090ToPart"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '100')">
					<xsl:call-template name="mab100ToName"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '104')">
					<xsl:call-template name="mab100ToName"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '108')">
					<xsl:call-template name="mab100ToName"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '200')">
					<xsl:call-template name="mab200ToName"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '204')">
					<xsl:call-template name="mab200ToName"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '208')">
					<xsl:call-template name="mab200ToName"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '304')">
					<xsl:call-template name="mab304ToUniformTitle"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '310')">
					<xsl:call-template name="mab310ToAlternativeTitle"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '331')">
					<xsl:call-template name="mab331ToMainTitle"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '334')">
					<xsl:call-template name="mab334ToPhysicalDescription"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '335')">
					<xsl:call-template name="mab335ToSubTitle"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '341')">
					<xsl:call-template name="mab310ToAlternativeTitle"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '359')">
					<xsl:call-template name="mab359ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '360')">
					<xsl:call-template name="mab360ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '361')">
					<xsl:call-template name="mab361ToPart"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '365')">
					<xsl:call-template name="mab365ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '369')">
					<xsl:call-template name="mab369ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '403')">
					<xsl:call-template name="mab403ToOriginInfo"/>
					<!--
						mab425ToOriginInfo
						necessary if there is no 410
					-->
				</xsl:for-each>
				<xsl:for-each select="key('fields', '410')">
					<xsl:call-template name="mab410ToOriginInfo"/>
					<!--
						mab410ToOriginInfo
						includes 410, 410x, 412, 412x
						checks for 403 and 425
					-->
				</xsl:for-each>

				<xsl:for-each select="key('fields', '415')">
					<xsl:call-template name="mab415ToOriginInfo"/>
					<!--
						mab415ToOriginInfo
						includes 415, 417
						checks for 403 and 425
					-->
				</xsl:for-each>
				<xsl:for-each select="key('fields', '425')">
					<xsl:call-template name="mab425ToOriginInfo"/>
					<!--
						mab425ToOriginInfo
						necessary if there is no 410
					-->
				</xsl:for-each>
				<xsl:for-each select="key('fields', '429')">
					<xsl:call-template name="mab429ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '431')">
					<xsl:call-template name="mab431ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '432')">
					<xsl:call-template name="mab432ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '433')">
					<xsl:call-template name="mab433ToPhysicalDescription"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '434')">
					<xsl:call-template name="mab433ToPhysicalDescription"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '435')">
					<xsl:call-template name="mab433ToPhysicalDescription"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '451')">
					<xsl:call-template name="mab451ToRelatedItem"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '453')">
					<xsl:call-template name="mab010ToRelatedItem"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '501')">
					<xsl:call-template name="mab501ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '519')">
					<xsl:call-template name="mab519ToNote"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '540')">
					<xsl:call-template name="mab540ToIdentifier"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '542')">
					<xsl:call-template name="mab542ToIdentifier"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '544')">
					<xsl:call-template name="mab544ToLocation"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '552')">
					<xsl:call-template name="mab552ToIdentifier"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '580')">
					<xsl:call-template name="mab580ToIdentifier"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '590')">
					<xsl:call-template name="mab590ToRelatedItem"/>
				</xsl:for-each>
				
				
				<!--  begin : for 611 - 659 Sekundärausgabe-->	
				
				<mods:relatedItem displayLabel="secondary edition">
					<xsl:for-each select="key('fields', '611')">
						<xsl:call-template name="mab611ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '617')[@ind='a']">
						<xsl:call-template name="mab617ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '634')">
						<xsl:call-template name="mab634ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '635')">
						<xsl:call-template name="mab635ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '646')">
						<xsl:call-template name="mab646ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '647')">
						<xsl:call-template name="mab647ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '621')">
						<xsl:call-template name="mab621ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '659')">
						<xsl:call-template name="mab659ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '653')">
						<xsl:call-template name="mab653ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '637')">
						<xsl:call-template name="mab637ToRelatedItem" />
					</xsl:for-each>
					<xsl:for-each select="key('fields', '655')">
						<xsl:call-template name="mab655ToRelatedItem" />
					</xsl:for-each>
				</mods:relatedItem>

				<!--  end: 611-659	-->
				

				<xsl:for-each select="key('fields', '902')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '907')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '912')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '917')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '922')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '927')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '932')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '937')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '942')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
				<xsl:for-each select="key('fields', '947')">
					<xsl:call-template name="mab902ToSubject"/>
				</xsl:for-each>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab001ToRecordIdentifier">
		<xsl:element name="mods:recordInfo">
			<xsl:element name="mods:recordIdentifier">
				<xsl:attribute name="source"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab010ToRelatedItem">
		<xsl:element name="mods:relatedItem">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'host'"/></xsl:attribute>
			<xsl:element name="mods:recordInfo">
				<xsl:element name="mods:recordIdentifier">
					<xsl:attribute name="source"><xsl:value-of select="'local'"/></xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab025ToIdentifier">
		<xsl:if test="@ind='z'">
			<xsl:element name="mods:identifier">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="type"><xsl:value-of select="'zdb-id'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	
	
	<xsl:template name="mab037ToLanguage">
		<xsl:element name="mods:language">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="mods:languageTerm">
				<xsl:attribute name="type"><xsl:value-of select="'code'"/></xsl:attribute>
				<xsl:attribute name="authority"><xsl:value-of select="'rfc4646'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab089ToPart">
		<xsl:element name="mods:part">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'host'"/></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="."/></xsl:attribute>
			<xsl:element name="mods:detail">
				<xsl:element name="mods:number">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab090ToPart">
		<xsl:element name="mods:part">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'host'"/></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="."/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab100ToName">
		<xsl:element name="mods:name">
			<xsl:if test="@ind=' ' or @ind='a'">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:if test="@nr='100'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'author1'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='104'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'author2'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='108'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'author3'"/></xsl:attribute>
				</xsl:if>
			</xsl:if>
			<xsl:if test="not(@ind=' ' or @ind='a' or @ind='f')">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, concat(@nr, @ind))"/></xsl:attribute>
				<xsl:if test="@nr='100'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'editor1'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='104'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'editor2'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='108'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'editor3'"/></xsl:attribute>
				</xsl:if>
			</xsl:if>
			<xsl:attribute name="type"><xsl:value-of select="'personal'"/></xsl:attribute>
			<xsl:attribute name="authority"><xsl:value-of select="'pnd'"/></xsl:attribute>
			<xsl:element name="mods:namePart">
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:element name="mods:role">
				<xsl:element name="mods:roleTerm">
					<xsl:attribute name="type"><xsl:value-of select="'code'"/></xsl:attribute>
					<xsl:attribute name="authority"><xsl:value-of select="'marcrelator'"/></xsl:attribute>
					<xsl:if test="@ind=' ' or @ind='a'">
						<xsl:value-of select="'aut'"/>
					</xsl:if>
					<xsl:if test="@ind='f'">
						<xsl:value-of select="'hnr'"/>
					</xsl:if>
					<xsl:if test="not(@ind=' ' or @ind='a' or @ind='f')">
						<xsl:value-of select="'asn'"/>
					</xsl:if>
				
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab200ToName">
		<xsl:element name="mods:name">
			<xsl:if test="@ind=' ' or @ind='a'">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:if test="@nr='200'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'body1'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='204'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'body2'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='208'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'body3'"/></xsl:attribute>
				</xsl:if>
			</xsl:if>
			<xsl:if test="not(@ind=' ' or @ind='a')">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, concat(@nr, @ind))"/></xsl:attribute>
				<xsl:if test="@nr='200'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'body4'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='204'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'body5'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@nr='208'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'body6'"/></xsl:attribute>
				</xsl:if>
			</xsl:if>
			<xsl:attribute name="type"><xsl:value-of select="'corporate'"/></xsl:attribute>
			<xsl:attribute name="authority"><xsl:value-of select="'gkd'"/></xsl:attribute>
			<xsl:element name="mods:namePart">
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:element name="mods:role">
				<xsl:element name="mods:roleTerm">
					<xsl:attribute name="type"><xsl:value-of select="'code'"/></xsl:attribute>
					<xsl:attribute name="authority"><xsl:value-of select="'marcrelator'"/></xsl:attribute>
					<xsl:if test="@ind=' ' or @ind='a'">
						<xsl:value-of select="'aut'"/>
					</xsl:if>
					<xsl:if test="not(@ind=' ' or @ind='a')">
						<xsl:value-of select="'asn'"/>
					</xsl:if>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab304ToUniformTitle">
		<xsl:element name="mods:titleInfo">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="$UNIFORM_TITLE"/></xsl:attribute>
			<xsl:element name="mods:title">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab310ToAlternativeTitle">
		<xsl:element name="mods:titleInfo">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="$ALTERNATIVE_TITLE"/></xsl:attribute>
			<xsl:if test="@nr='341'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'parallelTitle'"/></xsl:attribute>
			</xsl:if>
			<xsl:element name="mods:title">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab331ToMainTitle">
		<xsl:element name="mods:titleInfo">
			<xsl:if test="@ind=' '">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="displayLabel"><xsl:value-of select="'mainTitle'"/></xsl:attribute>
				<xsl:element name="mods:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="@ind='a'">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="displayLabel"><xsl:value-of select="'NEB follow the title'"/></xsl:attribute>
				<xsl:element name="mods:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="@ind='b'">
				<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="displayLabel"><xsl:value-of select="'NEB with the title'"/></xsl:attribute>
				<xsl:element name="mods:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab334ToPhysicalDescription">
		<xsl:element name="mods:physicalDescription">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="mods:form">
				<xsl:attribute name="type"><xsl:value-of select="'material'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab335ToSubTitle">
		<xsl:element name="mods:titleInfo">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="mods:subTitle">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab359ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'statement of responsibility'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab360ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'subseries'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab361ToPart">
		<xsl:element name="mods:part">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'constituent'"/></xsl:attribute>
			<xsl:element name="mods:detail">
				<xsl:element name="mods:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab365ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'remainder of title for whole item'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab369ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'reponsibility for whole item'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab403ToOriginInfo">
		<xsl:choose>
			<xsl:when test="key('fields', '410')">
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="mods:originInfo">
					<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher1'"/></xsl:attribute>
					<xsl:element name="mods:edition">
						<xsl:value-of select="key('fields', '403')"/>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="mab410ToOriginInfo">
		<xsl:element name="mods:originInfo">
			<xsl:if test="@ind=' '">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher1'"/></xsl:attribute>
				<xsl:element name="mods:place">
					<xsl:element name="mods:placeTerm">
						<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:element>
				<xsl:variable name="publisher" select="key('fields', '412')[@ind=' ']"/>
				<xsl:if test="$publisher">
					<xsl:element name="mods:publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="@ind='a'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'printer1'"/></xsl:attribute>
				<xsl:element name="mods:place">
					<xsl:element name="mods:placeTerm">
						<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:element>
				<xsl:variable name="printer" select="key('fields', '412')[@ind='a']"/>
				<xsl:if test="$printer">
					<xsl:element name="mods:publisher">
						<xsl:value-of select="$printer"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="key('fields', '425')">
				<xsl:element name="mods:dateIssued">
					<xsl:attribute name="keyDate"><xsl:value-of select="'yes'"/></xsl:attribute>
					<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
					<xsl:value-of select="if (key('fields', '425')[@ind=' ']) then key('fields', '425')[@ind=' '] else key('fields', '425')[@ind='a']" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '403')">
				<xsl:element name="mods:edition">
					<xsl:value-of select="key('fields', '403')"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template name="mab415ToOriginInfo">
		<xsl:element name="mods:originInfo">
			<xsl:if test="@ind=' '">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher2'"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="@ind='a'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'printer2'"/></xsl:attribute>
			</xsl:if>
			<xsl:element name="mods:place">
				<xsl:element name="mods:placeTerm">
					<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
			<xsl:if test="key('fields', '417')">
				<xsl:element name="mods:publisher">
					<xsl:value-of select="key('fields', '417')"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '425')">
				<xsl:element name="mods:dateIssued">
					<xsl:attribute name="keyDate"><xsl:value-of select="'yes'"/></xsl:attribute>
					<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
					<xsl:value-of select="if (key('fields', '425')[@ind=' ']) then key('fields', '425')[@ind=' '] else key('fields', '425')[@ind='a']" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '403')">
				<xsl:element name="mods:edition">
					<xsl:value-of select="key('fields', '403')"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab425ToOriginInfo">
		<xsl:choose>
			<xsl:when test="key('fields', '410')">
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="mods:originInfo">
					<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher1'"/></xsl:attribute>
					<xsl:element name="mods:dateIssued">
						<xsl:attribute name="keyDate"><xsl:value-of select="'yes'"/></xsl:attribute>
						<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
						<xsl:value-of select="if (key('fields', '425')[@ind=' ']) then key('fields', '425')[@ind=' '] else key('fields', '425')[@ind='a']" />
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="mab429ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'subject completeness'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab431ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'indexes'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab432ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'summary of volumes'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab433ToPhysicalDescription">
		<xsl:element name="mods:physicalDescription">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="mods:extent">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab451ToRelatedItem">
		<xsl:element name="mods:relatedItem">
		<xsl:if test="@ind=' '">
			<xsl:attribute name="displayLabel"><xsl:value-of select="'series1'"/></xsl:attribute>
		</xsl:if>
		<xsl:if test="@ind='a'">
			<xsl:attribute name="displayLabel"><xsl:value-of select="'series2'"/></xsl:attribute>
		</xsl:if>
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'series'"/></xsl:attribute>
			<xsl:element name="mods:titleInfo">
				<xsl:element name="mods:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab501ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab519ToNote">
		<xsl:element name="mods:note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'thesis'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab540ToIdentifier">
			<xsl:element name="mods:identifier">
				<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="type"><xsl:value-of select="'isbn'"/></xsl:attribute>
				<xsl:if test="@ind='b'">
					<xsl:attribute name="invalid"><xsl:value-of select="'yes'"/></xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab542ToIdentifier">
			<xsl:element name="mods:identifier">
				<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="type"><xsl:value-of select="'issn'"/></xsl:attribute>
				<xsl:if test="@ind='b'">
					<xsl:attribute name="invalid"><xsl:value-of select="'yes'"/></xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab544ToLocation">
		<xsl:element name="mods:location">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="mods:physicalLocation">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab552ToIdentifier">
			<xsl:element name="mods:identifier">
				<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:if test="@ind='a'">
				<xsl:attribute name="type"><xsl:value-of select="'doi'"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="@ind='b'">
				<xsl:attribute name="type"><xsl:value-of select="'urn'"/></xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab580ToIdentifier">
		<xsl:element name="mods:identifier">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'local'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab590ToRelatedItem">
		<xsl:element name="mods:relatedItem">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'host'"/></xsl:attribute>
			<xsl:element name="mods:titleInfo">
				<xsl:element name="mods:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	
	
	
		
	<xsl:template name="mab611ToRelatedItem">
		<xsl:element name="mods:originInfo">
			<xsl:if test="@ind=' '">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher3'"/></xsl:attribute>
				<xsl:element name="mods:place">
					<xsl:element name="mods:placeTerm">
						<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:element>
				<xsl:variable name="publisher" select="key('fields', '613')[@ind=' ']"/>
				<xsl:if test="$publisher">
					<xsl:element name="mods:publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="@ind='a'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'manufacturer'"/></xsl:attribute>
				<xsl:element name="mods:place">
					<xsl:element name="mods:placeTerm">
						<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:element>
				<xsl:variable name="printer" select="key('fields', '613')[@ind='a']"/>
				<xsl:if test="$printer">
					<xsl:element name="mods:publisher">
						<xsl:value-of select="$printer"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="key('fields', '619')">
				<xsl:element name="mods:dateCaptured">
					<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
					<xsl:value-of select="key('fields', '619')"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '640')">
				<xsl:element name="mods:edition">
					<xsl:value-of select="key('fields', '640')"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>	
	<xsl:template name="mab617ToRelatedItem">
		<xsl:element name="mods:originInfo">
			<xsl:attribute name="displayLabel"><xsl:value-of select="'creator'"/></xsl:attribute>
			<xsl:element name="mods:place">
				<xsl:element name="mods:placeTerm">
					<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
			<xsl:variable name="publisher" select="key('fields', '617')[@ind=' ']"/>
				<xsl:if test="$publisher">
					<xsl:element name="mods:publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
				</xsl:if>
			<xsl:if test="key('fields', '619')">
				<xsl:element name="mods:dateCaptured">
					<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
					<xsl:value-of select="key('fields', '619')"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '640')">
				<xsl:element name="mods:edition">
					<xsl:value-of select="key('fields', '640')"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab634ToRelatedItem">
		<xsl:element name="mods:identifier">
			<xsl:attribute name="type"><xsl:value-of select="'isbn'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab635ToRelatedItem">
		<xsl:element name="mods:identifier">
			<xsl:attribute name="type"><xsl:value-of select="'issn'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab646ToRelatedItem">
		<xsl:element name="mods:location">
			<xsl:element name="mods:physicalLocation">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab647ToRelatedItem">
		<xsl:element name="mods:note">
			<xsl:attribute name="type"><xsl:value-of select="'digital master'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab621ToRelatedItem">
		<xsl:element name="mods:relatedItem">
			<xsl:attribute name="type"><xsl:value-of select="'series'"/></xsl:attribute>
			<xsl:element name="mods:titleInfo">
				<xsl:element name="mods:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab659ToRelatedItem">
		<xsl:element name="mods:accessCondition">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab653ToRelatedItem">
		<xsl:element name="mods:physicalDescription">
			<xsl:element name="mods:form">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab637ToRelatedItem">
		<xsl:element name="mods:physicalDescription">
			<xsl:element name="mods:extent">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template name="mab655ToRelatedItem">
		<xsl:element name="mods:location">
			<xsl:element name="mods:url">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'electronic resource'"/></xsl:attribute>
				<xsl:attribute name="usage"><xsl:value-of select="'primary display'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	
	
	
	
	
	
	
	<xsl:template name="mab902ToSubject">
		<xsl:element name="mods:subject">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="authority"><xsl:value-of select="'rswk'"/></xsl:attribute>
			<xsl:element name="mods:topic">
					<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
