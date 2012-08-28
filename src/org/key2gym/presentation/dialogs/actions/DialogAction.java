/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.dialogs.actions;

import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.FormDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public abstract class DialogAction extends AbstractAction {

    public DialogAction(AbstractDialog dialog) {
        this.dialog = dialog;
        this.strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
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
     * Gets the dialog's strings bundle.
     *
     * @return the strings bundle
     */
    protected ResourceBundle getStrings() {
        return strings;
    }

    public AbstractDialog getDialog() {
        return dialog;
    }
    
    private AbstractDialog dialog;
    private ResourceBundle strings;
}
