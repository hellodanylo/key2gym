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
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.text.MessageFormat;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.key2gym.persistence.connections.configurations.MySQLEclipseLinkConnectionConfiguration;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MySQLEclipseLinkPersistenceFactory extends PersistenceFactory<MySQLEclipseLinkConnectionConfiguration> {
    
    public MySQLEclipseLinkPersistenceFactory(MySQLEclipseLinkConnectionConfiguration config) {
        super(config);
        
        Properties properties = new Properties();
        
        dataSource = new ComboPooledDataSource(config.getCodeName());
        dataSource.setJdbcUrl(MessageFormat.format("jdbc:mysql://{0}:{1}/{2}?useUnicode=true&characterEncoding=utf8",
                config.getHost(),
                config.getPort(),
                config.getDatabase()));
        dataSource.setUser(config.getUser());
        dataSource.setPassword(config.getPassword());
        
        dataSource.setInitialPoolSize(1);
        dataSource.setMaxPoolSize(1);
        dataSource.setMaxIdleTime(0);
        
        /*
         * Specifies EclipseLink as the persistence provider.
         */
        properties.put("provider", "org.eclipse.persistence.jpa.PersistenceProvider");

        /*
         * Sets the data source.
         */
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
        
        /*
         * Specifies Apache log4j wrapper as the custom logger.
         */
        properties.put(PersistenceUnitProperties.LOGGING_LOGGER, "org.eclipse.persistence.logging.CommonsLoggingSessionLog");
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "CONFIG");

        /*
         * Optinal ddl option allows to automatically generate the schema.
         */
        if (config.getDDL() != null) {
            properties.put(PersistenceUnitProperties.DDL_GENERATION, config.getDDL());
            
            properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_BOTH_GENERATION);
            /*Karen
             * Requires to set InnoDB as the engine for the new tables.
             */
            properties.put(PersistenceUnitProperties.TABLE_CREATION_SUFFIX, "engine=InnoDB");
        }
        
        factory = Persistence.createEntityManagerFactory(PERSITENCE_UNIT, properties);

    }
    
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return factory;
    }
    
    private ComboPooledDataSource dataSource;
    private EntityManagerFactory factory;
}
