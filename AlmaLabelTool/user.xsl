<xsl:stylesheet version="2.0" xmlns:xb="http://com/exlibris/urm/user_record/xmlbeans"  
                              xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xmlns:char2num="http://libis.be/char2num"
                              xmlns:num2char="http://libis.be/num2char"
                              exclude-result-prefixes="xb xsi char2num num2char">
                              
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
                     <userIdentifiercd type="{xb:type}" >*<xsl:value-of select="./xb:value" /><xsl:call-template name="checksum"><xsl:with-param name="string" select="./xb:value"/></xsl:call-template>*</userIdentifiercd>
                  </xsl:for-each>
               </userIdentifiers>
            </physical_item_display_for_printing>
         </section-01>
      </printout>
   </xsl:template>
   
   <!-- LIBIS FUNCTIONS -->

   <xsl:template name="checksum">
    <xsl:param name="string"/>
    <xsl:param name="sum" select="0"/>
    <xsl:variable name="num" select="document('')//char2num:char2num/entry[@char=substring($string, 1, 1)]/text()"/>
    <xsl:choose>
      <xsl:when test="string-length($string) &gt; 1">
        <xsl:call-template name="checksum">
          <xsl:with-param name="string" select="substring($string, 2)"/>
          <xsl:with-param name="sum" select="$sum+$num"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="document('')//num2char:num2char/entry[@num=(($sum+$num) mod 43)]/text()"/>
      </xsl:otherwise>
    </xsl:choose>
   </xsl:template>
   
   <char2num:char2num>
    <entry char="0">0</entry>
    <entry char="1">1</entry>
    <entry char="2">2</entry>
    <entry char="3">3</entry>
    <entry char="4">4</entry>
    <entry char="5">5</entry>
    <entry char="6">6</entry>
    <entry char="7">7</entry>
    <entry char="8">8</entry>
    <entry char="9">9</entry>
    <entry char="A">10</entry>
    <entry char="B">11</entry>
    <entry char="C">12</entry>
    <entry char="D">13</entry>
    <entry char="E">14</entry>
    <entry char="F">15</entry>
    <entry char="G">16</entry>
    <entry char="H">17</entry>
    <entry char="I">18</entry>
    <entry char="J">19</entry>
    <entry char="K">20</entry>
    <entry char="L">21</entry>
    <entry char="M">22</entry>
    <entry char="N">23</entry>
    <entry char="O">24</entry>
    <entry char="P">25</entry>
    <entry char="Q">26</entry>
    <entry char="R">27</entry>
    <entry char="S">28</entry>
    <entry char="T">29</entry>
    <entry char="U">30</entry>
    <entry char="V">31</entry>
    <entry char="W">32</entry>
    <entry char="X">33</entry>
    <entry char="Y">34</entry>
    <entry char="Z">35</entry>
    <entry char="-">36</entry>
    <entry char=".">37</entry>
    <entry char=" ">38</entry>
    <entry char="$">39</entry>
    <entry char="/">40</entry>
    <entry char="+">41</entry>
    <entry char="%">42</entry>
   </char2num:char2num>
   
   <num2char:num2char>
    <entry num="0">0</entry>
    <entry num="1">1</entry>
    <entry num="2">2</entry>
    <entry num="3">3</entry>
    <entry num="4">4</entry>
    <entry num="5">5</entry>
    <entry num="6">6</entry>
    <entry num="7">7</entry>
    <entry num="8">8</entry>
    <entry num="9">9</entry>
    <entry num="10">A</entry>
    <entry num="11">B</entry>
    <entry num="12">C</entry>
    <entry num="13">D</entry>
    <entry num="14">E</entry>
    <entry num="15">F</entry>
    <entry num="16">G</entry>
    <entry num="17">H</entry>
    <entry num="18">I</entry>
    <entry num="19">J</entry>
    <entry num="20">K</entry>
    <entry num="21">L</entry>
    <entry num="22">M</entry>
    <entry num="23">N</entry>
    <entry num="24">O</entry>
    <entry num="25">P</entry>
    <entry num="26">Q</entry>
    <entry num="27">R</entry>
    <entry num="28">S</entry>
    <entry num="29">T</entry>
    <entry num="30">U</entry>
    <entry num="31">V</entry>
    <entry num="32">W</entry>
    <entry num="33">X</entry>
    <entry num="34">Y</entry>
    <entry num="35">Z</entry>
    <entry num="36">-</entry>
    <entry num="37">.</entry>
    <entry num="38">~</entry>
    <entry num="39">$</entry>
    <entry num="40">/</entry>
    <entry num="41">+</entry>
    <entry num="42">%</entry>
   </num2char:num2char>  
   
</xsl:stylesheet>