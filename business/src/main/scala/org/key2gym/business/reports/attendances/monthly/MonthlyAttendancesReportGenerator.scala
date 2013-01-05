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
package org.key2gym.business.reports.attendances.monthly

import java.util.List
import javax.persistence.EntityManager
import org.joda.time.ReadableInterval
import org.key2gym.business.api.ValidationException
import org.key2gym.business.reports.XMLReportGenerator
import org.key2gym.business.resources.ResourcesManager._
import org.key2gym.business.entities.MonthlyAttendances
import scala.collection.JavaConversions._

/**
 * Reports the monthly attendances for a given interval.
 * 
 * @author Danylo Vashchilenko
 */
class MonthlyAttendancesReportGenerator extends XMLReportGenerator[ReadableInterval] {

  /**
   * Actually generates the report.
   * 
   * @param interval the interval to report
   * @param em the entity manager with full access to the database
   * @throws ValidationException if the input is invalid
   * @return the report object
   */
  override protected def doGenerate(interval: ReadableInterval, 
				    em: EntityManager): AnyRef  = {
    
    validateInput(interval, em)

    val days = em.createNamedQuery("MonthlyAttendances.findByDateInterval", 
				   classOf[MonthlyAttendances])
      .setParameter("intervalStart", interval.getStart.toDate)
      .setParameter("intervalEnd", interval.getEnd.toDate)
      .getResultList()
    
    var number = 1
    for(day <- days) {
      day.setNumber(number)
      number += 1
    }
    
    new MonthlyAttendancesReport(
      formatTitle(interval, em),
      interval.getStart.toDate,
      interval.getEnd.toDate,
      new java.util.Date,
      days
    )
    
  }
  
  override def formatTitle(interval: ReadableInterval, em: EntityManager): String = {

    validateInput(interval, em)
    
    getString("Report.MonthlyAttendances.Title.withDateIntervalStartAndEnd", 
	      interval.getStart.toDate, interval.getEnd.toDate)
  }
  
  override def getTitle: String = getString("Report.MonthlyAttendances.Title")

  override def getSecondaryFormats: Array[String] = Array("html")

  /**
   * Validates the input interval.
   *
   * The interval must be truncated to month part.
   *
   * @param interval the interval to validate
   * @throws ValidationException if the interval is invalid
   */
  override def validateInput(interval: ReadableInterval, em: EntityManager) {   

    val start = interval.getStart
    val end = interval.getEnd

    if(start.getMillisOfDay != 0
       || end.getMillisOfDay != 0
       || start.getDayOfMonth != 1 
       || end.getDayOfMonth != 1) {
      throw new ValidationException(
	getString("Invalid.Interval.NotTruncatedToMonth")
      )
    }
  }

}
