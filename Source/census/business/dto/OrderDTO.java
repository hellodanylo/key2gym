/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.dto;

import java.math.BigDecimal;
import java.util.List;
import org.joda.time.DateMidnight;

/**
 *
 * @author daniel
 */
public class OrderDTO {
    
    public OrderDTO() {
        
    }

    public OrderDTO(Short id, DateMidnight date, BigDecimal payment) {
        this.id = id;
        this.date = date;
        this.payment = payment;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setDue(BigDecimal due) {
        this.due = due;
    }

    public BigDecimal getDue() {
        return due;
    }

    public void setDate(DateMidnight date) {
        this.date = date;
    }

    public DateMidnight getDate() {
        return date;
    }

    public Short getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Short attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Short getClientId() {
        return clientId;
    }

    public void setClientId(Short clientId) {
        this.clientId = clientId;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }
    

    private Short id;
    private BigDecimal payment;
    private BigDecimal total;
    private BigDecimal due;
    private List<ItemDTO> items;
    private DateMidnight date;
    private Short clientId;
    private String clientFullName;
    private Short attendanceId;
    private String keyTitle;
}
