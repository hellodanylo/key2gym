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

import org.key2gym.business.StorageService;
import org.key2gym.presentation.MainFrame;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the main class of the application.
 *
 * It's responsible for the following tasks:
 * <p/>
 *
 * <ul> <li> Initializing Logging system </li> <li> Reading and applying
 * application properties. <li> Launching MainFrame </li> </ul>
 *
 * @author Danylo Vashchilenko
 */
public class Starter {

    private static final Logger logger = Logger.getLogger(Starter.class.getName());
    private static final Map<String, String> properties = new HashMap<>();

    /**
     * The main method which performs all task as described in class
     * description.
     *
     * @param args an array of arguments
     */
    public static void main(String[] args) {
        /*
         * Configures the logger using 'etc/log.properties' which should be on
         * the class path.
         */
        try {
            PropertyConfigurator.configure(new FileInputStream("etc/log.properties"));
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Starter.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        logger.info("Starting...");

        /*
         * Puts the default values of various properties.
         */
        properties.put("storage", "default");

        /*
         * The array contains the names of all expected arguments.
         */
        String[] expectedArgumentsNames = new String[]{"storage"};

        /*
         * Parses the arguments looking for expected arguments. The arguments
         * are in the '--ARGUMENTNAME=ARGUMENTVALUE' format.
         */
        for (String arg : args) {
            for (String expectedArgumentName : expectedArgumentsNames) {
                String preffix = "--" + expectedArgumentName + "=";
                if (arg.startsWith(preffix)) {
                    properties.put(expectedArgumentName, arg.substring(preffix.length()));
                }
            }
        }

        /*
         * Storage service starts up on the first call of
         * StorageService.getInstance().
         */
        StorageService.getInstance();

        Properties applicationProperties = new Properties();
        try {
            applicationProperties.load(new FileInputStream("etc/application.properties"));
        } catch (IOException ex) {
            logger.fatal(ex);
        }

        Locale.setDefault(new Locale((String)applicationProperties.get("locale.language"), (String)applicationProperties.get("locale.country")));

        String ui = (String) applicationProperties.get("ui");
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (ui.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            logger.fatal("Failed to change the L&F!");
        }

        logger.info("Started!");

        try {
            EventQueue.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    MainFrame.getInstance().setVisible(true);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(Starter.class.getName()).error("Unexpected Exception!", ex);
        }

        synchronized (MainFrame.getInstance()) {
            while (MainFrame.getInstance().isVisible()) {
                try {
                    MainFrame.getInstance().wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Starter.class.getName()).error("Unexpected Exception!", ex);
                }
            }
        }

        Logger.getLogger(Starter.class.getName()).info("Shutting down!");
        StorageService.getInstance().closeEntityManager();
    }

    public static Map<String, String> getProperties() {
        return properties;
    }
}
