/*
 * Copyright 2012-2013 Danylo Vashchilenko
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
package org.key2gym.business.api.services;

import java.util.List;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.AttendanceDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface AttendancesService extends BasicService {

    /**
     * Checks in a casual client. <p>
     *
     * <ul> 
     * <li>There must be a subscription having 1 attendance unit, a term
     * of 1 day and allowing to open an attendance right now without penalties</li> 
     * </ul>
     *
     * @param keyId the key's ID.
     * @return the new attendance's ID.
     * @throws NullPointerException if keyId is null
     * @throws ValidationException if the key's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    Integer checkInCasualClient(Integer keyId) throws BusinessException, ValidationException, SecurityViolationException;

    /**
     * Checks in a registered client. 
     * <p/>
     *
     * <ul> 
     * <li>The client's attendances balance has to be more than 0</li>
     * <li>The expiration date has to be at least tomorrow</li> 
     * <li>The key has to have 0 open attendances associated with it</li>
     * </ul>
     *
     * @param clientId the client's ID
     * @param keyId the key's ID
     * @return the new attendance's ID
     * @throws NullPointerException if any of the arguments is null
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @throws ValidationException if any of the IDs passed is invalid
     */
    Integer checkInRegisteredClient(Integer clientId, Integer keyId) throws BusinessException, ValidationException, SecurityViolationException;

    /**
     * Recalculates the penalties for the given attendance.
     * 
     * @param attendanceId the attendance's ID
     * @throws BusinessException if the attendance is closed
     * @throws ValidationException if the attendance's ID is invalid
     * @throws SecurityViolationException if access to the attendance is denied
     */
    void recalculatePenalties(Integer attendanceId) throws BusinessException, ValidationException, SecurityViolationException;
    
    /**
     * Checks out a client. <p>
     *
     * <ul>
     * <li>The attendance has to be open.</li>
     * <li>If the attendance is anonymous, it has to have full payment recorded
     * in the associated order.</li>
     * </ul>
     *
     * @param attendanceId the attendance's ID
     * @throws NullPointerException if the attendanceId is null
     * @throws ValidationException if the attendance's ID is invalid
     * @throws BusinessException if current business rules restrict this operation
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    void checkOut(Integer attendanceId) throws BusinessException, ValidationException, SecurityViolationException;

    /**
     * Finds the client's attendances.
     *
     * @param id the client's ID
     * @return the list of the client's attendances
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the client's ID is invalid
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    List<AttendanceDTO> findAttendancesByClient(Integer id) throws ValidationException, SecurityViolationException;

    /**
     * Finds all attendance that were open on the date. 
     * <p/>
     *
     * <ul>
     * <li>If date is not today, the caller must have MANAGER role</li>
     * <li>If date is not today, the caller must have *_ADMINSTRATOR role</li>
     * </ul>
     *
     * @param date the date
     * @return the list of all attendances open on the date
     * @throws NullPointerException if the date is null
     * @throws SecurityViolationException if current security rules restrict this
     * operation
     */
    List<AttendanceDTO> findAttendancesByDate(DateMidnight date) throws SecurityViolationException;

    /**
     * Finds an open attendance with the key provided.
     *
     * @param keyId the key's ID
     * @return the attendance's ID
     * @throws NullPointerException if they keyId is null
     * @throws ValidationException if the key's ID is invalid
     * @throws BusinessException there isn't any open attendances with the key
     * provided
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    Integer findOpenAttendanceByKey(Integer keyId) throws ValidationException, BusinessException, SecurityViolationException;

    /**
     * Gets an attendance by its ID. <p>
     *
     * @param attendanceId the attendance's ID
     * @return the attendance, or null, if the ID is invalid
     * @throws NullPointerException if attendanceId is null
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    AttendanceDTO getAttendanceById(Integer attendanceId) throws SecurityViolationException;

    /**
     * Gets whether the attendance is casual. <p>
     *
     * <ul> <li>The attendance is casual, if it does not have a client
     * associated with it</li> </ul>
     *
     * @param attendanceId the attendance's ID
     * @return true, if the attendance is casual; false, otherwise
     * @throws NullPointerException if the attendanceId is null
     * @throws ValidationException if the attendance's ID is invalid
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     */
    Boolean isCasual(Integer attendanceId) throws ValidationException, SecurityViolationException;
}
