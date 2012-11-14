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

import com.googlecode.flyway.core.api.Version;
import com.googlecode.flyway.core.api.migration.MigrationInfoProvider;
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
public class V4JDBCMigration implements JdbcMigration, MigrationInfoProvider {

    @Override
    public void migrate(Connection connection) throws Exception {
        
        ResultSet resultSet = null;
        Statement stmt = null;
        
        List<Integer[]> administrators = new LinkedList<Integer[]>();
        
        try {
            stmt = connection.createStatement();

            /*
             * Creates the sequence for the group's primary key.
             */
            stmt.executeUpdate("CREATE SEQUENCE id_grp_seq");

            /*
             * Creates the groups table.
             */
            stmt.executeUpdate("CREATE TABLE group_grp (id_grp INTEGER NOT NULL DEFAULT(nextval('id_grp_seq')) PRIMARY KEY, name TEXT NOT NULL)");

            /*
             * Assignes the sequence to the column.
             */
            stmt.executeUpdate("ALTER SEQUENCE id_grp_seq OWNED BY group_grp.id_grp");

            /*
             * Populates the groups table.
             */
            stmt.executeUpdate("INSERT INTO group_grp (name) VALUES ('manager')");
            stmt.executeUpdate("INSERT INTO group_grp (name) VALUES ('senior_administrator')");
            stmt.executeUpdate("INSERT INTO group_grp (name) VALUES ('junior_administrator')");

            /*
             * Creates the administrator-group table.
             */
            stmt.executeUpdate("CREATE TABLE administrator_group_agr (idadm_agr INTEGER NOT NULL, idgrp_agr INTEGER NOT NULL, PRIMARY KEY (idadm_agr, idgrp_agr))");

            /*
             * Creates the view to be used by the application server.
             */
            stmt.executeUpdate("CREATE VIEW v_administrator_group AS SELECT adm.username, adm.password, grp.name FROM administrator_group_agr agr INNER JOIN administrator_adm adm ON adm.id_adm = agr.idadm_agr INNER JOIN group_grp grp ON grp.id_grp =  agr.idgrp_agr");

            /*
             * Fetches all administrators.
             */
            resultSet = stmt.executeQuery("SELECT id_adm, permissions_level FROM administrator_adm");
            
            while(resultSet.next()) {
                administrators.add(new Integer[]{resultSet.getInt("id_adm"), resultSet.getInt("permissions_level")});
            }
            
            stmt.executeUpdate("ALTER TABLE administrator_adm DROP COLUMN permissions_level");
        } finally {
            if(stmt != null) {
                stmt.close();
            }
        }

        

        /*
         * Inserts properties into altered table
         */

        PreparedStatement insertStatement = null;
        try {

           insertStatement = connection.prepareStatement("INSERT INTO administrator_group_agr (idadm_agr, idgrp_agr) VALUES (?, ?)");

            for(Integer[] administrator : administrators) {
                Integer permissionsLevel = administrator[1];

                if (permissionsLevel.equals(1)) {
                    /*
                     * Maps 1 to manager and senior_administrator.
                     */
                    insertStatement.setInt(1, administrator[0]);
                    insertStatement.setInt(2, 1);

                    insertStatement.executeUpdate();

                    insertStatement.setInt(1, administrator[0]);
                    insertStatement.setInt(2, 2);

                    insertStatement.executeUpdate();

                } else if (permissionsLevel.equals(3)) {
                    /*
                     * Maps 3 to senior_administrator.
                     */
                    insertStatement.setInt(1, administrator[0]);
                    insertStatement.setInt(2, 2);

                    insertStatement.executeUpdate();

                } else {
                    /*
                     * Maps 5 to junior_administrator.
                     */
                    insertStatement.setInt(1, administrator[0]);
                    insertStatement.setInt(2, 3);

                    insertStatement.executeUpdate();
                }
            }
        } finally {
            if (insertStatement != null) {
                insertStatement.close();
            }
        }

    }

    @Override
    public Version getVersion() {
        return new Version("4");
    }

    @Override
    public String getDescription() {
        return "init support for jdbc realm";
    }
}
