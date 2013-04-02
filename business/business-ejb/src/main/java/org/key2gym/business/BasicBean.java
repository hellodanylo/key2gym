package org.key2gym.business;

/*
 * Copyright 2012-2013 Danylo Vashchilenko
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


import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.key2gym.business.api.interfaces.BasicInterface;
import org.key2gym.business.resources.ResourcesManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class BasicBean implements BasicInterface {

    static {
	org.apache.log4j.PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
    }
    
    protected boolean callerHasRole(String role) {
        return sessionContext.isCallerInRole(role);
    }

    protected boolean callerHasAnyRole(String... roles) {
        for (String role : roles) {
            if (sessionContext.isCallerInRole(role)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    protected String getString(String key, String... arguments) {
        return ResourcesManager.getString(key, arguments);
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    protected SessionContext getSessionContext() {
        return sessionContext;
    }    
        
    @Resource
    private SessionContext sessionContext;
    
    @PersistenceContext(name="PU")
    protected EntityManager em;
}
