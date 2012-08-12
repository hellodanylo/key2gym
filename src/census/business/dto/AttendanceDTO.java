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

import org.joda.time.DateTime;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AttendanceDTO {

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public Short getClientId() {
        return clientId;
    }

    public void setClientId(Short clientId) {
        this.clientId = clientId;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public DateTime getDateTimeBegin() {
        return DateTimeBegin;
    }

    public void setDateTimeBegin(DateTime DateTimeBegin) {
        this.DateTimeBegin = DateTimeBegin;
    }

    public DateTime getDateTimeEnd() {
        return DateTimeEnd;
    }

    public void setDateTimeEnd(DateTime DateTimeEnd) {
        this.DateTimeEnd = DateTimeEnd;
    }

    public Short getKeyId() {
        return keyId;
    }

    public void setKeyId(Short keyId) {
        this.keyId = keyId;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyString) {
        this.keyTitle = keyString;
    }
    
    private Short id;
    private DateTime DateTimeBegin;
    private String clientFullName;
    private Short clientId;
    private String keyTitle;
    private Short keyId;
    private DateTime DateTimeEnd;  
}
