
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:mets="http://www.loc.gov/METS/" xmlns:toc="http://www.escidoc.de/schemas/tableofcontent/0.1" xmlns:virr="http://purl.org/escidoc/metadata/profiles/0.1/virrelement" xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5" xmlns:tei="http://www.tei-c.org/ns/1.0" exclude-result-prefixes="#all">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:param name="modsUrl" select="'2provide'"/>
	<xsl:variable name="modsXml" select="document($modsUrl)"/>
	<xsl:param name="childmodsUrl" select="()"/>
	<xsl:variable name="childmodsXml" select="document($childmodsUrl)"/>
	<xsl:param name="metsUrl" select="'2provide'"/>
	<xsl:variable name="metsXml" select="document($metsUrl)"/>
	
	<xsl:key name="page-by-href" match="toc:div[TYPE='page']" use="toc:ptr/@xlink:href"/>
	
	<xsl:template match="/">
		<tei:TEI>
			<xsl:call-template name="header"/>
			<xsl:call-template name="text"/>
		</tei:TEI>
	</xsl:template>
	
	<xsl:template match="toc:div[@TYPE='structural-element']">
		<xsl:variable name="pages" select="./toc:div[@TYPE='page']"></xsl:variable>
		<xsl:variable name="genre" select="./virr:virrelement/mods:mods/mods:genre"></xsl:variable>
		<xsl:variable name="partNumber" select="./virr:virrelement/mods:mods/mods:titleInfo/mods:partNumber"></xsl:variable>
		<xsl:variable name="title" select="./virr:virrelement/mods:mods/mods:titleInfo/mods:title"></xsl:variable>
		<xsl:variable name="subTitle" select="./virr:virrelement/mods:mods/mods:titleInfo/mods:subTitle"></xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$pages[1]/toc:ptr/@xlink:href = preceding::toc:ptr/@xlink:href">
				<tei:div>
					<xsl:attribute name="xml:id">
						<xsl:value-of select="generate-id()"/>
					</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$genre =('pre-title-page', 'chapter', 'other', 'introduction', 'law')">
							<xsl:attribute name="type">
								<xsl:value-of select="'section'"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="$genre ='chronological-index'">
							<xsl:attribute name="type">
								<xsl:value-of select="'index'"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="$genre ='toc'">
							<xsl:attribute name="type">
								<xsl:value-of select="'content'"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="$genre ='title-page'">
							<xsl:attribute name="type">
								<xsl:value-of select="'titlepage'"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="type">
								<xsl:value-of select="./virr:virrelement/mods:mods/mods:genre"/>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="$partNumber!='' and $title!='' and $subTitle!=''">
							<xsl:if test="ends-with(normalize-space($title), '.')">
								<xsl:choose>
									<xsl:when test="ends-with(normalize-space($subTitle), '.')">
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, ' ', substring($subTitle, 1, string-length($subTitle) - 1))"/>
										</tei:head>
									</xsl:when>
									<xsl:otherwise>
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, ' ', $subTitle)"/>
										</tei:head>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
							<xsl:if test="not(ends-with(normalize-space($title), '.'))">
								<xsl:choose>
									<xsl:when test="ends-with(normalize-space($subTitle), '.')">
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, '. ', substring($subTitle, 1, string-length($subTitle) - 1))"/>
										</tei:head>
									</xsl:when>
									<xsl:otherwise>
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, '. ', $subTitle)"/>
										</tei:head>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
						</xsl:when>
						<xsl:when test="$partNumber!='' and $title!=''">
							<xsl:choose>
								<xsl:when test="ends-with(normalize-space($title), '.')">
									<tei:head>
										<xsl:value-of select="concat($partNumber, ' ', substring($title, 1, string-length($title) - 1))"/>
									</tei:head>
								</xsl:when>
								<xsl:otherwise>
									<tei:head>
										<xsl:value-of select="concat($partNumber, ' ', $title)"/>
									</tei:head>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$title!=''">
							<xsl:choose>
								<xsl:when test="ends-with(normalize-space($title), '.')">
									<tei:head>
										<xsl:value-of select="substring($title, 1, string-length($title) - 1)"/>
									</tei:head>
								</xsl:when>
								<xsl:otherwise>
									<tei:head>
										<xsl:value-of select="$title"/>
									</tei:head>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise></xsl:otherwise>
					</xsl:choose>
					<xsl:for-each select="$pages[position() > 1]">
						<xsl:variable name="page_number" select="substring-after(./toc:ptr/@xlink:href, '_')"></xsl:variable>
						<xsl:variable name="page_in_mets" select="$metsXml/mets:mets/mets:structMap/mets:div/mets:div[@ID=$page_number]"></xsl:variable>
						<tei:pb>
							<xsl:attribute name="facs">
								<xsl:value-of select="$page_in_mets/@CONTENTIDS"/>
							</xsl:attribute>
							<xsl:attribute name="n">
								<xsl:value-of select="$page_in_mets/@ORDERLABEL"/>
							</xsl:attribute>
							<xsl:attribute name="xml:id">
								<xsl:value-of select="$page_number"/>
							</xsl:attribute>
						</tei:pb>
					</xsl:for-each>
				</tei:div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$genre = ('cover', 'fly-leaf', 'trailer-record')">
						<xsl:for-each select="$pages">
							<xsl:variable name="page_number" select="substring-after(./toc:ptr/@xlink:href, '_')"></xsl:variable>
							<xsl:variable name="page_in_mets" select="$metsXml/mets:mets/mets:structMap/mets:div/mets:div[@ID=$page_number]"></xsl:variable>
							<tei:pb>
								<xsl:attribute name="facs">
									<xsl:value-of select="$page_in_mets/@CONTENTIDS"/>
								</xsl:attribute>
								<xsl:attribute name="n">
									<xsl:value-of select="$page_in_mets/@ORDERLABEL"/>
								</xsl:attribute>
								<xsl:attribute name="xml:id">
									<xsl:value-of select="$page_number"/>
								</xsl:attribute>
								<xsl:if test="$genre = 'cover'">
									<xsl:attribute name="type">
										<xsl:value-of select="'cover'"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:if test="$genre = ('fly-leaf', 'trailer-record')">
									<xsl:attribute name="type">
										<xsl:value-of select="'endsheet'"/>
									</xsl:attribute>
								</xsl:if>
							</tei:pb>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="count($pages) &gt; 0">
							<xsl:variable name="first_page_number" select="substring-after($pages[1]/toc:ptr/@xlink:href, '_')"></xsl:variable>
							<xsl:variable name="first_page_in_mets" select="$metsXml/mets:mets/mets:structMap/mets:div/mets:div[@ID=$first_page_number]"></xsl:variable>
							<tei:pb>
								<xsl:attribute name="facs">
									<xsl:value-of select="$first_page_in_mets/@CONTENTIDS"/>
								</xsl:attribute>
								<xsl:attribute name="n">
									<xsl:value-of select="$first_page_in_mets/@ORDERLABEL"/>
								</xsl:attribute>
								<xsl:attribute name="xml:id">
									<xsl:value-of select="$first_page_number"/>
								</xsl:attribute>
							</tei:pb>
						</xsl:if>
						<tei:div>
							<xsl:attribute name="xml:id">
								<xsl:value-of select="generate-id()"/>
							</xsl:attribute>
							<xsl:choose>
								<xsl:when test="$genre =('pre-title-page', 'chapter', 'other', 'introduction', 'law')">
									<xsl:attribute name="type">
										<xsl:value-of select="'section'"/>
									</xsl:attribute>
								</xsl:when>
								<xsl:when test="$genre ='chronological-index'">
									<xsl:attribute name="type">
										<xsl:value-of select="'index'"/>
									</xsl:attribute>
								</xsl:when>
								<xsl:when test="$genre ='toc'">
									<xsl:attribute name="type">
										<xsl:value-of select="'content'"/>
									</xsl:attribute>
								</xsl:when>
								<xsl:when test="$genre ='title-page'">
									<xsl:attribute name="type">
										<xsl:value-of select="'titlepage'"/>
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="type">
										<xsl:value-of select="./virr:virrelement/mods:mods/mods:genre"/>
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="$partNumber!='' and $title!='' and $subTitle!=''">
							<xsl:if test="ends-with(normalize-space($title), '.')">
								<xsl:choose>
									<xsl:when test="ends-with(normalize-space($subTitle), '.')">
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, ' ', substring($subTitle, 1, string-length($subTitle) - 1))"/>
										</tei:head>
									</xsl:when>
									<xsl:otherwise>
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, ' ', $subTitle)"/>
										</tei:head>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
							<xsl:if test="not(ends-with(normalize-space($title), '.'))">
								<xsl:choose>
									<xsl:when test="ends-with(normalize-space($subTitle), '.')">
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, '. ', substring($subTitle, 1, string-length($subTitle) - 1))"/>
										</tei:head>
									</xsl:when>
									<xsl:otherwise>
										<tei:head>
											<xsl:value-of select="concat($partNumber, ' ', $title, '. ', $subTitle)"/>
										</tei:head>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
						</xsl:when>
						<xsl:when test="$partNumber!='' and $title!=''">
							<xsl:choose>
								<xsl:when test="ends-with(normalize-space($title), '.')">
									<tei:head>
										<xsl:value-of select="concat($partNumber, ' ', substring($title, 1, string-length($title) - 1))"/>
									</tei:head>
								</xsl:when>
								<xsl:otherwise>
									<tei:head>
										<xsl:value-of select="concat($partNumber, ' ', $title)"/>
									</tei:head>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="$title!=''">
							<xsl:choose>
								<xsl:when test="ends-with(normalize-space($title), '.')">
									<tei:head>
										<xsl:value-of select="substring($title, 1, string-length($title) - 1)"/>
									</tei:head>
								</xsl:when>
								<xsl:otherwise>
									<tei:head>
										<xsl:value-of select="$title"/>
									</tei:head>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise></xsl:otherwise>
							</xsl:choose>
							<xsl:for-each select="$pages[position() > 1]">
								<xsl:variable name="page_number" select="substring-after(./toc:ptr/@xlink:href, '_')"></xsl:variable>
								<xsl:variable name="page_in_mets" select="$metsXml/mets:mets/mets:structMap/mets:div/mets:div[@ID=$page_number]"></xsl:variable>
								<tei:pb>
									<xsl:attribute name="facs">
										<xsl:value-of select="$page_in_mets/@CONTENTIDS"/>
									</xsl:attribute>
									<xsl:attribute name="n">
										<xsl:value-of select="$page_in_mets/@ORDERLABEL"/>
									</xsl:attribute>
									<xsl:attribute name="xml:id">
										<xsl:value-of select="$page_number"/>
									</xsl:attribute>
								</tei:pb>
							</xsl:for-each>
						</tei:div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="./toc:div[@TYPE='structural-element']"/>
	</xsl:template>
	
	<xsl:template name="text">
		<tei:text>
			<xsl:attribute name="xml:id">
				<xsl:value-of select="generate-id()"/>
			</xsl:attribute>
			<tei:body>
				<xsl:attribute name="xml:id">
					<xsl:value-of select="generate-id()"/>
				</xsl:attribute>
				<xsl:apply-templates select="//toc:div[@ID='logical_0']/toc:div"/>
			</tei:body>
		</tei:text>
	</xsl:template>
	
	<xsl:template name="header">
		<tei:teiHeader>
			<tei:fileDesc>
				<tei:titleStmt>
					<xsl:if test="$modsXml//mods:titleInfo[@displayLabel='mainTitle']">
						<tei:title>
							<xsl:attribute name="type">
								<xsl:value-of select="'main'"/>
							</xsl:attribute>
							<xsl:value-of select="$modsXml//mods:titleInfo[@displayLabel='mainTitle']/mods:title"/>
						</tei:title>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="$childmodsXml">
							<xsl:if test="$childmodsXml//mods:part/mods:detail/mods:number">
								<tei:title>
									<xsl:attribute name="type">
										<xsl:value-of select="'sub'"/>
									</xsl:attribute>
									<xsl:value-of select="$childmodsXml//mods:part/mods:detail/mods:number"/>
								</tei:title>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="$modsXml//mods:titleInfo/mods:subTitle">
								<tei:title>
									<xsl:attribute name="type">
										<xsl:value-of select="'sub'"/>
									</xsl:attribute>
									<xsl:value-of select="$modsXml//mods:titleInfo/mods:subTitle"/>
								</tei:title>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="$modsXml//mods:name[@type='personal']">
							<tei:author>
								<xsl:value-of select="$modsXml//mods:name[@type='personal'][1]/mods:namePart"/>
							</tei:author>
						</xsl:when>
						<xsl:otherwise>
							<tei:author>
								<xsl:value-of select="'n/a'"/>
							</tei:author>
						</xsl:otherwise>
					</xsl:choose>
				</tei:titleStmt>
				<tei:publicationStmt>
					<tei:date>
						<xsl:attribute name="when">
							<xsl:value-of select="current-dateTime()"/>
						</xsl:attribute>
					</tei:date>
				</tei:publicationStmt>
				<tei:sourceDesc>
					<tei:p>
						<xsl:value-of select="'generated by DLC editor'"/>
					</tei:p>
				</tei:sourceDesc>
			</tei:fileDesc>
		</tei:teiHeader>
	</xsl:template>

</xsl:stylesheet>