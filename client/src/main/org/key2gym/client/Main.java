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
package org.key2gym.client;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Properties properties = new Properties();

    /**
     * The main method which performs all task as described in class
     * description.
     *
     * @param args an array of arguments
     */
    public static void main(String[] args) {
        /*
         * Configures the logger using 'etc/logging.properties'.
         */
        try (InputStream input = new FileInputStream(PATH_LOGGING_PROPERTIES)) {
            PropertyConfigurator.configure(input);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to load logging properties:", ex);
            return;
        }

        logger.info("Starting...");

        /*
         * Loads the application properties file and fills the registry.
         */
        try (FileInputStream input = new FileInputStream(PATH_APPLICATION_PROPERTIES)) {
            properties.load(input);
        } catch (IOException ex) {
            logger.fatal("Failed to load the application properties file:", ex);
            return;
        }

        /*
         * Parses command line and fills the registry.
         */
        Options options = new Options();
        options.addOption(ARGUMENT_CONNECTION, true, ARGUMENT_CONNECTION_DESCRIPTION);

        CommandLine cmd;
        try {
            cmd = new GnuParser().parse(options, args);
        } catch (ParseException ex) {
            logger.fatal("Failed to parse command line:", ex);
            return;
        }

        if (cmd.hasOption(ARGUMENT_CONNECTION)) {
            properties.put(PROPERTY_CONNECTION, cmd.getOptionValue(ARGUMENT_CONNECTION));
        }
        
        /*
         * Loads the connection properties file.
         */
        if (properties.containsKey(PROPERTY_CONNECTION)) {
            Properties connectionProperties = new Properties();
            String fileName = PATH_CONNECTIONS_FOLDER + "/" + properties.getProperty(PROPERTY_CONNECTION) + ".properties";
            try (FileInputStream input = new FileInputStream(fileName)) {
                connectionProperties.load(input);
            } catch (IOException ex) {
                logger.fatal("Failed to load the application properties file:", ex);
                return;
            }
            
            /*
             * Copies the connection.url property.
             */
            if(connectionProperties.containsKey(PROPERTY_CONNECTION_URL)) {
                properties.setProperty(PROPERTY_CONNECTION_URL, connectionProperties.getProperty(PROPERTY_CONNECTION_URL));
            } else {
                logger.fatal("The connection properties file ("+properties.getProperty(PROPERTY_CONNECTION) +") does not contain the required property "+connectionProperties.getProperty(PROPERTY_CONNECTION_URL) +"!");
                return;
            }
        } else {
            logger.fatal("No connection has been specified!");
            return;
        }

        /*
         * Changes the application's locale.
         */
        Locale.setDefault(new Locale(properties.getProperty(PROPERTY_LOCALE_LANGUAGE), properties.getProperty(PROPERTY_LOCALE_COUNTRY)));

        /*
         * Changes the application's L&F.
         */
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

        logger.info("Started!");

        launchAndWaitMainFrame();

        logger.info("Shutting down!");
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
    private static final String PATH_CONNECTIONS_FOLDER = "etc/connections";
    /*
     * Command-line arguments.
     */
    private static final String ARGUMENT_CONNECTION = "connection";
    private static final String ARGUMENT_CONNECTION_DESCRIPTION = "the name of the connection to use.";

    /*
     * Application registry properties.
     */
    public static final String PROPERTY_LOCALE_COUNTRY = "locale.country";
    public static final String PROPERTY_LOCALE_LANGUAGE = "locale.language";
    public static final String PROPERTY_CONNECTION = "connection";
    public static final String PROPERTY_REFRESH_PERIOD = "refreshPeriod";
    public static final String PROPERTY_CONNECTION_URL = "connection.url";

    public static Properties getProperties() {
        return properties;
    }
}
