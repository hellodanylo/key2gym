/*
 * Copyright 2012-2013 Danylo Vashchilenko
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

import java.math.BigDecimal;
import javax.swing.JTextField;
import org.key2gym.client.colors.Palette;

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
