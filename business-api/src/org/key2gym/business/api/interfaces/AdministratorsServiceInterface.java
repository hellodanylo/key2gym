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

import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.AdministratorDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface AdministratorsServiceInterface extends BasicInterface {

    /**
     * Gets an administrator by its ID.
     *
     * @param id the administrator's ID
     * @throws NullPointerException if id is null
     * @throws ValidationException if the ID is invalid
     * @return the administrator
     */
    AdministratorDTO getById(Integer id);
    
    /**
     * Gets an administrator by its username.
     *
     * @param username the administrator's username
     * @throws NullPointerException if username is null
     * @throws ValidationException if the username is invalid
     * @return the administrator
     */
    AdministratorDTO getByUsername(String username) throws ValidationException;

    /**
     * Gets the current administrator.
     *
     * @return the currently logged in adminstrator
     */
    AdministratorDTO getCurrent();    
}
