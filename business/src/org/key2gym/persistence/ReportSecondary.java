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
package org.key2gym.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "report_secondary_rps")
public class ReportSecondary implements Serializable {
    
    @Id
    @Column(name = "idrpt_rps")
    private Integer reportId;
            
    @Column(name="secondary_format", nullable = false)
    private String secondaryFormat;
 
    @Basic(fetch= FetchType.LAZY)
    @Column(name="secondary_body", nullable = false)
    @Lob
    private byte[] secondaryBody;

    public Integer getId() {
        return reportId;
    }

    public void setId(Integer id) {
        this.reportId = id;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getSecondaryFormat() {
        return secondaryFormat;
    }

    public void setSecondaryFormat(String secondaryFormat) {
        this.secondaryFormat = secondaryFormat;
    }

    public byte[] getSecondaryBody() {
        return secondaryBody;
    }

    public void setSecondaryBody(byte[] secondaryBody) {
        this.secondaryBody = secondaryBody;
    }
}