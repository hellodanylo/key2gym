/*
 * Copyright 2012-2013 Danylo Vashchilenko
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
import org.key2gym.business.entities.Client;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "client_freeze_cfz")
@NamedQueries({
    @NamedQuery(name = "ClientFreeze.findAll", query = "SELECT c FROM ClientFreeze c"),
    @NamedQuery(name = "ClientFreeze.findById", query = "SELECT c FROM ClientFreeze c WHERE c.id = :id"),
    @NamedQuery(name = "ClientFreeze.findByClient", query = "SELECT c FROM ClientFreeze c WHERE c.client = :client"),
    @NamedQuery(name = "ClientFreeze.findByDateIssued", query = "SELECT c FROM ClientFreeze c WHERE c.dateIssued = :dateIssued"),
    @NamedQuery(name = "ClientFreeze.findByDateIssuedRange" , query = "SELECT c FROM ClientFreeze c WHERE c.dateIssued BETWEEN :rangeBegin AND :rangeEnd"),
    @NamedQuery(name = "ClientFreeze.findByClientAndDateIssuedRange" , query = "SELECT c FROM ClientFreeze c WHERE c.client = :client AND c.dateIssued BETWEEN :rangeBegin AND :rangeEnd")})
@SequenceGenerator(name="id_cfz_seq", allocationSize = 1)
public class ClientFreeze implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_cfz", columnDefinition="TINYINT UNSIGNED")
    private Integer id;
            
    @Basic(optional = false)
    @Column(name = "date_issued", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateIssued;
    
    @Basic(optional = false)
    @Column(name = "days", columnDefinition="TINYINT UNSIGNED NOT NULL")
    private Integer days;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "note", columnDefinition="TEXT NOT NULL")
    private String note;
    
    @JoinColumn(name = "idcln_cfz", nullable = false)
    @ManyToOne(optional = false)
    private Client client;
    
    @JoinColumn(name = "idadm_cfz", referencedColumnName = "id_adm", nullable = false)
    @ManyToOne(optional=false)
    private Administrator administrator;
    

    public ClientFreeze() {
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

        public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClientFreeze)) {
            return false;
        }
        ClientFreeze other = (ClientFreeze) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.ClientFreeze[ id=" + id + " ]";
    }
}
