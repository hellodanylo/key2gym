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

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getKeyId() {
        return keyId;
    }

    public void setKeyId(Integer keyId) {
        this.keyId = keyId;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyString) {
        this.keyTitle = keyString;
    }
    
    private Integer id;
    private DateTime DateTimeBegin;
    private String clientFullName;
    private Integer clientId;
    private String keyTitle;
    private Integer keyId;
    private DateTime DateTimeEnd;  
}
