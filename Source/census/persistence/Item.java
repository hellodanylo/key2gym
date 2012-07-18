/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author daniel
 */
@Entity
@Table(name = "item_itm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findPure", query = "SELECT i FROM Item i WHERE i.id NOT IN (SELECT s.item.id FROM ItemSubscription s)"),
    @NamedQuery(name = "Item.findAvailable", query = "SELECT i FROM Item i WHERE i.quantity IS NULL OR i.quantity > 0"),
    @NamedQuery(name = "Item.findPureAvailable", query = "SELECT i FROM Item i WHERE (i.quantity is NULL OR i.quantity > 0) AND i.id NOT IN (SELECT s.item.id FROM ItemSubscription s)"),
    @NamedQuery(name = "Item.findById", query = "SELECT i FROM Item i WHERE i.id = :id"),
    @NamedQuery(name = "Item.findByBarcode", query = "SELECT i FROM Item i WHERE i.barcode = :barcode"),
    @NamedQuery(name = "Item.findByQuantity", query = "SELECT i FROM Item i WHERE i.quantity = :quantity"),
    @NamedQuery(name = "Item.findByPrice", query = "SELECT i FROM Item i WHERE i.price = :price")})

public class Item implements Serializable {
    
   private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_itm")
    private Short id;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "title")
    private String title;
    
    @Basic(optional = false)
    @Column(name = "barcode")
    private Long barcode;
    
    @Basic(optional = false)
    @Column(name = "quantity")
    private Short quantity;
    
    @Basic(optional = false)
    @Column(name = "price")
    private BigDecimal price;
    
    @JoinColumn(name="id_itm", referencedColumnName="iditm_its")
    @OneToOne(mappedBy="item", cascade={CascadeType.REMOVE})
    private ItemSubscription itemSubscription;
    
    @JoinTable(name = "financial_activity_purchase_fnp", 
        inverseJoinColumns = {@JoinColumn(name = "idfna_fnp", referencedColumnName = "id_fna")}, 
        joinColumns = { @JoinColumn(name = "iditm_fnp", referencedColumnName = "id_itm")})
    @ManyToMany
    private List<FinancialActivity> financialActivities;
    
    public Item() {
    }

    public Item(Short id) {
        this.id = id;
    }

    public Item(Short id, Long barcode, String title, Short quantity, BigDecimal price) {
        this.id = id;
        this.barcode = barcode;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public ItemSubscription getItemSubscription() {
        return itemSubscription;
    }
    
    public void setItemSubscription(ItemSubscription itemSubscription) {
        this.itemSubscription = itemSubscription;
    }
    
    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    public List<FinancialActivity> getFinancialActivities() {
        return financialActivities;
    }

    public void setFinancialActivities(List<FinancialActivity> financialActivities) {
        this.financialActivities = financialActivities;
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
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "census.pu.Item[ id=" + id + " ]";
    }    
}
