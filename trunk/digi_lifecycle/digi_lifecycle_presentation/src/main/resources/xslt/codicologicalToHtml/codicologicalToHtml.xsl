<?xml version="1.0" encoding="UTF-8"?>
<!--
  CDDL HEADER START
  The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
  
  You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0" xmlns:dlc="http://dlc.mpdl.mpg.de">
    <xsl:output encoding="UTF-8" indent="yes" method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes" />
   
    
    
    <xsl:template match="bookMark|objectType|lacuna|pagesMisbound|leavesRebound|leavesLoosePrinted|leavesLooseHandwritten|tippedIn|tippedInLooseDesc|tippedInPastedDesc|endpaper|endpaperDesc|supportDesc|collationDesc|paginationDesc|edge|edgeDesc|leafMarker|leafMarkerDesc|pagesTrimmed|watermark|seal|sealDesc|heraldry|marginalDecoration|decoratedBorders|illustrationsMiscellaneous|initials|initialsDesc|rubrics|rubricsDesc|musicNotation|marginalia|marginaliaDesc|notesHandwrittenMiscellaneous|addedWork|exLibrisRemarkHandwritten|exLibrisRemarkUnidentified|donationText|dedicationText|signatureAuthor|bookPlate|bookPlateDesc|ownerStamp|stampMiscellaneous|label|provenanceReference|notesBookseller|shelfMarkMiscellaneous|acquisitionDateInstitution|bindingMaterial|bindingDesc|headbandDesc|sewingDesc|bookCoverDecoration|bookCoverDecorationDesc|ribbonMarker|papersPastedCover|dateCoined|initialsCoined|wastePaper|wastePaperDesc|pasteDownMarbeledPaper|width|height|condition|numberedCopy|miscellaneous|VDNumber|EDIT16Number|ICCUNumber|ISTCNumber|identificationNumberMiscellaneous">
    		 <xsl:if test="(normalize-space(text()) and text()!='false') or (@available!='false')">
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
      	<div class="cdcBlock cdc">	    
			<span class="cdcLabelHeader">
			<xsl:value-of select="dlc:i18n(concat('cdc_', name()))"/>
			</span>
			 <xsl:apply-templates/>
		</div>
		
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
