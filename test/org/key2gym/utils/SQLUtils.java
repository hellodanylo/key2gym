/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.utils;

import java.io.IOException;
import javax.persistence.EntityManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SQLUtils {
    public static void executeSQL(EntityManager em, String scriptName) throws IOException {
        String[] queries = FileUtils.readFile("../test/census/sql/"+scriptName+".sql").split(";");
        
        for(String query : queries) {
            em.createNativeQuery(query).executeUpdate();
        }
    }
}
