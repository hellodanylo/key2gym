/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.persistence;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author daniel
 */
@Entity
@Table(name = "time_range_tmr")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TimeRange.findAll", query = "SELECT t FROM TimeRange t"),
    @NamedQuery(name = "TimeRange.findById", query = "SELECT t FROM TimeRange t WHERE t.id = :id"),
    @NamedQuery(name = "TimeRange.findByTimeBegin", query = "SELECT t FROM TimeRange t WHERE t.timeBegin = :timeBegin"),
    @NamedQuery(name = "TimeRange.findByTimeEnd", query = "SELECT t FROM TimeRange t WHERE t.timeEnd = :timeEnd")})
public class TimeRange implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tmr")
    private Short id;
    @Basic(optional = false)
    @Column(name = "time_begin")
    @Temporal(TemporalType.TIME)
    private Date timeBegin;
    @Basic(optional = false)
    @Column(name = "time_end")
    @Temporal(TemporalType.TIME)
    private Date timeEnd;
    @OneToMany(mappedBy = "timeRange")
    private List<ItemSubscription> itemSubscriptionList;

    public TimeRange() {
    }

    public TimeRange(Short id) {
        this.id = id;
    }

    public TimeRange(Short id, Date timeBegin, Date timeEnd) {
        this.id = id;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Date getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(Date timeBegin) {
        this.timeBegin = timeBegin;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    @XmlTransient
    public List<ItemSubscription> getItemSubscriptionList() {
        return itemSubscriptionList;
    }

    public void setItemSubscriptionList(List<ItemSubscription> itemSubscriptionList) {
        this.itemSubscriptionList = itemSubscriptionList;
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
        if (!(object instanceof TimeRange)) {
            return false;
        }
        TimeRange other = (TimeRange) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getReadableRepresentation() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(getTimeBegin()) + " - "
                + sdf.format(getTimeEnd());
    }

    @Override
    public String toString() {
        return "census.pu.TimeRange[ id=" + id + " ]";
    }
}
