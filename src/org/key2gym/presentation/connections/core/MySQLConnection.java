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
public class MySQLConnection extends NetworkConnection {
    public MySQLConnection(Properties properties) {
        super(properties);
        this.database = properties.getProperty("database");
        this.user = properties.getProperty("user");
        this.password = properties.getProperty("password");
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
        getProperties().put("database", database);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        getProperties().put("password", password);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
        getProperties().put("user", user);
    }
    
    public static boolean isSupported(String type) {
        return type.equals("mysql");
    }
    
    private String user;
    private String password;
    private String database;
}
