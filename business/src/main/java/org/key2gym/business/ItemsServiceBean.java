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

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ItemDTO;
import org.key2gym.business.api.remote.ItemsServiceRemote;
import org.key2gym.persistence.Item;
import org.key2gym.persistence.Property;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(ItemsServiceRemote.class)
public class ItemsServiceBean extends BasicBean implements ItemsServiceRemote {

    @Override
    public void addItem(ItemDTO item) throws ValidationException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (item == null) {
            throw new NullPointerException("The item is null."); //NOI18N
        }

        validateTitle(item.getTitle());
        validateQuantity(item.getQuantity());
        validatePrice(item.getPrice());
        validateBarcode(item.getBarcode(), null);

        Item entityItem = new Item();
	entityItem.setBarcode(item.getBarcode());
	entityItem.setTitle(item.getTitle());
	entityItem.setQuantity(item.getQuantity());
	entityItem.setPrice(item.getPrice());

        entityManager.persist(entityItem);
        entityManager.flush();
    }

    @Override
    public List<ItemDTO> getItemsAvailable() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<ItemDTO> result = new LinkedList<ItemDTO>();
        List<Item> items = entityManager.createNamedQuery("Item.findAvailable") //NOI18N
                .getResultList();

        Property property = entityManager.find(Property.class, "time_range_mismatch_penalty_item_id");
        Integer penaltyItemId = Integer.valueOf(property.getString());

        for (Item item : items) {
            /*
             * Skips the time range mismatch penalty item.
             */
            if (item.getId() == penaltyItemId) {
                continue;
            }

            result.add(wrapItem(item));
        }
        return result;
    }

    @Override
    public List<ItemDTO> getAllItems() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<ItemDTO> result = new LinkedList<ItemDTO>();
        List<Item> items = entityManager.createNamedQuery("Item.findAll").getResultList();  //NOI18N

        for (Item item : items) {
            result.add(wrapItem(item));
        }

        return result;
    }

    @Override
    public List<ItemDTO> getPureItems() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<ItemDTO> result = new LinkedList<ItemDTO>();
        List<Item> items = entityManager.createNamedQuery("Item.findPure") //NOI18N
                .getResultList();

        for (Item item : items) {
            result.add(wrapItem(item));
        }

        return result;
    }

    @Override
    public List<ItemDTO> getPureItemsAvailable() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<ItemDTO> result = new LinkedList<ItemDTO>();
        List<Item> items = entityManager.createNamedQuery("Item.findPureAvailable") //NOI18N
                .getResultList();

        Property property = entityManager.find(Property.class, "time_range_mismatch_penalty_item_id");
        Integer penaltyItemId = Integer.valueOf(property.getString());

        for (Item item : items) {
            /*
             * Skips the time range mismatch penalty item.
             */
            if (item.getId() == penaltyItemId) {
                continue;
            }

            result.add(wrapItem(item));
        }

        return result;
    }

    @Override
    public void updateItem(ItemDTO item) throws ValidationException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (item == null) {
            throw new NullPointerException("The item is null."); //NOI18N
        }

        if (item.getId() == null) {
            throw new NullPointerException("The item's ID is null."); //NOI18N
        }

        validateBarcode(item.getBarcode(), item.getId());
        validateTitle(item.getTitle());
        validateQuantity(item.getQuantity());
        validatePrice(item.getPrice());

        /*
         * Checks the ID.
         */
        if (entityManager.find(Item.class, item.getId()) == null) {
            throw new ValidationException(getString("Invalid.Item.ID"));
        }

        Item entityItem = new Item();
	entityItem.setBarcode(item.getBarcode());
	entityItem.setTitle(item.getTitle());
	entityItem.setQuantity(item.getQuantity());
	entityItem.setPrice(item.getPrice());

        // note change
        entityManager.merge(entityItem);
        entityManager.flush();
    }

    @Override
    public void removeItem(Integer itemId) throws ValidationException, BusinessException, SecurityViolationException {

        if (itemId == null) {
            throw new NullPointerException("The itemId is null."); //NOI18N
        }

        Item item = entityManager.find(Item.class, itemId);

        if (item == null) {
            throw new ValidationException(getString("Invalid.Item.ID"));
        }

        if (item.getItemSubscription() != null) {
            throw new BusinessException(getString("BusinessRule.Item.IsSubscription"));
        }

        if (!item.getOrderLines().isEmpty()) {
            throw new BusinessException(MessageFormat.format(getString("BusinessRule.Item.HasUnarchivedPurchases.withItemTitle"), new Object[]{item.getTitle()}));
        }

        // TODO: note change
        entityManager.remove(item);
        entityManager.flush();
    }

    /**
     * Wraps an item into a DTO.
     *
     * @param item the item to wrap
     * @return the DTO containing the item
     */
    public ItemDTO wrapItem(Item item) {
        ItemDTO result = new ItemDTO();
        result.setId(item.getId());
        result.setBarcode(item.getBarcode());
        result.setItemSubscription(item.getItemSubscription() != null);
        result.setPrice(item.getPrice().setScale(2));
        result.setQuantity(item.getQuantity());
        result.setTitle(item.getTitle());

        return result;
    }

    public void validateBarcode(Long value, Integer id) throws ValidationException {
	if(value == null) {
	    return;
	}

        try {

            Item item = (Item) entityManager.createNamedQuery("Item.findByBarcode") //NOI18N
                    .setParameter("barcode", value) //NOI18N
                    .getSingleResult();

            if (id != null && item.getId() == id) {
                return;
            }

            String message = MessageFormat.format(
                    getString("Invalid.Item.Barcode.AlreadyInUse.withItemTitle"),
                    item.getTitle());
            throw new ValidationException(message);

        } catch (NoResultException ex) {
            return;
        }
    }

    public void validateQuantity(Integer value) throws ValidationException {
        if (value == null) {
            return;
        }


    }

    public void validatePrice(BigDecimal value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The price is null."); //NOI18N
        }

        if (value.scale() > 2) {
            throw new ValidationException(getString("Invalid.Money.TwoDigitsAfterDecimalPointMax"));
        }

        value = value.setScale(2);

        if (value.precision() > 5) {
            throw new ValidationException(getString("Invalid.Money.ThreeDigitsBeforeDecimalPointMax"));
        } else if (value.compareTo(new BigDecimal(0)) < 0) {
            String message = MessageFormat.format(
                    getString("Invalid.Property.CanNotBeNegative.withPropertyName"),
                    getString("Property.Price"));
            throw new ValidationException(message);
        }
    }

    public void validateTitle(String value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The title is null."); //NOI18N
        }
        value = value.trim();
        if (value.isEmpty()) {
            throw new ValidationException(getString("Invalid.Item.Title.CanNotBeEmpty"));
        }
    }
    @PersistenceContext(unitName = "PU")
    private EntityManager entityManager;
}
