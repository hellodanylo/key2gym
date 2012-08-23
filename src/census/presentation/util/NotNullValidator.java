/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import org.jdesktop.beansbinding.Validator;

/**
 * The validator that checks for the value being not null.
 *
 * <p>
 *
 * The value is only valid, if it's not null.
 *
 * @author Danylo Vashchilenko
 */
public class NotNullValidator extends Validator<Object> {

    @Override
    public Result validate(Object value) {
        return value == null ? new Validator.Result(0, "The value is null.") : null;
    }
}
