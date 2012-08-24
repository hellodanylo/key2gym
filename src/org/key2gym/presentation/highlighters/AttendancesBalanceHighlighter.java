/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.highlighters;

import java.awt.Color;
import javax.swing.JTextField;
import org.key2gym.presentation.colors.Palette;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AttendancesBalanceHighlighter extends AbstractHighlighter {

    public AttendancesBalanceHighlighter(JTextField textField) {
        super(textField);
    }

    @Override
    protected ColorScheme getHighlightModelFor(String text) {
        Short attendancesBalance;
        try {
            attendancesBalance = Short.parseShort(text);
        } catch (NumberFormatException ex) {
            attendancesBalance = null;
        }

        if (attendancesBalance == null) {
            /*
             * If the value is not available, resets the colors to defaults.
             */
            return NULL_SCHEME;
        } else if (attendancesBalance < 1) {
            return EMPTY_SCHEME;
        } else if (attendancesBalance < 3) {
            return SHORT_SCHEME;
        } else {
            return LARGE_SCHEME;
        }
    }

    private static final  ColorScheme EMPTY_SCHEME = new ColorScheme(
            Palette.ATTENDANCES_BALANCE_EMPTY_BACKGROUND,
            Palette.ATTENDANCES_BALANCE_EMPTY_FOREGROUND);
    private static final  ColorScheme SHORT_SCHEME = new ColorScheme(
            Palette.ATTENDANCES_BALANCE_SHORT_BACKGROUND,
            Palette.ATTENDANCES_BALANCE_SHORT_FOREGROUND);
    private static final  ColorScheme LARGE_SCHEME = new ColorScheme(
            Palette.ATTENDANCES_BALANCE_LARGE_BACKGROUND,
            Palette.ATTENDANCES_BALANCE_LARGE_FOREGROUND);
}
