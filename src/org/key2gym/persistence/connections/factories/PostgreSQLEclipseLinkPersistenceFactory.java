/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.persistence.connections.factories;

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
            /*
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
