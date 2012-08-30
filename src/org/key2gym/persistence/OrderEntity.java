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
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "order_ord")
@NamedQueries({
    @NamedQuery(name = "OrderEntity.findAll", query = "SELECT o FROM OrderEntity o"),
    @NamedQuery(name = "OrderEntity.findByClientAndDateRecordedRangeOrderByDateRecordedDesc", query = "SELECT o FROM OrderEntity o WHERE o.client = :client AND o.dateRecorded BETWEEN :rangeBegin AND :rangeEnd ORDER BY o.dateRecorded DESC"),
    @NamedQuery(name = "OrderEntity.findAllIdsOrderByIdDesc", query = "SELECT o.id FROM OrderEntity o ORDER BY o.id DESC"),
    @NamedQuery(name = "OrderEntity.findById", query = "SELECT o FROM OrderEntity o WHERE o.id = :id"),
    @NamedQuery(name = "OrderEntity.findByClientAndDateRecorded", query = "SELECT o FROM OrderEntity o WHERE o.client = :client AND o.dateRecorded = :dateRecorded"),
    @NamedQuery(name = "OrderEntity.findByAttendance", query = "SELECT o FROM OrderEntity o WHERE o.attendance = :attendance"),
    @NamedQuery(name = "OrderEntity.findDefaultByDateRecorded", query = "SELECT o FROM OrderEntity o WHERE o.attendance IS NULL AND o.client IS NULL AND o.dateRecorded = :dateRecorded"),
    @NamedQuery(name = "OrderEntity.findByDateRecordedOrderByIdDesc", query = "SELECT o FROM OrderEntity o WHERE o.dateRecorded = :dateRecorded ORDER BY o.id DESC"),
    @NamedQuery(name = "OrderEntity.sumPaymentsForDateRecorded", query = "SELECT SUM(o.payment) FROM OrderEntity o WHERE o.dateRecorded = :dateRecorded")})
@SequenceGenerator(name="id_orf_seq", allocationSize = 1)
public class OrderEntity implements Serializable {
    
    @Id
    @Basic(optional = false)
    @Column(name = "id_ord", columnDefinition="SMALLINT UNSIGNED")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "date_recorded", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateRecorded;
    
    @Basic(optional = false)
    @Column(name = "payment", nullable = false, precision = 6, scale = 2)
    private BigDecimal payment;
    
    @OneToMany(mappedBy="orderEntity", cascade={CascadeType.ALL})
    private List<OrderLine> orderLines;
    
    @JoinColumn(name = "idcln_ord", referencedColumnName = "id_cln", nullable = true)
    @ManyToOne
    private Client client;
    
    @JoinColumn(name = "idatd_ord", referencedColumnName = "id_atd", nullable = true)
    @OneToOne
    private Attendance attendance;

    public OrderEntity() {
    }

    public OrderEntity(Integer id) {
        this.id = id;
    }

    public OrderEntity(Integer id, Date dateRecorded, BigDecimal payment) {
        this.id = id;
        this.dateRecorded = dateRecorded;
        this.payment = payment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return dateRecorded;
    }

    public void setDate(Date date) {
        this.dateRecorded = date;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }
    
    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
    
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        
        for(OrderLine orderLine : orderLines) { 
            total = total.add(orderLine.getTotal());
        }
        
        return total;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderEntity)) {
            return false;
        }
        OrderEntity other = (OrderEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.OrderEntity[ id=" + id + " ]";
    }
    
}
