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
package org.key2gym.presentation.colors;

import java.awt.Color;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface Palette {

    /*
     * Basic palette
     */
    public static final Color ERROR_BACKGROUND = new Color(255, 173, 206);
    public static final Color ERROR_FOREGROUND = new Color(168, 0, 0);
    public static final Color WARNING_BACKGROUND = new Color(255, 226, 115);
    public static final Color WARNING_FOREGROUND = new Color(255, 98, 0);
    public static final Color OK_BACKGROUND = new Color(211, 255, 130);
    public static final Color OK_FOREGROUND = new Color(98, 179, 0);
    /*
     * Money balance
     */
    public static final Color MONEY_BALANCE_POSITIVE_OR_ZERO_BACKGROUND = OK_BACKGROUND;
    public static final Color MONEY_BALANCE_POSITITVE_OR_ZERO_FOREGROUND = OK_FOREGROUND;
    public static final Color MONEY_BALANCE_NEGATIVE_BACKGROUND = ERROR_BACKGROUND;
    public static final Color MONET_BALANCE_NEGATIVE_FOREGROUND = ERROR_FOREGROUND;
    /*
     * Attendances balance
     */
    public static final Color ATTENDANCES_BALANCE_LARGE_BACKGROUND = OK_BACKGROUND;
    public static final Color ATTENDANCES_BALANCE_LARGE_FOREGROUND = OK_FOREGROUND;
    public static final Color ATTENDANCES_BALANCE_SHORT_BACKGROUND = WARNING_BACKGROUND;
    public static final Color ATTENDANCES_BALANCE_SHORT_FOREGROUND = WARNING_FOREGROUND;
    public static final Color ATTENDANCES_BALANCE_EMPTY_BACKGROUND = ERROR_BACKGROUND;
    public static final Color ATTENDANCES_BALANCE_EMPTY_FOREGROUND = ERROR_FOREGROUND;
    /*
     * Expiration date
     */
    public static final Color EXPIRATION_DATE_PASSED_BACKGROUND = ERROR_BACKGROUND;
    public static final Color EXPIRATION_DATE_PASSED_FOREGROUND = ERROR_FOREGROUND;
    public static final Color EXPIRATION_DATE_SOON_BACKGROUND = WARNING_BACKGROUND;
    public static final Color EXPIRATION_DATE_SOON_FOREGROUND = WARNING_FOREGROUND;
    public static final Color EXPIRATION_DATE_NOT_SOON_BACKGROUND = OK_BACKGROUND;
    public static final Color EXPIRATION_DATE_NOT_SOON_FOREGROUND = OK_FOREGROUND;
}
