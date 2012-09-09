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

import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.SubscriptionDTO;
import org.key2gym.persistence.Item;
import org.key2gym.persistence.ItemSubscription;
import org.key2gym.persistence.TimeSplit;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SubscriptionsService extends BusinessService {

    protected SubscriptionsService() {
    }

    /**
     * Adds a subscription.
     *
     * <ul>
     *
     * <li> The permissions level has to be PL_ALL.
     *
     * <li> All properties except ID are required
     *
     * </ul>
     *
     * @param subscription the subscription to add
     * @throws ValidationException if any of the required properties is invalid
     * @throws NullPointerException if subscription or any of the required
     * properties is null
     * @throws IllegalStateException if the session or the transaction is not
     * active
     * @throws SecurityException if current business rules restrict this
     * operation
     */
    public void addSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(MessageFormat.format(
                    strings.getString("Security.Operation.Denied.withName"),
                    strings.getString("Operation.Create")));
        }

        if (subscription == null) {
            throw new NullPointerException("The subscription is null.");
        }

        ItemsService.getInstance().validateBarcode(subscription.getBarcode(), null);
        ItemsService.getInstance().validateTitle(subscription.getTitle());
        ItemsService.getInstance().validateQuantity(subscription.getQuantity());
        ItemsService.getInstance().validatePrice(subscription.getPrice());

        Item entityItem = new Item(
                null,
                subscription.getBarcode(),
                subscription.getTitle(),
                subscription.getQuantity(),
                subscription.getPrice());

        validateTerm(subscription.getTermDays());
        validateTerm(subscription.getTermMonths());
        validateTerm(subscription.getTermYears());
        validateUnits(subscription.getUnits());

        if (subscription.getTimeSplitId() == null) {
            throw new NullPointerException("The subscription.getTimeRangeId() is null.");
        }
        TimeSplit timeRange = entityManager.find(TimeSplit.class, subscription.getTimeSplitId());

        if (timeRange == null) {
            throw new ValidationException(MessageFormat.format(
                    strings.getString("IDInvalid.withName"),
                    strings.getString("ID.TimeSplit")));
        }

        ItemSubscription entityItemSubscription = new ItemSubscription(
                null,
                subscription.getUnits(),
                subscription.getTermDays(),
                subscription.getTermMonths(),
                subscription.getTermYears());
        entityItemSubscription.setTimeSplit(timeRange);

        // note change
        entityManager.persist(entityItem);
        entityManager.flush();

        entityItemSubscription.setId(entityItem.getId());

        entityItemSubscription.setItem(entityItem);
        entityItem.setItemSubscription(entityItemSubscription);

        entityManager.persist(entityItemSubscription);
        entityManager.flush();
    }

    /**
     * Gets all subscriptions.
     *
     * @return a list of subscriptions.
     */
    public List<SubscriptionDTO> getAllSubscriptions() {
        List<SubscriptionDTO> result = new LinkedList<>();
        List<ItemSubscription> itemSubscriptions = entityManager.createNamedQuery("ItemSubscription.findAll") //NOI18N
                .getResultList();

        for (ItemSubscription itemSubscription : itemSubscriptions) {
            SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
            subscriptionDTO.setId(itemSubscription.getId());
            subscriptionDTO.setBarcode(itemSubscription.getItem().getBarcode());
            subscriptionDTO.setPrice(itemSubscription.getItem().getPrice());
            subscriptionDTO.setQuantity(itemSubscription.getItem().getQuantity());
            subscriptionDTO.setTitle(itemSubscription.getItem().getTitle());
            subscriptionDTO.setTermDays(itemSubscription.getTermDays());
            subscriptionDTO.setTermMonths(itemSubscription.getTermMonths());
            subscriptionDTO.setTermYears(itemSubscription.getTermYears());
            subscriptionDTO.setTimeSplitId(itemSubscription.getTimeSplit().getId());
            subscriptionDTO.setUnits(itemSubscription.getUnits());

            result.add(subscriptionDTO);
        }

        return result;
    }

    /**
     * Updates a subscription.
     *
     * <ul>
     *
     * <li> The permissions level has to be PL_ALL.
     *
     * <li> All properties are required
     *
     * </ul>
     *
     * @param subscription the subscription to update
     * @throws ValidationException if any of the required properties is invalid
     * @throws NullPointerException if subscription or any of the required
     * properties is null
     * @throws IllegalStateException if the session or the transaction is not
     * active
     * @throws SecurityException if current security rules restrict this
     * operation
     */
    public void updateSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(MessageFormat.format(
                    strings.getString("Security.Operation.Denied.withName"),
                    strings.getString("Operation.Update")));
        }

        if (subscription == null) {
            throw new NullPointerException("The subscription is null.");
        }

        ItemsService.getInstance().validateBarcode(subscription.getBarcode(), subscription.getId());
        ItemsService.getInstance().validateTitle(subscription.getTitle());
        ItemsService.getInstance().validateQuantity(subscription.getQuantity());
        ItemsService.getInstance().validatePrice(subscription.getPrice());

        if (subscription.getId() == null) {
                        throw new ValidationException(MessageFormat.format(
                    strings.getString("IDInvalid.withName"),
                    strings.getString("ID.Subscription")
            ));
        }

        if (entityManager.find(ItemSubscription.class, subscription.getId()) == null) {
            throw new ValidationException("The subscription's ID is invalid.");
        }

        Item entityItem = new Item(
                subscription.getId(),
                subscription.getBarcode(),
                subscription.getTitle(),
                subscription.getQuantity(),
                subscription.getPrice());

        validateTerm(subscription.getTermDays());
        validateTerm(subscription.getTermMonths());
        validateTerm(subscription.getTermYears());
        validateUnits(subscription.getUnits());

        if (subscription.getTimeSplitId() == null) {
            throw new NullPointerException("The subscription.getTimeRangeId() is null.");
        }
        TimeSplit timeSplit = entityManager.find(TimeSplit.class, subscription.getTimeSplitId());
        if (timeSplit == null) {
            throw new ValidationException(MessageFormat.format(
                    strings.getString("IDInvalid.withName"),
                    strings.getString("ID.TimeSplit")));
        }

        ItemSubscription entityItemSubscription = new ItemSubscription(
                subscription.getId(),
                subscription.getUnits(),
                subscription.getTermDays(),
                subscription.getTermMonths(),
                subscription.getTermYears());
        entityItemSubscription.setTimeSplit(timeSplit);
        entityItemSubscription.setItem(entityItem);

        entityItem.setItemSubscription(entityItemSubscription);

        // note change
        entityManager.merge(entityItem);
        entityManager.merge(entityItemSubscription);
        entityManager.flush();
    }

    /**
     * Removes a subscription. <p>
     *
     * <ul> <li>The permissions level has to be PL_ALL</li> <li>The subscription
     * can not have any unarchived purchases</li> </ul>
     *
     * @param id the subscription's ID
     * @throws IllegalStateException if the session or the transaction is not
     * active
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the subscription's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws SecurityException if current security rules restrict this
     * operation
     *
     */
    public void removeSubscription(Integer id) throws ValidationException, BusinessException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(MessageFormat.format(
                    strings.getString("Security.Operation.Denied.withName"),
                    strings.getString("Operation.Removal")));
        }

        if (id == null) {
            throw new NullPointerException("The id is null.");
        }

        ItemSubscription itemSubscription = entityManager.find(ItemSubscription.class, id);

        if (itemSubscription == null) {
            throw new ValidationException("The subscription's ID is invalid.");
        }

        if (!itemSubscription.getItem().getOrderLines().isEmpty()) {
            throw new BusinessException(MessageFormat.format(
                    strings.getString("BusinessRule.Item.HasUnarchivedPurchases.withItemTitle"),
                    itemSubscription.getItem().getTitle()));
        }

        entityManager.remove(itemSubscription);
        entityManager.flush();
    }

    private void validateUnits(Integer units) throws ValidationException {
        if (units == null) {
            throw new NullPointerException("The units is null.");
        }

        if (units < 0) {
            throw new ValidationException(MessageFormat.format(
                    strings.getString("CanNotBeNegative.withField"),
                    strings.getString("Field.Units")));
        }
    }

    private void validateTerm(Integer term) throws ValidationException {
        if (term == null) {
            throw new NullPointerException("The term is null.");
        }

        if (term < 0) {
            throw new ValidationException(MessageFormat.format(
                    strings.getString("CanNotBeNegative.withField"),
                    strings.getString("Field.Term")));
        }
    }
    /**
     * Singleton instance.
     */
    private static SubscriptionsService instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class
     */
    public static SubscriptionsService getInstance() {
        if (instance == null) {
            instance = new SubscriptionsService();
        }
        return instance;
    }
}
