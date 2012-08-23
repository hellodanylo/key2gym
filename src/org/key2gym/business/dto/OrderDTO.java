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
package org.key2gym.business.dto;

import java.math.BigDecimal;
import java.util.List;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OrderDTO {
    
    public OrderDTO() {
        
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

    public List<OrderLineDTO> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineDTO> orderLines) {
        this.orderLines = orderLines;
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

    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }
    
    private Short id;
    private BigDecimal payment;
    private BigDecimal total;
    private BigDecimal due;
    private List<OrderLineDTO> orderLines;
    private DateMidnight date;
    private Short clientId;
    private String clientFullName;
    private Short attendanceId;
    private String keyTitle;
    private BigDecimal moneyBalance;
}
