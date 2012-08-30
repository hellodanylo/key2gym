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
package org.key2gym.persistence.connections;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.key2gym.persistence.connections.configurations.ConnectionConfiguration;

/**
 * This class is responsible for loading and managing connections.
 * <p/>
 * 
 * It uses reflection API to find classes that can read the connection
 * properties file. The manager will look for connection classes that match the
 * following criterias:
 * <p/>
 * 
 * <ul>
 * 
 * <li> a subclass of <code>ConnectionConfiguration</code> </li>
 * <li> named in the following format: &lt;CodeName&gt;ConnectionConfiguration </li>
 * <li> located in the <code>org.key2gym.presentation.connections.core</code>
 * package </li>
 * 
 * </ul>
 * <p/>
 * 
 * @author Danylo Vashchilenko
 */
public class ConnectionConfigurationsManager {

    public ConnectionConfigurationsManager() {
        Logger logger = Logger.getLogger(this.getClass().getName());

        connectionsList = new LinkedList<>();

        ClassLoader classLoader = this.getClass().getClassLoader();

        /*
         * The connections are located in './etc/connections' directory.
         */
        Path connectionsDirectory = Paths.get(System.getProperty("user.dir"), "etc", "connections");
        File[] files = connectionsDirectory.toFile().listFiles();

        /*
         * A connections file has got to gave .properties extension.
         */
        PathMatcher extensionMatcher = FileSystems.getDefault().getPathMatcher("glob:*.properties");

        for (File file : files) {

            Path fileName = file.toPath().getFileName();

            /*
             * Skips all objects except files with .properties extension.
             */
            if (!extensionMatcher.matches(fileName) || file.isDirectory()) {
                continue;
            }

            Properties properties = new Properties();

            /*
             * Each connection has a code name which its connection file's base
             * name.
             */
            String codeName = fileName.toString().split("\\.")[0];
            properties.put("codeName", codeName);

            /*
             * Attempts to load the properties file.
             */
            try(FileInputStream input = new FileInputStream(file)) {
                properties.load(input);
            } catch (IOException ex) {
                logger.error("Failed to load '" + codeName + "' as a properties file.", ex);
                continue;
            } 

            String type = properties.getProperty("type");

            String connectionClassBinaryName = ConnectionConfiguration.class.getPackage().getName() + "." + type + "ConnectionConfiguration";
            Class connectionClass;

            try {
                connectionClass = classLoader.loadClass(connectionClassBinaryName);
            } catch (ClassNotFoundException ex) {
                logger.error("Missing connection configuration class for '" + type + "': " + connectionClassBinaryName);
                continue;
            }

            if (!ConnectionConfiguration.class.isAssignableFrom(connectionClass)) {
                logger.error(connectionClass.getName() + " is of a wrong type.");
                continue;
            }

            ConnectionConfiguration connection;

            /*
             * Attemps to instantiate the connection class.
             */
            try {
                connection = (ConnectionConfiguration) connectionClass.getConstructor(Properties.class).newInstance(properties);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                logger.error("Failed to instantiate " + connectionClass.getName() + ".", ex);
                continue;
            }

            connectionsList.add(connection);

            logger.debug("Loaded connection configuration: '" + codeName + "'.");
        }
    }

    /**
     * Gets all connections.
     * 
     * @return the list of all connections.
     */
    public List<ConnectionConfiguration> getConnections() {
        return connectionsList;
    }

    /**
     * Sets the selected connection
     * 
     * @param connection the new selected connection
     */
    public void selectConnection(ConnectionConfiguration connection) {
        this.selectedConnection = connection;
    }

    /**
     * Gets the selected connection.
     * 
     * @return the selected connection
     */
    public ConnectionConfiguration getSelectedConnection() {
        return selectedConnection;
    }

    /*
     * Connections
     */
    private List<ConnectionConfiguration> connectionsList;
    private ConnectionConfiguration selectedConnection;
}
