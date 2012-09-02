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
package org.key2gym;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.metadatatable.MetaDataTableRow;
import com.googlecode.flyway.core.migration.SchemaVersion;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.key2gym.business.StorageService;
import org.key2gym.persistence.PersistenceSchemaVersion;
import org.key2gym.persistence.connections.ConnectionConfigurationsManager;
import org.key2gym.persistence.connections.configurations.ConnectionConfiguration;
import org.key2gym.persistence.connections.factories.PersistenceFactory;
import org.key2gym.presentation.MainFrame;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.ConnectionsManagerDialog;

/**
 * This is the main class of the application.
 *
 * It's responsible for the following tasks:
 * <p/>
 *
 * <ul> 
 * 
 * <li> Initializing Logging system.</li> 
 * 
 * <li> Processing command line arguments. </li>
 * 
 * <li> Reading and applying application properties.</li>
 * 
 * <li> Choosing connection to use. </li>
 * 
 * <li> Launching MainFrame.</li> 
 * 
 * </ul>
 *
 * @author Danylo Vashchilenko
 */
public class Starter {

    private static final Logger logger = Logger.getLogger(Starter.class.getName());
    private static final Properties properties = new Properties();

    /**
     * The main method which performs all task as described in class
     * description.
     *
     * @param args an array of arguments
     */
    public static void main(String[] args) {
        /*
         * Configures the logger using 'etc/log.properties'.
         */
        try (InputStream input = new FileInputStream(PATH_LOGGING_PROPERTIES)) {
            PropertyConfigurator.configure(input);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Starter.class.getName()).log(Level.SEVERE, "Failed to load logging properties:", ex);
            return;
        }

        logger.info("Starting...");

        ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");

        /*
         * Parses command line and fills the registry.
         */
        parseCommandLine(args);

        /*
         * Fills the registry with properties from the application properties file.
         */
        try (FileInputStream input = new FileInputStream(PATH_APPLICATION_PROPERTIES)) {
            properties.load(input);
        } catch (IOException ex) {
            logger.fatal("Failed to load the application properties file:", ex);
            return;
        }

        /*
         * Changes the application's locale.
         */
        initLocale();

        /*
         * Changes the application's L&F.
         */
        initUI();

