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
package org.key2gym.business.api.interfaces;

import java.math.BigDecimal;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.CashAdjustmentDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface CashServiceInterface {

    /**
     * Gets the cash adjustment by the date. If it does not exists, it will
     * be created.
     * <p>
     * 
     * <ul>
     * <li> The caller must have the MANAGER role.</li>
     * </ul>
     *
     * @param date the date
     * @throws NullPointerException if date is null
     * @throws SecurityViolationException if the caller does not have MANAGER role
     * @return the cash adjustment
     */
    CashAdjustmentDTO getAdjustmentByDate(DateMidnight date) throws SecurityViolationException;

    /**
     * Returns final cash for the date.
     * <p>
     * 
     * <ul>
     * <li>If the date is not today, the caller must have MANAGER role</li>
     * <li>If the date is today, *_ADMINISTRATOR role is required</li>
     * </ul>
     *
     * @param date the date to look up
     * @throws SecurityViolationException if the caller does not have roles required
     * @throws NullPointerException if the date is null
     * @return the final cash for the date
     */
    BigDecimal getCashByDate(DateMidnight date) throws SecurityViolationException;

    /**
     * Records the cash adjustment. If the record already exists, it will be
     * updated. Otherwise, it will be created.
     * <p>
     * 
     * <ul>
     * <li> The caller must have the MANAGER role.</li>
     * </ul>
     *
     * @param cashAdjustment the cash adjustment
     * @throws SecurityViolationException if the caller does not have MANAGER role
     * @throws NullPointerException if the cash adjustment or any of its
     * required properties is null
     * @throws ValidationException if any of the required properties is invalid
     */
    void recordCashAdjustment(CashAdjustmentDTO cashAdjustment) throws ValidationException, SecurityViolationException;
    
}
