/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.business;

import org.key2gym.business.StorageService;
import org.key2gym.business.AttendancesService;
import org.key2gym.business.SessionsService;
import org.key2gym.Starter;
import org.key2gym.business.api.BusinessException;
import org.key2gym.persistence.Session;
import org.key2gym.utils.SQLUtils;
import java.io.IOException;
import javax.persistence.EntityManager;
import org.apache.log4j.PropertyConfigurator;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author daniel
 */
public class AttendancesServiceTest {

    public AttendancesServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        PropertyConfigurator.configure(Starter.class.getClassLoader().getResourceAsStream("etc/log.properties"));

        Starter.getProperties().put("storage", "testing");
        StorageService.getInstance().beginTransaction();

        EntityManager entityManager = StorageService.getInstance().getEntityManager();
        SQLUtils.executeSQL(entityManager, "clean");
        SQLUtils.executeSQL(entityManager, "attendances");

        instance = AttendancesService.getInstance();

        /*
         * Sets an empty session as the current one. This is done just to pass
         * the session check. We would have to create a real session to call
         * code that actually depends on a session being open.
         */
        SessionsService.getInstance().setSession(new Session());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of checkInRegisteredClient method, of class AttendancesService.
     */
    @Test
    public void testCheckInRegisteredClient() throws Exception {
        System.out.println("checkInRegisteredClient");
        
        assertEquals(Short.valueOf((short) 1), instance.checkInRegisteredClient(Short.valueOf((short) 1), Short.valueOf((short) 1)));

        Short result = null;

        try {
            result = instance.checkInRegisteredClient(Short.valueOf((short) 2), Short.valueOf((short) 2));
        } catch (BusinessException ex) {
        }
        assertNull(result);

        try {
            result = instance.checkInRegisteredClient(Short.valueOf((short) 3), Short.valueOf((short) 3));
        } catch (BusinessException ex) {
        }
        assertNull(result);

        try {
            result = instance.checkInRegisteredClient(Short.valueOf((short) 4), Short.valueOf((short) 4));
        } catch (BusinessException ex) {
        }
        assertNull(result);
    }
    
    private AttendancesService instance;
}
