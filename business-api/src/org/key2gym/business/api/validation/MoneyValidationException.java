/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.business.api.validation;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MoneyValidationException {
    
    public MoneyValidationException(Reason reason) {
        this.reason = reason;
    }
    
    private Reason reason;
    
    public enum Reason { TWO_DIGITS_AFTER_DECIMAL_POINT_MAX, LIMIT_REACHED };
}
