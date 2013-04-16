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

import static org.key2gym.client.resources.ResourcesManager.getString;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdesktop.beansbinding.Converter;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.key2gym.business.api.ValidationException;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DateTimeToStringConverter extends Converter<ReadableDateTime, String> {

    private String format;
    private String fieldName;
    
    public DateTimeToStringConverter(String fieldName, String format) {
        this.format = format;
        this.fieldName = fieldName;
    }

    @Override
    public String convertForward(ReadableDateTime value) {
        return value.toString(format);
    }

    @Override
    public ReadableDateTime convertReverse(String value) {
        value = value.trim();
        if(value.isEmpty())
            return null;

        try {
            return new DateTime(new SimpleDateFormat(format).parse(value));
        } catch (ParseException ex) {
            throw new IllegalArgumentException(
	        new ValidationException(
		    getString("Message.FieldIsNotFilledInCorrectly.withFieldName",
			      fieldName)
		)
	    );
        }
    }
}
