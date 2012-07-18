/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.CashAdjustmentDTO;
import census.persistence.CashAdjustment;
import java.math.BigDecimal;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CashService extends BusinessService {

    /**
     * Returns final cash for the date.
     *
     * <ul>
     *
     * <li> If the date is not today, the permissions level has to be PL_ALL.
     *
     * </ul>
     *
     * @param date the date to look up
     * @throws IllegalStateException if the session is not active
     * @throws NullPointerException if the date is null
     * @throws SecurityException if current security rules restrict this
     * operation
     * @return the final cash for the date
     */
    public BigDecimal getCashByDate(DateMidnight date) throws SecurityException {
        assertSessionActive();

        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        if (!date.equals(DateMidnight.now()) && !SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("AccessDenied"));
        }

        BigDecimal cash = (BigDecimal) entityManager.createNamedQuery("FinancialActivity.sumPaymentsForDateRecorded") //NOI18N
                .setParameter("dateRecorded", date.toDate()) //NOI18N
                .getSingleResult(); 

        /*
         * The sum aggregate function returns null, when there is not 
         * any financial activities.
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
     * Records the cash adjustment. If the records already exists, it will be
     * updated. Otherwise, it will be created.
     *
     * <ul>
     *
     * <li> The permissions level has to be PL_ALL
     *
     * </ul>
     *
     * @param cashAdjustment the cash adjustment
     * @throws IllegalStateException if no transaction is active; if no session
     * is open
     * @throws SecurityException if current security rules restrict this
     * operation
     * @throws NullPointerException if the cash adjustment or any of its
     * required properties is null
     * @throws ValidationException if any of the required properties is invalid
     */
    public void recordCashAdjustment(CashAdjustmentDTO cashAdjustment) throws SecurityException, ValidationException {
        assertSessionActive();
        assertTransactionActive();

        if (!SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("OperationDenied"));
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
     *
     * <ul>
     *
     * <li> The permissions level has to be PL_ALL.</li>
     *
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
        assertSessionActive();
        assertTransactionActive();

        if (!SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("AccessDenied"));
        }

        if (date == null) {
            throw new NullPointerException("The date is null."); //NOI18N
        }

        CashAdjustment cashAdjustmentEntity = entityManager.find(CashAdjustment.class, date.toDate());

        if (cashAdjustmentEntity == null) {
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

    private void validateAmount(BigDecimal value) throws ValidationException {
        if (value == null) {
            throw new NullPointerException("The amount is null."); //NOI18N
        }

        if (value.scale() > 2) {
            throw new ValidationException(bundle.getString("TwoDigitsAfterDecimalPointMax"));
        } else if (value.precision() > 5) {
            throw new ValidationException(bundle.getString("ThreeDigitsBeforeDecimalPointMax"));
        }
    }
    /*
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
