/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.api.ValidationException;
import census.presentation.CensusFrame;
import java.math.BigDecimal;
import java.util.ResourceBundle;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author daniel
 */
public class CardIntegerToStringConverter extends Converter<Integer, String> {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    private String fieldName;

    public CardIntegerToStringConverter(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Integer convertReverse(String value) {
        try {
            return value.trim().isEmpty() ? null : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(new ValidationException(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("census/presentation/resources/Strings").getString("Message.FilledIsNotFilledInCorrectly.withFieldName"), new Object[] {fieldName})));
        }
    }

    @Override
    public String convertForward(Integer value) {
        return value == null ? "" : value.toString();
    }
}
