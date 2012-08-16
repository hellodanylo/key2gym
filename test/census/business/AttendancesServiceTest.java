/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.CensusStarter;
import census.business.api.BusinessException;
import census.persistence.Session;
import census.utils.SQLUtils;
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
        PropertyConfigurator.configure(CensusStarter.class.getClassLoader().getResourceAsStream("etc/log.properties"));

        CensusStarter.getProperties().put("storage", "testing");
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
