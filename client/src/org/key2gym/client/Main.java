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
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

        ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

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
        options.addOption(ARGUMENT_DATASOURCE, true, ARGUMENT_DATASOURCE_DESCRIPTION);

        CommandLine cmd = null;
        try {
            cmd = new GnuParser().parse(options, args);
        } catch (ParseException ex) {
            logger.fatal("Failed to parse command line:", ex);
            System.exit(1);
        }

        if (cmd.hasOption(ARGUMENT_DATASOURCE)) {
            properties.put(PROPERTY_DATASOURCE, cmd.getOptionValue(ARGUMENT_DATASOURCE));
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


        /*
         * Creates the context to lookup the EJBs.
         */
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
        properties.put("java.naming.provider.url", "ejbd://localhost:4201");

        properties.put("openejb.authentication.realmName", "PropertiesLogin");
        properties.put("java.naming.security.principal", "daniel");
        properties.put("java.naming.security.credentials", "password");

        InitialContext ctx;
        try {
            ctx = new InitialContext(properties);
        } catch (NamingException ex) {
            logger.fatal("Failed to create the context: ", ex);
            return;
        }

        logger.info("Started!");

        launchAndWaitMainFrame();

        logger.info("Shutting down!");

        /*
         * Releases all resources. 
         */
        try {
            ctx.close();
        } catch (NamingException ex) {
            logger.fatal("Failed to close the context: ", ex);
            return;
        }
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
    private static final String ARGUMENT_DATASOURCE = "datasource";
    private static final String ARGUMENT_DATASOURCE_DESCRIPTION = "the jndi name of the datasource to use.";

    /*
     * Application registry properties.
     */
    public static final String PROPERTY_LOCALE_COUNTRY = "locale.country";
    public static final String PROPERTY_LOCALE_LANGUAGE = "locale.language";
    public static final String PROPERTY_DATASOURCE = "datasource";

    public static Properties getProperties() {
        return properties;
    }
}
