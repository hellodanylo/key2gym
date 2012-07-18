/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.api;

import census.business.api.ValidationException;

/**
 *
 * @author daniel
 */
public interface Validator<T> {
    public void validate(T value) throws ValidationException;
}
