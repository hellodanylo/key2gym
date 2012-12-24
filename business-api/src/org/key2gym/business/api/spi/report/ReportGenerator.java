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
package org.key2gym.business.api.spi.report;

import java.util.Locale;
import javax.persistence.EntityManager;
import org.key2gym.business.api.ValidationException;

/**
 * This interface describes a report generator.
 * 
 * A report generator provides means to generate reports of a specific type.
 * The generator may take some data as an input for each report. The generator
 * initially generate report in the primary format. The resulting report
 * then can be used to convert it to the secondary formats.
 * 
 * @author Danylo Vashchilenko
 */
public interface ReportGenerator {
    
    /**
     * Returns the report's title.
     * 
     * @return the title 
     */
    public String getTitle();
    
    /**
     * Returns the report's primary format.
     * 
     * A primary format is the format in which each instance of the report
     * is initially persisted. It can be then converted into secondary formats. 
     * @return 
     */
    public String getPrimaryFormat();

    /**
     * Generates a report.
     * 
     * @param input an input object
     * @param em the entity manager with the full access to the database
     * @throws ValidationException if the input is invalid
     * @throws IllegalArgumentException if the input is null or an instance
     * of an unexpected class
     * @return the report
     */
    public byte[] generate(Object input, EntityManager em) throws ValidationException;

    /**
     * Formats a title for a specific report from the input.
     * 
     * @param input the input object
     * @param em the entity with the full access to the database
     * @throws ValidationException if the input is invalid 
     * @throws IllegalArgumentException if the input is null or an instance
     * of an unexpected class
     * @return the title
     */
    public String formatTitle(Object input, EntityManager em) throws ValidationException;

    /**
     * Returns the array of the report's secondary format.
     * 
     * @return the secondary formats 
     */
    public String[] getSecondaryFormats();

    /**
     * Converts the report in the primary format to the secondary format
     *
     * @param report the report in the primary format
     * @param secondaryFormat the name of the secondary format
     * @return the report in the secondary format
     */
    public byte[] convert(byte[] report, String secondaryFormat);
}
