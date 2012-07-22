/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author daniel
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
public class Key implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_key")
    private Short id;

    @Basic(optional = false)
    @Column(name = "title")
    private String title;
        
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "key")
    private List<Attendance> attendances;
    
    private static final long serialVersionUID = 1L;

    public Key() {
    }

    public Key(Short id, String title) {
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
