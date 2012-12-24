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
package org.key2gym.business.api.dtos;

import java.io.Serializable;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ReportDTO implements Serializable {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ReportGeneratorDTO getReportGenerator() {
        return reportGenerator;
    }

    public void setReportGenerator(ReportGeneratorDTO reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    public DateTime getDateTimeGenerated() {
        return dateTimeGenerated;
    }

    public void setDateTimeGenerated(DateTime dateTimeGenerated) {
        this.dateTimeGenerated = dateTimeGenerated;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }
    
    private Integer id;
    private String title;
    private DateTime dateTimeGenerated;
    private String note;
    private List<String> formats;
    private ReportGeneratorDTO reportGenerator;
}
