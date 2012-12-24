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
package org.key2gym.business.reports.attendances.daily

import org.key2gym.business.entities.DailyAttendances
import java.util.Date
import java.util.LinkedList
import java.util.List
import javax.xml.bind.annotation._

/**
 *
 * @author Danylo Vashchilenko
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = Array("title", "periodBegin", "periodEnd", "generated", "days"))
class DailyAttendancesReport {

  private var title: String = _
  @XmlSchemaType(name="date")
  private var periodBegin: Date = _
  @XmlSchemaType(name="date")
  private var periodEnd: Date = _
  private var generated: Date = _
  @XmlElement(name="day", defaultValue="0.0")
  private var days: List[DailyAttendances] = _
  
  def setTitle(title: String) = this.title = title
  
  def setPeriodBegin(periodBegin: Date) = this.periodBegin = periodBegin
  
  def setPeriodEnd(periodEnd: Date) = this.periodEnd = periodEnd
  
  def setGenerated(generated: Date) = this.generated = generated
  
  def setDays(days: List[DailyAttendances]) = this.days = days

}
