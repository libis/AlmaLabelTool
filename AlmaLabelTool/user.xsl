<xsl:stylesheet version="2.0" xmlns:xb="http://com/exlibris/urm/user_record/xmlbeans"  
                              xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              exclude-result-prefixes="xb xsi">
                              
   <xsl:output method="xml" indent="yes"/>    
   
   <xsl:template match="node()|@*">
      <printout>
         <result>
            <error></error>
         </result>
         <form-name>Default</form-name>
         <form-language>eng</form-language>
         <form-format>00</form-format>
         
         <section-01>
            <physical_item_display_for_printing>
               <userAddressPreferred>
                  <fullname><xsl:value-of select="//xb:userDetails/xb:fullName" /></fullname>
                  <line1><xsl:value-of select="//xb:userAddressList/xb:userAddress[@preferred='true']/xb:line1" /></line1>
                  <line2><xsl:value-of select="//xb:userAddressList/xb:userAddress[@preferred='true']/xb:line2" /></line2>
                  <line3><xsl:value-of select="//xb:userAddressList/xb:userAddress[@preferred='true']/xb:line3" /></line3>
                  <line4><xsl:value-of select="//xb:userAddressList/xb:userAddress[@preferred='true']/xb:line4" /></line4>
                  <line5><xsl:value-of select="//xb:userAddressList/xb:userAddress[@preferred='true']/xb:line5" /></line5>
               </userAddressPreferred>
               <userIdentifiers>
                  <xsl:for-each select="//xb:userIdentifiers/xb:userIdentifier">
                     <userIdentifier type="{xb:type}" ><xsl:value-of select="./xb:value" /></userIdentifier>
                  </xsl:for-each>
               </userIdentifiers>
            </physical_item_display_for_printing>
         </section-01>
      </printout>
   </xsl:template>
   
   
</xsl:stylesheet>