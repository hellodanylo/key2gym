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
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ClientDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface ClientsServiceInterface extends BasicInterface {

    /**
     * Finds the client by its card.
     *
     * @param card the client's card
     * @throws NullPointerException if the card is null
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the client's ID, or null, if the card is not assigned to any Client.
     */
    Integer findByCard(Integer card) throws SecurityViolationException;

    /**
     * Finds all clients whose full names match the requirement.
     *
     * <ul>
     * <li>If exact match is required, a full name matches, if it's
     * exactly the same as the full name provided</li>
     * <li>If exact match is not required, a full name matches, if it
     * contains the full name provided</li>
     * <li>Both type of matches are case-insensitive</li>
     * </ul>
     *
     * @param fullName the full name
     * @param exactMatch whether an exact match is required
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws NullPointerException if either fullName or exactMatch is null
     * @return the list of the clients whose full name matches
     */
    List<ClientDTO> findByFullName(String fullName, Boolean exactMatch) throws SecurityViolationException;

    /**
     * Gets the client's by its ID.
     *
     * @param clientId the client's ID.
     * @return the client
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws NullPointerException if the clientId is null
     * @throws ValidationException if the client's ID is invalid
     */
    ClientDTO getById(Integer clientId) throws ValidationException, SecurityViolationException;

    /**
     * Gets the ID of the next client to be registered.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the next client's ID
     */
    Integer getNextId() throws SecurityViolationException;

    /**
     * Gets a template client for registration. A template client is an instance
     * of client with some properties set to their default values.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return a template client
     */
    ClientDTO getTemplateClient() throws SecurityViolationException;

    /**
     * Returns whether the client has a debt.
     *
     * @param clientId the client's ID
     * @return true, if the client's money balance is not negative.
     * @throws NullPointerException if the clientId is null
     * @throws ValidationException if the client's ID is invalid
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    Boolean hasDebt(Integer clientId) throws ValidationException, SecurityViolationException;

    /**
     * Registers a new client.
     *
     * <ul>
     * <li>Full name, card and note properties are always required to be
     * non-null</li>
     * <li>If useSecuredProperties is true, attendancesBalance, registrationDate,
     * expirationDate properties are required to be non-null.</li>
     * <li> If useSecuredProperties is true, the caller must have MANAGER role</li>
     * </ul>
     *
     * @param client the client's new information
     * @param useSecuredProperties if true, the special fields are used.     
     * @throws NullPointerException if either client or useSecuredProperties is
     * null, or any of the required properties is null
     * @throws ValidationException if any of the required properties is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws SecurityViolationException if the caller does not required security roles
     * @return the new client's ID
     */
    Integer registerClient(ClientDTO client, Boolean useSecuredProperties) throws BusinessException, ValidationException, SecurityViolationException;

    /**
     * Updates the information about the client.
     *
     * <ul>
     * <li>Full name, card and note properties are always required to be
     * non-null</li>
     * <li>If useSecuredProperties is true, attendancesBalance, registrationDate,
     * expirationDate properties are required to be non-null.</li>
     * <li> If useSecuredProperties is true, the caller must have MANAGER role</li>
     * </ul>
     *
     * @param client the Client's ID.
     * @param useSecuredProperties if true, the special fields are used.
     * @throws NullPointerException if either of the arguments or required
     * properties is null
     * @throws SecurityViolationException if the caller does not required security roles
     * @throws ValidationException if any of the required properties is invalid
     */
    void updateClient(ClientDTO client, Boolean useSecuredProperties) throws SecurityViolationException, ValidationException;
    
}
