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

import java.io.Serializable
import java.math.BigDecimal
import javax.persistence._
import java.lang.Integer
import java.util._
import org.joda.time.DateMidnight
import org.key2gym.persistence._

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "client_cln")
@NamedQueries(Array(
    new NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c"),
    new NamedQuery(name = "Client.findById", query = "SELECT c FROM Client c WHERE c.id = :id"),
    new NamedQuery(name = "Client.findAllIdsOrderByIdDesc", query = "SELECT c.id FROM Client c ORDER BY c.id DESC"),
    new NamedQuery(name = "Client.findByCard", query = "SELECT c FROM Client c WHERE c.card = :card"),
    new NamedQuery(name = "Client.findByFullNameExact", query = "SELECT c FROM Client c WHERE c.fullName = :fullName"),
    new NamedQuery(name = "Client.findByFullNameNotExact", query = "SELECT c FROM Client c WHERE LOCATE(:fullName, c.fullName) != 0"),
    new NamedQuery(name = "Client.findByRegistrationDate", query = "SELECT c FROM Client c WHERE c.registrationDate = :registrationDate"),
    new NamedQuery(name = "Client.findByAttendancesBalance", query = "SELECT c FROM Client c WHERE c.attendancesBalance = :attendancesBalance"),
    new NamedQuery(name = "Client.findByExpirationDate", query = "SELECT c FROM Client c WHERE c.expirationDate = :expirationDate")))
@SequenceGenerator(name="id_cln_seq", allocationSize = 1)
class Client extends Serializable {
  
  @Id
  @Basic(optional = false)
  @Column(name = "id_cln")
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  protected var id: Int = _
  
  @Column(name = "card")
  protected var card: Integer = null
  
  @Basic(optional = false)
  @Column(name = "money_balance")
  protected var moneyBalance: BigDecimal = _
  
  @Basic(optional = false)
  @Lob
  @Column(name = "full_name")
  protected var fullName: String = _
  
  @Basic(optional = false)
  @Lob
  @Column(name = "note")
  protected var note: String = _
  
  @Basic(optional = false)
  @Column(name = "registration_date")
  @Temporal(TemporalType.DATE)
  protected var registrationDate: Date = _
  
  @Basic(optional = false)
  @Column(name = "attendances_balance")
  private var attendancesBalance: Int = _
  
  @Basic(optional = false)
  @Column(name = "expiration_date")
  @Temporal(TemporalType.DATE)
  protected var expirationDate: Date = _;
  
  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "client")
  protected var freezes: List[ClientFreeze] = _
  
  @JoinColumn(referencedColumnName="idcln_prf", insertable=false, updatable=false)
  @OneToOne(cascade = Array(CascadeType.ALL), mappedBy = "client", fetch=FetchType.LAZY)
  protected var clientProfile: ClientProfile = _
  
  @OneToMany(mappedBy = "client")
  protected var orders: List[OrderEntity] = _
  
  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "client")
  protected var attendances: List[Attendance] = _
       
  def getId(): Int = id

  def getFullName(): String = fullName
  def setFullName(fullName: String) = this.fullName = fullName
    
  def getNote(): String = note
  def setNote(note: String) = this.note = note

  def getRegistrationDate() = registrationDate
  def setRegistrationDate(registrationDate: Date) = this.registrationDate = registrationDate

  def getAttendancesBalance(): Int = attendancesBalance
  def setAttendancesBalance(attendancesBalance: Int) = this.attendancesBalance = attendancesBalance

  def getExpirationDate(): Date = expirationDate
  def setExpirationDate(expirationDate: Date) = this.expirationDate = expirationDate

  def getClientProfile(): ClientProfile = clientProfile
  
  def getMoneyBalance(): BigDecimal = moneyBalance
  def setMoneyBalance(moneyBalance: BigDecimal) = this.moneyBalance = moneyBalance

  def getAttendances(): List[Attendance] = attendances

  def getCard(): Integer = card
  def setCard(card: Integer) = card

  def getOrders(): List[OrderEntity] = orders

  def getFreezes(): List[ClientFreeze] = freezes

  def rollExpirationDate(itemSubscription: ItemSubscription,
			 forward: Boolean) {
    
    var date = if (expirationDate.compareTo(new Date()) > 0) 
		 new DateMidnight(expirationDate)
	       else 
		 new DateMidnight()

    if(forward) {
      date = date.plusYears(itemSubscription.getTermYears())
      date = date.plusMonths(itemSubscription.getTermMonths())
      date = date.plusDays(itemSubscription.getTermDays())
    } else {
      date = date.minusYears(itemSubscription.getTermYears())
      date = date.minusMonths(itemSubscription.getTermMonths())
      date = date.minusDays(itemSubscription.getTermDays())
    }

    expirationDate = date.toDate
  }
}
