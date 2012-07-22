/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.AttendanceDTO;
import census.business.dto.ItemDTO;
import census.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.NoResultException;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AttendancesService extends BusinessService {

    /**
     * Opens an attendance and associates it with the client.
     *
     * <ul>
     *
     * <li>The client's attendances balance has to be more than 0 <li>The
     * expiration date has to be no sooner than tomorrow <li>The key has to have
     * 0 open attendances associated with it
     *
     * </ul>
     *
     * @param clientId the client's ID
     * @param keyId the key's ID
     * @return the new attendance's ID
     * @throws NullPointerException if any of the arguments is null
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws IllegalStateException if the transaction or the session is not
     * active
     */
    public Short openClientAttendance(Short clientId, Short keyId)
            throws BusinessException, ValidationException {
        Client client;
        Key key;

        assertSessionActive();
        assertTransactionActive();

        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        if (keyId == null) {
            throw new NullPointerException("The keyId is null."); //NOI18N
        }

        client = (Client) entityManager.find(Client.class, clientId);
        
        if(client == null) {
            throw new ValidationException(bundle.getString("ClientIDInvalid"));
        }

        key = entityManager.find(Key.class, keyId);

        if (key == null) {
            throw new ValidationException(bundle.getString("KeyIDInvalid"));
        }

        if (!entityManager.createNamedQuery("Key.findAvailable").getResultList().contains(key)) { //NOI18N
            throw new BusinessException(bundle.getString("KeyNotAvailable"));
        }

        if (client.getAttendancesBalance() < 1) {
            throw new BusinessException(bundle.getString("ClientNoAttendancesLeft"));
        }

        if (client.getExpirationDate().compareTo(new Date()) < 0) {
            throw new BusinessException(bundle.getString("ClientSubscriptionExpired"));
        }

        client.setAttendancesBalance((short) (client.getAttendancesBalance() - 1));

        Attendance attendance = new Attendance();
        attendance.setId(getNextId());
        attendance.setClient(client);
        attendance.setKey(key);
        attendance.setDatetimeBegin(new Date());
        attendance.setDatetimeEnd(Attendance.DATETIME_END_UNKNOWN);
        
        Object[] subscriptions = (Object[])entityManager
                .createNamedQuery("ItemSubscriptionAndDateRecorded.findByClientOrderByDateRecordedDesc") //NOI18N
                .setParameter("client", client) //NOI18N
                .setMaxResults(1)
                .getSingleResult();
        
        ItemSubscription itemSubscription = (ItemSubscription)subscriptions[0];
        
        List<TimeRange> timeRanges = entityManager
                .createNamedQuery("TimeRange.findAll") //NOI18N
                .getResultList();
        int over = -1;
        LocalTime now = new LocalTime();
        for (TimeRange timeRange : timeRanges) {
            LocalTime begin = new LocalTime(timeRange.getTimeBegin().getTime());
            LocalTime end = new LocalTime(timeRange.getTimeEnd().getTime());

            if(timeRange.equals(itemSubscription.getTimeRange())) {
                over = 0;
            } else if(over != -1) {
                over++;
            } 
            
            if(now.isAfter(begin) && now.isBefore(end)) {
                break;
            }
        }
        
        Short orderId = OrdersService.getInstance().findByClientIdAndDate(clientId, new DateMidnight(), Boolean.TRUE);
        Short itemId = Short.valueOf(((Property)entityManager
                .createNamedQuery("Property.findByName") //NOI18N
                .setParameter("name", "time_range_mismatch_penalty_item_id") //NOI18N
                .getSingleResult())
                .getValue());
                
        try {       
            for(int i = 0; i < over;i++) {
                OrdersService.getInstance().addPurchase(orderId, itemId);
            }
        } catch (SecurityException ex) {
            throw new RuntimeException("Unexpected SecurityException.");
        }

        // TODO: note changes
        entityManager.persist(attendance);
        entityManager.flush();

        return attendance.getId();
    }

    /**
     * Opens an anonymous attendance.
     *
     * <ul>
     *
     * <li>There should be a subscription having 1 attendance unit, a term of 1
     * day and a time range allowing to open an attendance right now.
     *
     * </ul>
     *
     * @param keyId the key's ID.
     * @return the new attendance's ID.
     * @throws NullPointerException if keyId is null
     * @throws ValidationExceltion if the key's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws IllegalStateException if the transaction or the session is not
     * active
     */
    public Short openAnonymousAttendance(Short keyId)
            throws BusinessException, ValidationException {
        Attendance attendance;
        Key key;
        OrderEntity order;

        assertSessionActive();
        assertTransactionActive();

        if (keyId == null) {
            throw new NullPointerException("The keyId is null."); //NOI18N
        }

        key = entityManager.find(Key.class, keyId);

        if (key == null) {
            throw new ValidationException(bundle.getString("KeyIDInvalid"));
        }

        attendance = new Attendance();
        attendance.setId(getNextId());
        attendance.setDatetimeBegin(new Date());
        attendance.setDatetimeEnd(Attendance.DATETIME_END_UNKNOWN);
        attendance.setKey(key);

        order = new OrderEntity();
        order.setId(OrdersService.getInstance().getNextId());
        order.setAttendance(attendance);
        order.setDate(attendance.getDatetimeBegin());
        order.setPayment(BigDecimal.ZERO);

        ItemSubscription itemSubscription = findValidAnonymousSubscriptionForNow();
        if (itemSubscription == null) {
            throw new BusinessException(bundle.getString("AttendanceAnonymousSubscriptionNotAvailable"));
        }
        
        OrderLine orderLine = new OrderLine();
        orderLine.setItem(itemSubscription.getItem());
        orderLine.setOrder(order);
        orderLine.setQuantity((short)1);
        
        List<OrderLine> orderLines = new LinkedList<>();
        orderLines.add(orderLine);
        order.setOrderLines(orderLines);

        attendance.setOrder(order);

        entityManager.persist(orderLine);
        entityManager.persist(order);
        entityManager.persist(attendance);
        entityManager.flush();

        return attendance.getId();
    }

    /**
     * Finds an attendance by its ID.
     *
     * <ul>
     *
     * <li>The permission level has to be ALL to access attendances open in the
     * past.
     *
     * </ul>
     *
     * @param attendanceId the attendance's ID
     * @return the attendance, or null, if none was found
     * @throws NullPointerException if attendanceId is null
     * @throws ValidationException if attendanceId invalid
     * @throws SecurityException if current security rules restrict this
     * operation
     * @throws IllegalStateException if the session is not active
     */
    public AttendanceDTO getAttendanceById(Short attendanceId)
            throws IllegalArgumentException, SecurityException {

        assertSessionActive();

        if (attendanceId == null) {
            throw new NullPointerException("The attendanceId is null."); //NOI18N
        }

        Attendance attendance = entityManager.find(Attendance.class, attendanceId);

        if (attendance == null) {
            return null;
        }

        if (attendance.getDatetimeBegin().before(new DateMidnight().toDate()) &&
            SessionsService.getInstance().getPermissionsLevel() != SessionsService.PL_ALL) {
            throw new SecurityException(bundle.getString("AccessDenied"));
        }

        AttendanceDTO attendanceDTO = new AttendanceDTO();

        attendanceDTO.setClientFullName(attendance.getClient() == null ? null : attendance.getClient().getFullName());
        attendanceDTO.setClientId(attendance.getClient() == null ? null : attendance.getClient().getId());
        attendanceDTO.setId(attendanceId);
        attendanceDTO.setDateTimeBegin(new DateTime(attendance.getDatetimeBegin()));
        attendanceDTO.setDateTimeEnd(attendance.getDatetimeEnd().equals(Attendance.DATETIME_END_UNKNOWN)
                ? null
                : new DateTime(attendance.getDatetimeEnd()));
        attendanceDTO.setKeyId(attendance.getKey().getId());
        attendanceDTO.setKeyTitle(attendance.getKey().getTitle());

        return attendanceDTO;
    }

    /**
     * Finds all attendance that were open on the date.
     *
     * <ul>
     *
     * <li>The permission level has to be ALL to access attendances open not
     * today.
     *
     * </ul>
     *
     * @param date the date
     * @return the list of all attendances open on the date
     * @throws NullPointerException if the date is null
     * @throws SecurityException if current security rules restrict this
     * operation
     * @throws IllegalStateException if the session is not active
     */
    public List<AttendanceDTO> findAttendancesByDate(DateMidnight date)
            throws SecurityException {

        assertSessionActive();

        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        if (!date.equals(new DateMidnight()) && !SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("AccessDenied"));
        }

        List<Attendance> attendances = entityManager.createNamedQuery("Attendance.findByDatetimeBeginRangeOrderByDateTimeBeginDesc") //NOI18N
                .setParameter("low", date.toDate()) //NOI18N
                .setParameter("high", date.plusDays(1).toDate()) //NOI18N
                .getResultList();
        List<AttendanceDTO> result = new LinkedList<>();

        for (Attendance attendance : attendances) {
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

            result.add(attendanceDTO);
        }

        return result;
    }

    /**
     * Finds the client's attendances.
     *
     * @param id the client's ID
     * @return the list of the client's attendances
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the client's ID is invalid
     * @throws IllegalStateException if the session is not active
     */
    public List<AttendanceDTO> findAttendancesByClient(Short id)
            throws ValidationException {

        assertSessionActive();

        if (id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }

        Client client = entityManager.find(Client.class, id);

        if (client == null) {
            throw new ValidationException(bundle.getString("ClientIDInvalid"));
        }

        List<Attendance> attendances = entityManager
                .createNamedQuery("Attendance.findByClientOrderByDateTimeBeginDesc")
                .setParameter("client", client)
                .getResultList();
        
        List<AttendanceDTO> result = new LinkedList<>();

        for (Attendance attendance : attendances) {
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

            result.add(attendanceDTO);
        }

        return result;
    }

    /**
     * Gets whether the attendance is anonymous.
     *
     * @param attendanceId the attendance's ID
     * @return true, if the attendance is anonymous; false, otherwise
     * @throws IllegalStateException if the session is not active
     * @throws NullPointerException if the attendanceId is null
     * @throws ValidationException if the attendance's ID is invalid
     */
    public Boolean isAnonymous(Short attendanceId) throws ValidationException {
        assertSessionActive();

        if (attendanceId == null) {
            throw new NullPointerException("The attendanceId is null."); //NOI18N
        }

        Attendance attendance = entityManager.find(Attendance.class, attendanceId);

        if (attendance == null) {
            throw new ValidationException(bundle.getString("AttendanceIDInvalid"));
        }

        return attendance.getClient() == null;
    }

    /**
     * Closes an attendance.
     *
     * <ul>
     *
     * <li> The attendance has to be open. </li>
     * 
     * <li> If the attendance is anonymous,
     * it has to have full payment recorded in the associated order. </li>
     *
     * </ul>
     *
     * @param attendanceId the attendance's ID
     * @throws NullPointerException if the attendanceId is null
     * @throws ValidationException if the attendance's ID is invalid
     * @throws BusinessException if current business rules restrict this
     * @throws IllegalStateException if the transaction or the session is not
     * active operation
     */
    public void closeAttendance(Short attendanceId)
            throws BusinessException, ValidationException {

        assertSessionActive();
        assertTransactionActive();

        if (attendanceId == null) {
            throw new NullPointerException("The attendanceId is null."); //NOI18N
        }
        Attendance attendance = entityManager.find(Attendance.class, attendanceId);

        if (attendance == null) {
            throw new ValidationException(bundle.getString("AttendanceIDInvalid"));
        }

        if (!attendance.getDatetimeEnd().equals(Attendance.DATETIME_END_UNKNOWN)) {
            throw new BusinessException(bundle.getString("AttendanceAlreadyClosed"));
        }

        OrderEntity order = attendance.getOrder();

        if (order != null) {
            BigDecimal total = BigDecimal.ZERO;

            if(order.getOrderLines() != null) {
                for(OrderLine orderLine : order.getOrderLines()) {
                    Item item = orderLine.getItem();
                    ItemDTO itemDTO = new ItemDTO(item.getId(), item.getBarcode(), item.getTitle(), item.getQuantity(), item.getPrice());

                    for(int i = 0; i < orderLine.getQuantity();i++) {
                        total = total.add(item.getPrice());
                    }
                }
            }

            if (attendance.getClient() == null && total.compareTo(order.getPayment()) != 0) {
                throw new BusinessException(bundle.getString("ExactPaymentRequiredToCloseAnonymousAttendance"));
            }
        }

        // TODO: note change
        attendance.setDatetimeEnd(new Date());

        entityManager.flush();
    }

    /**
     * Finds an open attendance with the key provided.
     *
     * @param keyId the key's ID
     * @return the attendance's ID
     * @throws NullPointerException if they keyId is null
     * @throws ValidationException if the key's ID is invalid
     * @throws BusinessException there isn't any open attendances with the key
     * provided
     * @throws IllegalStateException if the session is not active
     */
    public Short findOpenAttendanceByKey(Short keyId) throws ValidationException, BusinessException {

        assertSessionActive();

        if (keyId == null) {
            throw new NullPointerException("The keyId is null."); // NOI18N
        }

        Key key = entityManager.find(Key.class, keyId);

        if (key == null) {
            throw new ValidationException(bundle.getString("KeyIDInvalid"));
        }

        Attendance attendance;
        try {
            attendance = (Attendance) entityManager.createNamedQuery("Attendance.findOpenByKey") //NOI18N
                    .setParameter("key", key) //NOI18N
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new BusinessException(bundle.getString("NoOpenAttendanceWithKey"));
        }

        return attendance.getId();
    }

    /**
     * Finds the current time range.
     * 
     * @return the current time range or null, if none is found
     */
    private TimeRange findCurrentTimeRange() {
        List<TimeRange> timeRanges = entityManager.createNamedQuery("TimeRange.findAll") //NOI18N
                .getResultList();
        LocalTime time = new LocalTime();
        for (TimeRange timeRange : timeRanges) {
            LocalTime begin = new LocalTime(timeRange.getTimeBegin().getTime());
            LocalTime end = new LocalTime(timeRange.getTimeEnd().getTime());

            if (time.compareTo(begin) >= 0 && time.compareTo(end) <= 0) {
                return timeRange;
            }
        }
        
        return null;
    }

    /**
     * Finds an anonymous subscription appropriate for the current moment.
     *
     * @return the subscription, or null if none is found
     */
    private ItemSubscription findValidAnonymousSubscriptionForNow() {
        TimeRange currentTimeRange = findCurrentTimeRange();

        /*
         * No time range is available
         */
        if (currentTimeRange == null) {
            return null;
        }

        ItemSubscription itemSubscription;
        try {
            itemSubscription = (ItemSubscription) entityManager.createNamedQuery("ItemSubscription.findAnonymousByTimeRange") //NOI18N
                    .setParameter("timeRange", currentTimeRange) //NOI18N
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
        return itemSubscription;
    }

    /**
     * Gets the next attendance's ID.
     *
     * @throws IllegalStateException if the session is not active
     * @return the ID the next attendance will have
     */
    public Short getNextId() {
        // TODO: get rid of this method in favor of generation
        assertSessionActive();
        try {
            Short id = (Short) entityManager.createNamedQuery("Attendance.findAllIdsOrderByIdDesc") //NOI18N
                    .setMaxResults(1).getSingleResult();
            return (short) (id + 1);
        } catch (NoResultException ex) {
            return 1;
        }
    }
    
    /*
     * Singleton instance.
     */
    private static AttendancesService instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class
     */
    public static AttendancesService getInstance() {
        if (instance == null) {
            instance = new AttendancesService();
        }
        return instance;
    }
}
