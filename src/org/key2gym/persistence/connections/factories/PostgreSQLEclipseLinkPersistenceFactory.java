/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.persistence.connections.factories;

import com.googlecode.flyway.core.Flyway;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.text.MessageFormat;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.key2gym.persistence.connections.configurations.PostgreSQLEclipseLinkConnectionConfiguration;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PostgreSQLEclipseLinkPersistenceFactory extends PersistenceFactory<PostgreSQLEclipseLinkConnectionConfiguration> {

    public PostgreSQLEclipseLinkPersistenceFactory(PostgreSQLEclipseLinkConnectionConfiguration config) {
        super(config);

        Properties properties = new Properties();

        dataSource = new ComboPooledDataSource(config.getCodeName());
        dataSource.setJdbcUrl(MessageFormat.format("jdbc:postgresql://{0}:{1}/{2}",
                config.getHost(),
                config.getPort(),
                config.getDatabase()));
        dataSource.setUser(config.getUser());
        dataSource.setPassword(config.getPassword());

        dataSource.setInitialPoolSize(2);
        dataSource.setMaxPoolSize(2);
        dataSource.setMaxIdleTime(0);
        dataSource.setMinPoolSize(1);
        dataSource.setCheckoutTimeout(5000);

        Flyway flyway = new Flyway();

        flyway.setDataSource(dataSource);
        flyway.setSqlMigrationPrefix("V");
        flyway.setLocations("db/schemas", "org/key2gym/persistence/migration");
        flyway.migrate();

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
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "INFO");
        
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
