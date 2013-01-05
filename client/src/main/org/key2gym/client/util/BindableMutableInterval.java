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

package org.key2gym.client.util;

import org.joda.time.*;

/*
 * A thin wrapper over MutableInterval to conform it to JavaBeans specification
 * required to beans binding.
 *
 * More specifically:
 * <ul>
 * <li>start and end properties' setter and getter methods have different types</li>
 * </ul>

 * @author Danylo Vashchilenko
 */
public class BindableMutableInterval extends MutableInterval {

    public BindableMutableInterval(Object interval) {
	super(interval);
    }

    public BindableMutableInterval(ReadableInstant start, ReadableInstant end) {
	super(start, end);
    }

    public void setStart(DateTime start) {
	super.setStart(start);
    }

    public void setEnd(DateTime end) {
	super.setEnd(end);
    }
}
