/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.highlighters;

import java.math.BigDecimal;
import javax.swing.JTextField;
import org.key2gym.presentation.colors.Palette;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MoneyBalanceHighlighter extends AbstractHighlighter {

    public MoneyBalanceHighlighter(JTextField textField) {
        super(textField);
    }

    @Override
    protected ColorScheme getHighlightModelFor(String text) {
        BigDecimal moneyBalance;
        try {
            moneyBalance = new BigDecimal(text);
        } catch(NumberFormatException ex) {
            moneyBalance = null;
        }
        
        if (moneyBalance == null) {
            /*
             * If the value is not available, resets the colors to defaults.
             */
            return NULL_SCHEME;
        } else if (moneyBalance.compareTo(BigDecimal.ZERO) < 0) {
            return NEGATIVE_SCHEME;
        } else {
            return POSITIVE_OR_ZERO_SCHEME;
        }
    }

    protected static final ColorScheme NEGATIVE_SCHEME = new ColorScheme(
            Palette.MONEY_BALANCE_NEGATIVE_BACKGROUND,
            Palette.MONET_BALANCE_NEGATIVE_FOREGROUND);
    
    protected static final ColorScheme POSITIVE_OR_ZERO_SCHEME = new ColorScheme(
            Palette.MONEY_BALANCE_POSITIVE_OR_ZERO_BACKGROUND,
            Palette.MONEY_BALANCE_POSITITVE_OR_ZERO_FOREGROUND);
}
