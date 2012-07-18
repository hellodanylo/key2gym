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
@Table(name = "financial_activity_fna")
@NamedQueries({
    @NamedQuery(name = "FinancialActivity.findAll", query = "SELECT f FROM FinancialActivity f"),
    @NamedQuery(name = "FinancialActivity.findByClientAndDateRecordedRangeOrderByDateRecordedDesc", query = "SELECT f FROM FinancialActivity f WHERE f.client = :client AND f.dateRecorded BETWEEN :rangeBegin AND :rangeEnd ORDER BY f.dateRecorded DESC"),
    @NamedQuery(name = "FinancialActivity.findAllIdsOrderByIdDesc", query = "SELECT f.id FROM FinancialActivity f ORDER BY f.id DESC"),
    @NamedQuery(name = "FinancialActivity.findById", query = "SELECT f FROM FinancialActivity f WHERE f.id = :id"),
    @NamedQuery(name = "FinancialActivity.findByClientAndDateRecorded", query = "SELECT f FROM FinancialActivity f WHERE f.client = :client AND f.dateRecorded = :dateRecorded"),
    @NamedQuery(name = "FinancialActivity.findByAttendance", query = "SELECT f FROM FinancialActivity f WHERE f.attendance = :attendance"),
    @NamedQuery(name = "FinancialActivity.findDefaultByDateRecorded", query = "SELECT f FROM FinancialActivity f WHERE f.attendance IS NULL AND f.client IS NULL AND f.dateRecorded = :dateRecorded"),
    @NamedQuery(name = "FinancialActivity.findByDateRecorded", query = "SELECT f FROM FinancialActivity f WHERE f.dateRecorded = :dateRecorded"),
    @NamedQuery(name = "FinancialActivity.sumPaymentsForDateRecorded", query = "SELECT SUM(f.payment) FROM FinancialActivity f WHERE f.dateRecorded = :dateRecorded")})
public class FinancialActivity implements Serializable {
    
    @Id
    @Basic(optional = false)
    @Column(name = "id_fna")
    private Short id;
    
    @Basic(optional = false)
    @Column(name = "date_recorded")
    @Temporal(TemporalType.DATE)
    private Date dateRecorded;
    
    @Basic(optional = false)
    @Column(name = "payment")
    private BigDecimal payment;
    
    @JoinTable(name = "financial_activity_purchase_fnp", 
        joinColumns = {@JoinColumn(name = "idfna_fnp", referencedColumnName = "id_fna")}, 
        inverseJoinColumns = { @JoinColumn(name = "iditm_fnp", referencedColumnName = "id_itm")})
    @ManyToMany
    private List<Item> items;
    
    @JoinColumn(name = "idcln_fna", referencedColumnName = "id_cln")
    @ManyToOne
    private Client client;
    
    @JoinColumn(name = "idatd_fna", referencedColumnName = "id_atd")
    @OneToOne
    private Attendance attendance;

    public FinancialActivity() {
    }

    public FinancialActivity(Short id) {
        this.id = id;
    }

    public FinancialActivity(Short id, Date dateRecorded, BigDecimal payment) {
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

    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
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
        if (!(object instanceof FinancialActivity)) {
            return false;
        }
        FinancialActivity other = (FinancialActivity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "census.persistence.FinancialActivity[ id=" + id + " ]";
    }
    
}
