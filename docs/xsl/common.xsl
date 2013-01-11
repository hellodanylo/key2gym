<?xml version="1.0" encoding="utf-8"?>

<!--
   Copyright 2012-2013 Danylo Vashchilenko

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

<xsl:stylesheet version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:cmn="http://key2gym.org/documentation/common"
		xmlns:xs="http://www.w3.org/2001/XMLSchema">

  <!-- 
       Returns a string created by concatenating a given string given 
       number of times.
       
       Multiplying an empty string will always result in an empty output string.
       Multiplying any string by 0 will always return an empty output string.
       
       Example: cmn:string-mult('a', 3) will return 'aaa' 
  -->
  <xsl:function name="cmn:string-mult">
    
    <!-- The string to multiply. -->
    <xsl:param name="string" as="xs:string"/>
    <!-- The number by which to 'multiply' the input string. -->
    <xsl:param name="times" as="xs:integer"/>

    <xsl:choose>
      <xsl:when test="$times = 0">
	<xsl:value-of select="''"/>
      </xsl:when>
      <xsl:when test="$times = 1">
	<xsl:value-of select="$string" />
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of 
	    select="concat($string, cmn:string-mult($string, $times - 1))" />
      </xsl:otherwise>
    </xsl:choose>

  </xsl:function>
 
  <!-- 
       Returns the relative path to the website's base directory from a given page.

       The resulting string will not have a trailing '/'.
       
       Example: cmn:relative-path-to-base('test/one/two') will return '../../..'
  -->
  <xsl:function name="cmn:relative-path-to-base">
    <!-- The URI of the page from which to find the relative path. -->
    <xsl:param name="uri" as="xs:string"/>

    <xsl:variable name="depth" select="cmn:count($uri, '/')-1"/>

    <xsl:choose>
      <!-- If the page is top-level. -->
      <xsl:when test="$depth = 0">
	<xsl:text>.</xsl:text>
      </xsl:when>
      <!-- If the page is several levels down. -->
      <xsl:otherwise>
	<xsl:variable name="path" select="cmn:string-mult('../', $depth)"/>
	<!-- Removes the trailing '/'. -->
	<xsl:value-of select="substring($path, 1, string-length($path)-1)" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!-- Counts occurences of a string within another string. -->
  <xsl:function name="cmn:count" as="xs:integer">
    <!-- The string to search in. -->
    <xsl:param name="string" as="xs:string"/>
    <!-- The string to count. -->
    <xsl:param name="fragment" as="xs:string"/>

    <xsl:variable name="after" select="substring-after($string, $fragment)"/>

    <xsl:choose>
      <!-- If the string does not contain the fragment. -->
      <xsl:when test="$after = ''">
	<xsl:copy-of select="0"/>
      </xsl:when>
      <!-- If the string contains the fragment. -->
      <xsl:otherwise>
	<!-- Recursively counts occurrences of the fragment 
	     in the rest of the string. -->
	<xsl:copy-of select="1+cmn:count($after, $fragment)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!-- 
       Converts an URI sequence to an URI string. 
       
       Example: cmn:uri-seq-to-uri('/', 'a', 'b') will return '/a/b'
  -->
  <xsl:function name="cmn:uri-seq-to-uri" as="xs:string">
    <xsl:param name="uri.seq" as="xs:string*"/>

    <xsl:value-of select="concat('/', string-join($uri.seq[position() > 1], '/'))"/>
  
  </xsl:function>

  <!-- 
       Converts an URI string to an URI sequence. 
       
       Example: cmn:uri-seq-to-uri('/', 'a', 'b') will return '/a/b'
  -->
  <xsl:function name="cmn:uri-to-uri-seq" as="xs:string*">
    <xsl:param name="uri" as="xs:string"/>

    <xsl:copy-of 
	select="insert-before(tokenize($uri, '/')[position() > 1], 1, '/')"/>
  
  </xsl:function>

  <!-- Returns the title of a given page or category. -->
  <xsl:function name="cmn:get-title">
    <xsl:param name="uri" as="xs:string"/>

    <xsl:value-of select="$website/map//*[@name=$uri]/@title"/>

  </xsl:function>

  
  <!-- 
       Generates the breadcrumbs for the current page. 
       
       The output is available in 2 formats: plain text and HTML.
       With plain text the breadcrumbs are simply divider separed sequence of titles.
       With HTML links and other elements are used when appropriate.
  -->
  <xsl:template name="breadcrumbs">
    <!-- If true the output is a plain string, otherwise HTML tags are used. -->
    <xsl:param name="plainText" as="xs:boolean" select="false()"/>

    <!-- The string to insert between breadcrumbs parts. -->
    <xsl:param name="divider" as="xs:string" select="'>'"/>

    <!-- Selects the parent categories and the current page 
	 if it's not an index page. -->
    <xsl:param name="breadcrumbs" select="$website/map//*[@name=$page.uri]/ancestor-or-self::*[name() = 'category' or (name() = 'page' and cmn:uri-to-uri-seq(@name)[last()] != 'index')]"/>

    <xsl:choose>
      <xsl:when test="$plainText">
	<xsl:value-of 
	    select="string-join($breadcrumbs/@title, concat(' ', $divider, ' '))"/>
      </xsl:when>
      <xsl:otherwise>
	
	<ul class="breadcrumb">
	  
	  <xsl:for-each select="$breadcrumbs">
	    
	    <xsl:choose>
	      <!-- If the element is not the last one. -->
	      <xsl:when test="position() != last()">
		
		<!-- A link is only provided to categories with index pages. -->
		<xsl:choose>

		  <!-- If the category has an index page. -->
		  <xsl:when test="cmn:category-has-index(@name)">
		    
		    <!-- 
			 The complete URL to this category's index page.
			 Replaces '//' with '/' to avoid duplication when 
			 the URI is simply '/'.
		    -->
		    <xsl:variable name="url" select="replace(concat($base.path, @name, '/index.html'), '//', '/')"/>
		    
		    <li>
		      <a href="{$url}">
			<xsl:value-of select="@title"/>
		      </a> 
		      <span class="divider">
			<xsl:value-of select="$divider"/>
		      </span>
		    </li>
		  </xsl:when>
		  <!-- If the category does not have an index page. -->
		  <xsl:otherwise>
		    <li>
		      <xsl:value-of select="@title"/>
		      <span class="divider">
			<xsl:value-of select="$divider"/>
		      </span>
		    </li>
		  </xsl:otherwise>
		</xsl:choose>
	      </xsl:when>
	      <!-- The last breadcrumb needs a special class and no link. -->
	      <xsl:otherwise>
		<li class="active">
		  <xsl:value-of select="@title"/>
		</li>
	      </xsl:otherwise>
	    </xsl:choose>
	    
	  </xsl:for-each>
	</ul>
	
      </xsl:otherwise>
    </xsl:choose>  
  </xsl:template>

   <!-- 
	Returns a sequence of sequences that serialize to the input sequence.
	If the goal is not clear yet, see the examples.

	Example: cmn:series((6,5,4)) will return ((6),(6,5),(6,5,4)).
   -->
   <xsl:function name="cmn:series" as="item()*">
     <!-- The input sequence of anyting. -->
     <xsl:param name="seq" as="item()*"/>

     <xsl:message select="$seq"/>

     <xsl:choose>
       <xsl:when test="count($seq) = 1">
	 <xsl:copy-of select="$seq"/>
       </xsl:when>
       <xsl:otherwise>
	 <xsl:copy-of select="insert-before($seq, 1, cmn:series($seq[position() &lt; last()]))"/> 
       </xsl:otherwise>
     </xsl:choose>

   </xsl:function>


   <!-- Returns whether a category has an index page. -->
   <xsl:function name="cmn:category-has-index" as="xs:boolean">
     <xsl:param name="uri" as="xs:string"/>

     <!-- 
	  The category's index page is its first page, 
	  whose URI ends with 'index'. 
     -->
     <xsl:copy-of select="cmn:uri-to-uri-seq($website/map//category[@name=$uri]/page[1]/@name)[last()] = 'index'"/>	
     
  </xsl:function>

  <!-- Generates the table of contents for the current page. -->
  <xsl:template name="table-of-contents">
    
    <xsl:variable name="page.meta" select="$website/map//page[@name=$page.uri]"/>
    
    <!-- We need a for-each loop here to the page meta into context. -->
    <xsl:for-each select="$page.meta">
      <xsl:call-template name="internal-table-of-contents">
	<!-- 
	     Begins with the page's parent category for we also need to list 
	     sibling pages and categories.
	-->
	<xsl:with-param name="base" select="parent::*[1]"/>
      </xsl:call-template>
    </xsl:for-each>
    
  </xsl:template>
  
  <!-- Internal template used by the 'table-of-contents' template. -->
  <xsl:template name="internal-table-of-contents">
    <xsl:param name="base" required="yes"/>
    
    <ol>
      <xsl:for-each select="$base/*">
	
	<xsl:choose>
	  <!-- If the element is a page. -->
	  <xsl:when test="name() = 'page'">
	    <xsl:choose>
	      <!-- If the page is a root index page. -->
	      <xsl:when test="@name = 'index'">
		<xsl:value-of select="@title"/>
	      </xsl:when>
	      <!-- If the page is a regular index page. -->
	      <xsl:when test="cmn:uri-to-uri-seq(@name)[last()] = 'index'">
		<!-- Skips it. -->
	      </xsl:when>
	      <!-- If the page is a regular page. -->
	      <xsl:otherwise>
		<xsl:variable name="url" 
			      select="concat($base.path, @name, '.html')"/>
		<li><a href="{$url}"><xsl:value-of select="@title"/></a></li>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:when>
	  <!-- If the element is a category with an index page. -->
	  <xsl:when test="name() = 'category' and cmn:category-has-index(@name)">	     
	    <li>
	      <xsl:variable name="url" 
			    select="concat($base.path, @name, '/index.html')"/>
	      <a href="{$url}"><xsl:value-of select="@title"/></a>
	      
	      <!-- If the category has got child elements, recursively
		   outputs the table of contents for it. -->
	      <xsl:if test="count(child::*) > 0">
		<xsl:call-template name="internal-table-of-contents">
		  <xsl:with-param name="base" select="."/>
		</xsl:call-template>
	      </xsl:if>
	      
	    </li>
	  </xsl:when>
	  <!-- If the element is a category without an index page. -->
	  <xsl:otherwise>
	    <li>
	      <xsl:value-of select="@title"/>
	      
	      <!-- If the category has got child elements, recursively
		   outputs the table of contents for it. -->
	      <xsl:if test="count(child::*) > 0">
		<xsl:call-template name="internal-table-of-contents">
		  <xsl:with-param name="base" select="."/>
		</xsl:call-template>
	      </xsl:if>
	      
	    </li>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:for-each>
    </ol>
    
  </xsl:template>
  
</xsl:stylesheet>