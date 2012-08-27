/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.connections.core;

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
 * <li> a subclass of <code>BasicConnection</code> </li>
 * <li> named in the following format: &lt;CodeName&gt;Connection </li>
 * <li> located in the <code>org.key2gym.presentation.connections.core</code>
 * package </li>
 * 
 * </ul>
 * <p/>
 * 
 * @author Danylo Vashchilenko
 */
public class ConnectionsManager {

    public ConnectionsManager() {
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

            String connectionClassBinaryName = BasicConnection.class.getPackage().getName() + "." + type + "Connection";
            Class connectionClass;

            try {
                connectionClass = classLoader.loadClass(connectionClassBinaryName);
            } catch (ClassNotFoundException ex) {
                logger.error("Missing connection class for '" + type + "': " + connectionClassBinaryName);
                continue;
            }

            if (!BasicConnection.class.isAssignableFrom(connectionClass)) {
                logger.error(connectionClass.getName() + " is of a wrong type.");
                continue;
            }

            BasicConnection connection;

            /*
             * Attemps to instantiate the connection class.
             */
            try {
                connection = (BasicConnection) connectionClass.getConstructor(Properties.class).newInstance(properties);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                logger.error("Failed to instantiate " + connectionClass.getName() + ".", ex);
                continue;
            }

            connectionsList.add(connection);

            logger.debug("Loaded connection: '" + codeName + "'.");
        }
    }

    /**
     * Gets all connections.
     * 
     * @return the list of all connections.
     */
    public List<BasicConnection> getConnections() {
        return connectionsList;
    }

    /**
     * Sets the selected connection
     * 
     * @param connection the new selected connection
     */
    public void selectConnection(BasicConnection connection) {
        this.selectedConnection = connection;
    }

    /**
     * Gets the selected connection.
     * 
     * @return the selected connection
     */
    public BasicConnection getSelectedConnection() {
        return selectedConnection;
    }

    /*
     * Connections
     */
    private List<BasicConnection> connectionsList;
    private BasicConnection selectedConnection;
}
