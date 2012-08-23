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

package org.key2gym.business;

import org.key2gym.business.dto.AdministratorDTO;
import org.key2gym.persistence.Administrator;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AdministratorsService extends BusinessService {
    
   /**
    * Gets an administrator by its ID.
    * 
    * @param id the administrator's ID
    * @throws IllegalStateException if the session is not active
    * @throws NullPointerException if the id is null
    * @return the administrator
    */
    public AdministratorDTO getById(Short id) {
        assertOpenSessionExists();
        
        if (id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }
        
        Administrator entityAdministrator = entityManager.find(Administrator.class, id);
        
        AdministratorDTO administrator = new AdministratorDTO();
        administrator.setFullName(entityAdministrator.getFullName());
        administrator.setId(entityAdministrator.getId());
        administrator.setNote(entityAdministrator.getNote());
        administrator.setPermissionsLevel(entityAdministrator.getPermissionsLevel());
        administrator.setUserName(entityAdministrator.getUsername());
        
        return administrator;
    }
    
    /**
     * Singleton instance.
     */
    private static AdministratorsService instance;
    
    /**
     * Gets an instance of this class.
     * 
     * @return an instance of this class
     */
    public static AdministratorsService getInstance() {
        if(instance == null) {
            instance = new AdministratorsService();
        }
        return instance;
    }

}
