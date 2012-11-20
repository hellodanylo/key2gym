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
import org.key2gym.business.api.dtos.ItemDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface ItemsServiceInterface extends BasicInterface {

    /**
     * Adds an item.
     *
     * <ul>
     * <li>The caller must have MANAGER role</li>
     * <li>All properties except ID are required</li>
     * </ul>
     *
     * @param item the item to add
     * @throws ValidationException if any of the required properties is invalid
     * @throws NullPointerException if item or any of the required properties is
     * null
     * @throws SecurityValidationException if the caller does not have MANAGER role
     * operation
     */
    void addItem(ItemDTO item) throws ValidationException, SecurityViolationException;

    /**
     * Gets all items.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the list of all items
     */
    List<ItemDTO> getAllItems() throws SecurityViolationException;

    /**
     * Gets all the items whose quantity is either more than 0 or infinite.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the list of items available
     */
    List<ItemDTO> getItemsAvailable()throws SecurityViolationException;

    /**
     * Gets pure items. Pure item is an item that is not associated with a
     * subscription.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return a list of items
     */
    List<ItemDTO> getPureItems() throws SecurityViolationException;

    /**
     * Gets pure items available in the stock. Pure item is an item that is not
     * associated with a subscription.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return a list of pure items available
     */
    List<ItemDTO> getPureItemsAvailable() throws SecurityViolationException;

    /**
     * Removes the item.
     *
     * <ul>
     * <li>The item can not be a subscription</li>
     * <li>The item can not have any unarchived purchases</li>
     * </ul>
     *
     * @param itemId the item's ID
     * @throws ValidationException if the item's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws SecurityException if the caller does not have MANAGER role
     */
    void removeItem(Integer itemId) throws ValidationException, BusinessException, SecurityViolationException;

    /**
     * Updates the item.
     *
     * <ul>
     * <li>All properties are required</li>
     * </ul>
     *
     * @param item the item to update
     * @throws ValidationException if any of the required properties is invalid
     * @throws NullPointerExceptionf if item or any of the required properties
     * is null
     * @throws SecurityException if the caller does not have MANAGER role
     */
    void updateItem(ItemDTO item) throws ValidationException, SecurityViolationException;
    
}
