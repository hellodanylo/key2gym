/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.business.api;

/**
 *
 * @author Danylo Vashchilenko
 */
public class UserException extends Exception {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
