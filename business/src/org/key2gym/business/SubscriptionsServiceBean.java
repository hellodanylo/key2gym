package org.key2gym.business;

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


import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.SubscriptionDTO;
import org.key2gym.business.api.remote.SubscriptionsServiceRemote;
import org.key2gym.persistence.Item;
import org.key2gym.persistence.ItemSubscription;
import org.key2gym.persistence.TimeSplit;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(SubscriptionsServiceRemote.class)
@DeclareRoles({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
@RolesAllowed({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
public class SubscriptionsServiceBean extends BasicBean implements SubscriptionsServiceRemote {

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

        itemsService.validateBarcode(subscription.getBarcode(), null);
        itemsService.validateTitle(subscription.getTitle());
        itemsService.validateQuantity(subscription.getQuantity());
        itemsService.validatePrice(subscription.getPrice());

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
        TimeSplit timeRange = getEntityManager().find(TimeSplit.class, subscription.getTimeSplitId());

        if (timeRange == null) {
            throw new ValidationException(MessageFormat.format(
                    getString("IDInvalid.withName"),
                    getString("ID.TimeSplit")));
        }

        ItemSubscription entityItemSubscription = new ItemSubscription(
                null,
                subscription.getUnits(),
                subscription.getTermDays(),
                subscription.getTermMonths(),
                subscription.getTermYears());
        entityItemSubscription.setTimeSplit(timeRange);

        getEntityManager().persist(entityItem);
        getEntityManager().flush();

        entityItemSubscription.setId(entityItem.getId());

        entityItemSubscription.setItem(entityItem);
        entityItem.setItemSubscription(entityItemSubscription);

        getEntityManager().persist(entityItemSubscription);
        getEntityManager().flush();
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions() {

        List<SubscriptionDTO> result = new LinkedList<SubscriptionDTO>();
        List<ItemSubscription> itemSubscriptions = getEntityManager().createNamedQuery("ItemSubscription.findAll") //NOI18N
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

        itemsService.validateBarcode(subscription.getBarcode(), subscription.getId());
        itemsService.validateTitle(subscription.getTitle());
        itemsService.validateQuantity(subscription.getQuantity());
        itemsService.validatePrice(subscription.getPrice());

        if (subscription.getId() == null) {
            throw new ValidationException(MessageFormat.format(
                    getString("IDInvalid.withName"),
                    getString("ID.Subscription")));
        }

        if (getEntityManager().find(ItemSubscription.class, subscription.getId()) == null) {
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
        TimeSplit timeSplit = getEntityManager().find(TimeSplit.class, subscription.getTimeSplitId());
        if (timeSplit == null) {
            throw new ValidationException(MessageFormat.format(
                    getString("IDInvalid.withName"),
                    getString("ID.TimeSplit")));
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

        getEntityManager().merge(entityItem);
        getEntityManager().merge(entityItemSubscription);
        getEntityManager().flush();
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

        ItemSubscription itemSubscription = getEntityManager().find(ItemSubscription.class, id);

        if (itemSubscription == null) {
            throw new ValidationException("The subscription's ID is invalid.");
        }

        if (!itemSubscription.getItem().getOrderLines().isEmpty()) {
            throw new BusinessException(MessageFormat.format(
                    getString("BusinessRule.Item.HasUnarchivedPurchases.withItemTitle"),
                    itemSubscription.getItem().getTitle()));
        }

        getEntityManager().remove(itemSubscription);
        getEntityManager().flush();
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
    
    @EJB
    private ItemsServiceBean itemsService;

}
