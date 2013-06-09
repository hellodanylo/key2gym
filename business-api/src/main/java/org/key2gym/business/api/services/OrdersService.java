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

import java.math.BigDecimal;
import java.util.List;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.OrderDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface OrdersService extends BasicService {

    /**
     * Finds orders by the date.
     * <p>
     *
     * <ul>
     * <li>If the date is not today, the caller has to have MANAGER role</li>
     * </ul>
     *
     * @param date the date
     * @throws SecurityException if the caller does not have required roles
     * @throws EJBException if any of the arguments is null
     */
    List<OrderDTO> findAllByDate(DateMidnight date) throws SecurityViolationException;

    /**
     * Finds an order by the client and the date. 
     * <p/>
     * 
     * If no order can be found, and createIfDoesNotExist is true, an order will be
     * created. Note that an active transaction is required to create an order.
     *
     * @param clientId the ID of the client
     * @param date the date to look up
     * @param createIfDoesNotExist if true, the order will be
     * created, if none is found
     * @throws EJBException if any of the arguments is null
     * @throws ValidationException if the client's ID provided is invalid
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the ID of the order, or null, if none was found and
     * a new one was not requested.
     */
    Integer findByClientIdAndDate(Integer clientId, DateMidnight date, Boolean createIfDoesNotExist) throws SecurityViolationException, ValidationException;

    /**
     * Finds the today's default order. 
     * <p/>
     * 
     * It's used to record all operations that are not associated with anybody or anything. 
     * If the record does not exist, and createIfDoesNotExist is true, it will be
     * created. Note that an active transaction is required to create a new order.
     *
     * @return the ID of the order, or null, if was not found and a
     * new one was not requested.
     * @throws EJBException if the createIfDoesNotExist is null
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    Integer findCurrentDefault(Boolean createIfDoesNotExist) throws SecurityViolationException, ValidationException;

    /**
     * Finds the today's order for the client. 
     * <p/>
     * 
     * If it does not exist, but a valid card was provided, and 
     * createIfDoesNotExist is true, an order will be created. Note that an 
     * active transaction is required to create an order.
     *
     * @param cardId the card of the client
     * @throws EJBException if the card or createIfDoesNotExist is null
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws ValidationException if the card is invalid
     * @return the ID of the order, or null, if none was found and
     * a new one was not requested.
     */
    Integer findCurrentForClientByCard(Integer card, Boolean createIfDoesNotExist) throws SecurityViolationException, ValidationException;

    /**
     * Finds the order associated with the attendance.
     * <p>
     *
     * <ul>
     * <li>The attendance must be anonymous</li>
     * </ul>
     *
     * @param attendanceId the attendance's ID
     * @throws EJBException if the attendance's ID is null
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws ValidationException if the attendance's ID is invalid
     * @throws BusinessException if current business rules resrict this
     * operation
     * @return the ID of the order or null, if none was found and a
     * new one was not requested.
     */
    Integer findForAttendanceById(Integer attendanceId) throws SecurityViolationException, ValidationException, BusinessException;

    /**
     * Finds orders for client within specified time period.
     *
     * @param id the client's ID
     * @param begin the beginning date
     * @param end the ending date
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws EJBException if any of the arguments is null
     * @throws ValidationException if the beginning date is after the ending
     * date, or the client's ID is invalid
     */
    List<OrderDTO> findForClientWithinPeriod(Integer id, DateMidnight begin, DateMidnight end) throws SecurityViolationException, ValidationException;

      /**
     * Records the purchase of the item of given quantity with the discount.
     * <p/>
     *
     * <ul>
     * <li>If the order is closed, the caller has to have MANAGER role</li>
     * <li>If an item subscription is being bought, the order has
     * to be associated with a client</li>
     * <li>The quantity must be positive</li>
     * </ul>
     *
     * @param orderId the order's ID
     * @param itemId the item's ID
     * @param discountId the discount's ID
     * @param quantity the quantity
     * @throws BusinessException if current business rules restrict this operation
     * @throws ValidationException if either of the IDs provided is invalid or the quantity isn't positive
     * @throws SecurityViolationException if the caller does not have required roles
     */
    public void addPurchase(Integer orderId, Integer itemId, Integer discountId, Integer quantity) throws SecurityViolationException, BusinessException, ValidationException;

    /**
     * Increases the recorded payment by the specified amount.
     * <p/>
     *
     * <ul>
     * <li>If the order is closed, the caller has to have MANAGER role</li>
     * <li>If the order is associated with a client, a negative recorded payment
     * is allowed, as long as the client's money balance is not negative</li>
     * </ul>
     *
     * @param orderId the order's ID
     * @param amount the amount to increase the recorded payment by
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws ValidationException if any of the arguments is invalid
     * @throws SecurityViolationException if the caller does not have required roles
     * @throws EJBException if any of the arguments is null
     */
    public void addPayment(Integer orderId, BigDecimal amount) throws BusinessException, ValidationException, SecurityViolationException;

    /**
     * Decreases the quantity of the order line by given quantity.
     * <p/>
     * 
     * The method will delete the order line, if the resulting quantity equals zero.
     *
     * <ul>
     * <li>If the order is not open, the caller has to have MANAGER role</li>
     * <li>If the item's ID is negative, it's forced and can not be removed</li>
     * <li>The subscriptions can not be removed from the orders
     * associated with attendances</li>
     * <li>The quantity must be positive</li>
     * </ul>
     *
     * @param orderLineId the order line's ID whose quantity is to be decreased
     * @param quantity the quantity to decrease by
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws SecurityViolationException if the caller does not have required roles
     * @throws EJBException if either of the arguments provided is null
     * @throws ValidationException if either of the IDs provided is invalid
     */
    public void removePurchase(Integer orderLineId, Integer quantity) throws BusinessException, ValidationException, SecurityViolationException;
    
    /**
     * Finds the order by its ID.
     *
     * @param id the order's ID.
     * @return the order's information.
     * @throws EJBException if the order's ID is null
     * @throws ValidationException if the order's ID is invalid
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    OrderDTO getById(Integer id) throws ValidationException, SecurityViolationException;
    
    /**
     * Returns the amount of payment associated with the order.
     *
     * @param orderId the order's ID
     * @return the amount of payment
     * @throws EJBException if the order's ID is null
     * @throws ValidationException if the order's ID is invalid
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    BigDecimal getPayment(Integer orderId) throws ValidationException;

    /**
     * Gets the sum of all payments received on the date.
     * <p/>
     * 
     * <ul>
     * <li>If the date is not today, the caller has to have MANAGER role</li>
     * </ul>
     *
     * @param date the date
     * @throws SecurityViolationException if the caller does not have required roles
     * @return the sum of all payments
     */
    BigDecimal getTotalForDate(DateMidnight date) throws SecurityViolationException;

}
