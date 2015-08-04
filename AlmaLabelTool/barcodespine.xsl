<xsl:stylesheet version="1.0" 
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:char2num="http://libis.be/char2num"
        xmlns:num2char="http://libis.be/num2char"
        exclude-result-prefixes="char2num num2char">
        
    <xsl:output indent="yes"/>
    
    <!-- KEEP ALL NODES -->
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- language always "en" for BIAF to work -->
    <xsl:template match="printout/form-language">
        <form-language>en</form-language>
    </xsl:template>
    
    <!-- form-format always "00" for BIAF to work -->
    <xsl:template match="printout/form-format">
        <form-format>00</form-format>
    </xsl:template>
    <!-- END KEEP ALL NODES -->
    
    <!-- LIBIS FUNCTIONS -->
    <!-- replace &amp; with &amp;&amp; =escape ampersand with another ampersand to avoid mf BIAF mnemonic interpretation! -->
    <!-- replace isn't available for XSLT 1.0 so we need to reinvent the wheel by creating our own replace -->
    <xsl:template name="string-replace-all">
     <xsl:param name="text" />
     <xsl:param name="replace" />
     <xsl:param name="by" />
     <xsl:choose>
       <xsl:when test="contains($text, $replace)">
         <xsl:value-of select="substring-before($text,$replace)" />
         <xsl:value-of select="$by" />
         <xsl:call-template name="string-replace-all">
           <xsl:with-param name="text"
           select="substring-after($text,$replace)" />
           <xsl:with-param name="replace" select="$replace" />
           <xsl:with-param name="by" select="$by" />
         </xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select="$text" />
       </xsl:otherwise>
     </xsl:choose>
   </xsl:template>
   
    
    <!-- get subfield: used in/created for spine labels-->
    <!-- returns space when subfield does not exist in string -->
    <!-- parameter "sub" should contain $$ and the subfield code example: '$$a'-->
    <xsl:template name="subfield">
        <xsl:param name="string"/>
        <xsl:param name="sub"/>
        
        <xsl:choose>
            <xsl:when test="contains($string, $sub)">
                <xsl:choose>
                    <xsl:when test="contains(substring-after($string, $sub), '$$')">
                        <xsl:value-of select="normalize-space(substring-before(substring-after($string, $sub), '$$'))"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="normalize-space(substring-after($string, $sub))"/>
                    </xsl:otherwise>  
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="' '"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> 
    
    <!-- split string on first space: used in/created for spine labels-->
    <!-- returns part one or 2 of the string depending on parameter "part"-->
    <!-- parameter "part" should contain '1' for first part or '2' for second part-->
    <!-- part '2' will be a space if there is no space in subfield -->
    <xsl:template name="splitstring">
        <xsl:param name="string"/>
        <xsl:param name="part"/>

        <xsl:choose>
            <xsl:when test="contains(normalize-space($string), ' ')">
                <xsl:choose>
                    <xsl:when test="$part='1'">
                        <xsl:value-of select="substring-before(normalize-space($string), ' ')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="substring-after(normalize-space($string), ' ')"/>
                    </xsl:otherwise>  
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$part='1'">
                        <xsl:value-of select="normalize-space($string)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="' '"/>
                    </xsl:otherwise>  
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> 
    
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
    
    <!-- END FUNCTIONS -->  
    
    <!-- change location code if needed: this is printed on top of barcode label (if standard label was not edited) -->
    <xsl:template match="printout/section-01/physical_item_display_for_printing/location_code">
        <location_code></location_code>
    </xsl:template>  

    <!-- add checkdigit in barcodecd  -->
    <!-- added nomalize_space after people noticed trailing space=barcode does not scan -->
    <xsl:template match="printout/section-01/physical_item_display_for_printing/barcode">
        <barcode><xsl:value-of select="normalize-space(.)" /></barcode>
        <barcodecd>*<xsl:value-of select="normalize-space(.)" /><xsl:call-template name="checksum">
         <xsl:with-param name="string" select="normalize-space(.)"/></xsl:call-template>*</barcodecd>
    </xsl:template>
    
    <!-- replace &amp; with &amp;&amp; =escape ampersand with another ampersand to avoid mf BIAF mnemonic interpretation! -->
    <xsl:template match="printout/section-01/physical_item_display_for_printing/call_number">
        <call_number><xsl:call-template name="string-replace-all">
                        <xsl:with-param name="text" select="." />
                        <xsl:with-param name="replace" select="'&amp;'" />
                        <xsl:with-param name="by" select="'&amp;&amp;'" />
                     </xsl:call-template></call_number>
    </xsl:template>
      

    
    <!-- add fields for spine label -->
    <xsl:template match="printout/section-01/physical_item_display_for_printing/row_call_number">
        
        <row_call_number><xsl:call-template name="string-replace-all">
                        <xsl:with-param name="text" select="." />
                        <xsl:with-param name="replace" select="'&amp;'" />
                        <xsl:with-param name="by" select="'&amp;&amp;'" />
                     </xsl:call-template></row_call_number>
                     
        <xsl:variable name="amp_row_call_number"><xsl:call-template name="string-replace-all">
                        <xsl:with-param name="text" select="." />
                        <xsl:with-param name="replace" select="'&amp;'" />
                        <xsl:with-param name="by" select="'&amp;&amp;'" />
                     </xsl:call-template></xsl:variable>
    
        <xsl:variable name="subfieldk">
            <xsl:call-template name="subfield">
                <xsl:with-param name="string" select="$amp_row_call_number"/>
                <xsl:with-param name="sub" select="'$$k'"/>
            </xsl:call-template>
        </xsl:variable>        
        <xsl:variable name="subfieldh">
            <xsl:call-template name="subfield">
                <xsl:with-param name="string" select="$amp_row_call_number"/>
                <xsl:with-param name="sub" select="'$$h'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="subfieldi">
            <xsl:call-template name="subfield">
                <xsl:with-param name="string" select="$amp_row_call_number"/>
                <xsl:with-param name="sub" select="'$$i'"/>
            </xsl:call-template>
        </xsl:variable>                
        <xsl:variable name="subfieldl">
            <xsl:call-template name="subfield">
                <xsl:with-param name="string" select="$amp_row_call_number"/>
                <xsl:with-param name="sub" select="'$$l'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="subfieldm">
            <xsl:call-template name="subfield">
                <xsl:with-param name="string" select="$amp_row_call_number"/>
                <xsl:with-param name="sub" select="'$$m'"/>
            </xsl:call-template>
        </xsl:variable>     
            <xsl:variable name="subfieldm1">
            <xsl:call-template name="splitstring">
                <xsl:with-param name="string" select="$subfieldm"/>
                <xsl:with-param name="part" select="'1'"/>
            </xsl:call-template>
        </xsl:variable>  
        <xsl:variable name="subfieldm2">
             <xsl:call-template name="splitstring">
                <xsl:with-param name="string" select="$subfieldm"/>
                <xsl:with-param name="part" select="'2'"/>
            </xsl:call-template>
        </xsl:variable>
        
        <!-- vertical-align spine label at bottom -->
        <xsl:choose>
          <xsl:when test="string-length(normalize-space($subfieldm2)) &gt; 1">
            <lbs-spine5><xsl:value-of select="$subfieldm2"/></lbs-spine5>
                       
            <xsl:choose>
              <xsl:when test="string-length(normalize-space($subfieldm1)) &gt; 1">
                <lbs-spine4><xsl:value-of select="$subfieldm1"/></lbs-spine4>                
                <xsl:choose>
                  <xsl:when test="string-length(normalize-space($subfieldl)) &gt; 1">
                    <lbs-spine3><xsl:value-of select="$subfieldl"/></lbs-spine3>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine2><xsl:value-of select="$subfieldi"/></lbs-spine2>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine1><xsl:value-of select="$subfieldh"/></lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine1><xsl:value-of select="$subfieldk"/></lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine2><xsl:value-of select="$subfieldh"/></lbs-spine2>
                             <lbs-spine1><xsl:value-of select="$subfieldk"/></lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>     
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine3><xsl:value-of select="$subfieldi"/></lbs-spine3>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine2><xsl:value-of select="$subfieldh"/></lbs-spine2>
                             <lbs-spine1><xsl:value-of select="$subfieldk"/></lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine3><xsl:value-of select="$subfieldh"/></lbs-spine3>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>                             
              </xsl:when>
              <xsl:otherwise>             
                <xsl:choose>
                  <xsl:when test="string-length(normalize-space($subfieldl)) &gt; 1">
                    <lbs-spine4><xsl:value-of select="$subfieldl"/></lbs-spine4>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine3><xsl:value-of select="$subfieldi"/></lbs-spine3>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine2><xsl:value-of select="$subfieldh"/></lbs-spine2>
                             <lbs-spine1><xsl:value-of select="$subfieldk"/></lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine3><xsl:value-of select="$subfieldh"/></lbs-spine3>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>     
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine4><xsl:value-of select="$subfieldi"/></lbs-spine4>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine3><xsl:value-of select="$subfieldh"/></lbs-spine3>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine4><xsl:value-of select="$subfieldh"/></lbs-spine4>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine4><xsl:value-of select="$subfieldk"/></lbs-spine4>
                             <lbs-spine3> </lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>   
              </xsl:otherwise>              
            </xsl:choose>          
            
          </xsl:when>
          <xsl:otherwise>       
            <xsl:choose>
              <xsl:when test="string-length(normalize-space($subfieldm1)) &gt; 1">
                <lbs-spine5><xsl:value-of select="$subfieldm1"/></lbs-spine5>                
                <xsl:choose>
                  <xsl:when test="string-length(normalize-space($subfieldl)) &gt; 1">
                    <lbs-spine4><xsl:value-of select="$subfieldl"/></lbs-spine4>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine3><xsl:value-of select="$subfieldi"/></lbs-spine3>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine2><xsl:value-of select="$subfieldh"/></lbs-spine2>
                             <lbs-spine1><xsl:value-of select="$subfieldk"/></lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine3><xsl:value-of select="$subfieldh"/></lbs-spine3>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>     
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine4><xsl:value-of select="$subfieldi"/></lbs-spine4>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine3><xsl:value-of select="$subfieldh"/></lbs-spine3>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine4><xsl:value-of select="$subfieldh"/></lbs-spine4>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine4><xsl:value-of select="$subfieldk"/></lbs-spine4>
                             <lbs-spine3> </lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>                             
              </xsl:when>
              <xsl:otherwise>             
                <xsl:choose>
                  <xsl:when test="string-length(normalize-space($subfieldl)) &gt; 1">
                    <lbs-spine5><xsl:value-of select="$subfieldl"/></lbs-spine5>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine4><xsl:value-of select="$subfieldi"/></lbs-spine4>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine3><xsl:value-of select="$subfieldh"/></lbs-spine3>
                             <lbs-spine2><xsl:value-of select="$subfieldk"/></lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine4><xsl:value-of select="$subfieldh"/></lbs-spine4>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine4><xsl:value-of select="$subfieldk"/></lbs-spine4>
                             <lbs-spine3> </lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>     
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($subfieldi)) &gt; 1">
                        <lbs-spine5><xsl:value-of select="$subfieldi"/></lbs-spine5>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine4><xsl:value-of select="$subfieldh"/></lbs-spine4>
                             <lbs-spine3><xsl:value-of select="$subfieldk"/></lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine4><xsl:value-of select="$subfieldk"/></lbs-spine4>
                             <lbs-spine3> </lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:choose>
                           <xsl:when test="string-length(normalize-space($subfieldh)) &gt; 1">
                             <lbs-spine5><xsl:value-of select="$subfieldh"/></lbs-spine5>
                             <lbs-spine4><xsl:value-of select="$subfieldk"/></lbs-spine4>
                             <lbs-spine3> </lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:when>
                           <xsl:otherwise>
                             <lbs-spine5><xsl:value-of select="$subfieldk"/></lbs-spine5>
                             <lbs-spine4> </lbs-spine4>
                             <lbs-spine3> </lbs-spine3>
                             <lbs-spine2> </lbs-spine2>
                             <lbs-spine1> </lbs-spine1>
                           </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>   
              </xsl:otherwise>              
            </xsl:choose>                
          </xsl:otherwise>
        </xsl:choose>

    </xsl:template>      

</xsl:stylesheet>