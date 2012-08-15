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
import java.awt.event.*;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

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
public class CensusDialog extends JDialog {

    /**
     * Constructs with the specified parent and modality.
     * 
     * @param parent the parent frame of this dialog
     * @param modal if true, the dialog will be modal
     */
    public CensusDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        
        cancelAction = new CancelAction();
        
        /*
         * Binds the escape key to the cancel action.
         */
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        getRootPane().getActionMap().put("cancel", cancelAction);
        
        result = null;
        exception = null;
    }
    
    /**
     * Performs pre-closing routine.
     *
     * @param evt an optional WindowEvent
     * @see CensusDialog
     */
    protected void formWindowClosing(java.awt.event.WindowEvent evt) {
        setResult(RESULT_CANCEL);
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
     * @return the action used to perform cancellation 
     */
    public CancelAction getCancelAction() {
        return cancelAction;
    }   
    
    protected class CancelAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            setResult(RESULT_CANCEL);
            dispose();
        }
    }
    
    private CancelAction cancelAction;
    private Integer result;
    private RuntimeException exception;
    public static final Integer RESULT_OK = 0;
    public static final Integer RESULT_CANCEL = 1;
    public static final Integer RESULT_EXCEPTION = 2;
}
