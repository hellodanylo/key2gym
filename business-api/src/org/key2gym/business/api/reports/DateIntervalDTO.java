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

package org.key2gym.business.api.reports;

import java.io.Serializable;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DateIntervalDTO implements Serializable {

    public DateMidnight getBegin() {
        return begin;
    }

    public void setBegin(DateMidnight begin) {
        this.begin = begin;
    }

    public DateMidnight getEnd() {
        return end;
    }

    public void setEnd(DateMidnight end) {
        this.end = end;
    }
    
    private DateMidnight begin;
    private DateMidnight end;
}
