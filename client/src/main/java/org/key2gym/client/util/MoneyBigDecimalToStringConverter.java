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
package org.key2gym.client.util;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.jdesktop.beansbinding.Converter;
import org.key2gym.business.api.ValidationException;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MoneyBigDecimalToStringConverter extends Converter<BigDecimal, String> {
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");
    private String fieldName;

    public MoneyBigDecimalToStringConverter(String fieldName) {
        this.fieldName = fieldName;
    }
    
    @Override
    public BigDecimal convertReverse(String value) {
        try {
            return value.isEmpty() ? null : new BigDecimal(value);
        } catch(NumberFormatException ex) {
            throw new IllegalArgumentException(new ValidationException(MessageFormat.format(strings.getString("Message.FieldIsNotFilledInCorrectly.withFieldName"), new Object[] {fieldName})));
        }
    }

    @Override
    public String convertForward(BigDecimal value) {
        return value.toString();
    }  
}