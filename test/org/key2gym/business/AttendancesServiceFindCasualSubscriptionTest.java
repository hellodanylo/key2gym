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
package org.key2gym.business;

import java.math.BigDecimal;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.LocalTime;
import org.junit.*;
import org.key2gym.Starter;
import org.key2gym.persistence.Item;
import org.key2gym.persistence.ItemSubscription;
import org.key2gym.persistence.Session;
import org.key2gym.persistence.TimeSplit;
import static org.junit.Assert.*;

/**
 *
 * @author daniel
 */
public class AttendancesServiceFindCasualSubscriptionTest {

    public AttendancesServiceFindCasualSubscriptionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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

        morningTimeSplit = new TimeSplit(null, new LocalTime(12, 0).toDateTimeToday().toDate(), "morning");
        entityManager.persist(morningTimeSplit);

        afternoonTimeSplit = new TimeSplit(null, new LocalTime(17, 0).toDateTimeToday().toDate(), "afternoon");
        entityManager.persist(afternoonTimeSplit);

        eveningTimeSplit = new TimeSplit(null, new LocalTime(23, 0).toDateTimeToday().toDate(), "evening");
        entityManager.persist(eveningTimeSplit);
        
        morningSubscription = buildSubscription(morningTimeSplit);
        afternoonSubscription = buildSubscription(afternoonTimeSplit);
        eveningSubscription = buildSubscription(eveningTimeSplit);

        lateMorningTime = new LocalTime(7, 0);
        noonTime = new LocalTime(12, 0);
        lateNoonTime = new LocalTime(15, 0);
        eveningTime = new LocalTime(17, 0);
        lateEveningTime = new LocalTime(20, 0);

        entityManager.flush();
    }
    
    private ItemSubscription buildSubscription(TimeSplit timeSplit) {
        Item item = new Item(null, null, "", null, BigDecimal.ZERO);
        entityManager.persist(item);
        entityManager.flush();
        
        ItemSubscription subscription = new ItemSubscription(null, (short)1, (short)1, (short)0, (short)0);
        subscription.setItem(item);
        subscription.setTimeSplit(timeSplit);
        subscription.setId(item.getId());
        
        entityManager.persist(subscription);
        
        return subscription;
    }

    @After
    public void tearDown() {
        entityManager.getTransaction().rollback();
    }

    @AfterClass
    public static void tearDownClass() {
        StorageService.getInstance().destroy();
    }
    
    @Test
    public void testMorning() {
        assertEquals(morningSubscription, service.findValidCasualSubscription(lateMorningTime));
    }
    
    @Test
    public void testAfternoon() {
        assertEquals(afternoonSubscription, service.findValidCasualSubscription(noonTime));
        assertEquals(afternoonSubscription, service.findValidCasualSubscription(lateNoonTime));
    }
    
    @Test
    public void testEvening() {
        assertEquals(eveningSubscription, service.findValidCasualSubscription(eveningTime));
        assertEquals(eveningSubscription, service.findValidCasualSubscription(lateEveningTime));
    }
    
    private ItemSubscription morningSubscription;
    private ItemSubscription afternoonSubscription;
    private ItemSubscription eveningSubscription;
    private TimeSplit morningTimeSplit;
    private TimeSplit afternoonTimeSplit;
    private TimeSplit eveningTimeSplit;
    private LocalTime lateMorningTime;
    private LocalTime noonTime;
    private LocalTime lateNoonTime;
    private LocalTime eveningTime;
    private LocalTime lateEveningTime;
    private static AttendancesService service;
    private static EntityManager entityManager;
    private static Logger logger;
}
