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
package org.key2gym.business.api.interfaces;

import java.util.List;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.KeyDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface KeysServiceInterface extends BasicInterface {

    /**
     * Adds a new key.
     *
     * @param key the key to add
     * @throws SecurityViolationException if the caller does not have MANAGER role
     * @throws ValidationException if any of the required properties is invalid
     */
    public void addKey(KeyDTO key) throws ValidationException, SecurityViolationException;
    
    /**
     * Gets all keys.
     *
     * @throws SecurityViolationException if the caller does not have *_ADMINISTRATOR or MANAGER role
     * @return the list of all keys
     */
    public List<KeyDTO> getAllKeys() throws SecurityViolationException;

    /**
     * Gets all keys available for new attendances.
     *
     * @throws SecurityViolationException if the caller does not have *_ADMINISTRATOR or MANAGER role
     * @return the list of keys available
     */
    public List<KeyDTO> getKeysAvailable() throws SecurityViolationException;

    /**
     * Gets all keys taken by attendances.
     *
     * @throws SecurityViolationException if the caller does not have *_ADMINISTRATOR or MANAGER role
     * @return the list of keys taken
     */
    public List<KeyDTO> getKeysTaken() throws SecurityViolationException;
    
    /**
     * Removes the key
     *
     * @param id the ID of the key to remove
     * @throws SecurityViolationException if the caller does not have MANAGER role
     * @throws ValidationException if the key's ID is invalid
     */
    public void removeKey(Integer id) throws ValidationException, SecurityViolationException;
    
    /**
     * Updates the key
     *
     * @param key the key to update
     * @throws SecurityViolationException if the caller does not have MANAGER role
     * @throws ValidationException if any of the required properties is invalid
     */
    public void updateKey(KeyDTO key) throws ValidationException, SecurityViolationException;
    
}
