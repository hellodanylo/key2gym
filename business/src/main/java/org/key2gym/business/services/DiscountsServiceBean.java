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



import java.util.LinkedList;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.key2gym.business.api.SecurityRoles;
import org.key2gym.business.api.dtos.DiscountDTO;
import org.key2gym.business.api.services.DiscountsService;
import org.key2gym.persistence.Discount;
import org.springframework.stereotype.Service;

/**
 *
 * @author Danylo Vashchilenko
 */
@Service("org.key2gym.business.api.services.DiscountsService")
@RolesAllowed({ SecurityRoles.JUNIOR_ADMINISTRATOR,
	SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER })
public class DiscountsServiceBean extends BasicBean implements DiscountsService {
    
    @Override
    public List<DiscountDTO> findAll() {
       
        List<Discount> discountEntities = getEntityManager().createNamedQuery("Discount.findAll")
                .getResultList();
        List<DiscountDTO> discountDTOs = new LinkedList<DiscountDTO>();
        
        for(Discount discount : discountEntities) {
            discountDTOs.add(wrapDiscount(discount));
        }
        
        return discountDTOs;
    }
    
    public DiscountDTO wrapDiscount(Discount discount) {
        DiscountDTO dto = new DiscountDTO();
        dto.setId(discount.getId());
        dto.setPercent(discount.getPercent());
        dto.setTitle(discount.getTitle());
        return dto;
    }
}
