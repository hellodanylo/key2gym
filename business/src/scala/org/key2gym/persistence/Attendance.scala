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
package org.key2gym.persistence;

import java.io.Serializable;
import java.util.Date;
import javax.persistence._;
import javax.xml.bind.annotation.XmlRootElement;
import scala.reflect._;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "attendance_atd")
@XmlRootElement
@NamedQueries(value = Array(
    new NamedQuery(name = "Attendance.findAll", query = "SELECT a FROM Attendance a"),
    new NamedQuery(name = "Attendance.findById", query = "SELECT a FROM Attendance a WHERE a.id = :id"),
    new NamedQuery(name = "Attendance.findAllIdsOrderByIdDesc", query = "SELECT a.id FROM Attendance a ORDER BY a.id DESC"),
    new NamedQuery(name = "Attendance.findByClientOrderByDateTimeBeginDesc", query = "SELECT a FROM Attendance a WHERE a.client = :client ORDER BY a.datetimeBegin DESC"),
    new NamedQuery(name = "Attendance.findByDatetimeBegin", query = "SELECT a FROM Attendance a WHERE a.datetimeBegin = :datetimeBegin"),
    new NamedQuery(name = "Attendance.findByDatetimeEnd", query = "SELECT a FROM Attendance a WHERE a.datetimeEnd = :datetimeEnd"),
    new NamedQuery(name = "Attendance.findByDatetimeBeginRangeOrderByDateTimeBeginDesc", query = "SELECT a FROM Attendance a WHERE a.datetimeBegin BETWEEN :low AND :high ORDER BY a.datetimeBegin DESC"),
    new NamedQuery(name = "Attendance.findOpenByKey", query = "SELECT a FROM Attendance a WHERE a.datetimeEnd = '2004-04-04 09:00:01' AND a.key = :key"),
    new NamedQuery(name = "Attendance.countWithKey", query = "SELECT COUNT(a) FROM Attendance a WHERE a.key = :key")))
class Attendance extends Serializable {
    
  @Id
  @Basic(optional = false)
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "id_atd")
  private var id: Int = _
    
  @Basic(optional = false)
  @Column(name = "datetime_begin")
  @Temporal(TemporalType.TIMESTAMP)
  protected var datetimeBegin: Date = null
    
  @Basic(optional = false)
  @Column(name = "datetime_end")
  @Temporal(TemporalType.TIMESTAMP)
  protected var datetimeEnd: Date = null

  @Version
  @Column(name="version")
  private var version: Int = _
    
  @JoinColumn(name = "idkey_atd")
  @ManyToOne(optional = false)
  private var key: Key = _

  @JoinColumn(name = "idcln_atd")
  @ManyToOne(optional = true)
  private var client: Client = _

  @OneToOne(mappedBy = "attendance")
  private var order: OrderEntity = _

  def getId = id
  
  def getDatetimeBegin = datetimeBegin
  def getDatetimeEnd = datetimeEnd

  def isOpen: Boolean = datetimeEnd == Attendance.datetimeEndUnknown
  def close = datetimeEnd = Attendance.datetimeEndUnknown

  def getKey = key
  def setKey(key: Key) = this.key = key

  def getClient = client
  def setClient(client: Client) = this.client = client

  def getOrder = order
  def setOrder(order: OrderEntity) = this.order = order

}

object Attendance {

  def apply(key: Key, client: Client): Attendance = {
    val attendance = new Attendance

    attendance.datetimeBegin = new Date()
    attendance.datetimeEnd = datetimeEndUnknown
    attendance.setKey(key)
    attendance.setClient(client)

    attendance
  }

  def apply(key: Key): Attendance = apply(key, null)

  /**
   * This constant is used to represent the ending timestamp
   * of open attendances.
   *
   * The same as 2004-04-04 09:00:01.
   */
  private val datetimeEndUnknown = new Date(1081058401000l);
}
