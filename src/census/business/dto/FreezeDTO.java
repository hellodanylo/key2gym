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
package census.business.dto;

import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FreezeDTO {
    
    public FreezeDTO() {
        
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }
     
    public Short getClientId() {
        return clientId;
    }

    public void setClientId(Short clientId) {
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

    public Short getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Short administratorId) {
        this.administratorId = administratorId;
    }

    public Short getDays() {
        return days;
    }

    public void setDays(Short days) {
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
    
    private Short id;
    private Short clientId;
    private String clientFullName;
    private DateMidnight dateIssued;
    private Short administratorId;
    private String administratorFullName;
    private Short days;
    private String note;
}
