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
		xmlns:cmn="http://key2gym.org/documentation/common">

  <xsl:function name="cmn:string-mult">

    <xsl:param name="string"/>
    <xsl:param name="times" />

    <xsl:choose>
      <xsl:when test="$times = 0">
	<xsl:value-of select="''"/>
      </xsl:when>
      <xsl:when test="$times = 1">
	<xsl:value-of select="$string" />
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="concat($string, cmn:string-mult($string, $times - 1))" />
      </xsl:otherwise>
    </xsl:choose>

  </xsl:function>
 
  <xsl:function name="cmn:relative-path-to-base">
    <!-- The URI of the page from which to find the relative path. -->
    <xsl:param name="uri" />

    <!-- Takes as many '../' as deep the URI is. -->
    <xsl:variable name="base.path" select="cmn:string-mult('../', count(tokenize($uri, '/'))-1)"/>

    <!-- Strips away the last '/'. -->
    <xsl:variable name="path" select="substring($base.path, 1, string-length($base.path)-1)" />

    <xsl:choose>
      <xsl:when test="string-length($path) = 0">
	<xsl:text>.</xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$path" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="cmn:get-title">
    <xsl:value-of select="cmn:find-page-meta($page.uri.seq, $website/categories)/@title"/>
  </xsl:function>
  
  <!-- Generates the breadcrumbs for the current page. -->
  <xsl:template name="breadcrumbs">
    <!-- If true the output is a string, otherwise HTML tags are used. -->
    <xsl:param name="plainText" select="false()"/>
    <!-- The string to insert between breadcrumbs parts. -->
    <xsl:param name="divider" select="'>'"/>

    <xsl:variable name="titles"
		  select="insert-before(cmn:breadcrumbs-titles($page.uri.seq), 1, $website/title)"/>
    
    <xsl:choose>
      <xsl:when test="$plainText">	
	<xsl:value-of select="string-join($titles, concat(' ', $divider, ' '))"/>
      </xsl:when>
      <xsl:otherwise>
	
	<ul class="breadcrumb">
	
	  <!-- Outputs each title using the selected format. -->
	  <xsl:for-each select="$titles">
	    
	    <xsl:choose>
	      <!-- If the title is not the last one. -->
	      <xsl:when test="position() != last()">

		<!-- Saves the title's position. -->
		<xsl:variable name="position" select="position()"/>
		
		<xsl:variable name="breadcrumb.uri"
			      select="$page.uri.seq[position() &lt; $position]"/>
		
		<xsl:choose>
		  <xsl:when test="cmn:category-has-index($breadcrumb.uri)=true()">
		    
		    <xsl:variable name="url.part">
		      <xsl:choose>
			<!-- If the current title is the first one. -->
			<xsl:when test="position() = 1">
			  <!-- No path to the root category. -->
			  <xsl:value-of select="''"/>
			</xsl:when>
			<xsl:otherwise>
			  <!-- Path to the current category. -->
			  <xsl:value-of 
			      select="concat(string-join($breadcrumb.uri, '/'), '/')"/>
			</xsl:otherwise>
		      </xsl:choose>
		    </xsl:variable>
		    
		    <!-- The URL to the current category. -->
		    <xsl:variable name="url" 
				  select="concat($base.path, '/',  $url.part, 'index.html')"/>
		    
		    <li>
		      <a href="{$url}"><xsl:value-of select="."/></a> 
		      <span class="divider"><xsl:value-of select="$divider"/></span>
		    </li>
		  </xsl:when>
		  <!-- If the category does not have an index page. -->
		  <xsl:otherwise>
		    <li>
		      <xsl:value-of select="."/>
		      <span class="divider"><xsl:value-of select="$divider"/></span>
		    </li>
		  </xsl:otherwise>
		</xsl:choose>
	      </xsl:when>
	      <!-- The last breadcrumb is the active one. -->
	      <xsl:otherwise>
		<li class="active"><xsl:value-of select="."/></li>
	      </xsl:otherwise>
	    </xsl:choose>
	    
	  </xsl:for-each>
	</ul>
  
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:function name="cmn:breadcrumbs-titles">
    <!-- The sequence of URI parts being processed. -->
    <xsl:param name="uri"/>
    
    <xsl:variable name="title" 
		  select="$website/map//*[@name = string-join($uri, '/')]/@title"/>

    <xsl:choose>
      <!-- If the URI points to a top-level category. -->
      <xsl:when test="count($uri) = 1">
	<xsl:value-of select="$title"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:variable name="next.uri" select="$uri[position() &lt; last()]"/>
	<xsl:copy-of select="insert-before($title, 1, cmn:breadcrumbs-titles($next.uri))"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:function>

  <!-- Returns whether the category has an index page. -->
  <xsl:function name="cmn:category-has-index">
    <xsl:param name="uri"/>

    <xsl:choose>
      <!-- If the URI points to the root category. -->
      <xsl:when test="string-length($uri) = 0">
	<xsl:value-of select="count($website/map/page[@name='index']) > 0"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="tokenize($website/map//category[@name=$uri]/*[1]/@name, '/')[last()] = 'index'"/>	
      </xsl:otherwise>
    </xsl:choose>

  </xsl:function>

  <xsl:function name="cmn:find-page-meta">
    <!-- The tokenized URI of the page to find. -->
    <xsl:param name="uri.seq"/>
    <!-- The base from which to look. -->
    <xsl:param name="base"/>

    <xsl:variable name="nextElem" select="$base/*[@name = $uri.seq[1]]"/>

    <xsl:choose>
      <xsl:when test="count($uri.seq) = 1">
	<xsl:copy-of select="$nextElem"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy-of select="cmn:find-page-meta($uri.seq[position()>1], $nextElem)"/>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:function>

  <xsl:template name="table-of-contents">

   <xsl:variable name="page.meta" select="$website/map//page[@name=$page.uri]"/>

   <xsl:for-each select="$page.meta">
     <div class="well">
       <xsl:call-template name="internal-table-of-contents">
	 <xsl:with-param name="base" select="parent::*[1]"/>
       </xsl:call-template>
     </div>
   </xsl:for-each>

   </xsl:template>

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
	       <xsl:when test="tokenize(@name, '/')[last()] = 'index'">
		 <!-- Skips it. -->
	       </xsl:when>
	       <!-- If the page is a regular page. -->
	       <xsl:otherwise>
		 <xsl:variable name="url" select="concat($base.path, '/', @name, '.html')"/>
		 <li><a href="{$url}"><xsl:value-of select="@title"/></a></li>
	       </xsl:otherwise>
	     </xsl:choose>
	   </xsl:when>
	   <!-- If the element is a category with an index page. -->
	   <xsl:when test="name() = 'category' and cmn:category-has-index(@name)=true()">	     
	     <li>
	       <xsl:variable name="url" select="concat($base.path, '/', @name, '/index.html')"/>
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