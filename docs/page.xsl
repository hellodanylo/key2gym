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
		exclude-result-prefixes="xsl cmn">
  
  <xsl:import href="common.xsl" />
  
  <xsl:param name="page.uri" />
  <xsl:param name="page.uri.seq" select="cmn:uri-to-uri-seq($page.uri)"/>

  <xsl:param name="base.path" select="cmn:relative-path-to-base($page.uri)" />
  <xsl:param name="website" select="document('website.xml')/website" />

  <xsl:param name="style.css.path" select="concat($base.path, '/css/style.css')" />
  <xsl:param name="bootstrap.min.css.path" 
	     select="concat($base.path, '/css/bootstrap.min.css')" />

  <xsl:output method="html" encoding="utf-8" indent="yes" />
 
  <xsl:strip-space elements="*" />

  <xsl:template match="/">
    <html>
      <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" href="{$bootstrap.min.css.path}" media="screen" />
	<link rel="stylesheet" href="{$style.css.path}"/>
	<title>
	  <xsl:call-template name="breadcrumbs">
	    <xsl:with-param name="plainText" select="true()"/>
	  </xsl:call-template>
	</title>
      </head>
      <body>
	<a name="top">
	  <div class="header">
	    <h1><xsl:value-of select="$website/title"/></h1>
	  </div>
	</a>
	
	<!-- The breadcrumbs are displayed on each page
	     except for the root index page. -->
	<xsl:if test="$page.uri != '/index'">
	  <xsl:call-template name="breadcrumbs"/>
	</xsl:if>

	<div class="row">
	  <div class="span10">
	    <!-- Copies the content as is. -->
 	    <xsl:copy-of select="page/content/*" />
	    
	    <!-- Inserts a table of contents if the page is an index. -->
	    <xsl:if test="$page.uri.seq[last()] = 'index'">
	      <div class="well">
		<xsl:call-template name="table-of-contents"/>
	      </div>
	    </xsl:if>
	    
	    <xsl:value-of select="unparsed-text('src/html/footer.html')" 
			  disable-output-escaping="yes" />
	    
	  </div>
	  <div class="span2"></div>
	</div>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
