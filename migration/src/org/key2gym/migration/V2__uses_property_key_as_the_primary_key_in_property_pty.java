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
package org.key2gym.migration;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Danylo Vashchilenko
 */
public class V2__uses_property_key_as_the_primary_key_in_property_pty implements JdbcMigration {

    @Override
    public void migrate(Connection connection) throws Exception {

        List<String[]> properties = new LinkedList<String[]>();

        Statement stmt = null;
        ResultSet oldPropertiesResultSet = null;

        /*
         * Fetches all properties from the table
         */
        try {
            stmt = connection.createStatement();
            oldPropertiesResultSet = stmt.executeQuery("SELECT * FROM property_pty");

            while (oldPropertiesResultSet.next()) {
                properties.add(new String[]{oldPropertiesResultSet.getString("name"),
                            oldPropertiesResultSet.getString("current_value")});
            }
        } finally {
            if (oldPropertiesResultSet != null) {
                oldPropertiesResultSet.close();
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        /*
         * Removes all properties
         */
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM property_pty");
        } finally {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        /*
         * Alters the type of the table's primary key
         */
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("ALTER TABLE property_pty ALTER COLUMN id_pty TYPE text");
        } finally {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        /*
         * Removes obsolete column
         */
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("ALTER TABLE property_pty DROP COLUMN name");
        } finally {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

        /*
         * Renames current_value column to property_value
         */
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("ALTER TABLE property_pty RENAME COLUMN current_value TO property_value");
        } finally {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }
        
        PreparedStatement insertStatement = null;

        /*
         * Inserts properties into altered table
         */
        try {
            insertStatement = connection.prepareStatement("INSERT INTO property_pty (id_pty, property_value) VALUES (?, ?)");
            
            for(String[] property : properties) {
                insertStatement.setString(1, property[0]);
                insertStatement.setString(2, property[1]);
                
                insertStatement.executeUpdate();
            }
        } finally {
            if (insertStatement != null) {
                insertStatement.close();
                insertStatement = null;
            }
        }

    }
}
