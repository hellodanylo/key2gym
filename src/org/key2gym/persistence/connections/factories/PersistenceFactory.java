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
package org.key2gym.persistence.connections.factories;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import java.sql.SQLException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.key2gym.persistence.connections.configurations.ConnectionConfiguration;

/**
 *
 * @author Danylo Vashchilenko
 */
public abstract class PersistenceFactory<T extends ConnectionConfiguration> implements AutoCloseable {
    
    public PersistenceFactory(T connectionConfig) {
        this.connectionConfig = connectionConfig;
        this.factory = null;
        this.dataSource = null;
    }
    
    public T getConnectionConfiguration() {
        return connectionConfig;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected void setDataSource(ComboPooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return factory;
    }

    protected void setEntityManagerFactory(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void close() {
        try {
            DataSources.destroy(dataSource);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass()).error("Failed to destroy the data source:", ex);
            return;
        }
    }
    
    public static String PERSITENCE_UNIT = "PU";
            
    private ComboPooledDataSource dataSource;
    private EntityManagerFactory factory;
    private T connectionConfig;
}
