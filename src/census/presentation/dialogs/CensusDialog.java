/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.dialogs;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Basic class for Census dialogs. Implements basic session variables like:
 * 
 * <ul>
 * 
 * <li> Result - a general result code
 * 
 * <li> Exception - a runtime exception that this dialog encountered,
 * this variable is set, when Result is RESULT_EXCEPTION.
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
        result = null;
        exception = null;
    }

    /**
     * Sets the result to RESULT_CANCEL.
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

    public void setResult(Integer result) {
        this.result = result;
    }
    
    private Integer result;
    private RuntimeException exception;
    
    public static final Integer RESULT_OK = 0;
    public static final Integer RESULT_CANCEL = 1;
    public static final Integer RESULT_EXCEPTION = 2;
}
