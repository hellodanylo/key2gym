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
public class SubscriptionDTO {
    
    public Short getTermDays() {
        return termDays;
    }

    public void setTermDays(Short termDays) {
        this.termDays = termDays;
    }

    public Short getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Short termMonths) {
        this.termMonths = termMonths;
    }

    public Short getTermYears() {
        return termYears;
    }

    public void setTermYears(Short termYears) {
        this.termYears = termYears;
    }

    public Short getTimeSplitId() {
        return timeSplitId;
    }

    public void setTimeSplitId(Short timeSplitId) {
        this.timeSplitId = timeSplitId;
    }

    public Short getUnits() {
        return units;
    }

    public void setUnits(Short units) {
        this.units = units;
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    private Short id;
    private String title;
    private Long barcode;
    private Short quantity;
    private BigDecimal price;
    private Short units;
    private Short termDays;
    private Short termMonths;
    private Short termYears;
    private Short timeSplitId;
}
