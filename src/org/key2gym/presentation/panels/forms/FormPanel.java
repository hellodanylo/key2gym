/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.panels.forms;

import java.util.ResourceBundle;
import javax.swing.JPanel;

/**
 *
 * @author Danylo Vashchilenko
 */
public abstract class FormPanel<T extends Object> extends JPanel {

    public FormPanel() {
        strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
    }

    /**
     * Gets the l15d string for the key.
     *
     * @param key the key to look up the string
     * @return the l15d value
     */
    protected String getString(String key) {
        return strings.getString(key);
    }

    /**
     * Gets the strings bundle.
     *
     * @return the strings bundle
     */
    protected ResourceBundle getStrings() {
        return strings;
    }

    public abstract boolean trySave();

    public abstract void setForm(T form);

    public abstract T getForm();
    
    /**
     * L10n strings.
     */
    private ResourceBundle strings;
}
