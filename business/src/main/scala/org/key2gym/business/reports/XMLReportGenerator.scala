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

import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.io._
import java.util.List
import javax.persistence.EntityManager
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.Marshaller
import javax.xml.transform._
import javax.xml.transform.stream._
import org.joda.time.DateMidnight
import org.key2gym.business.api.ValidationException
import org.key2gym.business.api.spi.report.ReportGenerator
import org.key2gym.business.resources.ResourcesManager
import org.key2gym.business.api.reports._
import org.key2gym.business.entities.DailyRevenue
import org.apache.log4j.Logger
import scala.collection.JavaConversions._

/**
 *
 * @author Danylo Vashchilenko
 */
abstract class XMLReportGenerator[I] extends ReportGenerator {

  def doGenerate(input: I, em: EntityManager): Any

  /**
   * Generates the report.
   *
   * @param input the input to be used by concrete implementation
   * @param em the entity manager with full access to the database
   * @throws ValidationException if the input is invalid
   * @return the XML report 
   */
  @throws(classOf[ValidationException])
  def generate(input: Object, em: EntityManager): Array[Byte] = {

    val report = doGenerate(input.asInstanceOf[I], em)

    var context: JAXBContext = null
    var marshaller: Marshaller = null

    try {
      context = JAXBContext.newInstance(report.getClass)
      marshaller = context.createMarshaller()
    } catch {
      case ex: JAXBException =>
	throw new RuntimeException("Failed create a marshaller", ex)
    }

    val stream = new ByteArrayOutputStream()

    try {
      marshaller.marshal(report, stream)
    } catch {
      case ex: JAXBException =>
	throw new RuntimeException("Failed to marshall the report", ex)
    }
    
    stream.toByteArray
  }

  def convert(primaryBody: Array[Byte], format: String): Array[Byte] = {

    val xsltPath = this.getClass().getPackage().getName().replaceAll("\\.", "/") + "/xml2" + format + ".xml"

    val inputStream = Thread.currentThread().getContextClassLoader()
    	    .getResourceAsStream(xsltPath)
    
    if(inputStream == null) {
      throw new RuntimeException("Could not locate the resource: " + xsltPath)
    }

    val xslt = new StreamSource(inputStream)
    var transformer: net.sf.saxon.Controller = null

    try {
      transformer = TransformerFactory
	.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
	.newTransformer(xslt)
	.asInstanceOf[net.sf.saxon.Controller]
    } catch {
      case ex: TransformerConfigurationException =>
	throw new RuntimeException("Can not create a transformer", ex)
    }

    transformer.setUnparsedTextURIResolver(new LocalUnparsedTextURIResolver)

    val xml = new StreamSource(new ByteArrayInputStream(primaryBody))
    val output = new ByteArrayOutputStream()
    
    transformer.setParameter("locale.country", System.getProperty("locale.country"));
    transformer.setParameter("locale.language", System.getProperty("locale.language"));

    /* Passes all the l10n strings to the XSLT. */
    ResourcesManager.getStrings().getKeys().foreach(
      key => transformer.setParameter(key, ResourcesManager.getString(key))
    )
    
    try {
      transformer.transform(xml, new StreamResult(output))
    } catch {
      case ex: TransformerException =>
	throw new RuntimeException("Failed to transform the report", ex)
    }
    
    output.toByteArray
  }

  final def getPrimaryFormat: String = "xml"
}
