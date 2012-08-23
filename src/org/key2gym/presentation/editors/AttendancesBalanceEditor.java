/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.editors;

import org.key2gym.presentation.util.ColorConstants;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The editor class for the attendances balance values.
 *
 * <p>
 *
 * The class encapsulates a converter and a highlighter. It's recommended to use
 * getValue() and setValue() to alter the value, although the use of getText()
 * and setText() is not prohibited.
 *
 * @author Danylo Vashchilenko
 */
public class AttendancesBalanceEditor extends JTextField {

    public AttendancesBalanceEditor() {

        /*
         * Adds a document listener to this JTextField's document. The editor
         * needs to know up-to-date value, in order to update the highlight.
         */
        getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                processDocumentEvent(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                processDocumentEvent(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    /**
     * Process the document events.
     *
     * <p>
     *
     * The method attempts to convert new text to the money balance. It will
     * also update the component's highlight.
     *
     * @param e the document event
     */
    private void processDocumentEvent(DocumentEvent e) {
        try {
            attendancesBalance = getText().isEmpty() ? null : Short.parseShort(getText());
        } catch (NumberFormatException ex) {
            attendancesBalance = null;
        }

        updateHighlight();
    }

    /**
     * Gets the editor's money balance.
     *
     * @return the money balance, or null, if the value is not available.
     */
    public Short getValue() {
        return attendancesBalance;
    }

    /**
     * Sets the value of the attendances balance.
     *
     * <p>
     *
     * The editor will update the highlight.
     *
     * @param attendancesBalance the new attendances balance
     */
    public void setValue(Short attendancesBalance) {
        this.attendancesBalance = attendancesBalance;

        if (attendancesBalance == null) {
            setText(null);
        } else {
            setText(attendancesBalance.toString());
        }

        updateHighlight();
    }

    /**
     * Updates the component's highlight.
     */
    private void updateHighlight() {
        if (attendancesBalance == null) {
            /*
             * If the value is not available, resets the colors to defaults.
             */
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        } else if (attendancesBalance < 1) {
            setBackground(ColorConstants.ATTENDANCES_BALANCE_EMPTY_BACKGROUND);
            setForeground(ColorConstants.ATTENDANCES_BALANCE_EMPTY_FOREGROUND);
        } else if (attendancesBalance < 3) {
            setBackground(ColorConstants.ATTENDANCES_BALANCE_SHORT_BACKGROUND);
            setForeground(ColorConstants.ATTENDANCES_BALANCE_SHORT_FOREGROUND);
        } else {
            setBackground(ColorConstants.ATTENDANCES_BALANCE_LARGE_BACKGROUND);
            setForeground(ColorConstants.ATTENDANCES_BALANCE_LARGE_FOREGROUND);
        }
    }
    private Short attendancesBalance;
}
