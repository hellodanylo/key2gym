/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 *
 * @author Danylo Vashchilenko
 */
public class StorageService extends Observable {

    private EntityManager entityManager;
    private static StorageService instance;

    protected StorageService() {

        ResourceBundle config = ResourceBundle.getBundle("etc/mysql");

        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url",
                MessageFormat.format("jdbc:mysql://{0}:{1}/{2}?useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterSetResults=utf8",
                config.getString("host"),
                config.getString("port"),
                config.getString("database")));

        properties.put("javax.persistence.jdbc.password", config.getString("password"));
        properties.put("javax.persistence.jdbc.user", config.getString("user"));

        entityManager = Persistence.createEntityManagerFactory("Census PU", properties).createEntityManager();
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
        if (instance == null) {
            instance = new StorageService();
        }

        return instance;
    }
}
