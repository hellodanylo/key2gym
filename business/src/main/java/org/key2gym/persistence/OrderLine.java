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
package org.key2gym.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.key2gym.business.entities.OrderEntity;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "order_line_orl")
@NamedQueries({
    @NamedQuery(name="OrderLine.findByOrderAndItemAndDiscount", query="SELECT ol FROM OrderLine ol WHERE ol.orderEntity = :order AND ol.item = :item AND ol.discount = :discount"),
    @NamedQuery(name="OrderLine.findByOrderAndItemAndNoDiscount", query="SELECT ol FROM OrderLine ol WHERE ol.orderEntity = :order AND ol.item = :item AND ol.discount IS NULL")
})
@SequenceGenerator(name="id_orl_seq", allocationSize = 1)
public class OrderLine implements Serializable {
    
    @Id
    @Column(name = "id_orl", columnDefinition="SMALLINT UNSIGNED")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
            
    @JoinColumn(name="idord_orl", referencedColumnName="id_ord", nullable = false)//, insertable=false, updatable=false)
    @ManyToOne
    private OrderEntity orderEntity;
    
    @JoinColumn(name="iditm_orl", referencedColumnName="id_itm", nullable = false)//, insertable=false, updatable=false)
    @ManyToOne
    private Item item;
    
    @JoinColumn(name = "iddsc_orl", referencedColumnName = "id_dsc", nullable = true)
    @ManyToOne
    private Discount discount;
    
    @Column(name="quantity", columnDefinition="TINYINT UNSIGNED NULL")
    private Integer quantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public OrderEntity getOrder() {
        return orderEntity;
    }

    public void setOrder(OrderEntity order) {
        this.orderEntity = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
    
    public BigDecimal getTotal() {
        BigDecimal total = item.getPrice().multiply(new BigDecimal(quantity));
        if(discount != null) {
            total = total.divide(new BigDecimal(100));
            total = total.multiply(new BigDecimal(100-discount.getPercent()));
        }
        return total;
    }
}
