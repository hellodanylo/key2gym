package org.key2gym.business.services;

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


import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.persistence.NoResultException;

import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityRoles;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.SubscriptionDTO;
import org.key2gym.business.api.services.ItemsService;
import org.key2gym.business.api.services.SubscriptionsService;
import org.key2gym.business.entities.Item;
import org.key2gym.business.entities.ItemSubscription;
import org.key2gym.business.entities.TimeSplit;
import org.springframework.stereotype.Service;

/**
 * @author Danylo Vashchilenko
 */
@Service("org.key2gym.business.api.services.SubscriptionsService")
@RolesAllowed({SecurityRoles.JUNIOR_ADMINISTRATOR,
        SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER})
public class SubscriptionsServiceBean extends BasicBean implements SubscriptionsService {

    @Override
    public void addSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(MessageFormat.format(
                    getString("Security.Operation.Denied.withName"),
                    getString("Operation.Create")));
        }

        if (subscription == null) {
            throw new NullPointerException("The subscription is null.");
        }

        Item entityItem = new Item();
        entityItem.setBarcode(subscription.getBarcode());

        if (subscription.getBarcode() != null) {
            try {
                em.createNamedQuery("Item.findByBarcode")
                        .setParameter("barcode", subscription.getBarcode())
                        .getSingleResult();

                throw new ValidationException(getString("Invalid.Item.Barcode.AlreadyInUse"));
            } catch (NoResultException ex) {
                // the barcode is unique - fine!
            }
        }

        entityItem.setTitle(subscription.getTitle());
        entityItem.setFrozen(subscription.getFrozen());
        entityItem.setQuantity(subscription.getQuantity());
        entityItem.setPrice(subscription.getPrice());

        validateTerm(subscription.getTermDays());
        validateTerm(subscription.getTermMonths());
        validateTerm(subscription.getTermYears());
        validateUnits(subscription.getUnits());

        if (subscription.getTimeSplitId() == null) {
            throw new NullPointerException("The subscription.getTimeSplitId() is null.");
        }
        TimeSplit timeSplit = em.find(TimeSplit.class, subscription.getTimeSplitId());

        if (timeSplit == null) {
            throw new ValidationException(MessageFormat.format(
                    getString("IDInvalid.withName"),
                    getString("ID.TimeSplit")));
        }

        ItemSubscription entityItemSubscription = new ItemSubscription();
        entityItemSubscription.setUnits(subscription.getUnits());
        entityItemSubscription.setTermDays(subscription.getTermDays());
        entityItemSubscription.setTermMonths(subscription.getTermMonths());
        entityItemSubscription.setTermYears(subscription.getTermYears());
        entityItemSubscription.setTimeSplit(timeSplit);
        entityItemSubscription.setItem(entityItem);

        em.persist(entityItem);
        em.flush();

        entityItemSubscription.setId(entityItem.getId());
        entityItemSubscription.setItem(entityItem);
        entityItem.setItemSubscription(entityItemSubscription);

        em.persist(entityItemSubscription);
        em.flush();
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions() {

        List<SubscriptionDTO> result = new LinkedList<SubscriptionDTO>();
        List<ItemSubscription> itemSubscriptions = em.createNamedQuery("ItemSubscription.findAll") //NOI18N
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
            subscriptionDTO.setFrozen(itemSubscription.getItem().isFrozen());

            result.add(subscriptionDTO);
        }

        return result;
    }

    @Override
    public void updateSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(MessageFormat.format(
                    getString("Security.Operation.Denied.withName"),
                    getString("Operation.Update")));
        }

        if (subscription == null) {
            throw new NullPointerException("The subscription is null.");
        }

        if (subscription.getId() == null) {
            throw new NullPointerException("The subscription.getId() is null.");
        }

        if (em.find(ItemSubscription.class, subscription.getId()) == null) {
            throw new ValidationException("The subscription's ID is invalid.");
        }

        Item entityItem = em.find(Item.class, subscription.getId());
        if (entityItem == null) {
            throw new ValidationException(MessageFormat.format(
                    getString("IDInvalid.withName"),
                    getString("ID.Subscription")));
        }

        if (subscription.getBarcode() != null && !subscription.getBarcode().equals(entityItem.getBarcode())) {
            try {
                em.createNamedQuery("Item.findByBarcode")
                        .setParameter("barcode", subscription.getBarcode())
                        .getSingleResult();

                throw new ValidationException(getString("Invalid.Item.Barcode.AlreadyInUse"));
            } catch (NoResultException ex) {
                // the barcode is unique - fine!
            }
        }

        entityItem.setBarcode(subscription.getBarcode());
        entityItem.setTitle(subscription.getTitle());
        entityItem.setQuantity(subscription.getQuantity());
        entityItem.setPrice(subscription.getPrice());
        entityItem.setFrozen(subscription.getFrozen());

        validateTerm(subscription.getTermDays());
        validateTerm(subscription.getTermMonths());
        validateTerm(subscription.getTermYears());
        validateUnits(subscription.getUnits());

        if (subscription.getTimeSplitId() == null) {
            throw new NullPointerException("The subscription.getTimeSplitId() is null.");
        }
        TimeSplit timeSplit = em.find(TimeSplit.class, subscription.getTimeSplitId());
        if (timeSplit == null) {
            throw new ValidationException(MessageFormat.format(
                    getString("IDInvalid.withName"),
                    getString("ID.TimeSplit")));
        }

        ItemSubscription entityItemSubscription = em.find(ItemSubscription.class, subscription.getId());
        entityItemSubscription.setUnits(subscription.getUnits());
        entityItemSubscription.setTermDays(subscription.getTermDays());
        entityItemSubscription.setTermMonths(subscription.getTermMonths());
        entityItemSubscription.setTermYears(subscription.getTermYears());
        entityItemSubscription.setTimeSplit(timeSplit);
    }

    @Override
    public void removeSubscription(Integer id) throws ValidationException, BusinessException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(MessageFormat.format(
                    getString("Security.Operation.Denied.withName"),
                    getString("Operation.Removal")));
        }

        if (id == null) {
            throw new NullPointerException("The id is null.");
        }

        ItemSubscription itemSubscription = em.find(ItemSubscription.class, id);

        if (itemSubscription == null) {
            throw new ValidationException("The subscription's ID is invalid.");
        }

        if (!itemSubscription.getItem().getOrderLines().isEmpty()) {
            throw new BusinessException(MessageFormat.format(
                    getString("BusinessRule.Item.HasUnarchivedPurchases.withItemTitle"),
                    itemSubscription.getItem().getTitle()));
        }

        em.remove(itemSubscription);
    }

    private void validateUnits(Integer units) throws ValidationException {
        if (units == null) {
            throw new NullPointerException("The units is null.");
        }

        if (units < 0) {
            throw new ValidationException(MessageFormat.format(
                    getString("CanNotBeNegative.withField"),
                    getString("Field.Units")));
        }
    }

    private void validateTerm(Integer term) throws ValidationException {
        if (term == null) {
            throw new NullPointerException("The term is null.");
        }

        if (term < 0) {
            throw new ValidationException(MessageFormat.format(
                    getString("CanNotBeNegative.withField"),
                    getString("Field.Term")));
        }
    }

    // TODO: do proper lookup here
    private ItemsService itemsService;

}
