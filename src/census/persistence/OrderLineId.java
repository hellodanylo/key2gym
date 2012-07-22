/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.persistence;

import java.io.Serializable;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OrderLineId implements Serializable {
    private short orderId;
    private short itemId;

    public OrderLineId() {
        
    }
    
    public OrderLineId(short orderId, short itemId) {
        this.orderId = orderId;
        this.itemId = itemId;
    }
     
    @Override
    public int hashCode() {
        return (int)(orderId+itemId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof OrderLineId) {
            OrderLineId orderLine = (OrderLineId)obj;
            return (orderLine.orderId == orderId) && (orderLine.itemId == itemId);
        }
        return false;
    }
    
}
