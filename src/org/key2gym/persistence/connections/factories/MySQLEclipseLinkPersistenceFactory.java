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

import com.googlecode.flyway.core.Flyway;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.text.MessageFormat;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.key2gym.persistence.connections.configurations.MySQLEclipseLinkConnectionConfiguration;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MySQLEclipseLinkPersistenceFactory extends PersistenceFactory<MySQLEclipseLinkConnectionConfiguration> {

    public MySQLEclipseLinkPersistenceFactory(MySQLEclipseLinkConnectionConfiguration config) {
        super(config);
        
        Logger.getLogger(MySQLEclipseLinkPersistenceFactory.class).warn("The use of this connection type is DEPRECATED. The schema is not maintained anymore. Use it at yout own risk.");

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
        dataSource.setMinPoolSize(1);
        dataSource.setCheckoutTimeout(5000);

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
