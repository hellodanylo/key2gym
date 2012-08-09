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
public class OrderLineDTO {

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Short getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Short discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getDiscountTitle() {
        return discountTitle;
    }

    public void setDiscountTitle(String discountTitle) {
        this.discountTitle = discountTitle;
    }

    public Short getItemId() {
        return itemId;
    }

    public void setItemId(Short itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
   
    Short id;
    Short itemId;
    String itemTitle;
    BigDecimal itemPrice;
    Short quantity;
    String discountTitle;
    Short discountPercent;
    BigDecimal total;
    
    public static final Short FAKE_ID_DEBT = -1;
}
