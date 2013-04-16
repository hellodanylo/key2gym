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

package org.key2gym.business.services

import java.util.LinkedList
import java.util.List
import javax.annotation.security._
import javax.persistence.NoResultException
import org.key2gym.business.api._
import org.key2gym.business.api.dtos.ItemDTO
import org.key2gym.persistence._
import org.key2gym.business.entities._
import scala.collection.JavaConversions._
import org.key2gym.business.api.services.ItemsService
import org.key2gym.business.api.SecurityViolationException
import org.springframework.stereotype.Service

/**
 *
 * @author Danylo Vashchilenko
 */
@Service("org.key2gym.business.api.services.ItemsService")
class ItemsServiceBean extends BasicBean with ItemsService {
  
  @throws(classOf[ValidationException])
  @throws(classOf[SecurityViolationException])
  override def addItem(item: ItemDTO) {

    if (!callerHasRole(SecurityRoles.MANAGER))
      throw new SecurityViolationException(getString("Security.Operation.Denied"))        
    
    if (item == null)
      throw new NullPointerException("The item is null.")

    if(item.getBarcode != null) {
      try {
	em.createNamedQuery("Item.findByBarcode")
	  .setParameter("barcode", item.getBarcode())
	  .getSingleResult()

	throw new ValidationException(getString("Invalid.Item.Barcode.AlreadyInUse"))
      } catch {
	case ex: NoResultException => // the barcode is unique - fine!  
      }
    }

    val entityItem = new Item
    entityItem.setBarcode(item.getBarcode())
    entityItem.setTitle(item.getTitle())
    entityItem.setQuantity(item.getQuantity())
    entityItem.setPrice(item.getPrice())
    
    em.persist(entityItem)
  }

  @throws(classOf[SecurityViolationException])
  override def getItemsAvailable(): List[ItemDTO] = {

    if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, 
			  SecurityRoles.SENIOR_ADMINISTRATOR, 
			  SecurityRoles.MANAGER)) 
        throw new SecurityViolationException(getString("Security.Access.Denied"))
        

    val result = new LinkedList[ItemDTO]
    val items = em.createNamedQuery("Item.findAvailable", classOf[Item])
      .getResultList()

    val penaltyItemId = em.find(classOf[Property], "time_range_mismatch_penalty_item_id").getInteger

    for (item <- items) {
      /* Skips the time range mismatch penalty item. */
      if (item.getId != penaltyItemId)
	result.add(convertToDTO(item))
    }

    result
  }

  @throws(classOf[SecurityViolationException])
  override def getAllItems(): List[ItemDTO] = {

    if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, 
			  SecurityRoles.SENIOR_ADMINISTRATOR, 
			  SecurityRoles.MANAGER)) 
      throw new SecurityViolationException(getString("Security.Access.Denied"))

    val result = new LinkedList[ItemDTO]
    val items = em.createNamedQuery("Item.findAll", classOf[Item])
      .getResultList()
    
    for (item <- items) {
      result.add(convertToDTO(item))
    }
    
    result
  }

  @throws(classOf[SecurityViolationException])
  override def getPureItems(): List[ItemDTO] = {
    
    if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR,
			  SecurityRoles.SENIOR_ADMINISTRATOR,
			  SecurityRoles.MANAGER)) 
      throw new SecurityViolationException(getString("Security.Access.Denied"))
    
    val result = new LinkedList[ItemDTO]
    val items = em.createNamedQuery("Item.findPure", classOf[Item])
      .getResultList()
    
      for (item <- items) {
        result.add(convertToDTO(item))
      }
    
    result
  }

  @throws(classOf[SecurityViolationException])
  override def getPureItemsAvailable(): List[ItemDTO] = {

    if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR,
			  SecurityRoles.SENIOR_ADMINISTRATOR,
			  SecurityRoles.MANAGER)) 
      throw new SecurityViolationException(getString("Security.Access.Denied"))
    
    val result = new LinkedList[ItemDTO]
    val items = em.createNamedQuery("Item.findPureAvailable", classOf[Item])
      .getResultList()
    
    val penaltyItemId = em.find(classOf[Property], "time_range_mismatch_penalty_item_id")
      .getInteger
    
    for (item <- items) {
      /*
       * Skips the time range mismatch penalty item.
       */
      if (item.getId() != penaltyItemId)
        result.add(convertToDTO(item))
    }
    
    result
  }

  @throws(classOf[ValidationException]) 
  @throws(classOf[SecurityViolationException])
  override def updateItem(item: ItemDTO) {

    if (!callerHasRole(SecurityRoles.MANAGER))
      throw new SecurityViolationException(getString("Security.Operation.Denied"))

    if (item == null)
      throw new NullPointerException("The item is null.")
    
    if (item.getId() == null) 
      throw new NullPointerException("The item's ID is null.")
    
    val entityItem = em.find(classOf[Item], item.getId())
    
    if (entityItem == null)
      throw new ValidationException(getString("Invalid.Item.ID"))
    
    if(item.getBarcode != null && !item.getBarcode.equals(entityItem.getBarcode)) {
      try {
        em.createNamedQuery("Item.findByBarcode")
	  .setParameter("barcode", item.getBarcode())
	  .getSingleResult()

	throw new ValidationException(getString("Invalid.Item.Barcode.AlreadyInUse"))
      } catch {
	case ex: NoResultException => // the barcode is unique - fine!  
      }
    }
    
    entityItem.setBarcode(item.getBarcode())
    entityItem.setTitle(item.getTitle())
    entityItem.setQuantity(item.getQuantity())
    entityItem.setPrice(item.getPrice())
  }
  
  @throws(classOf[ValidationException]) 
  @throws(classOf[SecurityViolationException])
  @throws(classOf[BusinessException])
  override def removeItem(itemId: java.lang.Integer) {
    
    if (itemId == null) {
      throw new NullPointerException("The itemId is null.") //NOI18N
    }
    
    val item = em.find(classOf[Item], itemId)
    
    if (item == null) {
      throw new ValidationException(getString("Invalid.Item.ID"))
    }
    
    if (item.getItemSubscription() != null) {
      throw new BusinessException(getString("BusinessRule.Item.IsSubscription"))
    }
      
    if (!item.getOrderLines().isEmpty()) {
      throw new BusinessException(
	  getString("BusinessRule.Item.HasUnarchivedPurchases.withItemTitle", item.getTitle())
      )
    }
    
    em.remove(item)
  }
  
  /**
   * Wraps an item into a DTO.
   *
   * @param item the item to wrap
   * @return the DTO containing the item
   */
  def convertToDTO(item: Item): ItemDTO = {
    val result = new ItemDTO()
    result.setId(item.getId())
    result.setBarcode(item.getBarcode())
    result.setItemSubscription(item.getItemSubscription() != null)
    result.setPrice(item.getPrice().setScale(2))
    result.setQuantity(item.getQuantity())
    result.setTitle(item.getTitle())
    
    result
  }
}
