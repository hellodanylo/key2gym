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
package org.key2gym.business.api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SubscriptionDTO implements Serializable {
    
    public Integer getTermDays() {
        return termDays;
    }

    public void setTermDays(Integer termDays) {
        this.termDays = termDays;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public Integer getTermYears() {
        return termYears;
    }

    public void setTermYears(Integer termYears) {
        this.termYears = termYears;
    }

    public Integer getTimeSplitId() {
        return timeSplitId;
    }

    public void setTimeSplitId(Integer timeSplitId) {
        this.timeSplitId = timeSplitId;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
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
    private Integer units;
    private Integer termDays;
    private Integer termMonths;
    private Integer termYears;
    private Integer timeSplitId;
}
