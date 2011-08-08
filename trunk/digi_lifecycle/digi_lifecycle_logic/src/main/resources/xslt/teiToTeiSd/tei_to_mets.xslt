<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:mets="http://www.loc.gov/METS/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">

    <xsl:output indent="yes"/>

    <xsl:param name="tei">
        <xsl:copy-of select="/tei:TEI"/>
    </xsl:param>


    <xsl:template match="tei:TEI">
        <mets:mets
            xsi:schemaLocation="http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd">
            <xsl:attribute name="ID">
                <xsl:value-of select="@xml:id"/>
            </xsl:attribute>
            <!--
            <xsl:call-template name="metsHdr"/>
            <xsl:call-template name="dmdSec"/>
            -->
            <xsl:call-template name="fileSec"/>
            <xsl:call-template name="structMap-physical"/>
            <xsl:call-template name="structMap-logical"/>
            <xsl:call-template name="structLink"/>
        </mets:mets>
    </xsl:template>
<!--
	<xsl:template name="metsHdr">
        <mets:metsHdr>
            <xsl:attribute name="CREATEDATE">
                <xsl:value-of select="substring-before(string(fn:current-dateTime()), '.' )"/>
            </xsl:attribute>
            <mets:agent ROLE="CREATOR">
                <mets:name>
                    <xsl:value-of select="$creator"/>
                </mets:name>
            </mets:agent>
        </mets:metsHdr>
    </xsl:template>

    <xsl:template name="dmdSec">
        <mets:dmdSec ID="dmdTEIHDR">
            <mets:mdWrap MIMETYPE="text/xml" MDTYPE="TEIHDR" LABEL="TEI Header metadata">
                <mets:xmlData>
                    <xsl:copy-of select="/TEI/teiHeader"/>
                </mets:xmlData>
            </mets:mdWrap>

        </mets:dmdSec>
    </xsl:template>
    -->

    <xsl:template name="fileSec">
        <mets:fileSec>
            <mets:fileGrp USE="scans">
               <xsl:for-each select="$tei//tei:pb">
                 <mets:file>
                    <xsl:attribute name="ID">
                        <xsl:value-of select="concat('file_',@xml:id)"/>
                    </xsl:attribute>
                    <xsl:attribute name="MIMETYPE">
                        <xsl:value-of select="'image/jpeg'"/>
                    </xsl:attribute>
                    <mets:FLocat>
                        <xsl:attribute name="LOCTYPE">
                            <xsl:value-of select="'OTHER'"/>
                        </xsl:attribute>
                        <xsl:attribute name="xlink:href">
                            <xsl:value-of select="@facs"/>
                        </xsl:attribute>
                    </mets:FLocat>
                </mets:file>
            </xsl:for-each>
            </mets:fileGrp>
        </mets:fileSec>
    </xsl:template>


    <xsl:template name="structMap-physical">
        <mets:structMap TYPE="physical">
            <mets:div DMDID="dmd_0">
                <!--
           	 <xsl:attribute name="LABEL">
                    <xsl:call-template name="labelCitation"/>
                </xsl:attribute>
                -->
                <xsl:for-each select="//tei:pb">
                    <xsl:apply-templates select="." mode="structMap-physical">
                        <xsl:with-param name="n">
                            <xsl:value-of select="@n"/>
                        </xsl:with-param>
                        <xsl:with-param name="id">
                            <xsl:value-of select="@xml:id"/>
                        </xsl:with-param>
                    </xsl:apply-templates>
                </xsl:for-each>
            </mets:div>
        </mets:structMap>
    </xsl:template>

    <xsl:template match="tei:pb" mode="structMap-physical">
        <xsl:param name="n"/>
        <xsl:param name="id"/>
        <mets:div TYPE="page">
        	<xsl:attribute name="ID">
        		<xsl:value-of select="@xml:id"/>
        	</xsl:attribute>
            <xsl:attribute name="ORDER">
                <xsl:number count="//tei:pb" level="any"/>
            </xsl:attribute>
            <xsl:if test="$n != ''">
                <xsl:attribute name="ORDERLABEL">
                    <xsl:value-of select="$n"/>
                </xsl:attribute>
            </xsl:if>
             	<mets:fptr>
		    <xsl:attribute name="FILEID">
			<xsl:value-of select="concat('file_',@xml:id)"/>
		    </xsl:attribute>
		</mets:fptr>
        </mets:div>
    </xsl:template>

  
    
    <xsl:template name="structMap-logical">
        <mets:structMap TYPE="logical">
            <mets:div type="book">
                <!--
            <xsl:attribute name="LABEL">
                    <xsl:call-template name="labelCitation"/>
                </xsl:attribute>
                -->
                <xsl:apply-templates select="/tei:TEI/tei:text"/>
            </mets:div>
        </mets:structMap>
    </xsl:template>
    
    <xsl:template match="tei:front|tei:div|tei:titlePage|tei:front/tei:epigraph|tei:back|tei:body">
        <mets:div>
            <xsl:attribute name="ID">
                <xsl:value-of select="@xml:id"/>
            </xsl:attribute>
            <xsl:attribute name="ORDER">
                <xsl:value-of select="count(preceding-sibling::*[not(name(.) = 'pb') or not(name(.) = 'head')]) + 1"/>
            </xsl:attribute>
            <xsl:attribute name="TYPE">
                <xsl:choose>
                    <xsl:when test="name(.) = 'front'">
                        <xsl:value-of select="'frontmatter'"/>
                    </xsl:when>
                    <xsl:when test="name(.) = 'back'">
                        <xsl:value-of select="'backmatter'"/>
                    </xsl:when>
                    <xsl:when test="name(.) = 'body'">
                        <xsl:value-of select="'body'"/>
                    </xsl:when>
                    <xsl:when test="name(.) = 'titlePage'">
                        <xsl:value-of select="'title'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@type"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="LABEL">
                <xsl:choose>
                    <xsl:when test="name(.) = 'front'">
                        <xsl:value-of select="'Front Matter'"/>
                    </xsl:when>
                    <xsl:when test="name(.) = 'back'">
                        <xsl:value-of select="'Back Matter'"/>
                    </xsl:when>
                    <!-- don't want body to repeat head from text -->
                    <xsl:when test="name(.) = 'body'"/>
                    <xsl:when test="name(.) = 'titlePage'">
                        <xsl:value-of select="'Title Page'"/>
                    </xsl:when>
                    <xsl:when test="name(.) = 'epigraph'">
                        <xsl:value-of select="'Epigraph'"/>
                    </xsl:when>
                    <xsl:when test="name(.) = 'text'">
                                <xsl:value-of select="normalize-space(descendant::tei:head[1])"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="tei:head">
                                <xsl:value-of select="normalize-space(tei:head[1])"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="normalize-space(@n)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates />
            <!--
            <xsl:choose>
                <xsl:when test=".//tei:pb">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="preceding::tei:pb[position()=1]"/>
                </xsl:otherwise>
            </xsl:choose>
            -->
            
        </mets:div>
    </xsl:template>
    
    <!--
    <xsl:template match="tei:pb">
        <xsl:param name="n"><xsl:value-of select="@n"/></xsl:param>
        <xsl:param name="id"><xsl:value-of select="@xml:id"/></xsl:param>
        <xsl:param name="chunkId"/>
        <mets:div TYPE="page">
            <xsl:attribute name="ORDER">
                <xsl:value-of select="count(preceding::pb[ancestor::*[@xml:id = $chunkId]]) + 1"/>
            </xsl:attribute>
            <xsl:if test="$n != ''">
                <xsl:attribute name="ORDERLABEL">
                    <xsl:value-of select="$n"/>
                </xsl:attribute>
            </xsl:if>
        </mets:div>
    </xsl:template>
    -->
    
    
    <xsl:template name="structLink">
    	<mets:structLink>
    		<xsl:apply-templates mode="structLink" />
    	</mets:structLink>	
    </xsl:template>

 <xsl:template match="tei:front|tei:div|tei:titlePage|tei:front/tei:epigraph|tei:back|tei:body" mode="structLink">
            <xsl:choose>
                <xsl:when test=".//tei:pb">
                    <xsl:apply-templates mode="structLink">
                    	<xsl:with-param name="divId" select="@xml:id" />
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="preceding::tei:pb[position()=1]" mode="structLink">
                    	<xsl:with-param name="divId" select="@xml:id" />
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
    
    <xsl:template match="tei:pb" mode="structLink">
    	<xsl:param name="divId"/>
        <mets:smLink>
            <xsl:attribute name="xlink:from">
                <xsl:value-of select="$divId"/>
            </xsl:attribute>
           
            <xsl:attribute name="xlink:to">
                <xsl:value-of select="@xml:id"/>
	    </xsl:attribute>
           
        </mets:smLink>
    </xsl:template>

    <xsl:template match="text()"/>
    <xsl:template match="text()" mode="structLink"/>
    
    <!--
    <xsl:template name="labelCitation">
        <xsl:value-of select="normalize-space(/tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:author/text())"/>
        <xsl:value-of select="normalize-space(/tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:title[1]/text())"/>
    </xsl:template>
-->    


    <!-- 
        <mets:file ID="VAA1194-01-archive" GROUPID="VAA1194-01" MIMETYPE="image/tif" SEQ="1"
        CREATED="2004-07-22T14:00:00" USE="archive">
        <mets:FLocat LOCTYPE="PURL" xlink:href="http://purl.dlib.indiana.edu/iudl/"/>
        </mets:file>
    -->
    
  
    


</xsl:stylesheet>
