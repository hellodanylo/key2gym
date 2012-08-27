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
