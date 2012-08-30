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
@Table(name = "key_key")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Key.findAll", query = "SELECT k FROM Key k"),
    @NamedQuery(name = "Key.findById", query = "SELECT k FROM Key k WHERE k.id = :id"),
    @NamedQuery(name = "Key.findByTitle", query = "SELECT k FROM Key k WHERE k.title = :title"),
    @NamedQuery(name = "Key.findAvailable", query = "SELECT k From Key k WHERE k.id NOT IN (SELECT a.key.id FROM Attendance a WHERE a.datetimeEnd = '2004-04-04 09:00:01')"),
    @NamedQuery(name = "Key.findTaken", query = "SELECT k From Key k WHERE k.id IN (SELECT a.key.id FROM Attendance a WHERE a.datetimeEnd = '2004-04-04 09:00:01')")})
@SequenceGenerator(name="id_key_seq", allocationSize = 1)
public class Key implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_key", columnDefinition="TINYINT UNSIGNED")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "title", columnDefinition="TINYTEXT NOT NULL")
    private String title;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "key")
    private List<Attendance> attendances;

    public Key() {
    }

    public Key(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        if (!(object instanceof Key)) {
            return false;
        }
        Key other = (Key) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "census.pu.Key[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Attendance> getAttendances() {
        return attendances;
    }
}
