/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.api.ValidationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import org.jdesktop.beansbinding.Converter;
import org.joda.time.DateMidnight;

/**
 *
 * @author daniel
 */
public class DateMidnightToStringConverter extends Converter<DateMidnight, String> {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    private String format;
    private String fieldName;

    public DateMidnightToStringConverter(String fieldName, String format) {
        this.format = format;
        this.fieldName = fieldName;
    }

    @Override
    public String convertForward(DateMidnight value) {
        return value.toString(format);
    }

    @Override
    public DateMidnight convertReverse(String value) {
        value = value.trim();
        if(value.isEmpty())
            return null;

        try {
            return new DateMidnight(new SimpleDateFormat(format).parse(value));
        } catch (ParseException ex) {
            throw new IllegalArgumentException(new ValidationException(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("census/presentation/resources/Strings").getString("Message.FieldIsNotFilledInCorrectly.withFieldName"), new Object[] {fieldName})));
        }
    }
}
