/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.presentation.util;

import java.awt.Color;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ColorConstants {
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
