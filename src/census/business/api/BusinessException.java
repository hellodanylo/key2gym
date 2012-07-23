/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.api;

/**
 *
 * @author daniel
 */
public class BusinessException extends Exception {
    private String message;
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
