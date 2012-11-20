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
package org.key2gym.business.api.interfaces;

import java.util.List;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.FreezeDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface FreezesServiceInterface extends BasicInterface {

    /**
     * Records a freeze for the client.
     * <p/>
     * 
     * <ul>
     * <li>The caller has to have SENIOR_ADMINISTRATOR or MANAGER role</li>
     * <li>The client can not be expired</li>
     * <li>The number of days can not exceed 10</li>
     * <li>There can be at most 1 freeze per month</li>
     * </ul>
     *
     * @param clientId the client's ID
     * @throws SecurityViolationException if the caller does not have SENIOR_ADMINISTRATOR or MANAGER role
     * @throws NullPointerException if any of the arguments is null
     * @throws ValidationException if the client's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     */
    void addFreeze(Integer clientId, Integer days, String note) throws ValidationException, BusinessException, SecurityViolationException;

    /**
     * Finds all freezes.
     * <p/>
     * 
     * <ul>
     * <li>The caller has to have MANAGER role</li>
     * </ul>
     *
     * @throws SecurityViolationException of the caller does not have MANAGER role
     * @return the list of all freezes
     */
    List<FreezeDTO> findAll() throws SecurityViolationException;

    /**
     * Finds freezes having date issued within the range.
     * <p/>
     * 
     * <ul>
     * <li>The caller has to have MANAGE role</li>
     * <li>The beginning date has be before or equal to the ending date</li>
     * </ul>
     *
     * @param begin the beginning date
     * @param end the ending date
     * @throws NullPointerException if any of the arguments is null
     * @throws SecurityViolationException if the caller does not have MANAGER role
     * @throws BusinessException if any of the arguments is invalid
     * @return the list of freezes
     */
    List<FreezeDTO> findByDateIssuedRange(DateMidnight begin, DateMidnight end) throws SecurityViolationException, ValidationException;

    /**
     * Finds all freezes for the client.
     *
     * @param clientId the client's ID
     * @throws NullPointerException if the client's ID is null
     * @throws ValidationException if the client's ID is invalid
     * @return the list of all freezes for the client
     */
    List<FreezeDTO> findFreezesForClient(Integer clientId) throws ValidationException;

    /**
     * Removes the freeze by its ID.
     * <p/>
     * 
     * <ul>
     * <li>The caller has to have MANAGER role</li>
     * <li>The freeze has to be active, which is the expiration date can
     * not be in the past</li>
     * </ul>
     *
     * @param id the freeze's ID
     * @throws SecurityException if the caller does not have MANAGER role
     * @throws NullPointerException if the freeze's ID is null
     * @throws ValidationException if the freeze's ID is invalid
     * @throws BusinessException if current business rules restrict this operation
     */
    void remove(Integer id) throws SecurityViolationException, ValidationException, BusinessException;
    
}
