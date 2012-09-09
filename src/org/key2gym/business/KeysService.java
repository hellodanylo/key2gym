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
package org.key2gym.business;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.NoResultException;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.KeyDTO;
import org.key2gym.persistence.Key;

/**
 *
 * @author Danylo Vashchilenko
 */
public class KeysService extends BusinessService {

    /**
     * Gets all keys available for new attendances.
     * 
     * @throws IllegalStateException if the session is not active
     * @return the list of keys available 
     */
    public List<KeyDTO> getKeysAvailable() {
        assertOpenSessionExists();

        List<KeyDTO> result = new LinkedList();
        List<Key> keys;

        keys = entityManager.createNamedQuery("Key.findAvailable") //NOI18N
                .getResultList();

        for (Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }

        return result;
    }

    /**
     * Gets all keys taken by attendances.
     * 
     * @throws IllegalStateException if the session is not active
     * @return the list of keys taken 
     */
    public List<KeyDTO> getKeysTaken() {
        assertOpenSessionExists();

        List<KeyDTO> result = new LinkedList<>();
        List<Key> keys;

        keys = entityManager.createNamedQuery("Key.findTaken") //NOI18N
                .getResultList();

        for (Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }

        return result;
    }

    public List<KeyDTO> getAllKeys() {
        assertOpenSessionExists();

        List<KeyDTO> result = new LinkedList<>();
        List<Key> keys;

        keys = entityManager.createNamedQuery("Key.findAll") //NOI18N
                .getResultList();

        for (Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }

        return result;
    }

    /**
     * Adds a new key.
     * 
     * @param key the key to add
     * @throws ValidationException  
     */
    public void addKey(KeyDTO key) throws ValidationException, org.key2gym.business.api.SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (SessionsService.getInstance().getPermissionsLevel().compareTo(SessionsService.PL_ALL) < 0) {
            throw new org.key2gym.business.api.SecurityException(getString("Security.Operation.Denied"));
        }

        Key keyEntity = new Key();

        String title = key.getTitle().trim();
        if (title.isEmpty()) {
            throw new ValidationException(getString("Invalid.Property.CanNotBeEmpty.withPropertyName", "Property.Title"));
        }
        keyEntity.setTitle(title);

        entityManager.persist(keyEntity);
        entityManager.flush();
    }

    public void updateKey(KeyDTO key) throws ValidationException, org.key2gym.business.api.SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (SessionsService.getInstance().getPermissionsLevel().compareTo(SessionsService.PL_ALL) < 0) {
            throw new org.key2gym.business.api.SecurityException(getString("Security.Operation.Denied"));
        }

        Key keyEntity;
        
        try {
            keyEntity = entityManager.find(Key.class, key.getId());
        } catch(NoResultException ex) {
            throw new ValidationException(getString("Invalid.ID"));
        }

        String title = key.getTitle().trim();
        if (title.isEmpty()) {
            throw new ValidationException(getString("Invalid.Property.CanNotBeEmpty.withPropertyName", "Property.Title"));
        }
        keyEntity.setTitle(title);

        entityManager.merge(keyEntity);
        entityManager.flush();
    }
    
    public void removeKey(Integer id) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (SessionsService.getInstance().getPermissionsLevel().compareTo(SessionsService.PL_ALL) < 0) {
            throw new org.key2gym.business.api.SecurityException(getString("Security.Operation.Denied"));
        }

        Key keyEntity;
        
        try {
            keyEntity = entityManager.find(Key.class, id);
        } catch(NoResultException ex) {
            throw new ValidationException(getString("Invalid.ID"));
        }
        
        Long count = (Long)entityManager.createNamedQuery("Attendance.countWithKey")
                        .setParameter("key", keyEntity)
                        .getSingleResult();
        if(count > 0) {
            throw new ValidationException(getString("Invalid.Key.HasUnarchivedAttendances"));
        }

        entityManager.remove(keyEntity);
        entityManager.flush();
    }
        
    /**
     * Singleton instance.
     */
    private static KeysService instance;

    /**
     * Gets an instance of this class.
     * 
     * @return an instance of this class.
     */
    public static KeysService getInstance() {
        if (instance == null) {
            instance = new KeysService();
        }
        return instance;
    }
}
