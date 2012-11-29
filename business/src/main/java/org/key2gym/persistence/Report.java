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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "report_rpt")
@NamedQueries({
    @NamedQuery(name="Report.findAll", query = "SELECT r FROM Report r")
})
@SequenceGenerator(name="id_rpt_seq", allocationSize = 1)
public class Report implements Serializable {
    
    @Id
    @Column(name = "id_rpt")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
            
    @Column(name="primary_format", nullable = false)
    private String primaryFormat;
 
    @Column(name="note", nullable = false)
    private String note;
    
    @Column(name = "timestamp_generated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestampGenerated;
    
    @Column(name="title")
    private String title;
    
    @Column(name="report_generator_class")
    private String reportGeneratorClass;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrimaryFormat() {
        return primaryFormat;
    }

    public void setPrimaryFormat(String primaryFormat) {
        this.primaryFormat = primaryFormat;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getTimestampGenerated() {
        return timestampGenerated;
    }

    public void setTimestampGenerated(Date timestampGenerated) {
        this.timestampGenerated = timestampGenerated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReportGeneratorClass() {
        return reportGeneratorClass;
    }

    public void setReportGeneratorClass(String reportGeneratorClass) {
        this.reportGeneratorClass = reportGeneratorClass;
    }
}