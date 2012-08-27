/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.presentation.connections.core;

import java.util.Properties;

/**
 *
 * @author Danylo Vashchilenko
 */
public class NetworkConnection extends BasicConnection {
    
    public NetworkConnection(Properties properties) {
        super(properties);
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        getProperties().put("host", host);
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
        getProperties().put("port", port);
    }
   
    public static boolean isSupported(String type) {
        return false;
    }
    
    private String host;
    private String port;
}
