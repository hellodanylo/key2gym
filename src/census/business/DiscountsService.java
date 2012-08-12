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
    
    /**
     * Finds all discounts.
     * 
     * @return a list of all discounts 
     */
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
    
    /**
     * Singleton instance.
     */
    private static DiscountsService instance;

    /**
     * Gets an instance of this class.
     * 
     * @return an instance of this class 
     */
    public static DiscountsService getInstance() {
        if (instance == null) {
            instance = new DiscountsService();
        }
        return instance;
    }
}
