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

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "session_ssn")
@NamedQueries({
    @NamedQuery(name = "Session.findAll", query = "SELECT s FROM Session s"),
    @NamedQuery(name = "Session.findById", query = "SELECT s FROM Session s WHERE s.id = :id"),
    @NamedQuery(name = "Session.findByDatetimeBegin", query = "SELECT s FROM Session s WHERE s.datetimeBegin = :datetimeBegin"),
    @NamedQuery(name = "Session.findByAdministratorAndDateTimeBeginRange", query = "SELECT s FROM Session s WHERE s.datetimeBegin BETWEEN :rangeBegin AND :rangeEnd AND s.administrator = :administrator"),
    @NamedQuery(name = "Session.findByDatetimeEnd", query = "SELECT s FROM Session s WHERE s.datetimeEnd = :datetimeEnd")})

public class Session implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_ssn")
    private Short id;
    
    @Basic(optional = false)
    @Column(name = "datetime_begin", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeBegin;
    
    @Column(name = "datetime_end", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeEnd;
    
    @JoinColumn(name = "idadm_ssn", referencedColumnName = "id_adm", nullable = false)
    @ManyToOne(optional = false)
    private Administrator administrator;

    public Session() {
    }

    public Session(Short id) {
        this.id = id;
    }

    public Session(Short id, Date datetimeBegin) {
        this.id = id;
        this.datetimeBegin = datetimeBegin;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
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

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
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
        if (!(object instanceof Session)) {
            return false;
        }
        Session other = (Session) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.Session[ idSsn=" + id + " ]";
    }

}
