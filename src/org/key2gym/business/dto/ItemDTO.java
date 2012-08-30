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
package org.key2gym.business.dto;

import java.math.BigDecimal;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemDTO {
    
    public ItemDTO() {
        
    }

    public ItemDTO(Integer id, Long barcode, String title, Integer quantity, BigDecimal price) {
        this.id = id;
        this.barcode = barcode;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isItemSubscription() {
        return itemSubscription;
    }

    public void setItemSubscription(Boolean itemSubscription) {
        this.itemSubscription = itemSubscription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    private Integer id;
    private String title;
    private Long barcode;
    private Integer quantity;
    private BigDecimal price;
    private Boolean itemSubscription;
}
