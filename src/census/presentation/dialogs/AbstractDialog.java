/*
 * Copyright 2012 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package census.presentation.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * Basic class for Census dialogs. Implements basic session variables like:
 *
 * <ul>
 *
 * <li> Result - a general result code
 *
 * <li> Exception - a runtime exception that this dialog encountered, this
 * variable is set, when Result is RESULT_EXCEPTION.
 *
 * </ul>
 *
 * Sets the result to RESULT_CANCEL, when the close button is pressed.
 *
 * @author Danylo Vashchilenko
 */
public abstract class AbstractDialog extends JDialog {

    /**
     * Constructs with the specified parent and modality.
     *
     * @param parent the parent frame of this dialog
     * @param modal if true, the dialog will be modal
     */
    public AbstractDialog(JFrame parent, boolean modal, Button[] buttons) {
        super(parent, modal);
        strings = ResourceBundle.getBundle("census/presentation/resources/Strings");

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        cancelAction = new CancelAction();
        okAction = new OkAction();

        /*
         * Binds the escape key to the cancel action.
         */
        addHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelAction);

        result = null;
        exception = null;
    }
    
    public AbstractDialog(JFrame parent, boolean modal) {
        this(parent, modal, new Button[]{Button.OK, Button.CANCEL});
    }

    /**
     * Performs pre-closing routine.
     *
     * @param evt an optional WindowEvent
     * @see AbstractDialog
     */
    protected void formWindowClosing(java.awt.event.WindowEvent evt) {
        setResult(RESULT_CANCEL);
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

    /**
     * Returns this component. Useful for inner classes that need the reference
     * to the owning component.
     *
     * @return this component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * Gets the exception that terminated this dialog.
     *
     * @return the runtime exception
     */
    public RuntimeException getException() {
        return exception;
    }

    /**
     * Sets the exception that terminated this dialog.
     *
     * @param exception the runtime exception
     */
    public void setException(RuntimeException exception) {
        this.exception = exception;
    }

    /**
     * Gets the dialog's result code.
     *
     * @return the result code
     */
    public Integer getResult() {
        return result;
    }

    /**
     * Sets the dialog's result code.
     *
     * @param result the result code
     */
    public void setResult(Integer result) {
        this.result = result;
    }

    /**
     * Returns the dialog's cancel action.
     *
     * @return the action used to cancel the changes and close the dialog.
     */
    protected Action getCancelAction() {
        return cancelAction;
    }

    /**
     * Returns the dialog's OK action.
     *
     * @return the action used to confirm the changes and close the dialog.
     */
    protected Action getOkAction() {
        return okAction;
    }

    /**
     * Adds a dialog-wide hot key. 
     * <p>
     * As long as the dialog or its subcomponents has the focus, the triggering
     * of the key stroke will cause the action to be performed.
     * 
     * @param keyStroke the key stroke to listen for
     * @param action the action to perform when the key stroke is triggered
     */
    protected final void addHotKey(KeyStroke keyStroke, Action action) {
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, action.getClass().getName());
        getRootPane().getActionMap().put(action.getClass().getName(), action);
    }

    protected void onOkActionPerformed(ActionEvent evt) {
        setResult(RESULT_OK);
        dispose();
    }

    protected void onCancelActionPerformed(ActionEvent evt) {
        setResult(RESULT_CANCEL);
        dispose();
    }

    protected class CancelAction extends AbstractAction {

        public CancelAction() {
            putValue(NAME, getString("Button.Cancel"));
            putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/census/presentation/resources/cancel16.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onCancelActionPerformed(e);
        }
    }

    protected class OkAction extends AbstractAction {

        public OkAction() {
            putValue(NAME, getString("Button.Ok"));
            putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/census/presentation/resources/ok16.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onOkActionPerformed(e);
        }
    }
    private Action cancelAction;
    private Action okAction;
    private Integer result;
    private RuntimeException exception;
    private ResourceBundle strings;
    
    public enum Button {OK, CANCEL};
        
    // TODO: use enumeration instead
    public static final Integer RESULT_OK = 0;
    public static final Integer RESULT_CANCEL = 1;
    public static final Integer RESULT_EXCEPTION = 2;
}
