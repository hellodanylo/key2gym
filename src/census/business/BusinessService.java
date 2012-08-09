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

import java.util.ResourceBundle;
import javax.persistence.EntityManager;

/**
 * This is the parent class of all classes providing business services.
 * 
 * This class does not define the API to access a business service. It provides
 * common functionality to all business services.
 * 
 * @author Danylo Vashchilenko
 */
public abstract class BusinessService {

    protected StorageService storageService;
    protected SessionsService sessionService;
    protected ResourceBundle bundle;
    protected EntityManager entityManager;
    
    /**
     * Creates an instance of this class.
     */
    protected BusinessService() {
        storageService = StorageService.getInstance();
        sessionService = SessionsService.getInstance();
        bundle = ResourceBundle.getBundle("census.business.resources.Strings");
        entityManager = storageService.getEntityManager();
    }
    
    /**
     * Makes sure that the transaction is active.
     * 
     * @throws IllegalStateException if the transaction is not active
     */
    protected void assertTransactionActive() {
        if (!storageService.isTransactionActive()) {
            throw new IllegalStateException("The transaction has to be active."); //NOI18N
        }
    }

    /**
     * Makes sure that there is an open session.
     * 
     * @throws IllegalStateException if no session is open
     */
    protected void assertOpenSessionExists() {
        if (!sessionService.hasOpenSession()) {
            throw new IllegalStateException("A session has to be open."); //NOI18N
        }
    }
}
