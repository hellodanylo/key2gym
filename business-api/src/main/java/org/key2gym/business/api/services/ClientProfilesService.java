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
package org.key2gym.business.api.services;

import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ClientProfileDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface ClientProfilesService extends BasicService {

    /**
     * Detaches the profile from the client by its ID.
     * <p/>
     *
     * <ul>
     * <li>The caller must have MANAGER role</li>
     * <li>The client has to have a profile attached</li>
     * </ul>
     *
     * @param id the client's ID whose profile to detach
     * @throws SecurityViolationException if the caller does not have MANAGER role
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the id is invalid
     * @throws BusinessException if current business rules restrict this operation
     */
    void detachClientProfile(Integer id) throws SecurityViolationException, ValidationException, BusinessException;

    /**
     * Gets a client profile by ID.
     *
     * @param id the client profile's ID
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws ValidationException the ID is invalid
     * @return the client profile
     */
    ClientProfileDTO getById(Integer id) throws ValidationException;

    /**
     * Updates a client profile.
     * <p>
     *
     * <ul>
     * <li>The client profile's ID is also the client's ID</li>
     * <li>If the client does not have a profile, it will be created</li>
     * </ul>
     *
     * @param clientProfile the Client Profile
     * @throws NullPointerException if any of arguments or required properties
     * is null
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws ValidationException the ID is invalid or any of the required properties is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     */
    void updateClientProfile(ClientProfileDTO clientProfile) throws BusinessException, ValidationException, SecurityViolationException;
    
}
