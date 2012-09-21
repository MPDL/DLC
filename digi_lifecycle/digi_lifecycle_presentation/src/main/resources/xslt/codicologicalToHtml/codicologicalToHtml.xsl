<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0" xmlns:dlc="http://dlc.mpdl.mpg.de">
    <xsl:output encoding="UTF-8" indent="yes" method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes" />
   
    
    <!--Element with no child nodes -->
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
    
      <!--Attribute -->
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
    
    <!--Element with child nodes -->
    <xsl:template match="*">
    	<div class="eg3_container_1 cdcLabelHeader">
    		<xsl:value-of select="dlc:i18n(concat('cdc_', name()))"/>
    	</div>
    	<xsl:apply-templates select="@*|*"/>
    </xsl:template>
    
    
   


</xsl:stylesheet>
