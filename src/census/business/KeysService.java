/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.business.dto.KeyDTO;
import census.persistence.Key;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author daniel
 */
public class KeysService extends BusinessService {

    /*
     * Singleton instance
     */
    private static KeysService instance;
    
    /**
     * Gets all keys available for new attendances.
     * 
     * @throws IllegalStateException if the session is not active
     * @return the list of keys available 
     */
    public List<KeyDTO> getKeysAvailable() {
        assertSessionActive();
        
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
        assertSessionActive();
        
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
