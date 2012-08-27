/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.factories.connections;

import java.util.Properties;
import org.key2gym.presentation.connections.core.BasicConnection;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface PropertiesFactory<T extends BasicConnection> {
    public Properties createEntityManagerFactoryProperties(T connection);
    public Properties createEntityManagerProperties(T conneciton);
}
