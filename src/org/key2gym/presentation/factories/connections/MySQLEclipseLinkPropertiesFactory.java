/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

//        if (cib.containsKey("ddl")) {
//            properties.put("eclipselink.ddl-generation", config.get("ddl"));
//            properties.put("eclipselink.ddl-generation.table-creation-suffix", "engine=InnoDB");
//        }

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
