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
package census.presentation.util;

import census.business.api.ValidationException;
import census.presentation.MainFrame;
import java.math.BigDecimal;
import java.util.ResourceBundle;
import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author Danylo Vashchilenko
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
