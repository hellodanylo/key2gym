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
package org.key2gym.business.entities

import java.math.BigDecimal
import java.util.Date
import javax.persistence._
import javax.xml.bind.annotation._

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "v_daily_revenue")
@NamedQueries(Array(
    new NamedQuery(name = "DailyRevenue.findByDateInterval", query = "SELECT r FROM DailyRevenue r WHERE r.date BETWEEN :intervalStart AND :intervalEnd")
))
@XmlAccessorType(XmlAccessType.FIELD)
class DailyRevenue extends Serializable {
  
  @Transient
  protected var number: Int = _
  
  @Id
  @Temporal(TemporalType.DATE)
  @Column(name = "date_recorded")
  @XmlSchemaType(name="date")
  protected var date: Date = _

  @Column(name = "revenue")
  protected var revenue: BigDecimal = _

  def getNumber: Int = this.number
  def setNumber(number: Int) = this.number = number

  def getDate = this.date
  def setDate(date: Date) = this.date = date

  def getRevenue = this.revenue
  def setRevenue(revenue: BigDecimal) = this.revenue = revenue
}
