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

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "administrator_adm")
@NamedQueries({
    @NamedQuery(name = "Administrator.findAll", query = "SELECT a FROM Administrator a"),
    @NamedQuery(name = "Administrator.findById", query = "SELECT a FROM Administrator a WHERE a.id = :id"),
    @NamedQuery(name = "Administrator.findByUsername", query = "SELECT a FROM Administrator a WHERE a.username = :username"),
    @NamedQuery(name = "Administrator.findByUsernameAndPassword", query = "SELECT a FROM Administrator a WHERE a.username = :username AND a.password = :password")})
@SequenceGenerator(name="id_adm_seq", allocationSize = 1)
public class Administrator implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional=false)
    @Column(name = "id_adm", columnDefinition="TINYINT UNSIGNED NOT NULL")
    private Integer id;
    
    @Basic(optional=false)
    @Lob
    @Column(name = "username", columnDefinition="TINYTEXT NOT NULL")
    private String username;
    
    @Basic(optional=false)
    @Lob
    @Column(name = "full_name", columnDefinition="TINYTEXT NOT NULL")
    private String fullName;
    
    @Basic(optional=false)
    @Lob
    @Column(name = "password", columnDefinition="TINYTEXT NOT NULL")
    private String password;
    
    @Basic(optional=false)
    @Lob
    @Column(name = "address", columnDefinition="TINYTEXT NOT NULL")
    private String address;
    
    @Basic(optional=false)
    @Lob
    @Column(name = "telephone", columnDefinition="TINYTEXT NOT NULL")
    private String telephone;
    
    @Basic(optional=false)
    @Lob
    @Column(name = "note", columnDefinition="TEXT NOT NULL")
    private String note;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "administrator")
    private List<Session> sessionsList;

    public Administrator() {
    }

    public Administrator(Integer id) {
        this.id = id;
    }

    public Administrator(Integer id, String username, String fullName, String password, String address, String telephone, String note) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.address = address;
        this.telephone = telephone;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public List<Session> getSessions() {
        return sessionsList;
    }

    public void setSessions(List<Session> sessionList) {
        this.sessionsList = sessionList;
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
        if (!(object instanceof Administrator)) {
            return false;
        }
        Administrator other = (Administrator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.Administrator[ id=" + id + " ]";
    }
}
