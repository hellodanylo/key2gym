/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.editors;

import org.key2gym.presentation.util.ColorConstants;
import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.joda.time.DateMidnight;
import org.joda.time.Days;

/**
 * The editor class for the money balance values.
 *
 * <p>
 *
 * The class encapsulates a converter and a highlighter. It's recommended to use
 * getValue() and setValue() to alter the value, although the use of getText()
 * and setText() is not prohibited.
 *
 * @author Danylo Vashchilenko
 */
public class ExpirationDateEditor extends JTextField {

    public ExpirationDateEditor() {

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");

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
            expirationDate = getText().isEmpty() ? null : new DateMidnight(dateFormat.parse(getText()));
        } catch (ParseException ex) {
            expirationDate = null;
        }

        updateHighlight();
    }

    /**
     * Gets the editor's value.
     *
     * @return the expiration date, or null, if the value is not available.
     */
    public DateMidnight getValue() {
        return expirationDate;
    }

    /**
     * Sets the value of this editor.
     *
     * <p>
     *
     * The editor will update the highlight.
     *
     * @param expirationDate the new expiration date
     */
    public void setValue(DateMidnight expirationDate) {
        this.expirationDate = expirationDate;

        if (expirationDate == null) {
            setText(null);
        } else {
            setText(dateFormat.format(expirationDate.toDate()));
        }

        updateHighlight();
    }

    /**
     * Updates the component's highlight.
     */
    private void updateHighlight() {

        if (expirationDate == null) {
            /*
             * If the value is not available, resets the colors to defaults.
             */
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            return;
        }

        int daysTillExpiration = Days.daysBetween(DateMidnight.now(), expirationDate).getDays();


        if (daysTillExpiration > 2) {
            setBackground(ColorConstants.EXPIRATION_DATE_NOT_SOON_BACKGROUND);
            setForeground(ColorConstants.EXPIRATION_DATE_NOT_SOON_FOREGROUND);
        } else if (daysTillExpiration > 0) {
            setBackground(ColorConstants.EXPIRATION_DATE_SOON_BACKGROUND);
            setForeground(ColorConstants.EXPIRATION_DATE_SOON_FOREGROUND);
        } else {
            setBackground(ColorConstants.EXPIRATION_DATE_PASSED_BACKGROUND);
            setForeground(ColorConstants.EXPIRATION_DATE_PASSED_FOREGROUND);
        }
    }
    private DateMidnight expirationDate;
    private SimpleDateFormat dateFormat;
}
