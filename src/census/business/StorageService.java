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

    /**
     * Singleton instance.
     */
    private static StorageService instance;
    
    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class
     */
    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
        }

        return instance;
    }
}
