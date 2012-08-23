/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.util;

import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.FocusManager;
import javax.swing.JOptionPane;

/**
 *
 * @author Danylo Vashchilenko
 */
public class UserExceptionHandler implements CensusExceptionListener {
    
    private UserExceptionHandler() {
        strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
    }

    public Component getComponent() {
        return FocusManager.getCurrentManager().getFocusedWindow();
    }

    @Override
    public void processException(Exception ex) {
        Integer messageType;
        if (ex instanceof NotificationException) {
            messageType = JOptionPane.INFORMATION_MESSAGE;
        } else if (ex instanceof ValidationException || ex instanceof SecurityException) {
            messageType = JOptionPane.WARNING_MESSAGE;
        } else if (ex instanceof BusinessException) {
            messageType = JOptionPane.ERROR_MESSAGE;
        } else {
            throw new RuntimeException("Unexpexted exception type.");
        }
        JOptionPane.showMessageDialog(getComponent(), ex.getMessage(), strings.getString("Title.Message"), messageType);
    }
    
    private ResourceBundle strings;
    
    /**
     * Singleton instance.
     */
    private static UserExceptionHandler instance;
    
    /**
     * Returns an instance of this class.
     * 
     * @return an instance of this class 
     */
    public static UserExceptionHandler getInstance() {
        if(instance == null) {
            instance = new UserExceptionHandler();
        }
        
        return instance;
    }
}