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

import java.util.Observable;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Provides EntityManager for BusinessServices and transactions control for
 * presentation tier.
 *
 * @author Danylo Vashchilenko
 */
public class StorageService extends Observable {

    private EntityManager entityManager;

    protected StorageService(Properties factoryProperties, Properties entityManagerProperties) {
        entityManager = Persistence.createEntityManagerFactory("PU", factoryProperties).createEntityManager(entityManagerProperties);
    }

    /**
     * Initializes the StorageService with given properties.
     *
     * @param factoryProperties the properties to use when creating
     * EntityManagerFactory
     * @param entityManagerProperties the properties to use when creating
     * EntityManager
     * @throws IllegalAccessException if the service is already initialized
     */
    public static void initialize(Properties factoryProperties, Properties entityManagerProperties) throws IllegalAccessException {
        if (instance != null) {
            throw new IllegalAccessException("The storage has already been initialized!");
        }
        instance = new StorageService(factoryProperties, entityManagerProperties);
    }

    /**
     * Gets the storage's entity manager.
     * <p>
     *
     * <b> This method is to be used by business tier only! </b>
     *
     * @return the entity manager
     */
    EntityManager getEntityManager() {
        return entityManager;
    }

    public void beginTransaction() {
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

    public void destroy() {
        entityManager.getEntityManagerFactory().close();
    }
    
    /**
     * Singleton instance.
     */
    private static StorageService instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class, or null if it was not initialized
     */
    public static StorageService getInstance() {
        return instance;
    }
}
