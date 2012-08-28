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
