package org.key2gym.business;

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


import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.AttendanceDTO;
import org.key2gym.business.api.local.OrdersServiceLocal;
import org.key2gym.business.api.remote.AttendancesServiceRemote;
import org.key2gym.persistence.Attendance;
import org.key2gym.persistence.Client;
import org.key2gym.persistence.ItemSubscription;
import org.key2gym.persistence.Key;
import org.key2gym.persistence.OrderEntity;
import org.key2gym.persistence.OrderLine;
import org.key2gym.persistence.Property;
import org.key2gym.persistence.TimeSplit;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(AttendancesServiceRemote.class)
@DeclareRoles({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
public class AttendancesServiceBean extends BasicBean implements AttendancesServiceRemote {

    @Override
    public Integer checkInRegisteredClient(Integer clientId, Integer keyId)
            throws BusinessException, ValidationException, SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        Client client;
        Key key;

        /*
         * Arguments validation.
         */
        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        if (keyId == null) {
            throw new NullPointerException("The keyId is null."); //NOI18N
        }

        client = (Client) entityManager.find(Client.class, clientId, LockModeType.OPTIMISTIC);
        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        key = entityManager.find(Key.class, keyId);
        if (key == null) {
            throw new ValidationException(getString("Invalid.Key.ID"));
        }

        if (!entityManager.createNamedQuery("Key.findAvailable").getResultList().contains(key)) { //NOI18N
            throw new BusinessException(getString("BusinessRule.Key.NotAvailable"));
        }

        /*
         * Client should have attendances.
         */
        if (client.getAttendancesBalance() < 1) {
            throw new BusinessException(getString("BusinessRule.Client.NoAttendancesLeft"));
        }

        /*
         * Client's can't be expired.
         */
        if (client.getExpirationDate().compareTo(new Date()) < 0) {
            throw new BusinessException(getString("BusinessRule.Client.SubscriptionExpired"));
        }

        client.setAttendancesBalance((Integer) (client.getAttendancesBalance() - 1));

        /*
         * Builds an attendance record.
         */
        Attendance attendance = new Attendance();
        attendance.setClient(client);
        attendance.setKey(key);
        attendance.setDatetimeBegin(new Date());
        attendance.setDatetimeEnd(Attendance.DATETIME_END_UNKNOWN);

        /*
         * Finds the client's current subscription.
         */
        ItemSubscription itemSubscription;

        List<ItemSubscription> itemSubscriptions = (List<ItemSubscription>) entityManager.createNamedQuery("ItemSubscription.findByClientOrderByDateRecordedDesc") //NOI18N
                .setParameter("client", client) //NOI18N
                .getResultList();

        if (!itemSubscriptions.isEmpty()) {
            itemSubscription = itemSubscriptions.get(0);

            /*
             * Calculates the quantity of penalties to apply.
             */
            int penalties = calculatePenalties(itemSubscription.getTimeSplit(), new LocalTime());

            /*
             * If there are penalties to apply, does it.
             */
            if (penalties > 0) {
                
                OrdersServiceLocal ordersService;
                
                try {
                    ordersService = InitialContext.doLookup(OrdersServiceLocal.class.getSimpleName());
                } catch (NamingException ex) {
                    throw new EJBException(ex);
                }
                
                Integer orderId = ordersService.findByClientIdAndDate(clientId, new DateMidnight(), true);
                Integer itemId = entityManager.find(Property.class, "time_range_mismatch_penalty_item_id").getInteger();

                ordersService.addPurchase(orderId, itemId, null, penalties);
            }
        }

        entityManager.persist(attendance);
        client.getAttendances().add(attendance);
        entityManager.flush();

        return attendance.getId();
    }

    @Override
    public Integer checkInCasualClient(Integer keyId)
            throws BusinessException, ValidationException, SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        Attendance attendance;
        Key key;
        OrderEntity order;

        /*
         * Arguments validation.
         */
        if (keyId == null) {
            throw new NullPointerException("The keyId is null."); //NOI18N
        }

        key = entityManager.find(Key.class, keyId);

        if (key == null) {
            throw new ValidationException(getString("Invalid.Key.ID"));
        }

        if (!entityManager.createNamedQuery("Key.findAvailable").getResultList().contains(key)) { //NOI18N
            throw new BusinessException(getString("BusinessRule.Key.NotAvailable"));
        }

        /*
         * Build an attendance record.
         */
        attendance = new Attendance();
        attendance.setDatetimeBegin(new Date());
        attendance.setDatetimeEnd(Attendance.DATETIME_END_UNKNOWN);
        attendance.setKey(key);

        order = new OrderEntity();
        order.setAttendance(attendance);
        order.setDate(attendance.getDatetimeBegin());
        order.setPayment(BigDecimal.ZERO);

        ItemSubscription itemSubscription = findValidCasualSubscription(new LocalTime());
        if (itemSubscription == null) {
            throw new BusinessException(getString("BusinessRule.Attendance.Casual.SubscriptionNotAvailable"));
        }

        OrderLine orderLine = new OrderLine();
        orderLine.setItem(itemSubscription.getItem());
        orderLine.setOrder(order);
        orderLine.setQuantity((Integer) 1);

        List<OrderLine> orderLines = new LinkedList<OrderLine>();
        orderLines.add(orderLine);
        order.setOrderLines(orderLines);

        attendance.setOrder(order);

        entityManager.persist(orderLine);
        entityManager.persist(order);
        entityManager.persist(attendance);

        return attendance.getId();
    }

    @Override
    public AttendanceDTO getAttendanceById(Integer attendanceId) throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        if (attendanceId == null) {
            throw new NullPointerException("The attendanceId is null."); //NOI18N
        }

        Attendance attendance = entityManager.find(Attendance.class, attendanceId);

        if (attendance == null) {
            return null;
        }

        AttendanceDTO attendanceDTO = wrapAttendance(attendance);
        return attendanceDTO;
    }

    @Override
    public List<AttendanceDTO> findAttendancesByDate(DateMidnight date)
            throws SecurityViolationException {

        /*
         * Arguments validation.
         */
        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        if (!date.equals(DateMidnight.now()) && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityException(getString("Security.Access.Denied"));
        }

        List<Attendance> attendances = entityManager.createNamedQuery("Attendance.findByDatetimeBeginRangeOrderByDateTimeBeginDesc") //NOI18N
                .setParameter("low", date.toDate()) //NOI18N
                .setParameter("high", date.plusDays(1).toDate()) //NOI18N
                .getResultList();
        List<AttendanceDTO> result = new LinkedList<AttendanceDTO>();

        for (Attendance attendance : attendances) {
            result.add(wrapAttendance(attendance));
        }

        return result;
    }

    @Override
    public List<AttendanceDTO> findAttendancesByClient(Integer id)
            throws ValidationException, SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        if (id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }

        Client client = entityManager.find(Client.class, id);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        List<Attendance> attendances = entityManager.createNamedQuery("Attendance.findByClientOrderByDateTimeBeginDesc").setParameter("client", client).getResultList();

        List<AttendanceDTO> result = new LinkedList<AttendanceDTO>();

        for (Attendance attendance : attendances) {
            result.add(wrapAttendance(attendance));
        }

        return result;
    }

    @Override
    public Boolean isCasual(Integer attendanceId) throws ValidationException, SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        if (attendanceId == null) {
            throw new NullPointerException("The attendanceId is null."); //NOI18N
        }

        Attendance attendance = entityManager.find(Attendance.class, attendanceId);

        if (attendance == null) {
            throw new ValidationException(getString("Invalid.Attendance.ID"));
        }

        return attendance.getClient() == null;
    }

    @Override
    public void checkOut(Integer attendanceId) throws BusinessException, ValidationException, SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (attendanceId == null) {
            throw new NullPointerException("The attendanceId is null."); //NOI18N
        }
        Attendance attendance = entityManager.find(Attendance.class, attendanceId, LockModeType.OPTIMISTIC);

        if (attendance == null) {
            throw new ValidationException(getString("Invalid.Attendance.ID"));
        }

        if (!attendance.getDatetimeEnd().equals(Attendance.DATETIME_END_UNKNOWN)) {
            throw new BusinessException(getString("BusinessRule.Attendance.AlreadyClosed"));
        }

        OrderEntity order = attendance.getOrder();

        /*
         * If there is an order associated with the attendance, the attendance
         * is anonymous, and, therefore, the order should have a full payment.
         */
        if (order != null) {
            if (order.getTotal().compareTo(order.getPayment()) != 0) {
                throw new BusinessException(getString("BusinessRule.Attendance.Casual.ExactPaymentRequiredToClose"));
            }
        }

        attendance.setDatetimeEnd(new Date());

        entityManager.flush();
    }

    @Override
    public Integer findOpenAttendanceByKey(Integer keyId) throws ValidationException, BusinessException, SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        /*
         * Arguments validation.
         */
        if (keyId == null) {
            throw new NullPointerException("The keyId is null."); // NOI18N
        }

        Key key = entityManager.find(Key.class, keyId);

        if (key == null) {
            throw new ValidationException(getString("Invalid.Key.ID"));
        }

        Attendance attendance;
        try {
            attendance = (Attendance) entityManager.createNamedQuery("Attendance.findOpenByKey") //NOI18N
                    .setParameter("key", key) //NOI18N
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new BusinessException(getString("Invalid.Key.NoOpenAttendance"));
        }

        return attendance.getId();
    }

    /**
     * Builds an attendance DTO from an attendance entity.
     *
     * @param attendance the entity to build DTO from
     * @return the DTO
     */
    public AttendanceDTO wrapAttendance(Attendance attendance) {
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(attendance.getId());
        attendanceDTO.setDateTimeBegin(new DateTime(attendance.getDatetimeBegin()));
        attendanceDTO.setClientId(attendance.getClient() == null ? null : attendance.getClient().getId());
        attendanceDTO.setClientFullName(attendance.getClient() == null ? null : attendance.getClient().getFullName());
        attendanceDTO.setKeyId(attendance.getKey().getId());
        attendanceDTO.setKeyTitle(attendance.getKey().getTitle());
        attendanceDTO.setDateTimeEnd(attendance.getDatetimeEnd().equals(Attendance.DATETIME_END_UNKNOWN)
                ? null
                : new DateTime(attendance.getDatetimeEnd()));

        return attendanceDTO;
    }

    /**
     * Finds the current time split.
     *
     * @return the current time split or null, if none is found
     */
    public TimeSplit findTimeSplitForTime(LocalTime time) {
        List<TimeSplit> timeSplits = entityManager.createNamedQuery("TimeSplit.findAll") //NOI18N
                .getResultList();

        LocalTime now = time;
        LocalTime begin = now;

        for (TimeSplit timeSplit : timeSplits) {
            LocalTime end = new LocalTime(timeSplit.getTime());

            if (now.compareTo(begin) >= 0 && now.compareTo(end) < 0) {
                return timeSplit;
            }

            begin = end;
        }

        return null;
    }

    /**
     * Calculates the quantity of penalties for a client with give time split and
     * the time.
     * 
     * @param timeSplit the client's time split
     * @param time the time to use when calculating
     * @return the quantity of penalties
     */
    public int calculatePenalties(TimeSplit timeSplit, LocalTime time) {

        List<TimeSplit> timeSplits = entityManager.createNamedQuery("TimeSplit.findAll") //NOI18N
                .getResultList();

        int penalties = -1;
        LocalTime now = time;
        LocalTime begin = now;

        for (TimeSplit aTimeSplit : timeSplits) {
            LocalTime end = new LocalTime(aTimeSplit.getTime());

            if (aTimeSplit.equals(timeSplit)) {
                penalties = 0;
            } else if (penalties != -1) {
                penalties++;
            }

            if (now.compareTo(begin) >= 0 && now.compareTo(end) < 0) {
                break;
            }

            begin = end;
        }

        return penalties == -1 ? 0 : penalties;
    }

    /**
     * Finds an anonymous subscription appropriate for the time.
     *
     * @return the subscription, or null if none is found
     */
    public ItemSubscription findValidCasualSubscription(LocalTime time) {
        TimeSplit currentTimeSplit = findTimeSplitForTime(time);

        /*
         * No subscription can be valid now, if the current time split is null.
         */
        if (currentTimeSplit == null) {
            return null;
        }

        ItemSubscription itemSubscription;
        try {
            itemSubscription = (ItemSubscription) entityManager.createNamedQuery("ItemSubscription.findCasualByTimeSplit") //NOI18N
                    .setParameter("timeSplit", currentTimeSplit) //NOI18N
                    .getSingleResult();
        } catch (NoResultException ex) {
            itemSubscription = null;
        }
        return itemSubscription;
    }
    
    @PersistenceContext(unitName = "PU")
    private EntityManager entityManager;

    @EJB
    private OrdersServiceLocal ordersService;
}
