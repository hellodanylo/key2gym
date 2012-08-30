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

import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FreezeDTO {
    
    public FreezeDTO() {
        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
     
    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public DateMidnight getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(DateMidnight dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getAdministratorFullName() {
        return administratorFullName;
    }

    public void setAdministratorFullName(String administratorFullName) {
        this.administratorFullName = administratorFullName;
    }

    public Integer getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Integer administratorId) {
        this.administratorId = administratorId;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }
    
    private Integer id;
    private Integer clientId;
    private String clientFullName;
    private DateMidnight dateIssued;
    private Integer administratorId;
    private String administratorFullName;
    private Integer days;
    private String note;
}
