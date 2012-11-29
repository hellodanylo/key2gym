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
import scala.math.BigDecimal
import javax.persistence._
import java.lang.Integer
import java.util._
import org.joda.time.DateMidnight
import org.key2gym.persistence._
import org.key2gym.business.api.ValidationException
import org.key2gym.business.resources.ResourcesManager

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
  protected var moneyBalance: java.math.BigDecimal = _
  
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
  protected var attendancesBalance: Int = _
  
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

  @Version
  protected var version: Int = _
       
  /** Returns the ID */
  def getId(): Int = id

  /** Returns the full name */
  def getFullName(): String = fullName
  /** Sets the full name */
  def setFullName(fullName: String) = this.fullName = fullName
    
  /** Returns the note */
  def getNote(): String = note
  /** Sets the note */
  def setNote(note: String) = this.note = note

  /** Returns the registration date */
  def getRegistrationDate() = registrationDate
  /** Sets the registration date */
  def setRegistrationDate(registrationDate: Date) = this.registrationDate = registrationDate

  /** Returns the attendancse balance */
  def getAttendancesBalance(): Int = attendancesBalance
  /** Sets the attendances balance */
  def setAttendancesBalance(attendancesBalance: Int) = this.attendancesBalance = attendancesBalance

  /** Returns the expiration date */
  def getExpirationDate(): Date = expirationDate
  /** Sets the expiration date */
  def setExpirationDate(expirationDate: Date) = this.expirationDate = expirationDate

  /** Returns true if the client is expired.
    *
    * A client is expired if his expiration date is today
    * or before today.
    */
  def isExpired(): Boolean = expirationDate.compareTo(new Date) <= 0

  /** Returns the client's profile */
  def getClientProfile(): ClientProfile = clientProfile
  
  /** Returns the money balance */
  def getMoneyBalance(): BigDecimal = moneyBalance
  /** Sets the money balance
    *
    * TODO: validate the amount
    */
  def setMoneyBalance(moneyBalance: BigDecimal) = this.moneyBalance = moneyBalance.underlying

  /** Charges the client.
    *
    * The total charge amount is calculated
    * based on the item's price, quantity and discount.
    *
    * The client's money balance is then decreased by that amount.
    *
    * @throws ValidationException if the resulting money balance reaches the limit
    * @param item the item to charge with
    * @param quantity the quantity of items to charge
    * @param discount the discount to apply to the total charge amount
    */
  @throws(classOf[ValidationException])
  def charge(item: Item, quantity: Int, discount: Discount) {

    // Calcualates the total amount
    var amount: BigDecimal = item.getPrice()
    if (discount != null) {
      amount = amount / 100 * (100 - discount.getPercent())
    }
    amount = amount * quantity
    
    // Checks that the new money balance is within the limit
    var newMoneyBalance = BigDecimal(moneyBalance) - amount
    if (newMoneyBalance.precision > OrderEntity.moneyMaxPrecision) {
      throw new ValidationException(ResourcesManager.getString("Invalid.Client.MoneyBalance.LimitReached"))
    }
    
    moneyBalance = newMoneyBalance.underlying
  }

  /** Uncharges the client.
    *
    * The result is the opposite of using the charge method.
    *
    * @throws ValidationException if the resulting money balance reaches the limit
    * @param item the item to uncharge with
    * @param quantity the quantity of items to uncharge
    * @param discount the discount applied when the client was charged
    */
  @throws(classOf[ValidationException])
  def uncharge(item: Item, quantity: Int, discount: Discount) {

    // Calcualates the total amount
    var amount: BigDecimal = item.getPrice()
    if (discount != null) {
      amount = amount / 100 * (100 - discount.getPercent())
    }
    amount = amount * quantity
    
    // Checks that the new money balance is within range
    var newMoneyBalance = amount + moneyBalance
    if (newMoneyBalance.precision > OrderEntity.moneyMaxPrecision) {
      throw new ValidationException(ResourcesManager.getString("Invalid.Client.MoneyBalance.LimitReached"))
    }
    
    moneyBalance = newMoneyBalance.underlying
  }

  def transfer(amount: BigDecimal) {

    /*
     * Normalizes the scale, and throws an exception, if
     * the scale is too big.
     */
    if (amount.scale > 2) {
      throw new ValidationException(ResourcesManager.getString("Invalid.Money.TwoDigitsAfterDecimalPointMax"))
    }
    val scaledAmount = amount.setScale(2)

    // Checks that the new money balance is within range
    var newMoneyBalance = scaledAmount + moneyBalance
    if (newMoneyBalance.precision > OrderEntity.moneyMaxPrecision) 
      throw new ValidationException(ResourcesManager.getString("Invalid.Client.MoneyBalance.LimitReached"))
    
    moneyBalance = newMoneyBalance.underlying
  }

  /** Returns the list of attendances. */
  def getAttendances(): List[Attendance] = attendances

  /** Returns the card number. */
  def getCard(): Integer = card
  /** Sets the card. */
  def setCard(card: Integer) = card

  /** Returns the list of orders. */
  def getOrders(): List[OrderEntity] = orders
  /** Returns the list of freezes. */
  def getFreezes(): List[ClientFreeze] = freezes

  /** Activates the subscription on this client.
    *
    * Increases the client's attendances balance
    * and the expiration date according to the
    * subscription.
    *
    * If the client is expired, the expiration date
    * is calculated from today's date and the attendances
    * balance, if it's positive, is zeroed.
    *
    * If the client isn't expired, the current
    * values of both properties are taken in
    * account.
    *
    * @param itemSubscription the subscription to apply
    */
  def activate(itemSubscription: ItemSubscription) {
    if(isExpired && attendancesBalance > 0)
      attendancesBalance = 0

    var baseDate = if (expirationDate.compareTo(new Date()) > 0) 
		     expirationDate
		   else 
		     new Date()

    expirationDate = rollExpirationDate(baseDate, itemSubscription, true)
    attendancesBalance += itemSubscription.getUnits
  }
  
  /**
    * Deactivates the subscription on this client.
    *
    * The expiration date and the attendances balance
    * are rolled back according to the subscription.
    *
    * @param itemSubscription the subscription to apply
    */ 
  def deactivate(itemSubscription: ItemSubscription) {
    expirationDate = rollExpirationDate(expirationDate, itemSubscription, false)
    attendancesBalance -= itemSubscription.getUnits
  }

  /** Rolls the date according to the subscription.
    *
    * @param date the date to roll
    * @param itemSubscription the subscription to roll with
    * @param forward if true the date is moved forward in time
    * @return the resulting date
    */
  private def rollExpirationDate(date: Date, 
				 itemSubscription: ItemSubscription,  
				 forward: Boolean): Date = {
    var result = new DateMidnight(date)

    if(forward) {
      result = result.plusYears(itemSubscription.getTermYears())
      result = result.plusMonths(itemSubscription.getTermMonths())
      result = result.plusDays(itemSubscription.getTermDays())
    } else {
      result = result.minusYears(itemSubscription.getTermYears())
      result = result.minusMonths(itemSubscription.getTermMonths())
      result = result.minusDays(itemSubscription.getTermDays())
    }

    result.toDate
  }
}
