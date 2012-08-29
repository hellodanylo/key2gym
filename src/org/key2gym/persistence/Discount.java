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
import java.util.Collection;
import javax.persistence.*;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "discount_dsc")
@NamedQueries({
    @NamedQuery(name = "Discount.findAll", query = "SELECT d FROM Discount d"),
    @NamedQuery(name = "Discount.findById", query = "SELECT d FROM Discount d WHERE d.id = :id"),
    @NamedQuery(name = "Discount.findByPercent", query = "SELECT d FROM Discount d WHERE d.percent = :percent"),
    @NamedQuery(name = "Discount.findByTitle", query = "SELECT d FROM Discount d WHERE d.title = :title")})
public class Discount implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_dsc", columnDefinition="TINYINT UNSIGNED")
    private Short id;
    
    @Basic(optional = false)
    @Column(name = "percent", columnDefinition="TINYINT UNSIGNED NOT NULL")
    private short percent;
    
    @Basic(optional = false)
    @Column(name = "title", columnDefinition="TINYTEXT NOT NULL")
    private String title;
    
    @OneToMany(mappedBy = "discount")
    private Collection<OrderLine> orderLineCollection;

    public Discount() {
    }

    public Discount(Short idDsc) {
        this.id = idDsc;
    }

    public Discount(Short idDsc, short percent, String title) {
        this.id = idDsc;
        this.percent = percent;
        this.title = title;
    }

    public Short getId() {
        return id;
    }

    public void setIdDsc(Short idDsc) {
        this.id = idDsc;
    }

    public short getPercent() {
        return percent;
    }

    public void setPercent(short percent) {
        this.percent = percent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<OrderLine> getOrderLineCollection() {
        return orderLineCollection;
    }

    public void setOrderLineCollection(Collection<OrderLine> orderLineCollection) {
        this.orderLineCollection = orderLineCollection;
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
        if (!(object instanceof Discount)) {
            return false;
        }
        Discount other = (Discount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.Discount[ id=" + id + " ]";
    }
}
