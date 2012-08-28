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
