/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business.dto;

import java.math.BigDecimal;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CashAdjustmentDTO {

    public CashAdjustmentDTO() {
    }
    

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public DateMidnight getDate() {
        return date;
    }

    public void setDate(DateMidnight date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    

    private DateMidnight date;
    private BigDecimal amount;
    private String note;
}
