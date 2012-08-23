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
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "item_subscription_its")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemSubscription.findAll", query = "SELECT i FROM ItemSubscription i"),
    @NamedQuery(name = "ItemSubscription.findById", query = "SELECT i FROM ItemSubscription i WHERE i.id = :id"),
    @NamedQuery(name = "ItemSubscription.findByUnits", query = "SELECT i FROM ItemSubscription i WHERE i.units = :units"),
    @NamedQuery(name = "ItemSubscription.findByTermDays", query = "SELECT i FROM ItemSubscription i WHERE i.termDays = :termDays"),
    @NamedQuery(name = "ItemSubscription.findByTermMonths", query = "SELECT i FROM ItemSubscription i WHERE i.termMonths = :termMonths"),
    @NamedQuery(name = "ItemSubscription.findByTermYears", query = "SELECT i FROM ItemSubscription i WHERE i.termYears = :termYears"),
    @NamedQuery(name = "ItemSubscription.findCasualByTimeSplit", query = "SELECT i FROM ItemSubscription i WHERE i.units = 1 AND i.termDays = 1 AND i.termMonths = 0 AND i.termYears = 0 AND i.timeSplit = :timeSplit"),
    @NamedQuery(name = "ItemSubscription.findByClientOrderByDateRecordedDesc", query="SELECT i FROM ItemSubscription i, OrderLine ol, OrderEntity o WHERE i.item = ol.item AND ol.orderEntity = o AND o.client = :client ORDER BY o.dateRecorded DESC")})

public class ItemSubscription implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "iditm_its")
    private Short id;
    
    @Basic(optional = false)
    @Column(name = "units")
    private short units;
    
    @Basic(optional = false)
    @Column(name = "term_days")
    private short termDays;
    
    @Basic(optional = false)
    @Column(name = "term_months")
    private short termMonths;
    
    @Basic(optional = false)
    @Column(name = "term_years")
    private short termYears;
    
    @JoinColumn(name = "idtsp_its", referencedColumnName = "id_tsp")
    @ManyToOne(optional = false)
    private TimeSplit timeSplit;
    
    @JoinColumn(name = "iditm_its", referencedColumnName = "id_itm", nullable=false, insertable=false, updatable=false)
    @OneToOne(cascade={CascadeType.REMOVE}, optional = false)
    private Item item;

    public ItemSubscription() {
    }

    public ItemSubscription(Short id) {
        this.id = id;
    }

    public ItemSubscription(Short id, short units, short termDays, short termMonthes, short termYears) {
        this.id = id;
        this.units = units;
        this.termDays = termDays;
        this.termMonths = termMonthes;
        this.termYears = termYears;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public short getUnits() {
        return units;
    }

    public void setUnits(short units) {
        this.units = units;
    }

    public short getTermDays() {
        return termDays;
    }

    public void setTermDays(short termDays) {
        this.termDays = termDays;
    }

    public short getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(short termMonths) {
        this.termMonths = termMonths;
    }

    public short getTermYears() {
        return termYears;
    }

    public void setTermYears(short termYears) {
        this.termYears = termYears;
    }

    public TimeSplit getTimeSplit() {
        return timeSplit;
    }

    public void setTimeSplit(TimeSplit timeSplit) {
        this.timeSplit = timeSplit;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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
        if (!(object instanceof ItemSubscription)) {
            return false;
        }
        ItemSubscription other = (ItemSubscription) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.ItemSubscription[ id=" + id + " ]";
    }
    
}
