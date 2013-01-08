<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0" xmlns:dlc="http://dlc.mpdl.mpg.de">
    <xsl:output encoding="UTF-8" indent="yes" method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes" />
   
    
    
    <xsl:template match="bookMark|objectType|lacuna|pagesMisbound|leavesRebound|leavesLoosePrinted|leavesLooseHandwritten|tippedIn|tippedInLooseDesc|tippedInPastedDesc|endpaper|endpaperDesc|supportDesc|collationDesc|paginationDesc|edge|edgeDesc|leafMarker|leafMarkerDesc|pagesTrimmed|watermark|seal|sealDesc|heraldry|marginalDecoration|decoratedBorders|illustrationsMiscellaneous|initials|initialsDesc|rubrics|rubricsDesc|musicNotation|marginalia|marginaliaDesc|notesHandwrittenMiscellaneous|addedWork|exLibrisRemarkHandwritten|exLibrisRemarkUnidentified|donationText|dedicationText|signatureAuthor|bookPlate|bookPlateDesc|ownerStamp|stampMiscellaneous|label|provenanceReference|notesBookseller|shelfMarkMiscellaneous|acquisitionDateInstitution|bindingMaterial|bindingDesc|headbandDesc|sewingDesc|bookCoverDecoration|bookCoverDecorationDesc|ribbonMarker|papersPastedCover|dateCoined|initialsCoined|wastePaper|wastePaperDesc|pasteDownMarbeledPaper|width|height|condition|numberedCopy|miscellaneous|VDNumber|EDIT16Number|ICCUNumber|ISTCNumber|identificationNumberMiscellaneous">
    		 <xsl:if test="normalize-space(text()) or @available">
    		 	<div class="cdc">
				    <span class="cdcLabel">
						<xsl:value-of select="dlc:i18n(concat('cdc_', name()))"/>
					</span>
					<span class="cdcValue">
					<xsl:if test="text() and text()!='false' and text()!='true'">
						<xsl:value-of select="text()"/>
					</xsl:if>
					<xsl:if test="text() and (text()='false' or text()='true')">
						<xsl:value-of select="dlc:i18n(concat('cdc_available_', text()))"/>
					</xsl:if>
					<xsl:if test="@available">
						<xsl:value-of select="dlc:i18n(concat('cdc_available_', @available))"/>
					</xsl:if>
					</span>
		    	</div>
    		 </xsl:if>
    		
    
    </xsl:template>
    
    
      <xsl:template match="object|bodyOfVolume|provenance|binding">
      	<div class="cdc cdcLabelHeader">	    
			<xsl:value-of select="dlc:i18n(concat('cdc_', name()))"/>
		</div>
		 <xsl:apply-templates/>
      </xsl:template>
    
    <xsl:template match="text()">
    
    </xsl:template>
    <!--Element with no child nodes 
    <xsl:template match="*[not(*)]">
    	 <xsl:if test="normalize-space(text()) or @*">
    	 <div class="cdc">
		    <span class="cdcLabel">
				<xsl:value-of select="dlc:i18n(concat('cdc_', name()))"/>
			</span>
			<span class="cdcValue">
				<xsl:value-of select="text()"/>
			</span>
    	</div>
    	</xsl:if>
    	<xsl:apply-templates select="@*"/>
    </xsl:template>
    -->
      <!--Attribute 
     <xsl:template match="@*">
     	<div class="cdcAttr">
	    	<span class="cdcLabelAttr">
	    		<xsl:value-of select="dlc:i18n(concat('cdc_', name()))"/>
	    	</span>
	    	<span class="cdcValueAttr">
	    		<xsl:value-of select="."/>
	    	</span>
    	</div>
    </xsl:template>
    -->
    <!--Element with child nodes 
    <xsl:template match="*">
    	<div class="eg3_container_1 cdcLabelHeader">
    		<xsl:value-of select="dlc:i18n(concat('cdc_', name()))"/>
    	</div>
    	<xsl:apply-templates select="@*|*"/>
    </xsl:template>
    -->
    
   


</xsl:stylesheet>
