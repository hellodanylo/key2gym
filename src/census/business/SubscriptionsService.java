/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.business.api.SecurityException;
import census.business.dto.SubscriptionDTO;
import census.persistence.Item;
import census.persistence.ItemSubscription;
import census.persistence.TimeSplit;
import java.math.BigDecimal;
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
    
    /*
     * Singleton instance
     */
    private static SubscriptionsService instance;

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
     * @throws NullPointerException if subscription or any of the required properties is null
     * @throws IllegalStateException if the session or the transaction is not active
     * @throws SecurityException if current business rules restrict this operation
     */
    public void addSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();
        
        if(!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException("The creation operation is denied.");
        }

        if (subscription == null) {
            throw new NullPointerException("The subscription is null.");
        }

        validateBarcode(subscription.getBarcode(), null);
        validateTitle(subscription.getTitle());
        validateQuantity(subscription.getQuantity());
        validatePrice(subscription.getPrice());

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
        
        if(subscription.getTimeSplitId() == null) {
            throw new NullPointerException("The subscription.getTimeRangeId() is null.");
        }
        TimeSplit timeRange = entityManager.find(TimeSplit.class, subscription.getTimeSplitId());
        
        if(timeRange == null) {
            throw new ValidationException("The time range's ID is invalid");
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
        List<ItemSubscription> itemSubscriptions = entityManager
                .createNamedQuery("ItemSubscription.findAll") //NOI18N
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
     * @throws NullPointerException if subscription or any of the required properties is null
     * @throws IllegalStateException if the session or the transaction is not active
     * @throws SecurityException if current security rules restrict this operation
     */
    public void updateSubscription(SubscriptionDTO subscription) throws ValidationException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();
        
        if(!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException("The update operation is denied.");
        }

        if (subscription == null) {
            throw new NullPointerException("The subscription is null.");
        }

        validateBarcode(subscription.getBarcode(), subscription.getId());
        validateTitle(subscription.getTitle());
        validateQuantity(subscription.getQuantity());
        validatePrice(subscription.getPrice());
        
        if(subscription.getId() == null) {
            throw new NullPointerException("The subscription's ID is null.");
        }
        
        if(entityManager.find(ItemSubscription.class, subscription.getId()) == null) {
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
        
        if(subscription.getTimeSplitId() == null) {
            throw new NullPointerException("The subscription.getTimeRangeId() is null.");
        }
        TimeSplit timeRange = entityManager.find(TimeSplit.class, subscription.getTimeSplitId());
        if(timeRange == null) {
            throw new ValidationException("The time range's ID is invalid");
        }
        
        ItemSubscription entityItemSubscription = new ItemSubscription(
                subscription.getId(),
                subscription.getUnits(),
                subscription.getTermDays(),
                subscription.getTermMonths(),
                subscription.getTermYears());
        entityItemSubscription.setTimeSplit(timeRange);
        entityItemSubscription.setItem(entityItem);
        
        entityItem.setItemSubscription(entityItemSubscription);
        
        // note change
        entityManager.merge(entityItem);
        entityManager.merge(entityItemSubscription);
        entityManager.flush();
    }
    
    /**
     * Removes a subscription.
     * 
     * <ul>
     * 
     * <li> The permissions level has to be PL_ALL.
     * 
     * <li> The subscription can not have any unarchived purchases.
     * 
     * </ul>
     * 
     * @param id the subscription's ID
     * @throws IllegalStateException if the session or the transaction is not active
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the subscription's ID is invalid
     * @throws BusinessException if current business rules restrict this operation
     * @throws SecurityException if current security rules restrict this operation
     * 
     */
    public void removeSubscription(Short id) throws ValidationException, BusinessException, SecurityException {
        assertOpenSessionExists();
        assertTransactionActive();
       
        if(!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException("The removal operation is denied.");
        }
        
        if(id == null) {
            throw new NullPointerException("The id is null.");
        }
        
        ItemSubscription itemSubscription = entityManager.find(ItemSubscription.class, id);
        
        if(itemSubscription == null) {
            throw new ValidationException("The subscription's ID is invalid.");
        }
        
        if(!itemSubscription.getItem().getOrderLines().isEmpty()) {
            throw new BusinessException("The '" 
                    + itemSubscription.getItem().getTitle() 
                    + "' subscription has unarchived purchases. It can not be removed now.");
        }
        
        // TODO: note change
        entityManager.remove(itemSubscription);
        entityManager.flush();
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
                    .setParameter("barcode", value)
                    .getSingleResult();
            
            if(id != null && item.getId().equals(id))
                return;
            
            throw new ValidationException("Another item (" + item.getTitle() + ") has the same barcode.");
        
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
        } else if (value.precision() > 5) {
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
    
    private void validateUnits(Short units) throws ValidationException {
        if(units == null) {
            throw new NullPointerException("The units is null.");
        }
        
        if(units < 0) {
            throw new ValidationException("The units can not be negative.");
        }
    }
    
    private void validateTerm(Short term) throws ValidationException {
        if(term == null) {
            throw new NullPointerException("The term is null.");
        }
        
        if(term < 0) {
            throw new ValidationException("The term can not be negative.");
        }
    }
   
    /**
     * Gets an instance of this class.
     * 
     * @return an instance of this class 
     */
    public static SubscriptionsService getInstance() {
        if(instance == null) {
            instance = new SubscriptionsService();
        }
        return instance;
    }
}
