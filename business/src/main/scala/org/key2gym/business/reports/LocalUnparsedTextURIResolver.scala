/*
 * Copyright 2012 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.key2gym.business.reports

import java.io._
import java.net._
import net.sf.saxon.Configuration
import net.sf.saxon.lib.UnparsedTextURIResolver

/**
 * Resolves the URI against this package's path.
 *
 * This is an implementation of a non-standard feature of Saxon library.
 *
 * The XSLT files use `unparsed-test` functions to access some external files.
 * This resolver is designed to resolve those files' URIs.
 * 
 * @author Danylo Vashchilenko
 */ 
class LocalUnparsedTextURIResolver extends UnparsedTextURIResolver {
  def resolve(uri: URI, encoding: String, config: Configuration): Reader = {

    val filePath = this.getClass.getPackage.getName.replaceAll("\\.", "/") + uri.getPath
    val stream = Thread.currentThread.getContextClassLoader
			.getResourceAsStream(filePath)

    if(stream == null) {
      throw new RuntimeException("Failed to load resource: " + filePath) 
    }

    new InputStreamReader(stream)
  }
}
