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

package org.key2gym.migration;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.migration.SchemaVersion;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.postgresql.ds.PGSimpleDataSource;

/**
 *
 * @author Danylo Vashchilenko
 */
public class Main {

    public static void main(String[] args) {

        PropertyConfigurator.configure(LOGGING_PROPERTIES_FILE);

        Logger logger = Logger.getLogger(Main.class);

        Options options = new Options();
        options.addOption(PROPERTY_MODE, true, "the connection creation mode (either " + PROPERTY_MODE_DATASOURCE + " or " + PROPERTY_MODE_DIRECT + ").");
        options.addOption(PROPERTY_DATASOURCE_OPENEJB_URL, true, "the datasource's serving OpenEJB URL.");
        options.addOption(PROPERTY_DATASOURCE_JNDI, true, "the datasource's JNDI name.");
        options.addOption(PROPERTY_DIRECT_HOSTNAME, true, "the server's hostname.");
        options.addOption(PROPERTY_DIRECT_USER, true, "the JDBC user.");
        options.addOption(PROPERTY_DIRECT_PASSWORD, true, "the JDBC user's password.");
        options.addOption(PROPERTY_DIRECT_PORT, true, "the server's port.");
        options.addOption(PROPERTY_DIRECT_DATABASE, true, "the database's name.");
        options.addOption(PROPERTY_VERSION, true, "the schema version to migrate to.");
        options.addOption("h", "help", false, "prints this help.");

        CommandLine cmd = null;
        try {
            cmd = new GnuParser().parse(options, args);
        } catch (ParseException ex) {
            logger.error("Failed to parse the command line: ", ex);
            return;
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(MIGRATION_PROPERTIES_FILE));
        } catch (FileNotFoundException ex) {
            logger.info("Missing the properties files, therefore expecting all properties to be passed on the command line.");
        } catch (IOException ex) {
            logger.error("Failed to load the properties file: ", ex);
        }

        /*
         * Printing the help, when asked.
         */
        if (cmd.hasOption("help") || cmd.hasOption("h")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar key2gym-migration.jar [options]", options);
            return;
        }

        Collection<Option> optionsCollection = options.getOptions();

        /*
         * Overwrites the properties from the configuration file with the
         * command-line arguments.
         */
        for (Option option : optionsCollection) {
            if (cmd.hasOption(option.getOpt())) {
                properties.setProperty(option.getOpt(), cmd.getOptionValue(option.getOpt()));
            }
        }

        /*
         * The Flyway instance.
         */
        Flyway flyway = new Flyway();

        String mode = properties.getProperty(PROPERTY_MODE);

        if (mode == null) {
            logger.fatal("The mode was not specified!");
            return;
        } else if (mode.equals(PROPERTY_MODE_DATASOURCE)) {
            String jdniName = properties.getProperty(PROPERTY_DATASOURCE_JNDI);
            String openejbURL = properties.getProperty(PROPERTY_DATASOURCE_OPENEJB_URL);

            Properties openejbProperties = new Properties();

            if (openejbURL == null) {
                logger.fatal("The " + PROPERTY_DATASOURCE_OPENEJB_URL + " property is required, when the mode is " + PROPERTY_MODE_DATASOURCE + ".");
                return;
            } else if (jdniName == null) {
                logger.fatal("The " + PROPERTY_DATASOURCE_JNDI + " property is required, when the mode is " + PROPERTY_MODE_DATASOURCE + ".");
                return;
            }

            openejbProperties.setProperty("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
            openejbProperties.setProperty("java.naming.provider.url", openejbURL);

            InitialContext ctx;
            DataSource dataSource;

            try {
                ctx = new InitialContext(openejbProperties);
                dataSource = (DataSource) ctx.lookup(jdniName);
            } catch (NamingException ex) {
                logger.fatal("Failed to retrieve the datasouce: ", ex);
                return;
            }

            flyway.setDataSource(dataSource);

        } else if (mode.equals(PROPERTY_MODE_DIRECT)) {
            String hostname = properties.getProperty(PROPERTY_DIRECT_HOSTNAME);
            String port = properties.getProperty(PROPERTY_DIRECT_PORT);
            String username = properties.getProperty(PROPERTY_DIRECT_USER);
            String password = properties.getProperty(PROPERTY_DIRECT_PASSWORD);
            String database = properties.getProperty(PROPERTY_DIRECT_DATABASE);

            if (hostname == null) {
                logger.fatal("The " + PROPERTY_DIRECT_HOSTNAME + " property is required, when the mode is " + PROPERTY_MODE_DIRECT + ".");
                return;
            } else if (port == null) {
                logger.fatal("The " + PROPERTY_DIRECT_PORT + " property is required, when the mode is " + PROPERTY_MODE_DIRECT + ".");
                return;
            } else if (username == null) {
                logger.fatal("The " + PROPERTY_DIRECT_USER + " property is required, when the mode is " + PROPERTY_MODE_DIRECT + ".");
                return;
            } else if (password == null) {
                logger.fatal("The " + PROPERTY_DIRECT_PASSWORD + " property is required, when the mode is " + PROPERTY_MODE_DIRECT + ".");
                return;
            } else if (database == null) {
                logger.fatal("The " + PROPERTY_DIRECT_DATABASE + " property is required, when the mode is " + PROPERTY_MODE_DIRECT + ".");
                return;
            }

            PGSimpleDataSource dataSource = new PGSimpleDataSource();

            dataSource.setServerName(hostname);
            dataSource.setPortNumber(Integer.valueOf(port));
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setDatabaseName(database);

            flyway.setDataSource(dataSource);
        } else {
            logger.fatal("The mode is unknown!");
            return;
        }
        
        String version = properties.getProperty(PROPERTY_VERSION);
        
        if(version == null) {
            logger.fatal("The version property is required.");
            return;
        }
        
        flyway.setTarget(new SchemaVersion(version));
        
        /*
         * Asks the Flyway to look for the migrations in the current package.
         */
        flyway.setLocations(Main.class.getPackage().getName());
        
        flyway.migrate();
        
    }
    private static String LOGGING_PROPERTIES_FILE = "etc/logging.properties";
    private static String MIGRATION_PROPERTIES_FILE = "etc/migration.properties";
    private static String PROPERTY_MODE = "mode";
    private static String PROPERTY_MODE_DATASOURCE = "datasource";
    private static String PROPERTY_MODE_DIRECT = "direct";
    private static String PROPERTY_DATASOURCE_OPENEJB_URL = "openejb";
    private static String PROPERTY_DATASOURCE_JNDI = "jndi";
    private static String PROPERTY_DIRECT_HOSTNAME = "hostname";
    private static String PROPERTY_DIRECT_PORT = "port";
    private static String PROPERTY_DIRECT_DATABASE = "database";
    private static String PROPERTY_DIRECT_USER = "user";
    private static String PROPERTY_DIRECT_PASSWORD = "password";
    private static String PROPERTY_VERSION = "version";
}
