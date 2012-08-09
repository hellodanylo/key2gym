/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business;

import census.business.dto.DiscountDTO;
import census.persistence.Discount;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DiscountsService extends BusinessService {
    
    public List<DiscountDTO> findAll() {
        assertOpenSessionExists();
        
        List<Discount> discountEntities = entityManager.createNamedQuery("Discount.findAll")
                .getResultList();
        List<DiscountDTO> discountDTOs = new LinkedList<>();
        
        for(Discount discount : discountEntities) {
            discountDTOs.add(wrapDiscount(discount));
        }
        
        return discountDTOs;
    }
    
    protected DiscountDTO wrapDiscount(Discount discount) {
        DiscountDTO dto = new DiscountDTO();
        dto.setId(discount.getId());
        dto.setPercent(discount.getPercent());
        dto.setTitle(discount.getTitle());
        return dto;
    }
    
    /*
     * Singleton instance.
     */
    private static DiscountsService instance;

    public static DiscountsService getInstance() {
        if (instance == null) {
            instance = new DiscountsService();
        }
        return instance;
    }
}
