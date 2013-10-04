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
import java.math.BigDecimal
import java.util.Date
import java.util.List
import javax.persistence._
import org.key2gym.business.api.ValidationException
import org.key2gym.business.resources.ResourcesManager
import scala.collection.JavaConversions._

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "order_ord")
@NamedQueries(Array(
    new NamedQuery(name = "OrderEntity.findAll", query = "SELECT o FROM OrderEntity o"),
    new NamedQuery(name = "OrderEntity.findByClientAndDateRecordedRangeOrderByDateRecordedDesc", query = "SELECT o FROM OrderEntity o WHERE o.client = :client AND o.dateRecorded BETWEEN :rangeBegin AND :rangeEnd ORDER BY o.dateRecorded DESC"),
    new NamedQuery(name = "OrderEntity.findAllIdsOrderByIdDesc", query = "SELECT o.id FROM OrderEntity o ORDER BY o.id DESC"),
    new NamedQuery(name = "OrderEntity.findById", query = "SELECT o FROM OrderEntity o WHERE o.id = :id"),
    new NamedQuery(name = "OrderEntity.findByClientAndDateRecorded", query = "SELECT o FROM OrderEntity o WHERE o.client = :client AND o.dateRecorded = :dateRecorded"),
    new NamedQuery(name = "OrderEntity.findByAttendance", query = "SELECT o FROM OrderEntity o WHERE o.attendance = :attendance"),
    new NamedQuery(name = "OrderEntity.findDefaultByDateRecorded", query = "SELECT o FROM OrderEntity o WHERE o.attendance IS NULL AND o.client IS NULL AND o.dateRecorded = :dateRecorded"),
    new NamedQuery(name = "OrderEntity.findByDateRecordedOrderByIdDesc", query = "SELECT o FROM OrderEntity o WHERE o.dateRecorded = :dateRecorded ORDER BY o.id DESC"),
	new NamedQuery(name = "OrderEntity.sumPaymentsForDateRecorded", query = "SELECT SUM(o.payment) FROM OrderEntity o WHERE o.dateRecorded = :dateRecorded")))
@SequenceGenerator(name="id_orf_seq", allocationSize = 1)
class OrderEntity extends Serializable {

  @Id
  @Basic(optional = false)
  @Column(name = "id_ord")
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private var id: Int = _
  
  @Basic(optional = false)
  @Column(name = "date_recorded", nullable = false)
  @Temporal(TemporalType.DATE)
  protected var dateRecorded: Date = _
  
  @Basic(optional = false)
  @Column(name = "payment", nullable = false, precision = 6, scale = 2)
  protected var payment: BigDecimal = _
  
  @OneToMany(mappedBy="orderEntity", cascade=Array(CascadeType.ALL))
  protected var orderLines: List[OrderLine] = _ 
  
  @JoinColumn(name = "idcln_ord", referencedColumnName = "id_cln", nullable = true)
  @ManyToOne
  protected var client: Client = _
    
  @JoinColumn(name = "idatd_ord", referencedColumnName = "id_atd", nullable = true)
  @OneToOne
  protected var attendance: Attendance = _
    
  @Version
  @Column(name="version")
  protected var version: Int = _
   
  def getId() = id

  def getDate() = dateRecorded
  def setDate(date: Date) = this.dateRecorded = date

  def getPayment() = payment
  @throws(classOf[ValidationException])
  def recordPayment(amount: BigDecimal) {
    /*
     * Normalizes the scale, and throws an exception, if the scale is to
     * big.
     */
    if (amount.scale() > 2) {
      throw new ValidationException(ResourcesManager.getString("Invalid.Money.ScaleOverLimit"))
    }
    val scaledAmount = amount.setScale(2)
    
    val newPayment = payment.add(scaledAmount)
  
    if (newPayment.precision() > OrderEntity.moneyMaxPrecision) {
      throw new ValidationException(ResourcesManager.getString("Invalid.Order.Total.LimitReached"))
    }
    
    payment = newPayment
  }

  def getOrderLines() = this.orderLines
  def setOrderLines(orderLines: List[OrderLine]) = this.orderLines = orderLines

  def addPurchase(item: Item, discount: Discount) {
    
  }
   
  def getClient() = client

  def getAttendance() = attendance

  def getTotal() = {
    var total = BigDecimal.ZERO.setScale(2)
    
    for(orderLine <- orderLines) { 
      total = total.add(orderLine.getTotal())
    }
        
    total
  }
}

object OrderEntity {
  def apply(client: Client) = {
    val order = new OrderEntity

    order.setDate(new Date())
    order.client = client
    order.payment = BigDecimal.ZERO
    
    order
  }

  def apply(attendance: Attendance) = {
    val order = new OrderEntity

    order.setDate(new Date())
    order.attendance = attendance
    order.payment = BigDecimal.ZERO
    
    order  
  }

  def apply() = {
    val order = new OrderEntity

    order.setDate(new Date())
    order.payment = BigDecimal.ZERO
    
    order    
  }

  val moneyMaxPrecision = 6
}