        /*
         * Finds the appropriate persistence factory. The block is enclosed
         * in try-with-resources in order to ensure that the connection is closed.
         */
        try (PersistenceFactory persistenceFactory = findPersistenceFactory()) {


            Flyway flyway = new Flyway();
            flyway.setDataSource(persistenceFactory.getDataSource());

            /*
             * Migrates the database, if requested.
             */
            if (properties.containsKey(PROPERTY_MIGRATE)) {

                flyway.setSqlMigrationPrefix(MIGRATION_PREFIX);
                flyway.setLocations(MIGRATION_CLASS_PATH);

                /*
                 * If the user wants to migrate to a specific version,
                 * passes this version to Flyway.
                 */
                if (!properties.getProperty(PROPERTY_MIGRATE).equals(ARGUMENT_MIGRATE_LATEST)) {
                    flyway.setTarget(new SchemaVersion(properties.getProperty(PROPERTY_MIGRATE)));
                }
                flyway.migrate();
            }

            /*
             * Checks whether the database has the correct version.
             */
            MetaDataTableRow status = flyway.status();

            if (status == null) {
                JOptionPane.showMessageDialog(null, strings.getString("Message.MigrationRequired.Initial"));
                return;
            } else if (status.getVersion().compareTo(PersistenceSchemaVersion.CURRENT) < 0) {
                JOptionPane.showMessageDialog(null, MessageFormat.format(strings.getString("Message.MigrationRequired.withDatabaseAndApplicationVersions"), status.getVersion(), PersistenceSchemaVersion.CURRENT));
                return;
            }


            /*
             * Initializes the storage service with the persistence properties generated
             * from the selected connection.
             */
            logger.info("Initializing the storage service with the connection: " + persistenceFactory.getConnectionConfiguration().getCodeName());
            try {
                StorageService.initialize(persistenceFactory.getEntityManagerFactory());
            } catch (Exception ex) {
                logger.fatal("Failed to initializes the storage service:", ex);
                return;
            }

            logger.info("Started!");

            launchAndWaitMainFrame();

            logger.info("Shutting down!");

            /*
             * Releases all resources. 
             */
            StorageService.getInstance().destroy();
        }
    }

    /**
     * Parses the command-line arguments and puts the values into the application
     * registry.
     * <p/>
     * This method will terminate the VM, if the command line contains malformed arguments.
     * 
     * @param args the command-line arguments. 
     */
    private static void parseCommandLine(String[] args) {

        Options options = new Options();
        options.addOption(ARGUMENT_CONNECTION, true, "use the connection with the code name specified.");
        options.addOption(ARGUMENT_MIGRATE, true, "migrate to the schema version specified.");

        CommandLine cmd = null;
        try {
            cmd = new GnuParser().parse(options, args);
        } catch (ParseException ex) {
            logger.fatal("Failed to parse command line:", ex);
            System.exit(1);
        }

        if (cmd.hasOption(ARGUMENT_CONNECTION)) {
            properties.put(PROPERTY_CONNECTION, cmd.getOptionValue(ARGUMENT_CONNECTION));
        }

        if (cmd.hasOption(ARGUMENT_MIGRATE)) {
            properties.put(PROPERTY_MIGRATE, cmd.getOptionValue(ARGUMENT_MIGRATE));
        }
    }

    private static void initLocale() {
        Locale.setDefault(new Locale(properties.getProperty(PROPERTY_LOCALE_LANGUAGE), properties.getProperty(PROPERTY_LOCALE_COUNTRY)));
    }

    /**
     * Changes the application's L&F according to the registry.
     */
    private static void initUI() {
        String ui = (String) properties.get("ui");
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (ui.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            logger.error("Failed to change the L&F:", ex);
        }
    }

    /**
     * Finds the appropriate persistence factory.
     * 
     * The method first checks the registry to see whether
     * the connection has already been chosen. If it's not there, starts
     * the connections chooser dialog. Then instantiates a persistence factory
     * with the chosen connection.
     * <p/>
     * 
     * This method will terminate the VM, if a fatal exception is encountered.
     * 
     * @return the appropriate persistence factory
     */
    private static PersistenceFactory findPersistenceFactory() {

        ConnectionConfigurationsManager connectionsManager = new ConnectionConfigurationsManager();

        if (!properties.containsKey(PROPERTY_CONNECTION)) {
            ConnectionsManagerDialog dialog = new ConnectionsManagerDialog(connectionsManager);

            dialog.setVisible(true);

            /*
             * Quits if the user clicked cancel.
             */
            if (dialog.getResult().equals(AbstractDialog.Result.CANCEL)) {
                System.exit(1);
            }

            /*
             * Logs an exception and quits, if the connections manager encountered
             * an exception.
             */
            if (dialog.getResult().equals(AbstractDialog.Result.EXCEPTION)) {
                logger.fatal("The connections manager encountered an exception.", dialog.getException());
                System.exit(1);
            }
        } else {
            /*
             * Attemps to find a connection with a code name passed in the command line.
             */
            List<ConnectionConfiguration> connections = connectionsManager.getConnections();
            String connectionCodeName = properties.getProperty(PROPERTY_CONNECTION);
            for (ConnectionConfiguration connection : connections) {
                if (connection.getCodeName().equals(connectionCodeName)) {
                    connectionsManager.selectConnection(connection);
                }
            }

            /*
             * Reports and terminates, if the connection was not found.
             */
            if (connectionsManager.getSelectedConnection() == null) {
                logger.fatal("Missing connection specified in the arguments: " + properties.getProperty(PROPERTY_CONNECTION));
                System.exit(1);
            }
        }

        ConnectionConfiguration connection = connectionsManager.getSelectedConnection();

        /*
         * Attempts to load the properties factory class. 
         */
        String propertiesFactoryClassBinaryName = PersistenceFactory.class.getPackage().getName() + "." + connection.getType() + "PersistenceFactory";
        Class<? extends PersistenceFactory> propertiesFactoryClass = null;
        try {
            propertiesFactoryClass = (Class<? extends PersistenceFactory>) Starter.class.getClassLoader().loadClass(propertiesFactoryClassBinaryName);
        } catch (ClassNotFoundException ex) {
            logger.fatal("Missing persistence factory for connection type: " + connection.getType(), ex);
            System.exit(1);
        } catch (ClassCastException ex) {
            logger.fatal("Persistence factory for connection type '" + connection.getType() + "' is of the wrong type.", ex);
            System.exit(1);
        }

        PersistenceFactory persistenceFactory = null;

        /*
         * Attempts to instantiate the properties factory.
         */
        try {
            persistenceFactory = propertiesFactoryClass.getConstructor(connection.getClass()).newInstance(connection);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException ex) {
            logger.fatal("Failed to instantiate the persistence properties factory for connection type: " + connection.getType(), ex);
            System.exit(1);
        }

        return persistenceFactory;
    }

    /**
     * Launches and waits for the MainFrame to close.
     */
    private static void launchAndWaitMainFrame() {
        try {
            EventQueue.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    MainFrame.getInstance().setVisible(true);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            logger.error("Unexpected exception:", ex);
        }

        synchronized (MainFrame.getInstance()) {
            while (MainFrame.getInstance().isVisible()) {
                try {
                    MainFrame.getInstance().wait();
                } catch (InterruptedException ex) {
                    logger.error("Unexpected exception:", ex);
                }
            }
        }
    }
    /*
     * Environment files.
     */
    private static final String PATH_APPLICATION_PROPERTIES = "etc/application.properties";
    private static final String PATH_LOGGING_PROPERTIES = "etc/logging.properties";
    /*
     * Command-line arguments.
     */
    private static final String ARGUMENT_CONNECTION = "connection";
    private static final String ARGUMENT_MIGRATE = "migrate";
    private static final String ARGUMENT_MIGRATE_LATEST = "latest";

    /*
     * Application registry properties.
     */
    private static final String PROPERTY_LOCALE_COUNTRY = "locale.country";
    private static final String PROPERTY_LOCALE_LANGUAGE = "locale.language";
    private static final String PROPERTY_CONNECTION = "connection";
    private static final String PROPERTY_MIGRATE = "migrate";
    /*
     * Migration
     */
    private static final String MIGRATION_CLASS_PATH = "org/key2gym/persistence/migration";
    private static final String MIGRATION_PREFIX = "V";

    public static Properties getProperties() {
        return properties;
    }
}
