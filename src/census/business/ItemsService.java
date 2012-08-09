/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.business.api.SecurityException;
import census.business.dto.ItemDTO;
import census.persistence.Item;
import census.persistence.Property;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author daniel
 */
public class ItemsService extends BusinessService {
    /*
     * Singleton instance
     */
    private static ItemsService instance;
    
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
     * @throws NullPointerException if item or any of the required properties is null
     * @throws SecurityException if current security rules restrict this operation
     */
    public void addItem(ItemDTO item) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();

        if(!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("OperationDenied"));
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
        
        Property property = (Property)entityManager
                .createNamedQuery("Property.findByName")
                .setParameter("name", "time_range_mismatch_penalty_item_id")
                .getSingleResult();
        Short penaltyItemId = Short.valueOf(property.getString());

        for (Item item : items) {
            /*
             * Skips the time range mismatch penalty item.
             */
            if(item.getId().equals(penaltyItemId))
                continue;
            
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
        List<Item> items = entityManager
                .createNamedQuery("Item.findPure") //NOI18N
                .getResultList(); 
               
        for (Item item : items) {            
            result.add(wrapItem(item));
        }

        return result;
    }
    
    /**
     * Gets pure items available in the stock. 
     * Pure item is an item that is not associated with a subscription.
     * 
     * @return a list of pure items available
     */
    public List<ItemDTO> getPureItemsAvailable() {

        
        List<ItemDTO> result = new LinkedList<>();
        List<Item> items = entityManager
                .createNamedQuery("Item.findPureAvailable") //NOI18N
                .getResultList();
        
        Property property = (Property)entityManager
                .createNamedQuery("Property.findByName")
                .setParameter("name", "time_range_mismatch_penalty_item_id")
                .getSingleResult();
        Short penaltyItemId = Short.valueOf(property.getString());

        for (Item item : items) {
            /*
             * Skips the time range mismatch penalty item.
             */
            if(item.getId().equals(penaltyItemId))
                continue;
            
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
     * @throws IllegalStateException if the session or the transaction is not active
     * @throws NullPointerExceptionf if item or any of the required properties is null
     * @throws SecurityException if current security rules restrict this operation 
     */
    public void updateItem(ItemDTO item) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();
        
        if(!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("OperationDenied"));
        }

        if (item == null) {
            throw new NullPointerException("The item is null."); //NOI18N
        }
        
        if(item.getId() == null) {
            throw new NullPointerException("The item's ID is null."); //NOI18N
        }

        validateBarcode(item.getBarcode(), item.getId());
        validateTitle(item.getTitle());
        validateQuantity(item.getQuantity());
        validatePrice(item.getPrice());

        /*
         * Checks the ID.
         */
        if(entityManager.find(Item.class, item.getId()) == null) {
            throw new ValidationException(bundle.getString("ItemIDInvalid"));
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

     * <li> The item can not be a subscription.
     * 
     * <li> The item can not have any unarchived purchases.
     * 
     * </ul>
     * 
     * @param itemId the item's ID
     * @throws ValidationException if the item's ID is invalid
     * @throws BusinessException if current business rules restrict this operation
     * @throws IllegalStateException if the session or the transaction is not active
     * @throws SecurityException if current security rules restrict this operation
     */
    public void removeItem(Short itemId) throws ValidationException, BusinessException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();
        
        if(!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("OperationDenied"));
        }
        
        if(itemId == null) {
            throw new NullPointerException("The itemId is null."); //NOI18N
        }
        
        Item item = entityManager.find(Item.class, itemId);
        
        if(item == null) {
            throw new ValidationException(bundle.getString("ItemIDInvalid"));
        }
        
        if(item.getItemSubscription() != null) {
            throw new BusinessException(bundle.getString("ItemIsSubscription"));
        }
        
        if(!item.getOrderLines().isEmpty()) {
            throw new BusinessException(MessageFormat.format(bundle.getString("ItemHasUnarchivedPurchases"), new Object[] {item.getTitle()}));
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
    private ItemDTO wrapItem(Item item) {
        ItemDTO result = new ItemDTO();
        result.setId(item.getId());
        result.setBarcode(item.getBarcode());
        result.setItemSubscription(item.getItemSubscription() != null);
        result.setPrice(item.getPrice().setScale(2));
        result.setQuantity(item.getQuantity());
        result.setTitle(item.getTitle());
        
        return result;
    }

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

    private void validateBarcode(Long value, Short id) throws ValidationException {
        if (value == null) {
            return;
        }
        if (value < 0) {
            throw new ValidationException(bundle.getString("BarcodeCanNotBeNegative"));
        }
        
        try {
            
            Item item = (Item) entityManager.createNamedQuery("Item.findByBarcode") //NOI18N
                    .setParameter("barcode", value) //NOI18N
                    .getSingleResult();
            
            if(id != null && item.getId().equals(id))
                return;
            
            throw new ValidationException(MessageFormat.format(bundle.getString("AnotherItemHasSameBarcode"), new Object[] {item.getTitle()}));
        
        } catch(NoResultException ex) {
            return;
        }
    }

    private void validateQuantity(Short value) throws ValidationException {
        if (value == null) {
            return;
        }

        if (value < 0) {
            throw new ValidationException(bundle.getString("QuantityCanNotBeNegative"));
        } else if (value > 255) {
            throw new ValidationException(bundle.getString("QuantityOverLimit"));
        }
    }

    private void validatePrice(BigDecimal value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The price is null."); //NOI18N
        }

        if (value.scale() > 2) {
            throw new ValidationException(bundle.getString("TwoDigitsAfterDecimalPointMax"));
        }
        
        value = value.setScale(2);
        
        if (value.precision() > 5) {
            throw new ValidationException(bundle.getString("ThreeDigitsBeforeDecimalPointMax"));
        } else if (value.compareTo(new BigDecimal(0)) < 0) {
            throw new ValidationException(bundle.getString("PriceCanNotBeNegative"));
        }
    }

    private void validateTitle(String value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The title is null."); //NOI18N
        }
        value = value.trim();
        if (value.isEmpty()) {
            throw new ValidationException(bundle.getString("TitleCanNotBeNegative"));
        }
    }
}
