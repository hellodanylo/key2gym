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
import org.key2gym.business.dto.ItemDTO;
import org.key2gym.persistence.Item;
import org.key2gym.persistence.Property;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemsService extends BusinessService {

    protected ItemsService() {
    }

    /**
     * Adds an item.
     *
     * <ul>
     *
     * <li> The permissions level has to be PL_ALL.
     *
     * <li> All properties except ID are required.
     *
     * <li>
     *
     * @param item the item to add.
     * @throws ValidationException if any of the required properties is invalid
     * @throws IllegalStateException if the session or the transaction is not
     * active
     * @throws NullPointerException if item or any of the required properties is
     * null
     * @throws SecurityException if current security rules restrict this
     * operation
     */
    public void addItem(ItemDTO item) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("Security.Operation.Denied"));
        }

        if (item == null) {
            throw new NullPointerException("The item is null."); //NOI18N
        }

        validateTitle(item.getTitle());
        validateQuantity(item.getQuantity());
        validatePrice(item.getPrice());
        validateBarcode(item.getBarcode(), null);

        /*
         * The persistence layer will generate an ID.
         */
        item.setId(null);

        Item entityItem = new Item(
                item.getId(),
                item.getBarcode(),
                item.getTitle(),
                item.getQuantity(),
                item.getPrice());

        // note change
        entityManager.persist(entityItem);
        entityManager.flush();
    }

    /**
     * Gets all the items whose quantity is either more than 0 or infinite.
     *
     * @throws IllegalStateException if no session is open
     * @return the list of items available
     */
    public List<ItemDTO> getItemsAvailable() {
        assertOpenSessionExists();

        List<ItemDTO> result = new LinkedList<>();
        List<Item> items = entityManager.createNamedQuery("Item.findAvailable") //NOI18N
                .getResultList();

        Property property = entityManager.find(Property.class, "time_range_mismatch_penalty_item_id");
        Integer penaltyItemId = Integer.valueOf(property.getString());

        for (Item item : items) {
            /*
             * Skips the time range mismatch penalty item.
             */
            if (item.getId().equals(penaltyItemId)) {
                continue;
            }

            result.add(wrapItem(item));
        }
        return result;
    }

    /**
     * Gets all items.
     *
     * @return the list of all items
     */
    public List<ItemDTO> getAllItems() {

        List<ItemDTO> result = new LinkedList<>();
        List<Item> items = entityManager.createNamedQuery("Item.findAll").getResultList();  //NOI18N

        for (Item item : items) {
            result.add(wrapItem(item));
        }

        return result;
    }

    /**
     * Gets pure items. Pure item is an item that is not associated with a
     * subscription.
     *
     * @return a list of items
     */
    public List<ItemDTO> getPureItems() {


        List<ItemDTO> result = new LinkedList<>();
        List<Item> items = entityManager.createNamedQuery("Item.findPure") //NOI18N
                .getResultList();

        for (Item item : items) {
            result.add(wrapItem(item));
        }

        return result;
    }

    /**
     * Gets pure items available in the stock. Pure item is an item that is not
     * associated with a subscription.
     *
     * @return a list of pure items available
     */
    public List<ItemDTO> getPureItemsAvailable() {


        List<ItemDTO> result = new LinkedList<>();
        List<Item> items = entityManager.createNamedQuery("Item.findPureAvailable") //NOI18N
                .getResultList();

        Property property = entityManager.find(Property.class, "time_range_mismatch_penalty_item_id");
        Integer penaltyItemId = Integer.valueOf(property.getString());

        for (Item item : items) {
            /*
             * Skips the time range mismatch penalty item.
             */
            if (item.getId().equals(penaltyItemId)) {
                continue;
            }

            result.add(wrapItem(item));
        }

        return result;
    }

    /**
     * Updates the item.
     *
     * <ul>
     *
     * <li> The permissions level has to be PL_ALL.
     *
     * <li> All properties are required
     *
     * </ul>
     *
     * @param item the item to update
     * @throws ValidationException if any of the required properties is invalid
     * @throws IllegalStateException if the session or the transaction is not
     * active
     * @throws NullPointerExceptionf if item or any of the required properties
     * is null
     * @throws SecurityException if current security rules restrict this
     * operation
     */
    public void updateItem(ItemDTO item) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("Security.Operation.Denied"));
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
            throw new ValidationException(bundle.getString("Invalid.Item.ID"));
        }

        Item entityItem = new Item(
                item.getId(),
                item.getBarcode(),
                item.getTitle(),
                item.getQuantity(),
                item.getPrice());

        // note change
        entityManager.merge(entityItem);
        entityManager.flush();
    }

    /**
     * Removes the item.
     *
     * <ul>
     *
     * <li> The permissions level has to be PL_ALL.
     *
     * <li> The item can not be a subscription.
     *
     * <li> The item can not have any unarchived purchases.
     *
     * </ul>
     *
     * @param itemId the item's ID
     * @throws ValidationException if the item's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws IllegalStateException if the session or the transaction is not
     * active
     * @throws SecurityException if current security rules restrict this
     * operation
     */
    public void removeItem(Integer itemId) throws ValidationException, BusinessException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("Security.Operation.Denied"));
        }

        if (itemId == null) {
            throw new NullPointerException("The itemId is null."); //NOI18N
        }

        Item item = entityManager.find(Item.class, itemId);

        if (item == null) {
            throw new ValidationException(bundle.getString("Invalid.Item.ID"));
        }

        if (item.getItemSubscription() != null) {
            throw new BusinessException(bundle.getString("BusinessRule.Item.IsSubscription"));
        }

        if (!item.getOrderLines().isEmpty()) {
            throw new BusinessException(MessageFormat.format(bundle.getString("BusinessRule.Item.HasUnarchivedPurchases.withItemTitle"), new Object[]{item.getTitle()}));
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
    ItemDTO wrapItem(Item item) {
        ItemDTO result = new ItemDTO();
        result.setId(item.getId());
        result.setBarcode(item.getBarcode());
        result.setItemSubscription(item.getItemSubscription() != null);
        result.setPrice(item.getPrice().setScale(2));
        result.setQuantity(item.getQuantity());
        result.setTitle(item.getTitle());

        return result;
    }

    void validateBarcode(Long value, Integer id) throws ValidationException {
        if (value == null) {
            return;
        }
        if (value < 0) {
            String message = MessageFormat.format(
                    bundle.getString("Invalid.Property.CanNotBeNegative.withPropertyName"),
                    bundle.getString("Property.Barcode"));
            throw new ValidationException(message);
        }

        try {

            Item item = (Item) entityManager.createNamedQuery("Item.findByBarcode") //NOI18N
                    .setParameter("barcode", value) //NOI18N
                    .getSingleResult();

            if (id != null && item.getId().equals(id)) {
                return;
            }

            String message = MessageFormat.format(
                    bundle.getString("Invalid.Item.Barcode.AlreadyInUse.withItemTitle"),
                    item.getTitle());
            throw new ValidationException(message);

        } catch (NoResultException ex) {
            return;
        }
    }

    void validateQuantity(Integer value) throws ValidationException {
        if (value == null) {
            return;
        }

        if (value < 0) {
            String message = MessageFormat.format(
                    bundle.getString("Invalid.Property.CanNotBeNegative.withPropertyName"),
                    bundle.getString("Property.Quantity"));
            throw new ValidationException(message);
        } else if (value > 255) {
            String message = MessageFormat.format(
                    bundle.getString("Invalid.Property.OverLimit.withPropertyName"),
                    bundle.getString("Property.Quantity"));
            throw new ValidationException(message);
        }
    }

    void validatePrice(BigDecimal value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The price is null."); //NOI18N
        }

        if (value.scale() > 2) {
            throw new ValidationException(bundle.getString("Invalid.Money.TwoDigitsAfterDecimalPointMax"));
        }

        value = value.setScale(2);

        if (value.precision() > 5) {
            throw new ValidationException(bundle.getString("Invalid.Money.ThreeDigitsBeforeDecimalPointMax"));
        } else if (value.compareTo(new BigDecimal(0)) < 0) {
            String message = MessageFormat.format(
                    bundle.getString("Invalid.Property.CanNotBeNegative.withPropertyName"),
                    bundle.getString("Property.Price"));
            throw new ValidationException(message);
        }
    }

    void validateTitle(String value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The title is null."); //NOI18N
        }
        value = value.trim();
        if (value.isEmpty()) {
            throw new ValidationException(bundle.getString("Invalid.Item.Title.CanNotBeEmpty"));
        }
    }
    /**
     * Singleton instance.
     */
    private static ItemsService instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance
     */
    public static ItemsService getInstance() {
        if (instance == null) {
            instance = new ItemsService();
        }
        return instance;
    }
}
