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
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "report_body_rpb")
@NamedQueries({
	@NamedQuery(name = "ReportBody.findByReportIdAndFormat", query = "SELECT b FROM ReportBody b WHERE b.reportId = :reportId AND b.format = :format"),
	    @NamedQuery(name = "ReportBody.findFormatsByReportId", query = "SELECT b.format FROM ReportBody b WHERE b.reportId = :reportId"),
	    @NamedQuery(name = "ReportBody.removeByReportId", query = "DELETE FROM ReportBody b WHERE b.reportId = :reportId")
})
public class ReportBody implements Serializable {
    
    @Id
    @Column(name = "idrpt_rpb")
    private Integer reportId;
    
    @Id            
    @Column(name="format")
    private String format;
 
    @Basic(fetch= FetchType.EAGER)
    @Column(name="body")
    @Lob
    private byte[] body;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idrpt_rpb", referencedColumnName = "id_rpt", insertable=false, updatable=false)
    private Report report;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Report getReport() {
	return report;
    }
}