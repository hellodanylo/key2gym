/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.api;

/**
 *
 * @author daniel
 */
public class ValidationException extends Exception {
       
    public ValidationException(String message) {
        super(message);
    }
}
