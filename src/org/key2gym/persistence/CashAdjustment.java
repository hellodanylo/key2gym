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
import javax.persistence.*;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "cash_adjustment_cad")
@NamedQueries({
    @NamedQuery(name = "CashAdjustment.findAll", query = "SELECT c FROM CashAdjustment c"),
    @NamedQuery(name = "CashAdjustment.findByDateRecorded", query = "SELECT c FROM CashAdjustment c WHERE c.dateRecorded = :dateRecorded"),
    @NamedQuery(name = "CashAdjustment.findByAmount", query = "SELECT c FROM CashAdjustment c WHERE c.amount = :amount")})
public class CashAdjustment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "date_recorded")
    @Temporal(TemporalType.DATE)
    private Date dateRecorded;
    
    @Basic(optional = false)
    @Column(name = "amount", columnDefinition="DECIMAL(6,2) NOT NULL")
    private BigDecimal amount;
    
    @Column(name = "note", columnDefinition = "TEXT NOT NULL")
    private String note;

    public CashAdjustment() {
    }

    public CashAdjustment(Date dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public CashAdjustment(Date dateRecorded, BigDecimal amount) {
        this.dateRecorded = dateRecorded;
        this.amount = amount;
    }

    public Date getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Date dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dateRecorded != null ? dateRecorded.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CashAdjustment)) {
            return false;
        }
        CashAdjustment other = (CashAdjustment) object;
        if ((this.dateRecorded == null && other.dateRecorded != null) || (this.dateRecorded != null && !this.dateRecorded.equals(other.dateRecorded))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.CashAdjustment[ dateRecorded=" + dateRecorded + " ]";
    }

}
