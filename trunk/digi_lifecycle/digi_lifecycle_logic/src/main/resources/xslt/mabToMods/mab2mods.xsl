<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mab="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd" xmlns="http://www.loc.gov/mods/v3" exclude-result-prefixes="mab">
	<xsl:output indent="yes" method="xml" encoding="UTF-8"/>
	
	<xsl:variable name="IDPREFIX" select="'mab'"/>
	<xsl:variable name="UNIFORM_TITLE" select="'uniform'"/>
	<xsl:variable name="ALTERNATIVE_TITLE" select="'alternative'"/>
	<xsl:variable name="ABBREVIATED_TITLE" select="'abbreviated'"/>
	<xsl:variable name="TRANSLATED_TITLE" select="'translated'"/>
	
	<xsl:key match="mab:feld" name="fields" use="@nr"/>

	 <xsl:template match="/">
		<xsl:element name="mods">
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
					<xsl:call-template name="mab359ToNote"/>
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
				<!-- 
					611 - 659 MISSING!
					concrete sample required for implementation.
				-->
								<!-- 
				
				<xsl:for-each select="key('fields', '700')">
					<xsl:call-template name="mab700ToClassification"/>
				</xsl:for-each>
				-->
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
		<xsl:element name="recordInfo">
			<xsl:element name="recordIdentifier">
				<xsl:attribute name="source"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab010ToRelatedItem">
		<xsl:element name="relatedItem">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'host'"/></xsl:attribute>
			<xsl:element name="identifier">
				<xsl:attribute name="type"><xsl:value-of select="'local'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab025ToIdentifier">
		<xsl:if test="@ind='z'">
			<xsl:element name="identifier">
				<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="type"><xsl:value-of select="'zdb-id'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	
	
	<xsl:template name="mab037ToLanguage">
		<xsl:element name="language">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="languageTerm">
				<xsl:attribute name="type"><xsl:value-of select="'code'"/></xsl:attribute>
				<xsl:attribute name="authority"><xsl:value-of select="'rfc4646'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab089ToPart">
		<xsl:element name="part">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'host'"/></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="."/></xsl:attribute>
			<xsl:element name="detail">
				<xsl:element name="number">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab100ToName">
		<xsl:element name="name">
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
			<xsl:if test="not(@ind=' ' or @ind='a')">
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
			<xsl:element name="namePart">
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:element name="role">
				<xsl:element name="roleTerm">
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
	
	<xsl:template name="mab200ToName">
		<xsl:element name="name">
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
			<xsl:element name="namePart">
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:element name="role">
				<xsl:element name="roleTerm">
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
		<xsl:element name="titleInfo">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="$UNIFORM_TITLE"/></xsl:attribute>
			<xsl:element name="title">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab310ToAlternativeTitle">
		<xsl:element name="titleInfo">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="$ALTERNATIVE_TITLE"/></xsl:attribute>
			<xsl:if test="@nr='341'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'parallelTitle'"/></xsl:attribute>
			</xsl:if>
			<xsl:element name="title">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab331ToMainTitle">
		<xsl:element name="titleInfo">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="displayLabel"><xsl:value-of select="'mainTitle'"/></xsl:attribute>
			<xsl:element name="title">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab334ToPhysicalDescription">
		<xsl:element name="physicalDescription">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="form">
				<xsl:attribute name="type"><xsl:value-of select="'material'"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab335ToSubTitle">
		<xsl:element name="titleInfo">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="subTitle">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab359ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'statement of responsibility'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab360ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'subseries'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab361ToPart">
		<xsl:element name="part">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'constituent'"/></xsl:attribute>
			<xsl:element name="detail">
				<xsl:element name="title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab365ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'remainder of title for whole item'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab410ToOriginInfo">
		<xsl:element name="originInfo">
			<xsl:if test="@ind=' '">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher1'"/></xsl:attribute>
			<xsl:element name="place">
				<xsl:element name="placeTerm">
					<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
				<xsl:variable name="publisher" select="key('fields', '412')[@ind=' ']"/>
				<xsl:if test="$publisher">
					<xsl:element name="publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="@ind='a'">
				<xsl:attribute name="displayLabel"><xsl:value-of select="'printer1'"/></xsl:attribute>
			<xsl:element name="place">
				<xsl:element name="placeTerm">
					<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
				<xsl:variable name="printer" select="key('fields', '412')[@ind='a']"/>
				<xsl:if test="$printer">
					<xsl:element name="publisher">
						<xsl:value-of select="$printer"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="key('fields', '425')">
				<xsl:element name="dateIssued">
				<xsl:attribute name="keyDate"><xsl:value-of select="'yes'"/></xsl:attribute>
				<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
					<xsl:value-of select="key('fields', '425')"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '403')">
				<xsl:element name="edition">
					<xsl:value-of select="key('fields', '403')"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab415ToOriginInfo">
		<xsl:element name="originInfo">
		<xsl:if test="@ind=' '">
			<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher2'"/></xsl:attribute>
		</xsl:if>
		<xsl:if test="@ind='a'">
			<xsl:attribute name="displayLabel"><xsl:value-of select="'printer2'"/></xsl:attribute>
		</xsl:if>
			<xsl:element name="place">
				<xsl:element name="placeTerm">
					<xsl:attribute name="type"><xsl:value-of select="'text'"/></xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
			<xsl:if test="key('fields', '417')">
				<xsl:element name="publisher">
					<xsl:value-of select="key('fields', '417')"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '425')">
				<xsl:element name="dateIssued">
				<xsl:attribute name="keyDate"><xsl:value-of select="'yes'"/></xsl:attribute>
				<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
					<xsl:value-of select="key('fields', '425')"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="key('fields', '403')">
				<xsl:element name="edition">
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
				<xsl:element name="originInfo">
					<xsl:attribute name="displayLabel"><xsl:value-of select="'publisher1'"/></xsl:attribute>
					<xsl:element name="dateIssued">
						<xsl:attribute name="keyDate"><xsl:value-of select="'yes'"/></xsl:attribute>
						<xsl:attribute name="encoding"><xsl:value-of select="'w3cdtf'"/></xsl:attribute>
						<xsl:value-of select="key('fields', '425')"/>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="mab429ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'subject completeness'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab431ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'indexes'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab432ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'summary of volumes'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab433ToPhysicalDescription">
		<xsl:element name="physicalDescription">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="extent">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab451ToRelatedItem">
		<xsl:element name="relatedItem">
		<xsl:if test="@ind=' '">
			<xsl:attribute name="displayLabel"><xsl:value-of select="'series1'"/></xsl:attribute>
		</xsl:if>
		<xsl:if test="@ind='a'">
			<xsl:attribute name="displayLabel"><xsl:value-of select="'series2'"/></xsl:attribute>
		</xsl:if>
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'series'"/></xsl:attribute>
			<xsl:element name="titleInfo">
				<xsl:element name="title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab501ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab519ToNote">
		<xsl:element name="note">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'thesis'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab540ToIdentifier">
			<xsl:element name="identifier">
				<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="type"><xsl:value-of select="'isbn'"/></xsl:attribute>
				<xsl:if test="@ind='b'">
					<xsl:attribute name="invalid"><xsl:value-of select="'yes'"/></xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab542ToIdentifier">
			<xsl:element name="identifier">
				<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
				<xsl:attribute name="type"><xsl:value-of select="'issn'"/></xsl:attribute>
				<xsl:if test="@ind='b'">
					<xsl:attribute name="invalid"><xsl:value-of select="'yes'"/></xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:element>
	</xsl:template>	
	
	<xsl:template name="mab544ToLocation">
		<xsl:element name="location">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:element name="holdingSimple">
				<xsl:element name="copyInformation">
					<xsl:element name="shelfLocator">
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab552ToIdentifier">
			<xsl:element name="identifier">
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
		<xsl:element name="identifier">
			<xsl:attribute name="displayLabel"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'local'"/></xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab590ToRelatedItem">
		<xsl:element name="relatedItem">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select="'host'"/></xsl:attribute>
			<xsl:element name="titleInfo">
				<xsl:element name="title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="mab902ToSubject">
		<xsl:element name="subject">
			<xsl:attribute name="ID"><xsl:value-of select="concat($IDPREFIX, @nr)"/></xsl:attribute>
			<xsl:attribute name="authority"><xsl:value-of select="'rswk'"/></xsl:attribute>
			<xsl:element name="topic">
					<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
