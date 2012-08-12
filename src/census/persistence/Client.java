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
package census.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "client_cln")
@NamedQueries({
    @NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c"),
    @NamedQuery(name = "Client.findById", query = "SELECT c FROM Client c WHERE c.id = :id"),
    @NamedQuery(name = "Client.findAllIdsOrderByIdDesc", query = "SELECT c.id FROM Client c ORDER BY c.id DESC"),
    @NamedQuery(name = "Client.findByCard", query = "SELECT c FROM Client c WHERE c.card = :card"),
    @NamedQuery(name = "Client.findByFullNameExact", query = "SELECT c FROM Client c WHERE c.fullName = :fullName"),
    @NamedQuery(name = "Client.findByFullNameNotExact", query = "SELECT c FROM Client c WHERE LOCATE(:fullName, c.fullName) != 0"),
    @NamedQuery(name = "Client.findByRegistrationDate", query = "SELECT c FROM Client c WHERE c.registrationDate = :registrationDate"),
    @NamedQuery(name = "Client.findByAttendancesBalance", query = "SELECT c FROM Client c WHERE c.attendancesBalance = :attendancesBalance"),
    @NamedQuery(name = "Client.findByExpirationDate", query = "SELECT c FROM Client c WHERE c.expirationDate = :expirationDate")})
public class Client {
  
    @Id
    @Basic(optional = false)
    @Column(name = "id_cln")
    private Short id;
        
    @Column(name = "card")
    private Integer card;

    @Basic(optional = false)
    @Column(name = "money_balance")
    private BigDecimal moneyBalance;
    
    @OneToMany(mappedBy = "client")
    private List<OrderEntity> orderEntities;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    private List<Attendance> attendancesList;
       
    @Basic(optional = false)
    @Lob
    @Column(name = "full_name")
    private String fullName;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "note")
    private String note;
    
    @Basic(optional = false)
    @Column(name = "registration_date")
    @Temporal(TemporalType.DATE)
    private Date registrationDate;
    
    @Basic(optional = false)
    @Column(name = "attendances_balance")
    private short attendancesBalance;
    
    @Basic(optional = false)
    @Column(name = "expiration_date")
    @Temporal(TemporalType.DATE)
    private Date expirationDate;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    private List<ClientFreeze> freezes;
    
    @JoinColumn(referencedColumnName="idcln_prf", insertable=false, updatable=false)
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "client", fetch=FetchType.LAZY)
    private ClientProfile clientProfile;

    public Client() {
    }

    public Client(Short id) {
        this.id = id;
    }

    public Client(Short id, Integer card, String fullName, String note, Date registrationDate, Short attendancesBalance, Date expirationDate) {
        this.id = id;
        this.card = card;
        this.fullName = fullName;
        this.note = note;
        this.registrationDate = registrationDate;
        this.attendancesBalance = attendancesBalance;
        this.expirationDate = expirationDate;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
       this.fullName = fullName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Short getAttendancesBalance() {
        return attendancesBalance;
    }

    public void setAttendancesBalance(Short attendancesBalance) {
        this.attendancesBalance = attendancesBalance;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }
    
       public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public List<Attendance> getAttendances() {
        return attendancesList;
    }

    public void setAttendances(List<Attendance> attendancesList) {
        this.attendancesList = attendancesList;
    }

    public Integer getCard() {
        return card;
    }

    public void setCard(Integer card) {
        this.card = card;
    }

    public List<OrderEntity> getFinancialAcitivities() {
        return orderEntities;
    }

    public void setFinancialAcitivities(List<OrderEntity> financialAcitivityCollection) {
        this.orderEntities = financialAcitivityCollection;
    }

    public List<ClientFreeze> getFreezes() {
        return freezes;
    }

    public void setFreezes(List<ClientFreeze> clientFreezes) {
        this.freezes = clientFreezes;
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
        if (!(object instanceof Client)) {
            return false;
        }
        Client other = (Client) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "census.pu.Client[ id=" + id + " ]";
    }
}
