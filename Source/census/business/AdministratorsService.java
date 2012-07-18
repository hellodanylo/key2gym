/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business;

import census.business.dto.AdministratorDTO;
import census.persistence.Administrator;

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
        assertSessionActive();
        
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
    
    /*
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
