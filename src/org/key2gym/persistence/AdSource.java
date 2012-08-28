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
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "ad_source_ads")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AdSource.findAll", query = "SELECT a FROM AdSource a"),
    @NamedQuery(name = "AdSource.findById", query = "SELECT a FROM AdSource a WHERE a.id = :id")})
public class AdSource implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_ads", nullable = false)
    private Short id;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "title", nullable = false)
    private String title;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "adSource")
    private List<ClientProfile> clientProfileList;

    public AdSource() {
    }

    public AdSource(Short id) {
        this.id = id;
    }

    public AdSource(Short id, String title) {
        this.id = id;
        this.title = title;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlTransient
    public List<ClientProfile> getClientProfileList() {
        return clientProfileList;
    }

    public void setClientProfileList(List<ClientProfile> clientProfileList) {
        this.clientProfileList = clientProfileList;
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
        if (!(object instanceof AdSource)) {
            return false;
        }
        AdSource other = (AdSource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "census.persistence.AdSource[ id=" + id + " ]";
    }
    
}
