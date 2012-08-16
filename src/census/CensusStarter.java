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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the main class of the application.
 *
 * It's responsible for the following tasks:
 * <p/>
 * 
 * <ul>
 * <li> Initializing Logging system </li>
 * <li> Reading and applying application properties.
 * <li> Launching CensusFrame </li>
 * </ul>
 *
 * @author Danylo Vashchilenko
 */
public class CensusStarter {

    private static final Logger logger = Logger.getLogger(CensusStarter.class.getName());
    private static final Map<String, String> properties = new HashMap<>();
    
    /**
     * The main method which performs all task as described in class description.
     *
     * @param args an array of arguments
     */
    public static void main(String[] args) {
        /*
         * Configures the logger using 'etc/log.properties' which should be on the
         * class path.
         */
        PropertyConfigurator.configure(CensusStarter.class.getClassLoader().getResourceAsStream("etc/log.properties"));

        logger.info("Starting...");
        
        /*
         * Puts the default values of various properties.
         */
        properties.put("storage", "default");
        
        /*
         * The array contains the names of all expected arguments.
         */
        String[] expectedArgumentsNames = new String[] {"storage"};
        
        /*
         * Parses the arguments looking for expected arguments.
         * The arguments are in the '--ARGUMENTNAME=ARGUMENTVALUE' format.
         */
        for(String arg : args) {
            for(String expectedArgumentName : expectedArgumentsNames) {
                String preffix = "--"+expectedArgumentName+"=";
                if(arg.startsWith(preffix)) {
                    properties.put(expectedArgumentName, arg.substring(preffix.length()));
                }
            }
        }
        
        /*
         * Storage service starts up on the first call of StorageService.getInstance().
         */
        StorageService.getInstance();

        //<editor-fold defaultstate="collapsed" desc="Reading and applying applicaiton properties">
        ResourceBundle applicationProperties = ResourceBundle.getBundle("etc/census");
        
        Locale.setDefault(new Locale(applicationProperties.getString("locale.language"), applicationProperties.getString("locale.language")));
        
        String ui = applicationProperties.getString("ui");
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
    
    public static Map<String, String> getProperties() {
        return properties;
    }
}
