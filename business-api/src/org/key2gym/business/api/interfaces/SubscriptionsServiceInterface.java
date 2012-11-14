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
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.SubscriptionDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface SubscriptionsServiceInterface {

    /**
     * Adds a subscription.
     *
     * <ul>
     * <li>The caller has to have MANAGER role</li>
     * <li>All properties except the ID are required</li>
     * </ul>
     *
     * @param subscription the subscription to add
     * @throws ValidationException if any of the required properties is invalid
     * @throws NullPointerException if subscription or any of the required
     * properties is null
     * @throws SecurityViolationException if the caller does not have required roles
     */
    void addSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityViolationException;

    /**
     * Gets all subscriptions.
     *
     * @throws SecurityViolationException if the caller does not have *_ADMINISTRATOR or MANAGER role
     * @return a list of subscriptions
     */
    List<SubscriptionDTO> getAllSubscriptions() throws SecurityViolationException;

    /**
     * Removes a subscription. 
     * <p/>
     *
     * <ul> 
     * <li>The caller have to have MANAGER role</li>
     * <li>The subscription can not have any unarchived purchases</li> 
     * </ul>
     *
     * @param id the subscription's ID
     * @throws SecurityViolationException if the caller does not have required roles
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the subscription's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     *
     */
    void removeSubscription(Integer id) throws ValidationException, BusinessException, SecurityViolationException;

    /**
     * Updates a subscription.
     * <p/>
     * 
     * <ul>
     * <li>The caller have to have MANAGER role</li>
     * <li>All properties are required</li>
     * </ul>
     *
     * @param subscription the subscription to update
     * @throws SecurityViolationException if the caller does not have required roles
     * @throws ValidationException if any of the required properties is invalid
     * @throws NullPointerException if subscription or any of the required
     * properties is null
     */
    void updateSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityViolationException;
    
}
