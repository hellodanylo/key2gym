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
package org.key2gym.business.entities

import java.io.Serializable
import javax.persistence._

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "item_subscription_its")
@NamedQueries(Array(
    new NamedQuery(name = "ItemSubscription.findAll", query = "SELECT i FROM ItemSubscription i ORDER BY i.item.title"),
    new NamedQuery(name = "ItemSubscription.findById", query = "SELECT i FROM ItemSubscription i WHERE i.id = :id"),
    new NamedQuery(name = "ItemSubscription.findByUnits", query = "SELECT i FROM ItemSubscription i WHERE i.units = :units"),
    new NamedQuery(name = "ItemSubscription.findByTermDays", query = "SELECT i FROM ItemSubscription i WHERE i.termDays = :termDays"),
    new NamedQuery(name = "ItemSubscription.findByTermMonths", query = "SELECT i FROM ItemSubscription i WHERE i.termMonths = :termMonths"),
    new NamedQuery(name = "ItemSubscription.findByTermYears", query = "SELECT i FROM ItemSubscription i WHERE i.termYears = :termYears"),
    new NamedQuery(name = "ItemSubscription.findCasualByTimeSplit", query = "SELECT i FROM ItemSubscription i WHERE i.units = 1 AND i.termDays = 1 AND i.termMonths = 0 AND i.termYears = 0 AND i.timeSplit = :timeSplit"),
    new NamedQuery(name = "ItemSubscription.findByClientOrderByDateRecordedDesc", query="SELECT i FROM ItemSubscription i, OrderLine ol, OrderEntity o WHERE i.item = ol.item AND ol.orderEntity = o AND o.client = :client ORDER BY o.dateRecorded DESC")))
class ItemSubscription extends Serializable {

  @Id
  @Basic(optional = false)
  @Column(name = "iditm_its")
  protected var id: java.lang.Integer = _
    
  @Basic(optional = false)
  @Column(name = "units")
  protected var units: Int = _
  
  @Basic(optional = false)
  @Column(name = "term_days")
  protected var termDays: Int = _
  
  @Basic(optional = false)
  @Column(name = "term_months")
  protected var termMonths: Int = _
  
  @Basic(optional = false)
  @Column(name = "term_years")
  protected var termYears: Int = _
  
  @JoinColumn(name = "idtsp_its", referencedColumnName = "id_tsp")
  @ManyToOne(optional = false)
  protected var timeSplit: TimeSplit = _
  
  @JoinColumn(name = "iditm_its", referencedColumnName = "id_itm", insertable=false, updatable=false)
  @OneToOne(cascade=Array(CascadeType.REMOVE), optional = false)
  protected var item: Item = _

  def getId(): java.lang.Integer = this.id
  def setId(id: java.lang.Integer) = this.id = id

  def getUnits(): Int = this.units
  def setUnits(units: Int) = this.units = units

  def getTermDays(): Int = this.termDays
  def setTermDays(termDays: Int) = this.termDays = termDays

  def getTermMonths(): Int = this.termMonths
  def setTermMonths(termMonths: Int) = this.termMonths = termMonths
    
  def getTermYears(): Int = this.termYears
  def setTermYears(termYears: Int) = this.termYears = termYears
  
  def getTimeSplit(): TimeSplit = this.timeSplit
  def setTimeSplit(timeSplit: TimeSplit) = this.timeSplit = timeSplit

  def getItem(): Item = this.item
  def setItem(item: Item) = this.item = item
}
