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
public class BasicConnection {
    
    public BasicConnection(Properties properties) {
        this.properties = properties;
        this.codeName = properties.getProperty("codeName");
        this.title = properties.getProperty("title");
        this.type = properties.getProperty("type");
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
        properties.put("codeName", codeName);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        properties.put("title", title);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        properties.put("type", type);
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    private Properties properties;
    private String codeName;
    private String title;
    private String type;
}
