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
package org.key2gym.business.api.interfaces;

import java.util.List;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.AdSourceDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface AdSourcesServiceInterface extends BasicInterface {

    /**
     * Finds the ad source by its ID.
     *
     * @param adSourceId the ad source's ID
     * @throws ValidationException if the ad source's ID is null or invalid
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the ad source
     */
    AdSourceDTO findAdSourceById(Integer adSourceId) throws ValidationException, SecurityViolationException;

    /**
     * Gets all ad sources.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the list of all ad sources.
     */
    List<AdSourceDTO> getAdSources() throws SecurityViolationException;

    /**
     * Gets the IDs of all ad sources.
     *
     * @throws SecurityViolationException if the caller does not have either *_ADMINISTRATOR or MANAGER role
     * @return the list of IDs of all ad sources
     */
    List<Integer> getAdSourcesIds() throws SecurityViolationException;
    
}
