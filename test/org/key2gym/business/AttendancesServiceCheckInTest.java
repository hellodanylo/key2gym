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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateMidnight;
import org.joda.time.LocalTime;
import org.junit.*;
import static org.junit.Assert.*;
import org.key2gym.Starter;
import org.key2gym.persistence.*;

/**
 *
 * @author daniel
 */
public class AttendancesServiceCheckInTest {

    public AttendancesServiceCheckInTest() {
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
        
        logger.info("Started transaction.");

        keys = new LinkedList<>();
        for (int i = 0; i < 5; i++) {

            Key key = new Key();
            key.setTitle("key");

            keys.add(key);
            entityManager.persist(key);
        }

        clients = new LinkedList<>();

        Client client;
        client = new Client(1);
        client.setFullName("");
        client.setAttendancesBalance(0);
        client.setCard(12345678);
        client.setMoneyBalance(BigDecimal.ZERO);
        client.setRegistrationDate(new Date());
        client.setExpirationDate(new DateMidnight().plusDays(10).toDate());
        client.setAttendancesBalance( 5);
        client.setNote("");
        clients.add(client);
        entityManager.persist(client);
        
        TimeSplit timeSplit = new TimeSplit(null, new LocalTime().plusHours(1).toDateTimeToday().toDate(), "");
        entityManager.persist(timeSplit);
        entityManager.flush();
        
        Item item = new Item(null, null, "", null, BigDecimal.ZERO);
        entityManager.persist(item);
        entityManager.flush();
        
        subscriptions = new LinkedList<>();
        
        ItemSubscription subscription = new ItemSubscription(null, 1, 1, 0, 0);
        subscription.setItem(item);
        subscription.setTimeSplit(timeSplit);
        subscription.setId(item.getId());
        subscriptions.add(subscription);
        entityManager.persist(subscription);
        
        entityManager.flush();
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
    public void testNormalRegisteredClient() {
        Object result = null;
        Client client = clients.get(0);
        Key key = keys.get(0);

        try {
            result = service.checkInRegisteredClient(client.getId(), key.getId());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        // The ID should be valid
        assertNotNull(entityManager.find(Attendance.class, result));

        // The client's attendances balance should have been decreased
        assertEquals(4, (int)client.getAttendancesBalance());

        // An ttendance should have been recorded
        assertEquals(1, client.getAttendances().size());

        // The attendance should have the correct key
        assertEquals(key.getId(), client.getAttendances().get(0).getKey().getId());
    }
    @Test
    public void testNormalCasualClient() {
        Object result = null;
        Key key = keys.get(1);
        Attendance attendance;
        
        try {
            result = service.checkInCasualClient(key.getId());
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
        
        attendance = entityManager.find(Attendance.class, result);
        
        // The ID should be valid
        assertNotNull(attendance);
        
        // The attendance's client should be NULL
        assertNull(attendance.getClient());
        
        // The order should contain an appropriate subscription
        assertNotNull(attendance.getOrder());
        assertNotNull(attendance.getOrder().getOrderLines());
        assertEquals(attendance.getOrder().getOrderLines().size(), 1);
        assertEquals(attendance.getOrder().getOrderLines().get(0).getItem(), subscriptions.get(0).getItem());
    }

    private List<Key> keys;
    private List<Client> clients;
    private List<ItemSubscription> subscriptions;
    
    private static AttendancesService service;
    private static EntityManager entityManager;
    private static Logger logger;
}
