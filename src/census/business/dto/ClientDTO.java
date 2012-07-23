/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.dto;

import java.math.BigDecimal;
import org.joda.time.DateMidnight;

/**
 *
 * @author daniel
 */
public class ClientDTO {
    
    public ClientDTO() {
        
    }

    public ClientDTO(Short id, String fullName, Integer card, DateMidnight registrationDate, BigDecimal moneyBalance, Short attendancesBalance, DateMidnight expirationDate, String note) {
        this.id = id;
        this.fullName = fullName;
        this.card = card;
        this.registrationDate = registrationDate;
        this.moneyBalance = moneyBalance;
        this.attendancesBalance = attendancesBalance;
        this.expirationDate = expirationDate;
        this.note = note;
    }
  
    public Integer getCard() {
        return card;
    }

    public void setCard(Integer card) {
        this.card = card;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Short getAttendancesBalance() {
        return attendancesBalance;
    }

    public void setAttendancesBalance(Short attendancesBalance) {
        this.attendancesBalance = attendancesBalance;
    }

    public DateMidnight getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(DateMidnight expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public DateMidnight getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(DateMidnight registrationDate) {
        this.registrationDate = registrationDate;
    }
    private Short id;
    private String fullName;
    private DateMidnight registrationDate;
    private BigDecimal moneyBalance;
    private Short attendancesBalance;
    private DateMidnight expirationDate;
    private String note;
    private Integer card;
}
