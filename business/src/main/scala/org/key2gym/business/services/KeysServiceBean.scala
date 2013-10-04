/*
 * Copyright 2012-2013 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License") you may not
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
import javax.persistence.NoResultException
import org.key2gym.business.api._
import org.key2gym.business.api.dtos.KeyDTO
import org.key2gym.business.entities.Key
import scala.collection.JavaConversions._
import org.key2gym.business.api.services.KeysService
import org.key2gym.business.api.SecurityViolationException
import org.springframework.stereotype.Service

/**
 *
 * @author Danylo Vashchilenko
 */
@Service("org.key2gym.business.api.services.KeysService")
class KeysServiceBean extends BasicBean with KeysService {

  @throws(classOf[SecurityViolationException])
  override def getKeysAvailable(): List[KeyDTO] = {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
          throw new SecurityViolationException(getString("Security.Access.Denied"))
        }

        val result = new LinkedList[KeyDTO]
        val keys = getEntityManager().createNamedQuery("Key.findAvailable", classOf[Key]).getResultList()

        for (key <- keys) {
          result.add(new KeyDTO(key.getId, key.getTitle))
        }

        result
    }
  
  @throws(classOf[SecurityViolationException])
  override def getKeysTaken(): List[KeyDTO] = {
    
    if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
      throw new SecurityViolationException(getString("Security.Access.Denied"))
    }
    
    val result = new LinkedList[KeyDTO]()
    val keys = getEntityManager().createNamedQuery("Key.findTaken", classOf[Key]).getResultList()
    
    for (key <- keys) {
      result.add(new KeyDTO(key.getId, key.getTitle))
    }
    
    result
  }
  
  @throws(classOf[SecurityViolationException])
  def getAllKeys(): List[KeyDTO] = {
    
    if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
      throw new SecurityViolationException(getString("Security.Access.Denied"))
    }
    
    val result = new LinkedList[KeyDTO]()
    val keys = getEntityManager().createNamedQuery("Key.findAll", classOf[Key]).getResultList()
    
    for (key <- keys) {
      result.add(new KeyDTO(key.getId, key.getTitle))
    }
    
    result
  }
  
  @throws(classOf[SecurityViolationException])
  @throws(classOf[ValidationException])
  override def addKey(key: KeyDTO) {

    if (!callerHasRole(SecurityRoles.MANAGER)) {
      throw new SecurityViolationException(getString("Security.Operation.Denied"))
    }

    val keyEntity = new Key()
    keyEntity.setTitle(key.getTitle())
    
    getEntityManager().persist(keyEntity)
  }

  @throws(classOf[SecurityViolationException])
  @throws(classOf[ValidationException])
  override def updateKey(key: KeyDTO) {
    
    if (!callerHasRole(SecurityRoles.MANAGER)) {
      throw new SecurityViolationException(getString("Security.Operation.Denied"))
    }
    
    var keyEntity: Key = null
    
    try {
      keyEntity = getEntityManager().find(classOf[Key], key.getId())
    } catch {
	case ex: NoResultException => throw new ValidationException(getString("Invalid.ID"))
    }
    
    val title = key.getTitle().trim()
    if (title.isEmpty()) {
      throw new ValidationException(getString("Invalid.Property.CanNotBeEmpty.withPropertyName", "Property.Title"))
    }

    keyEntity.setTitle(title)
    
    getEntityManager().merge(keyEntity)
  }

  @throws(classOf[SecurityViolationException])
  @throws(classOf[ValidationException])
  override def removeKey(id: java.lang.Integer) {
    
    if (!callerHasRole(SecurityRoles.MANAGER)) {
      throw new SecurityViolationException(getString("Security.Operation.Denied"))
    }

    var keyEntity: Key = null

    try {
      keyEntity = getEntityManager().find(classOf[Key], id)
    } catch {
	case ex: NoResultException => throw new ValidationException(getString("Invalid.ID"))
    }

    val count = getEntityManager().createNamedQuery("Attendance.countWithKey", classOf[Long])
      .setParameter("key", keyEntity)
      .getSingleResult()
    
    if (count > 0) {
      throw new ValidationException(getString("Invalid.Key.HasUnarchivedAttendances"))
    }
    
    getEntityManager().remove(keyEntity)
  }
}
