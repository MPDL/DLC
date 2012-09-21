<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0" xmlns:dlc="http://dlc.mpdl.mpg.de">
    <xsl:output encoding="UTF-8" indent="yes" method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes" />
   
    
    <!--Element with no child nodes -->
    <xsl:template match="*[not(*)]">
    	<div class="cdc">
		    <xsl:if test="normalize-space(text())!=''">
		    <span class="cdcLabel">
				<xsl:value-of select="dlc:i18n(concat('codicological_', name()))"/>
			</span>
			<span class="cdcValue">
				<xsl:value-of select="text()"/>
			</span>
	    	</xsl:if>
    	</div>
    	<xsl:apply-templates select="@*"/>
    </xsl:template>
    
      <!--Attribute -->
     <xsl:template match="@*">
     	<div class="eg3_container_1">
	    	<span class="cdcLabelAttr">
	    		<xsl:value-of select="dlc:i18n(concat('codicological_', name()))"/>
	    	</span>
	    	<span class="cdcValueAttr">
	    		<xsl:value-of select="."/>
	    	</span>
    	</div>
    </xsl:template>
    
    <!--Element with child nodes -->
    <xsl:template match="*">
    	<div class="eg3_container_1 cdcLabelHeader">
    		<xsl:value-of select="dlc:i18n(concat('codicological_', name()))"/>
    	</div>
    	<xsl:apply-templates select="@*|*"/>
    </xsl:template>
    
    
   


</xsl:stylesheet>
