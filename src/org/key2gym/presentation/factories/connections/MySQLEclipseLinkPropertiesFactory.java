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
package org.key2gym.presentation.factories.connections;

import java.text.MessageFormat;
import java.util.Properties;
import org.key2gym.presentation.connections.core.MySQLEclipseLinkConnection;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MySQLEclipseLinkPropertiesFactory implements PropertiesFactory<MySQLEclipseLinkConnection> {

    @Override
    public Properties createEntityManagerFactoryProperties(MySQLEclipseLinkConnection connection) {

        Properties properties = new Properties();

        /*
         * Builds a JDBC URL from the connection's host, port and database information.
         */
        properties.put("javax.persistence.jdbc.url",
                MessageFormat.format("jdbc:mysql://{0}:{1}/{2}?useUnicode=true&characterEncoding=utf8",
                connection.getHost(),
                connection.getPort(),
                connection.getDatabase()));

        /*
         * Specifies the user and the password.
         */
        properties.put("javax.persistence.jdbc.password", connection.getPassword());
        properties.put("javax.persistence.jdbc.user", connection.getUser());
        
        /*
         * Specifies MySQL Connector as the JDBC provider.
         */
        properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        
        /*
         * Specifies Apache log4j wrapper as the custom logger.
         */
        properties.put("eclipselink.logging.logger", "org.eclipse.persistence.logging.CommonsLoggingSessionLog");
        properties.put("eclipselink.logging.level", "CONFIG");

        /*
         * Optinal ddl option allows to automatically generate the schema.
         */
        if (connection.getDDL() != null) {
            properties.put("eclipselink.ddl-generation", connection.getDDL());
            /*
             * Requires to set InnoDB as the engine for the new tables.
             */
            properties.put("eclipselink.ddl-generation.table-creation-suffix", "engine=InnoDB");
        }

        return properties;
    }

    @Override
    public Properties createEntityManagerProperties(MySQLEclipseLinkConnection conneciton) {
        Properties properties = new Properties();
        
        /*
         * Specifies EclipseLink as the persistence provider for this connection.
         */
        properties.put("provider", "org.eclipse.persistence.jpa.PersistenceProvider");
        return properties;
    }
}