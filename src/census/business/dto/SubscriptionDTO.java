/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business.dto;

import java.math.BigDecimal;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SubscriptionDTO{
    
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

    public Short getTimeRangeId() {
        return timeRangeId;
    }

    public void setTimeRangeId(Short timeRangeId) {
        this.timeRangeId = timeRangeId;
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
    private Short timeRangeId;
}
