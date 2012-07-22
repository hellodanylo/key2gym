/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author daniel
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
public class OrderEntity implements Serializable {
    
    @Id
    @Basic(optional = false)
    @Column(name = "id_ord")
    private Short id;
    
    @Basic(optional = false)
    @Column(name = "date_recorded")
    @Temporal(TemporalType.DATE)
    private Date dateRecorded;
    
    @Basic(optional = false)
    @Column(name = "payment")
    private BigDecimal payment;
    
    @OneToMany(mappedBy="orderEntity", cascade={CascadeType.ALL})
    private List<OrderLine> orderLines;
    
    @JoinColumn(name = "idcln_ord", referencedColumnName = "id_cln")
    @ManyToOne
    private Client client;
    
    @JoinColumn(name = "idatd_ord", referencedColumnName = "id_atd")
    @OneToOne
    private Attendance attendance;

    public OrderEntity() {
    }

    public OrderEntity(Short id) {
        this.id = id;
    }

    public OrderEntity(Short id, Date dateRecorded, BigDecimal payment) {
        this.id = id;
        this.dateRecorded = dateRecorded;
        this.payment = payment;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
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
        return "census.persistence.OrderEntity[ id=" + id + " ]";
    }
    
}
