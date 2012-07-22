/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import java.util.Observable;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 *
 * @author Danylo Vashchilenko
 */
public class StorageService extends Observable{
    
    private EntityManager entityManager;
    private static StorageService instance;
    
    protected StorageService() {
        entityManager = Persistence.createEntityManagerFactory("Census PU")
                .createEntityManager();
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public void beginTransaction() throws IllegalStateException {
        entityManager.getTransaction().begin();
    }
    
    public Boolean isTransactionActive() {
        return entityManager.getTransaction().isActive();
    }
    
    public void commitTransaction() {
        entityManager.getTransaction().commit();
        setChanged();
        notifyObservers();
    }
    
    public void rollbackTransaction() {
        entityManager.getTransaction().rollback();
    }
    
    public static StorageService getInstance() {
        if(instance == null) {
            instance = new StorageService();
        }
        
        return instance;
    }
}
