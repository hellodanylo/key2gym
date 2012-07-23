/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.dto;

import java.math.BigDecimal;

/**
 *
 * @author daniel
 */
public class ItemDTO {
    
    public ItemDTO() {
        
    }

    public ItemDTO(Short id, Long barcode, String title, Short quantity, BigDecimal price) {
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

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
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
    private Boolean itemSubscription;
    
    public static final Short FAKE_ID_DEBT = -1;
    public static final Short FAKE_ID_PENALTY = -2;
}
