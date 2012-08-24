/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.business;

import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.LocalTime;
import org.junit.*;
import org.key2gym.Starter;
import org.key2gym.persistence.Session;
import org.key2gym.persistence.TimeSplit;
import static org.junit.Assert.*;

/**
 *
 * @author daniel
 */
public class AttendancesServiceCalculatePenaltiesTest {

    public AttendancesServiceCalculatePenaltiesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure(Starter.class.getClassLoader().getResourceAsStream("etc/log.properties"));

        // Forces the StorageService to connect to the testing storage.
        Starter.getProperties().put("storage", "testing");

        entityManager = StorageService.getInstance().getEntityManager();
        service = AttendancesService.getInstance();
        logger = Logger.getLogger(AttendancesServiceCheckInTest.class.getName());

        /*
         * Sets an empty session as the current one. This is done just to pass
         * the session check. We would have to create a real session to call
         * code that actually depends on a session being open.
         */
        SessionsService.getInstance().setSession(new Session());
    }

    @Before
    public void setUp() {
        entityManager.getTransaction().begin();
        entityManager.getTransaction().setRollbackOnly();

        morning = new TimeSplit(null, new LocalTime(12, 0).toDateTimeToday().toDate(), "morning");
        entityManager.persist(morning);

        afternoon = new TimeSplit(null, new LocalTime(17, 0).toDateTimeToday().toDate(), "afternoon");
        entityManager.persist(afternoon);

        evening = new TimeSplit(null, new LocalTime(22, 0).toDateTimeToday().toDate(), "evening");
        entityManager.persist(evening);

        morningTime = new LocalTime(7, 0);
        lateMorningTime = new LocalTime(9, 0);
        
        noonTime = new LocalTime(12, 0);
        lateNoonTime = new LocalTime(15, 0);
        
        eveningTime = new LocalTime(17, 0);
        midEveningTime = new LocalTime(19, 0);
        lateEveningTime = new LocalTime(23, 0);

        entityManager.flush();
    }

    @After
    public void tearDown() {
        entityManager.getTransaction().rollback();
    }

    @AfterClass
    public static void tearDownClass() {
        StorageService.getInstance().closeEntityManager();
    }

    @Test
    public void testMorning() {
        assertEquals(0, service.calculatePenalties(morning, morningTime));
        assertEquals(0, service.calculatePenalties(morning, lateMorningTime));
        assertEquals(1, service.calculatePenalties(morning, noonTime));
        assertEquals(1, service.calculatePenalties(morning, lateNoonTime));
        assertEquals(2, service.calculatePenalties(morning, eveningTime));
        assertEquals(2, service.calculatePenalties(morning, midEveningTime));
        assertEquals(2, service.calculatePenalties(morning, lateEveningTime));
    }

    @Test
    public void testAfternoon() {
        assertEquals(0, service.calculatePenalties(afternoon, morningTime));
        assertEquals(0, service.calculatePenalties(morning, lateMorningTime));
        assertEquals(0, service.calculatePenalties(afternoon, noonTime));
        assertEquals(0, service.calculatePenalties(afternoon, lateNoonTime));
        assertEquals(1, service.calculatePenalties(afternoon, eveningTime));
        assertEquals(1, service.calculatePenalties(afternoon, midEveningTime));
        assertEquals(1, service.calculatePenalties(afternoon, lateEveningTime));
    }

    @Test
    public void testEvening() {
        assertEquals(0, service.calculatePenalties(evening, morningTime));
        assertEquals(0, service.calculatePenalties(evening, noonTime));
        assertEquals(0, service.calculatePenalties(evening, lateNoonTime));
        assertEquals(0, service.calculatePenalties(evening, eveningTime));
        assertEquals(0, service.calculatePenalties(evening, midEveningTime));
        assertEquals(0, service.calculatePenalties(evening, lateEveningTime));
    }
    private TimeSplit morning;
    private TimeSplit afternoon;
    private TimeSplit evening;
    private LocalTime morningTime;
    private LocalTime lateMorningTime;
    private LocalTime noonTime;
    private LocalTime lateNoonTime;
    private LocalTime eveningTime;
    private LocalTime midEveningTime;
    private LocalTime lateEveningTime;
    private static AttendancesService service;
    private static EntityManager entityManager;
    private static Logger logger;
}
