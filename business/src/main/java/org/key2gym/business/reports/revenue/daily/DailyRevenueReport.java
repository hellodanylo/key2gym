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
package org.key2gym.business.reports.revenue.daily;

import org.key2gym.business.entities.DailyRevenue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Danylo Vashchilenko
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"title", "periodBegin", "periodEnd", "generated", "days"})
public class DailyRevenueReport {

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPeriodBegin(Date periodBegin) {
        this.periodBegin = periodBegin;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public void setGenerated(Date generated) {
	this.generated = generated;
    }
    
    public void setDays(List<DailyRevenue> days) {
        this.days = days;
    }
    
    private String title;
    @XmlSchemaType(name="date")
    private Date periodBegin;
    @XmlSchemaType(name="date")
    private Date periodEnd;
    private Date generated;
    @XmlElement(name="day", defaultValue="0.0")
    private List<DailyRevenue> days;
}
