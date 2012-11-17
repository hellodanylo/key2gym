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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.UserException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.OrderDTO;
import org.key2gym.business.api.dtos.OrderLineDTO;
import org.key2gym.business.api.local.OrdersServiceLocal;
import org.key2gym.business.api.remote.OrdersServiceRemote;
import org.key2gym.persistence.Attendance;
import org.key2gym.persistence.Client;
import org.key2gym.persistence.Discount;
import org.key2gym.persistence.Item;
import org.key2gym.persistence.ItemSubscription;
import org.key2gym.persistence.OrderEntity;
import org.key2gym.persistence.OrderLine;
import org.key2gym.persistence.Property;

/**
 * 
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(OrdersServiceRemote.class)
@Local(OrdersServiceLocal.class)
@DeclareRoles({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
public class OrdersServiceBean extends BasicBean implements OrdersServiceRemote, OrdersServiceLocal {

    @Override
    public Integer findByClientIdAndDate(Integer clientId, DateMidnight date, Boolean createIfDoesNotExist)
            throws ValidationException {

        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        if (createIfDoesNotExist == null) {
            throw new NullPointerException("The createIfDoesNotExist is null."); //NOI18N
        }

        Client client;
        OrderEntity order;

        /*
         * Finds the client.
         */
        client = getEntityManager().find(Client.class, clientId);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        /*
         * Finds an order associtead with the client and issued
         * today.
         */
        try {
            order = (OrderEntity) getEntityManager().createNamedQuery("OrderEntity.findByClientAndDateRecorded") //NOI18N
                    .setParameter("client", client) //NOI18N
                    .setParameter("dateRecorded", date.toDate()) //NOI18N
                    .setMaxResults(1).getSingleResult();
            return order.getId();
        } catch (NoResultException ex) {
            /*
             * If none is found but a new one was requested, creates one.
             */
            if (createIfDoesNotExist) {


                order = new OrderEntity();
                order.setClient(client);
                order.setDate(date.toDate());
                order.setPayment(BigDecimal.ZERO);

                getEntityManager().persist(order);
                getEntityManager().flush();

                return order.getId();
            }
        }

        return null;
    }

    @Override
    public Integer findCurrentForClientByCard(Integer card, Boolean createIfDoesNotExist)
            throws ValidationException {


        if (card == null) {
            throw new NullPointerException("The card is null."); //NOI18N
        }

        if (createIfDoesNotExist == null) {
            throw new NullPointerException("The createIfDoesNotExist is null"); //NOI18N
        }

        Client client;
        OrderEntity order;

        try {
            client = (Client) getEntityManager().createNamedQuery("Client.findByCard") //NOI18N
                    .setParameter("card", card) //NOI18N
                    .setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
            throw new ValidationException(getString("Invalid.Client.Card"));
        }

        try {
            order = (OrderEntity) getEntityManager().createNamedQuery("OrderEntity.findByClientAndDateRecorded") //NOI18N
                    .setParameter("client", client) //NOI18N
                    .setParameter("dateRecorded", getToday()) //NOI18N
                    .setMaxResults(1).getSingleResult();
            return order.getId();
        } catch (NoResultException ex) {
            /*
             * If none is found but a new one was requested, create one.
             */
            if (createIfDoesNotExist) {


                order = new OrderEntity();
                order.setClient(client);
                order.setDate(getToday());
                order.setPayment(BigDecimal.ZERO);

                getEntityManager().persist(order);
                getEntityManager().flush();

                return order.getId();
            }
        }
        return null;
    }

    @Override
    public Integer findForAttendanceById(Integer attendanceId)
            throws ValidationException, BusinessException {

        /*
         * Arguments validation.
         */
        if (attendanceId == null) {
            throw new NullPointerException("The attendanceId is null."); //NOI18N
        }

        Attendance attendance = null;
        OrderEntity order = null;

        attendance = (Attendance) getEntityManager().find(Attendance.class, attendanceId);

        if (attendance == null) {
            throw new ValidationException(getString("Invalid.Attendance.ID"));
        }

        if (attendance.getClient() != null) {
            throw new BusinessException(getString("BusinessRule.Attendance.MustBeAnonymous"));
        }

        try {
            order = (OrderEntity) getEntityManager().createNamedQuery("OrderEntity.findByAttendance") //NOI18N
                    .setParameter("attendance", attendance) //NOI18N
                    .setMaxResults(1).getSingleResult();

            return order.getId();
        } catch (NoResultException ex) {
        }

        return null;
    }

    @Override
    public Integer findCurrentDefault(Boolean createIfDoesNotExist)
            throws ValidationException {

        /*
         * Arguments validation.
         */
        if (createIfDoesNotExist == null) {
            throw new NullPointerException("The createIfDoesNotExist is null."); //NOI18N
        }

        OrderEntity order;

        try {
            order = (OrderEntity) getEntityManager().createNamedQuery("OrderEntity.findDefaultByDateRecorded") //NOI18N
                    .setParameter("dateRecorded", new Date()) //NOI18N
                    .setMaxResults(1).getSingleResult();
            return order.getId();
        } catch (NoResultException ex) {
            /*
             * If none was found but a new one was requested, create one.
             */
            if (createIfDoesNotExist) {


                order = new OrderEntity();
                order.setDate(new Date());
                order.setPayment(BigDecimal.ZERO);


                getEntityManager().persist(order);
                getEntityManager().flush();

                return order.getId();
            }
        }
        return null;
    }

    @Override
    public List<OrderDTO> findAllByDate(DateMidnight date) throws SecurityViolationException {


        if (date == null) {
            throw new NullPointerException("The begin is null."); //NOI18N
        }

        if (!DateMidnight.now().equals(date) && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<OrderEntity> orders = getEntityManager().createNamedQuery("OrderEntity.findByDateRecordedOrderByIdDesc") //NOI18N
                .setParameter("dateRecorded", date.toDate()) //NOI18N
                .getResultList();

        List<OrderDTO> result = new LinkedList<OrderDTO>();

        for (OrderEntity order : orders) {
            result.add(wrapOrderEntity(order));
        }

        return result;
    }

    @Override
    public List<OrderDTO> findForClientWithinPeriod(Integer id, DateMidnight begin, DateMidnight end) throws ValidationException {


        if (id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }

        if (begin == null) {
            throw new NullPointerException("The begin is null."); //NOI18N
        }

        if (end == null) {
            throw new NullPointerException("The end is null."); //NOI18N
        }

        if (begin.isAfter(end)) {
            throw new ValidationException(getString("Invalid.DateRange.BeginningAfterEnding"));
        }

        Client client = getEntityManager().find(Client.class, id);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        List<OrderEntity> financialActivities = getEntityManager().createNamedQuery("OrderEntity.findByClientAndDateRecordedRangeOrderByDateRecordedDesc") //NOI18N
                .setParameter("client", client) //NOI18N
                .setParameter("rangeBegin", begin.toDate()) //NOI18N
                .setParameter("rangeEnd", end.toDate()) //NOI18N
                .getResultList();

        List<OrderDTO> result = new LinkedList<OrderDTO>();

        for (OrderEntity order : financialActivities) {
            result.add(wrapOrderEntity(order));
        }

        return result;
    }

    @Override
    public void addPurchase(Integer orderId, Integer itemId, Integer discountId, Integer quantity)
            throws BusinessException, ValidationException, SecurityViolationException {

        /*
         * Checks the caller's roles.
         */
        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (orderId == null) {
            throw new NullPointerException("The orderId is null.");
        }

        if (itemId == null) {
            throw new NullPointerException("The itemId is null."); //NOI18N
        }

        if (quantity == null) {
            throw new NullPointerException("The quantity is null.");
        }

        OrderEntity order;
        Item item;
        Discount discount;

        order = getEntityManager().find(OrderEntity.class, orderId, LockModeType.OPTIMISTIC);

        if (order == null) {
            throw new ValidationException(getString("Invalid.Order.ID"));
        }

        /*
         * Performs additional roles checks.
         */
        Boolean managerRoleRequired = (order.getAttendance() != null
                && !order.getAttendance().getDatetimeEnd().equals(Attendance.DATETIME_END_UNKNOWN))
                || !isToday(order.getDate());

        if (managerRoleRequired && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (quantity <= 0) {
            throw new ValidationException(getString("Invalid.OrderLine.Quantity"));
        }

        item = getEntityManager().find(Item.class, itemId);

        if (item == null) {
            throw new ValidationException(getString("Invalid.Item.ID"));
        }

        if (item.getItemSubscription() != null && order.getClient() == null) {
            throw new BusinessException(getString("BusinessRule.Order.Casual.CanNotPurchaseSubscriptions"));
        }

        if (discountId == null) {
            discount = null;
        } else {
            discount = getEntityManager().find(Discount.class, discountId);

            if (discount == null) {
                throw new ValidationException(getString("Invalid.Discount.ID"));
            }
        }

        /*
         * Checks the item's quantity, if it's countable.
         */
        Integer currentQuantity = item.getQuantity();
        if (currentQuantity != null) {
            /*
             * If the item is not in stock, throws a BusinessException.
             * The item's quantity will be decreased after all checks are performed.
             */
            if (currentQuantity == 0) {
                throw new BusinessException(getString("BusinessRule.Order.ItemNotInStock"));
            }
        }

        /*
         * Business logic specific to orders associated with clients.
         */
        if (order.getClient() != null) {
            Client client = order.getClient();

            /*
             * Charges the client's account. Checks whether the new value will
             * overreach the precision limit.
             */
            BigDecimal amount = item.getPrice();
            if (discount != null) {
                amount = amount.divide(new BigDecimal(100));
                amount = amount.multiply(new BigDecimal(100 - discount.getPercent()));
            }
            BigDecimal newMoneyBalance = client.getMoneyBalance().subtract(amount);
            if (newMoneyBalance.precision() > MONEY_MAX_PRESICION) {
                throw new ValidationException(getString("Invalid.Client.MoneyBalance.LimitReached"));
            }
            client.setMoneyBalance(newMoneyBalance);

            if (item.getItemSubscription() != null) {
                /*
                 * After the client has expired, it's attendances balance is
                 * kept until the client buys another subscription. The
                 * attendance's balance is not zeroed, if he buys another
                 * subscription before the expiration date.
                 */
                Integer attendancesBalance = client.getAttendancesBalance();
                /*
                 * Expiration base is the date from which we count the
                 * expiration date by adding the ItemSubscription's term. It's
                 * either today or the client's current expiration date,
                 * whatever is later.
                 */
                Date expirationBase = client.getExpirationDate();

                if (hasExpired(client.getExpirationDate())) {
                    attendancesBalance = 0;
                    expirationBase = new Date();
                }

                attendancesBalance += item.getItemSubscription().getUnits();
                client.setAttendancesBalance(attendancesBalance);

                client.setExpirationDate(rollExpirationDate(item.getItemSubscription(),
                        expirationBase, true));

            }
        }

        /*
         * The change should be here, because we have to change the date after
         * all checks have been performed.
         */
        if (currentQuantity != null) {
            item.setQuantity(currentQuantity - quantity);
        }

        /*
         * Attemps to find an appropriate order line.
         * Due to JPQL limitations we need a separate query,
         * when the order line's discount is null. Criteria API?
         */
        OrderLine orderLine;

        Query query;
        if (discount == null) {
            query = getEntityManager().createNamedQuery("OrderLine.findByOrderAndItemAndNoDiscount");
        } else {
            query = getEntityManager().createNamedQuery("OrderLine.findByOrderAndItemAndDiscount")
                    .setParameter("discount", discount);
        }

        query.setParameter("order", order)
                .setParameter("item", item);

        try {
            orderLine = (OrderLine) query
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            orderLine = null;
        }

        /*
         * Creates a new order line, if none was found.
         */
        if (orderLine == null) {
            orderLine = new OrderLine();
            orderLine.setItem(item);
            orderLine.setOrder(order);
            orderLine.setQuantity(quantity);
            orderLine.setDiscount(discount);
            getEntityManager().persist(orderLine);

            List<OrderLine> orderLines = order.getOrderLines();
            if (orderLines == null) {
                orderLines = new LinkedList<OrderLine>();
                order.setOrderLines(orderLines);
            }
            orderLines.add(orderLine);
        } else {
            orderLine.setQuantity(orderLine.getQuantity() + quantity);
        }
    }

    @Override
    public void removePurchase(Integer orderLineId, Integer quantity)
            throws BusinessException, ValidationException, SecurityViolationException {

        if (orderLineId == null) {
            throw new NullPointerException("The orderLineId is null.");
        }

        if (quantity == null) {
            throw new NullPointerException("The quantity is null.");
        }

        /*
         * Checks the caller's roles.
         */
        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (orderLineId < 0) {
            throw new BusinessException(getString("BusinessRule.Order.OrderLineForcedAndCanNotBeRemoved"));
        }

        if (quantity <= 0) {
            throw new ValidationException(getString("Invalid.OrderLine.Quantity"));
        }

        OrderEntity order;
        OrderLine orderLine;
        Item item;

        orderLine = getEntityManager().find(OrderLine.class, orderLineId);

        if (orderLine == null) {
            throw new ValidationException(getString("Invalid.OrderLine.ID"));
        }

        order = orderLine.getOrder();
        item = orderLine.getItem();

        Boolean managerRoleRequired = (order.getAttendance() != null
                && !order.getAttendance().getDatetimeEnd().equals(Attendance.DATETIME_END_UNKNOWN))
                || !isToday(order.getDate());

        if (managerRoleRequired && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (order.getAttendance() != null && item.getItemSubscription() != null) {
            throw new BusinessException(getString("BusinessRule.Order.Casual.SubscriptionCanNotBeRemoved"));
        }

        Property timeRangeMismatch = getEntityManager().find(Property.class, "time_range_mismatch_penalty_item_id");
        if (orderLine.getItem().getId().equals(timeRangeMismatch.getInteger())) {
            throw new BusinessException(getString("BusinessRule.Order.OrderLineForceAndCanNotBeRemoved"));
        }

        /*
         * Business logic specific to orders associated with clients.
         */
        if (order.getClient() != null) {

            Client client = order.getClient();

            /*
             * Give money back to the client.
             */
            client.setMoneyBalance(client.getMoneyBalance().add(item.getPrice()));

            if (item.getItemSubscription() != null) {
                /*
                 * After the client has expired, it's attendances balance is
                 * kept until the client buys another subscription. The
                 * attendance's balance is not zeroed, if he buys another
                 * subscription before the expiration date.
                 */
                Integer attendancesBalance = client.getAttendancesBalance() - item.getItemSubscription().getUnits();
                client.setAttendancesBalance(attendancesBalance);

                /*
                 * We count the expiration date by substracting the item
                 * subscription's term.
                 */
                client.setExpirationDate(rollExpirationDate(item.getItemSubscription(),
                        client.getExpirationDate(), false));

            }
        }

        /*
         * Restores the item's quantity, if it's finite. It's impossible to get
         * overflow here, for the item's quantity counter is being restored to
         * the state it already had before the item was purchased.
         */
        if (item.getQuantity() != null) {
            item.setQuantity(item.getQuantity() + quantity);
        }

        /*
         * Decreases the quantity of the order line, and removes it, if the
         * quantity is now zero.
         */
        orderLine.setQuantity(orderLine.getQuantity() - quantity);
        if (orderLine.getQuantity() == 0) {
            // EntityManager won't remove this relationship upon EntityManager.remove call
            order.getOrderLines().remove(orderLine);
            getEntityManager().remove(orderLine);
        }
    }

    @Override
    public void addPayment(Integer orderId, BigDecimal amount)
            throws BusinessException, ValidationException, SecurityViolationException {

        /*
         * Checks the caller's roles.
         */
        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (amount == null) {
            throw new NullPointerException("The amount is null."); //NOI18N
        }

        OrderEntity order = getEntityManager().find(OrderEntity.class, orderId, LockModeType.OPTIMISTIC);

        if (order == null) {
            throw new ValidationException(getString("Invalid.Order.ID"));
        }

        Boolean managerRoleRequired = (order.getAttendance() != null
                && !order.getAttendance().getDatetimeEnd().equals(Attendance.DATETIME_END_UNKNOWN))
                || !isToday(order.getDate());

        if (managerRoleRequired && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        /*
         * Normalizes the scale, and throws an exception, if the scale is to
         * big.
         */
        if (amount.scale() > 2) {
            throw new ValidationException(getString("Invalid.Money.TwoDigitsAfterDecimalPointMax"));
        }
        amount = amount.setScale(2);


        BigDecimal newTotalPaymentMaid = order.getPayment().add(amount);

        if (newTotalPaymentMaid.precision() > 5) {
            throw new ValidationException(getString("Invalid.Order.Total.LimitReached"));
        }

        /*
         * If the order is associted with a client, does some
         * checks and alters the client's money balance.
         */
        if (order.getClient() != null) {
            Client client = order.getClient();
            BigDecimal newMoneyBalance = client.getMoneyBalance().add(amount);

            if (newMoneyBalance.precision() > MONEY_MAX_PRESICION) {
                throw new ValidationException(getString("Invalid.Client.MoneyBalance.LimitReached"));
            }

            if (newTotalPaymentMaid.compareTo(BigDecimal.ZERO) < 0) {
                if (newMoneyBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException(getString("Invalid.Order.NotEnoughMoneyToWithdraw"));
                }
            }

            /*
             * Changes the client's money balance.
             */
            client.setMoneyBalance(newMoneyBalance);
        }

        order.setPayment(newTotalPaymentMaid);
    }

    @Override
    public BigDecimal getTotalForDate(DateMidnight date) throws SecurityViolationException {


        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        if (!DateMidnight.now().equals(date) && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        BigDecimal result = (BigDecimal) getEntityManager().createNamedQuery("OrderEntity.sumPaymentsForDateRecorded") //NOI18N
                .setParameter("dateRecorded", date.toDate()) //NOI18N
                .getSingleResult();

        if (result == null) {
            result = BigDecimal.ZERO;
        }

        return result.setScale(2);
    }

    @Override
    public OrderDTO getById(Integer id) throws ValidationException {

        /*
         * Arguments validation.
         */
        if (id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }

        OrderEntity order = getEntityManager().find(OrderEntity.class, id);

        if (order == null) {
            throw new ValidationException(getString("Invalid.Order.ID"));
        }

        OrderDTO orderDTO = wrapOrderEntity(order);

        return orderDTO;
    }

    @Override
    public BigDecimal getPayment(Integer orderId)
            throws ValidationException {

        /*
         * Arguments validation.
         */
        if (orderId == null) {
            throw new NullPointerException("The orderId is null."); //NOI18N
        }

        OrderEntity order = getEntityManager().find(OrderEntity.class,
                orderId);

        if (order == null) {
            throw new ValidationException(getString("Invalid.Order.ID"));
        }

        return order.getPayment();
    }

    public static OrderDTO wrapOrderEntity(OrderEntity order) {

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setDate(new DateMidnight(order.getDate()));
        orderDTO.setPayment(order.getPayment().setScale(2));

        /*
         * Order lines
         */
        List<OrderLineDTO> orderLineDTOs = new LinkedList<OrderLineDTO>();

        BigDecimal total = BigDecimal.ZERO.setScale(2);

        if (order.getOrderLines() != null) {
            for (OrderLine orderLine : order.getOrderLines()) {
                Item item = orderLine.getItem();
                Discount discount = orderLine.getDiscount();

                OrderLineDTO orderLineDTO = new OrderLineDTO();
                orderLineDTO.setId(orderLine.getId());
                orderLineDTO.setItemId(item.getId());
                orderLineDTO.setItemTitle(item.getTitle());
                orderLineDTO.setItemPrice(item.getPrice());
                if (discount != null) {
                    orderLineDTO.setDiscountPercent(discount.getPercent());
                    orderLineDTO.setDiscountTitle(discount.getTitle());
                    orderLineDTO.setDiscountId(discount.getId());
                }
                orderLineDTO.setQuantity(orderLine.getQuantity());
                orderLineDTO.setTotal(orderLine.getTotal());

                total = total.add(orderLine.getTotal());

                orderLineDTOs.add(orderLineDTO);
            }
        }
        orderDTO.setOrderLines(orderLineDTOs);

        /*
         * Client
         */
        if (order.getClient() != null) {
            orderDTO.setClientId(order.getClient().getId());
            orderDTO.setClientFullName(order.getClient().getFullName());
        }

        /*
         * Attendance
         */
        if (order.getAttendance() != null) {
            orderDTO.setAttendanceId(order.getAttendance().getId());
            orderDTO.setKeyTitle(order.getAttendance().getKey().getTitle());
        }

        /*
         * Total
         */
        orderDTO.setTotal(total);

        /*
         * Due
         */
        orderDTO.setDue(total.subtract(order.getPayment()));

        /*
         * Money balance
         */
        if (order.getClient() != null) {
            orderDTO.setMoneyBalance(order.getClient().getMoneyBalance().setScale(2));
        }
        return orderDTO;
    }

    /**
     * Returns a Date instance that represents the today's midnight.
     *
     * @return a Date instance
     */
    public Date getToday() {
        return new DateMidnight().toDate();
    }

    /**
     * Checks whether the provided date is today.
     *
     * @param date the date to check
     * @return true, if the date's time is past the today's midnight.
     */
    public boolean isToday(Date date) {
        DateMidnight today = new DateMidnight();
        DateMidnight tomorrow = today.plusDays(1);
        return today.getMillis() <= date.getTime() && tomorrow.getMillis() > date.getTime();
    }

    /**
     * Gets whether the expiration date has passed.
     * 
     * @param expirationDate the date to check
     * @return true, if the expiration date has passed
     */
    public boolean hasExpired(Date expirationDate) {
        return !new Date().before(expirationDate);
    }

    /**
     * Shifts the date according to the subscription's term.
     * 
     * @param itemSubscription the subscription to use
     * @param date the date to start with
     * @param forward if true, the date will be shifted into the future
     * @return the shifted date
     */
    public Date rollExpirationDate(ItemSubscription itemSubscription, Date date, Boolean forward) {
        Calendar expirationDate = new GregorianCalendar();
        expirationDate.setTime(date);

        expirationDate.roll(Calendar.YEAR, forward ? itemSubscription.getTermYears() : -itemSubscription.getTermYears());
        expirationDate.roll(Calendar.MONTH, forward ? itemSubscription.getTermMonths() : -itemSubscription.getTermMonths());
        expirationDate.roll(Calendar.DATE, forward ? itemSubscription.getTermDays() : -itemSubscription.getTermDays());

        return expirationDate.getTime();
    }
    
    private static final Integer MONEY_MAX_PRESICION = 6;
}
