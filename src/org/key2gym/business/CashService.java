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

import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.CashAdjustmentDTO;
import org.key2gym.persistence.CashAdjustment;
import java.math.BigDecimal;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CashService extends BusinessService {

    /**
     * Returns final cash for the date.
     * <p>
     * 
     * <ul>
     * <li> If the date is not today, the permissions level has to be PL_ALL </li>
     * </ul>
     *
     * @param date the date to look up
     * @throws IllegalStateException if no session is open
     * @throws NullPointerException if the date is null
     * @throws SecurityException if current security rules restrict this
     * operation
     * @return the final cash for the date
     */
    public BigDecimal getCashByDate(DateMidnight date) throws SecurityException {
        assertOpenSessionExists();

        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        if (!date.equals(DateMidnight.now()) && !SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("Security.Access.Denied"));
        }

        BigDecimal cash = (BigDecimal) entityManager.createNamedQuery("OrderEntity.sumPaymentsForDateRecorded") //NOI18N
                .setParameter("dateRecorded", date.toDate()) //NOI18N
                .getSingleResult(); 

        /*
         * The sum aggregate function returns null, when there is not 
         * any orders.
         */
        if(cash == null) {
            cash = BigDecimal.ZERO;
        }
        
        CashAdjustment adjustment = entityManager.find(CashAdjustment.class, date.toDate());

        if (adjustment != null) {
            cash = cash.add(adjustment.getAmount());
        }

        return cash;
    }

    /**
     * Records the cash adjustment. If the record already exists, it will be
     * updated. Otherwise, it will be created.
     * <p>
     * 
     * <ul>
     * <li> The permissions level has to be PL_ALL </li>
     * </ul>
     *
     * @param cashAdjustment the cash adjustment
     * @throws IllegalStateException if the transaction is not active; if no session
     * is open
     * @throws SecurityException if current security rules restrict this
     * operation
     * @throws NullPointerException if the cash adjustment or any of its
     * required properties is null
     * @throws ValidationException if any of the required properties is invalid
     */
    public void recordCashAdjustment(CashAdjustmentDTO cashAdjustment) throws SecurityException, ValidationException {
        assertOpenSessionExists();
        assertTransactionActive();

        if (!SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("Security.Operation.Denied"));
        }

        if (cashAdjustment == null) {
            throw new NullPointerException("The cashAdjustment is null."); //NOI18N
        }

        if (cashAdjustment.getDate() == null) {
            throw new NullPointerException("The cashAdjustment.getDate() is null."); //NOI18N
        }

        if (cashAdjustment.getAmount() == null) {
            throw new NullPointerException("The cashAdjustment.getAmount() is null."); //NOI18N
        }
        
        validateAmount(cashAdjustment.getAmount());
            
            
        if (cashAdjustment.getNote() == null) {
                throw new NullPointerException("The cashAdjustment.getNote() is null."); //NOI18N
        }

        CashAdjustment entityCashAdjustment = entityManager.find(CashAdjustment.class,
                cashAdjustment.getDate().toDate());

        if (entityCashAdjustment == null) {
            entityCashAdjustment = new CashAdjustment();
            entityCashAdjustment.setDateRecorded(cashAdjustment.getDate().toDate());
            entityCashAdjustment.setAmount(cashAdjustment.getAmount());
            entityCashAdjustment.setNote(cashAdjustment.getNote());

            entityManager.persist(entityCashAdjustment);
        } else {
            entityCashAdjustment.setDateRecorded(cashAdjustment.getDate().toDate());
            entityCashAdjustment.setAmount(cashAdjustment.getAmount());
            entityCashAdjustment.setNote(cashAdjustment.getNote());
        }

        entityManager.flush();
    }

    /**
     * Gets the cash adjustment by the date. If it does not exists, it will
     * be created.
     * <p>
     * 
     * <ul>
     * <li> The permissions level has to be PL_ALL</li>
     * </ul>
     *
     * @param date the date
     * @throws IllegalStateException if creating a new cash adjustment is 
     * required, but the transaction is not active; if no session is open
     * @throws SecurityException if current security rules restrict this
     * operation
     * @throws NullPointerException if date is null
     * @return the cash adjustment
     */
    public CashAdjustmentDTO getAdjustmentByDate(DateMidnight date) throws SecurityException {
        assertOpenSessionExists();

        if (!SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("Security.Access.Denied"));
        }

        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        CashAdjustment cashAdjustmentEntity = entityManager.find(CashAdjustment.class, date.toDate());

        if (cashAdjustmentEntity == null) {
            assertTransactionActive();
                    
            cashAdjustmentEntity = new CashAdjustment();
            cashAdjustmentEntity.setAmount(BigDecimal.ZERO);
            cashAdjustmentEntity.setDateRecorded(date.toDate());
            cashAdjustmentEntity.setNote("");
            
            entityManager.persist(cashAdjustmentEntity);
        }

        CashAdjustmentDTO cashAdjustmentDTO = new CashAdjustmentDTO();

        cashAdjustmentDTO.setAmount(cashAdjustmentEntity.getAmount());
        cashAdjustmentDTO.setDate(new DateMidnight(cashAdjustmentEntity.getDateRecorded()));
        cashAdjustmentDTO.setNote(cashAdjustmentEntity.getNote());

        return cashAdjustmentDTO;
    }

    /**
     * Validates a money amount.
     * 
     * @param value the amount to validate
     * @throws ValidationException if the amount is invalid
     */
    private void validateAmount(BigDecimal value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The amount is null."); //NOI18N
        }

        if (value.scale() > 2) {
            throw new ValidationException(bundle.getString("Invalid.Money.TwoDigitsAfterDecimalPointMax"));
        }
        
        value = value.setScale(2);
        
        if (value.precision() > 6) {
            throw new ValidationException(bundle.getString("Invalid.CashAdjustment.LimitReached"));
        }
    }
    
    /**
     * Singleton instance.
     */
    private static CashService instance;

    /**
     * Gets an instance of this class.
     *
     * @return an instance of this class
     */
    public static CashService getInstance() {
        if (instance == null) {
            instance = new CashService();
        }
        return instance;
    }
}
