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
package org.key2gym.client.highlighters;

import java.awt.Color;
import javax.swing.JTextField;
import org.key2gym.client.colors.Palette;

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
        Integer attendancesBalance;
        try {
            attendancesBalance = Integer.parseInt(text);
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
