/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.persistence.connections.configurations;

import java.util.Properties;

/**
 *
 * @author Danylo Vashchilenko
 */
public class PostgreSQLEclipseLinkConnectionConfiguration extends PostgreSQLConnectionConfiguration {

    public PostgreSQLEclipseLinkConnectionConfiguration(Properties properties) {
        super(properties);
        ddl = properties.getProperty("ddl");
    }

    public String getDDL() {
        return ddl;
    }

    public void setDDL(String ddl) {
        this.ddl = ddl;
        getProperties().put("ddl", ddl);
    }
    
    private String ddl;
}
