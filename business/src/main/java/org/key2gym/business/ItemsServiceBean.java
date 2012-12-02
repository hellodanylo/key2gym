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
import org.key2gym.business.api.*;
import org.key2gym.business.api.dtos.ItemDTO;
import org.key2gym.business.api.remote.ItemsServiceRemote;
import org.key2gym.persistence.*;
import org.key2gym.business.entities.*;

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

        Item entityItem = new Item();
	entityItem.setBarcode(item.getBarcode(),
			      em.createNamedQuery("Item.getAllBarcodes").getResultList());
	entityItem.setTitle(item.getTitle());
	entityItem.setQuantity(item.getQuantity());
	entityItem.setPrice(item.getPrice());

        em.persist(entityItem);
        em.flush();
    }

    @Override
    public List<ItemDTO> getItemsAvailable() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<ItemDTO> result = new LinkedList<ItemDTO>();
        List<Item> items = em.createNamedQuery("Item.findAvailable") //NOI18N
                .getResultList();

        Property property = em.find(Property.class, "time_range_mismatch_penalty_item_id");
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
        List<Item> items = em.createNamedQuery("Item.findAll").getResultList();  //NOI18N

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
        List<Item> items = em.createNamedQuery("Item.findPure") //NOI18N
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
        List<Item> items = em.createNamedQuery("Item.findPureAvailable") //NOI18N
                .getResultList();

        Property property = em.find(Property.class, "time_range_mismatch_penalty_item_id");
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

        Item entityItem = em.find(Item.class, item.getId());

        if (entityItem == null) {
            throw new ValidationException(getString("Invalid.Item.ID"));
        }

	entityItem.setBarcode(item.getBarcode(),
			      em.createNamedQuery("Item.getAllBarcodes").getResultList());
	entityItem.setTitle(item.getTitle());
	entityItem.setQuantity(item.getQuantity());
	entityItem.setPrice(item.getPrice());
    }

    @Override
    public void removeItem(Integer itemId) throws ValidationException, BusinessException, SecurityViolationException {

        if (itemId == null) {
            throw new NullPointerException("The itemId is null."); //NOI18N
        }

        Item item = em.find(Item.class, itemId);

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
        em.remove(item);
        em.flush();
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
}
