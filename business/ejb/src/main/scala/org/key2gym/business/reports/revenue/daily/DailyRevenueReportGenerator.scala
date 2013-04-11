/*
 * Copyright 2012-2013 Danylo Vashchilenko
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


import javax.persistence.EntityManager
import org.joda.time.ReadableInterval
import org.key2gym.business.api.ValidationException
import org.key2gym.business.reports.XMLReportGenerator
import org.key2gym.business.resources.ResourcesManager._
import org.key2gym.business.entities.DailyRevenue
import scala.collection.JavaConversions._

/**
 * Reports the daily revenue for a given interval.
 * 
 * @author Danylo Vashchilenko
 */
class DailyRevenueReportGenerator extends XMLReportGenerator[ReadableInterval] {

    /**
     * Actually generates the report.
     * 
     * The interval's instants must be truncated to day parts.
     * 
     * @param interval the interval to report
     * @param em the entity manager with full access to the database
     * @throws ValidationException if the interval is not truncated
     * @return the report object
     */
    override def doGenerate(interval: ReadableInterval, em: EntityManager): AnyRef  = {
      
      validateInput(interval, em)

      val days = em.createNamedQuery("DailyRevenue.findByDateInterval", 
				     classOf[DailyRevenue])
        .setParameter("intervalStart", interval.getStart.toDate)
        .setParameter("intervalEnd", interval.getEnd.toDate)
        .getResultList()
      
      var number = 1
      for(day <- days) {
        day.setNumber(number)
	number += 1
      }
      
      new DailyRevenueReport(
	formatTitle(interval, em),
	interval.getStart.toDate,
	interval.getEnd.toDate,
	new java.util.Date,
	days
      )
    }
  
  override def formatTitle(interval: ReadableInterval, em: EntityManager): String = {

    validateInput(interval, em)
    
    getString("Report.DailyRevenue.Title.withDateIntervalStartAndEnd", 
	      interval.getStart.toDate, interval.getEnd.toDate)
  }
  
  override def getTitle: String = getString("Report.DailyRevenue.Title")

  override def getSecondaryFormats: Array[String] = Array("html")

  override def validateInput(interval: ReadableInterval, em: EntityManager) {
    
    if(interval.getStart.getMillisOfDay != 0 
       || interval.getEnd.getMillisOfDay != 0) {
      throw new ValidationException(getString("Invalid.Interval.NotTruncatedToDate"))
    }

  }
}
