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
package census;

import census.business.StorageService;
import census.presentation.CensusFrame;
import java.util.Locale;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the main class of the application.
 *
 * It's responsible for the following tasks:
 *
 * <ul>
 *
 * <li> Initializing Logging system </li>
 *
 * <li> Showing CensusFrame </li>
 *
 * </ul>
 *
 * @author Danylo Vashchilenko
 */
public class CensusStarter {

    private static final Logger logger = Logger.getLogger(CensusStarter.class.getName());

    /**
     * Arguments passed to the application.
     *
     * @param args an array of arguments
     */
    public static void main(String[] args) {
        /*
         * Configures the logger using 'log.properties' which should be on the
         * CLASSPATH.
         */
        PropertyConfigurator.configure(CensusStarter.class.getClassLoader().getResourceAsStream("log.properties"));

        logger.info("Starting...");
        
        /*
         * Storage service starts up the first call of StorageService.getInstance().
         */
        StorageService.getInstance();

        //<editor-fold defaultstate="collapsed" desc="Debugging code">
        Locale.setDefault(new Locale("ru", "RU"));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Changes the L&F to Nimbus">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            logger.fatal("Failed to change the L&F!");
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Creates and launches a CensusFrame">
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                CensusFrame.getInstance().setVisible(true);
            }
        });
        //</editor-fold>

        logger.info("Started!");
    }
}
