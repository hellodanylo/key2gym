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
package census.business;

import census.business.dto.KeyDTO;
import census.persistence.Key;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Danylo Vashchilenko
 */
public class KeysService extends BusinessService {
    
    /**
     * Gets all keys available for new attendances.
     * 
     * @throws IllegalStateException if the session is not active
     * @return the list of keys available 
     */
    public List<KeyDTO> getKeysAvailable() {
        assertOpenSessionExists();
        
        List<KeyDTO> result = new LinkedList();
        List<Key> keys;
        
        keys = entityManager.createNamedQuery("Key.findAvailable") //NOI18N
                .getResultList();
        
        for(Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }
        
        return result;
    }
    
    /**
     * Gets all keys taken by attendances.
     * 
     * @throws IllegalStateException if the session is not active
     * @return the list of keys taken 
     */
    public List<KeyDTO> getKeysTaken() {
        assertOpenSessionExists();
        
        List<KeyDTO> result = new LinkedList<>();
        List<Key> keys;
        
        keys = entityManager.createNamedQuery("Key.findTaken") //NOI18N
                .getResultList();
        
        for(Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }
        
        return result;
    }
    
    /**
     * Singleton instance.
     */
    private static KeysService instance;
    
    /**
     * Gets an instance of this class.
     * 
     * @return an instance of this class.
     */
    public static KeysService getInstance() {
        if(instance == null) {
            instance = new KeysService();
        }
        return instance;
    }
}
