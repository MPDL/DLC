<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:mets="http://www.loc.gov/METS/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">

    
    <xsl:output indent="yes"/>
    
   <xsl:template match="@*|*|processing-instruction()|comment()|text()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|processing-instruction()|comment()|text()"/>
    </xsl:copy>
  </xsl:template>
    
   <xsl:template match="mets:div">
    <xsl:copy>
      <xsl:if test="not(@ID) or @ID=''">
        <xsl:attribute name="ID">
          <xsl:value-of select="generate-id()"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="@*|*|processing-instruction()|comment()|text()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
