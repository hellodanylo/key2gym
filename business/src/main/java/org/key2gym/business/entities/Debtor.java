package org.key2gym.business.entities;

import javax.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "v_debtors")
@NamedQueries({
        @NamedQuery(name = "Debtor.findAll", query = "select d from Debtor d order by d.lastAttendance asc")
})
public class Debtor {
    @Id
    @Column(name = "id_cln")
    protected Integer clientId;

    @Column(name = "full_name")
    protected String fullName;

    @Column(name = "money_balance")
    protected BigDecimal moneyBalance;

    @Column(name = "last_attendance")
    @Temporal(TemporalType.DATE)
    protected Date lastAttendance;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public Date getLastAttendance() {
        return lastAttendance;
    }

    public void setLastAttendance(Date lastAttendance) {
        this.lastAttendance = lastAttendance;
    }
}
