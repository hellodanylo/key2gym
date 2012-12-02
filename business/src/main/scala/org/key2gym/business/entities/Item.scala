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
import java.util.List
import javax.persistence._
import scala.collection.JavaConversions._
import org.key2gym.business.api.ValidationException
import org.key2gym.business.resources.ResourcesManager.getString
import org.key2gym.persistence._
import java.text.MessageFormat

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "item_itm")
@NamedQueries(Array(
    new NamedQuery(name = "Item.findAll", 
		   query = "SELECT i FROM Item i"),
    new NamedQuery(name = "Item.findPure", 
		   query = "SELECT i FROM Item i WHERE i.id NOT IN (SELECT s.item.id FROM ItemSubscription s)"),
    new NamedQuery(name = "Item.findAvailable", 
		   query = "SELECT i FROM Item i WHERE i.quantity IS NULL OR i.quantity > 0"),
    new NamedQuery(name = "Item.findPureAvailable", 
		   query = "SELECT i FROM Item i WHERE (i.quantity is NULL OR i.quantity > 0) AND i.id NOT IN (SELECT s.item.id FROM ItemSubscription s)"),
    new NamedQuery(name = "Item.findById", 
		   query = "SELECT i FROM Item i WHERE i.id = :id"),
    new NamedQuery(name = "Item.findByBarcode", 
		   query = "SELECT i FROM Item i WHERE i.barcode = :barcode"),
    new NamedQuery(name = "Item.findByQuantity", 
		   query = "SELECT i FROM Item i WHERE i.quantity = :quantity"),
    new NamedQuery(name = "Item.findByPrice", 
		   query = "SELECT i FROM Item i WHERE i.price = :price"),
    new NamedQuery(name = "Item.getAllBarcodes", 
		   query = "SELECT DISTINCT i.barcode FROM Item i")))
@SequenceGenerator(name="id_itm_seq", allocationSize = 1)
class Item extends Serializable {
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="id_itm_seq")
  @Basic(optional = false)
  @Column(name = "id_itm")
  protected var id: java.lang.Integer = _
  
  @Basic(optional = false)
  @Lob
  @Column(name = "title")
  protected var title: String = _
  
  @Basic(optional = true)
  @Column(name = "barcode")
  protected var barcode: java.lang.Long = _
  
  @Basic(optional = true)
  @Column(name = "quantity")
  protected var quantity: java.lang.Integer = _
  
  @Basic(optional = false)
  @Column(name = "price")
  protected var price: BigDecimal = _
  
  @OneToMany(mappedBy="item")
  protected var orderLines: List[OrderLine] = _
  
  @JoinColumn(name="id_itm", referencedColumnName="iditm_its")
  @OneToOne(mappedBy="item", cascade=Array(CascadeType.REMOVE))
  protected var itemSubscription: ItemSubscription = _
  
  /** Returns the ID */
  def getId(): Int = this.id
  
  /** Returns the title */
  def getTitle(): String = this.title

  /** Sets the title.
   *
   * The title should contains at least
   * one printable character.
   *
   * @param title the new title
   * @throws NullPointerException if the title is null
   * @throws ValidationException if the title is invalid*/
  def setTitle(title: String) { 
    if (title == null) {
      throw new NullPointerException("The title is null."); //NOI18N
    }
    
    val trimmedTitle  = title.trim();
    if (trimmedTitle.isEmpty()) {
      throw new ValidationException(
	getString("Invalid.Item.Title.CanNotBeEmpty")
      )
    }

    this.title = trimmedTitle
  }
  
  /** Returns the price */
  def getPrice(): BigDecimal = this.price
  /** Sets the price
   *
   * The price should at most have scale of 2,
   * and the precision of OrderEntity.moneyMaxPrecision
   *
   * @param price the new price
   * @throws NullPointerException if the price is null
   * @throws ValidationException if the price is invalid
   */
  def setPrice(price: BigDecimal) {

    if (price == null) {
      throw new NullPointerException("The price is null.")
    }

    // Should not have scale larger than 2
    if (price.scale > 2) {
      throw new ValidationException(
	getString("Invalid.Money.ScaleOverLimit")
      )
    }

    val scaledPrice = price.setScale(2)

    // Should not have precision larger than maxPrecision
    if (scaledPrice.precision > OrderEntity.moneyMaxPrecision) {
      throw new ValidationException(
	getString("Invalid.Money.ValueOverLimit.withLimit", 
		  "9" * (OrderEntity.moneyMaxPrecision - 2))
      )
    }

    // Should not be negative
    if (scaledPrice.compareTo(new BigDecimal(0)) < 0) {
      throw new ValidationException(
	getString("Invalid.Property.CanNotBeNegative.withPropertyName",
		  getString("Property.Price"))
      )
    }

    this.price = scaledPrice
  }
  
  /** Returns the subscription.
   *
   * @return subscription, if this item is a subscription; null, otherwise
   */
  def getItemSubscription(): ItemSubscription = this.itemSubscription
  /** Sets the subscription */
  def setItemSubscription(itemSubscription: ItemSubscription) = this.itemSubscription = itemSubscription
  
  /** Returns the barcode */
  def getBarcode(): java.lang.Long = this.barcode

  /** Sets the barcode.
   *
   * The barcode should not be negative.
   *
   * @param quantity the new barocode
   * @throws ValidationException if the barcode is not valid
   */  
  def setBarcode(barcode: java.lang.Long) {
    
    // No effect if the barcode is the same
    if(barcode == this.barcode) {
      return
    }

    // Null is fine
    if(barcode == null) {
      this.barcode = barcode
      return
    }

    // Can not be negative
    if (barcode < 0) {
      throw new ValidationException(
	getString("Invalid.Property.CanNotBeNegative.withPropertyName", 
		  getString("Property.Barcode"))
      )
    }

    this.barcode = barcode
  }

  /** Returns the quantity */
  def getQuantity(): java.lang.Integer = this.quantity
  
  /** Sets the quantity.
    *
    * @param quantity the new quantity
    * @throws ValidationException if the quantity is not valid
    */
  def setQuantity(quantity: java.lang.Integer) {
    // Quantity has to be within range [0; 255]
    if(quantity != null) {
      if (quantity < 0) {
	throw new ValidationException(
	  getString("Invalid.Property.CanNotBeNegative.withPropertyName", 
		    getString("Property.Quantity"))
	)
      } else if (quantity > 255) {
	throw new ValidationException(
	  getString("Invalid.Property.OverLimit.withPropertyName", 
		    getString("Property.Quantity"))
	)
      }
    }
    
    this.quantity = quantity
  }
  
  def getOrderLines(): List[OrderLine] = this.orderLines
}
