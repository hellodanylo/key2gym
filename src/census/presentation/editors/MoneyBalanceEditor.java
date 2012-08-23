/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.editors;

import census.presentation.util.ColorConstants;
import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The editor class for the money balance values.
 *
 * <p>
 *
 * The class encapsulates a converter and a highlighter. It's recommended to use
 * getValue() and setValue() to alter the value, although the use
 * of getText() and setText() is not prohibited.
 *
 * @author Danylo Vashchilenko
 */
public class MoneyBalanceEditor extends JTextField {

    public MoneyBalanceEditor() {

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
            moneyBalance = getText().isEmpty() ? null : new BigDecimal(getText());
        } catch (NumberFormatException ex) {
            moneyBalance = null;
        }

        updateHighlight();
    }

    /**
     * Gets the editor's value.
     *
     * @return the money balance, or null, if the value is not available.
     */
    public BigDecimal getValue() {
        return moneyBalance;
    }

    /**
     * Sets the value of this editor.
     *
     * <p>
     *
     * The editor will update the highlight.
     *
     * @param moneyBalance the new money balance
     */
    public void setValue(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;

        if (moneyBalance == null) {
            setText(null);
        } else {
            setText(moneyBalance.toPlainString());
        }

        updateHighlight();
    }

    /**
     * Updates the component's highlight.
     */
    private void updateHighlight() {
        if (moneyBalance == null) {
            /*
             * If the value is not available, resets the colors to defaults.
             */
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        } else if (moneyBalance.compareTo(BigDecimal.ZERO) < 0) {
            /*
             * If the amount is negative, it's a debt, so it marks the component
             * with warning colors.
             */
            setBackground(ColorConstants.MONEY_BALANCE_NEGATIVE_BACKGROUND);
            setForeground(ColorConstants.MONET_BALANCE_NEGATIVE_FOREGROUND);
        } else {
            /*
             * If the amount is not negative, everything is ok.
             */
            setBackground(ColorConstants.MONEY_BALANCE_POSITIVE_OR_ZERO_BACKGROUND);
            setForeground(ColorConstants.MONEY_BALANCE_POSITITVE_OR_ZERO_FOREGROUND);
        }
    }
    private BigDecimal moneyBalance;
}
