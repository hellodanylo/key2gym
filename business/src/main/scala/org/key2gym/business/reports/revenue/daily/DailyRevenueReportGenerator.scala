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
package org.key2gym.business.reports.revenue.daily

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
import org.key2gym.business.reports.XMLReportGenerator
import org.key2gym.business.resources.ResourcesManager
import org.key2gym.business.api.reports._
import org.key2gym.business.entities.DailyRevenue
import org.apache.log4j.Logger
import scala.collection.JavaConversions._

/**
 *
 * @author Danylo Vashchilenko
 */
class DailyRevenueReportGenerator extends XMLReportGenerator[DateIntervalDTO] {

    /**
     * Actually generates the report.
     * 
     * The input for this generator is an array of two DateMidnight instances.
     * They represent the range of dates to generate the revenue for.
     * The first date has to be before or equal to the second date.
     * 
     * @param input the array of two dates
     * @param em the entity manager with full access to the database
     * @throws ValidationException if the input is invalid
     * @return the report object 
     */
    def doGenerate(input: DateIntervalDTO, em: EntityManager): AnyRef  = {
      
      /*
       * Casts the input object.
       */
      val interval = input.asInstanceOf[DateIntervalDTO]
      
      /*
       * Validates the input.
       */
      if (interval.getBegin.compareTo(interval.getEnd) > 1) {
        throw new ValidationException(ResourcesManager.getStrings().getString("Invalid.DateRange.BeginningAfterEnding"))
      }
      
      /*
       * The resulting report entity.
       */
      val report = new DailyRevenueReport()
      
      report.setTitle(formatTitle(input, em))
      report.setPeriodBegin(interval.getBegin.toDate)
      report.setPeriodEnd(interval.getEnd.toDate)
      report.setGenerated(new java.util.Date)

      val days = em.createNamedQuery("DailyRevenue.findByDateRange", classOf[DailyRevenue])
        .setParameter("rangeBegin", interval.getBegin.toDate)
        .setParameter("rangeEnd", interval.getEnd.toDate)
        .getResultList()
      
      var number = 1
      for(day <- days) {
        day.setNumber(number)
	number += 1
      }
      
      report.setDays(days)
      
      return report
    }
  
  def formatTitle(input: Object, em: EntityManager): String = {
    ResourcesManager.getString("Report.DailyRevenue.Title")
  }
  
  def getTitle: String = {
    ResourcesManager.getStrings().getString("Report.DailyRevenue.Title")
  }

  def getSecondaryFormats: Array[String] =  {
    Array("html")
  }
}
