/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.persistence;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@IdClass(OrderLineId.class)
@Table(name = "order_line_orl")
public class OrderLine implements Serializable {
    
    @Id
    @Column(name = "idord_orl")
    private short orderId;
    
    @Id
    @Column(name = "iditm_orl")
    private short itemId;
    
    @Column(name="quantity")
    private short quantity;
    
    @PrimaryKeyJoinColumn(name="idord_orl", referencedColumnName="id_ord")//, insertable=false, updatable=false)
    @ManyToOne
    private OrderEntity orderEntity;
    
    @PrimaryKeyJoinColumn(name="iditm_orl", referencedColumnName="id_itm")//, insertable=false, updatable=false)
    @ManyToOne
    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        itemId = item.getId();
    }

    public OrderEntity getOrder() {
        return orderEntity;
    }

    public void setOrder(OrderEntity order) {
        this.orderEntity = order;
        orderId = order.getId();
    }

    public short getQuantity() {
        return quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }
    
}
