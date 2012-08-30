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
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "attendance_atd")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Attendance.findAll", query = "SELECT a FROM Attendance a"),
    @NamedQuery(name = "Attendance.findById", query = "SELECT a FROM Attendance a WHERE a.id = :id"),
    @NamedQuery(name = "Attendance.findAllIdsOrderByIdDesc", query = "SELECT a.id FROM Attendance a ORDER BY a.id DESC"),
    @NamedQuery(name = "Attendance.findByClientOrderByDateTimeBeginDesc", query = "SELECT a FROM Attendance a WHERE a.client = :client ORDER BY a.datetimeBegin DESC"),
    @NamedQuery(name = "Attendance.findByDatetimeBegin", query = "SELECT a FROM Attendance a WHERE a.datetimeBegin = :datetimeBegin"),
    @NamedQuery(name = "Attendance.findByDatetimeEnd", query = "SELECT a FROM Attendance a WHERE a.datetimeEnd = :datetimeEnd"),
    @NamedQuery(name = "Attendance.findByDatetimeBeginRangeOrderByDateTimeBeginDesc", query = "SELECT a FROM Attendance a WHERE a.datetimeBegin BETWEEN :low AND :high ORDER BY a.datetimeBegin DESC"),
    @NamedQuery(name = "Attendance.findOpenByKey", query = "SELECT a FROM Attendance a WHERE a.datetimeEnd = '2004-04-04 09:00:01' AND a.key = :key")})
public class Attendance implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id_atd", columnDefinition="SMALLINT UNSIGNED")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "datetime_begin", columnDefinition="TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeBegin;
    
    @Basic(optional = false)
    @Column(name = "datetime_end", columnDefinition="TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeEnd = DATETIME_END_UNKNOWN;
    
    /**
     * The same as 2004-04-04 09:00:01.
     */
    public static final Date DATETIME_END_UNKNOWN = new Date(1081058401000l);
    
    @JoinColumn(name = "idkey_atd", nullable = false)
    @ManyToOne(optional = false)
    private Key key;

    @JoinColumn(name = "idcln_atd", nullable = true)
    @ManyToOne(optional = true)
    private Client client;
    
    @OneToOne(mappedBy = "attendance")
    private OrderEntity orderEntity;

    public Attendance() {
    }

    public Attendance(Integer idAtd) {
        this.id = idAtd;
    }

    public Attendance(Integer id, Date datetimeBegin, Date datetimeEnd) {
        this.id = id;
        this.datetimeBegin = datetimeBegin;
        this.datetimeEnd = datetimeEnd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDatetimeBegin() {
        return datetimeBegin;
    }

    public void setDatetimeBegin(Date datetimeBegin) {
        this.datetimeBegin = datetimeBegin;
    }

    public Date getDatetimeEnd() {
        return datetimeEnd;
    }

    public void setDatetimeEnd(Date datetimeEnd) {
        this.datetimeEnd = datetimeEnd;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    
        public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public OrderEntity getOrder() {
        return orderEntity;
    }

    public void setOrder(OrderEntity order) {
        this.orderEntity= order;
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
        if (!(object instanceof Attendance)) {
            return false;
        }
        Attendance other = (Attendance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.Attendance[ idAtd=" + id + " ]";
    }    
}
