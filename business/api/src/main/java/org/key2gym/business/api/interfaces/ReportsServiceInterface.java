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
package org.key2gym.business.api.interfaces;

import java.util.List;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.business.api.dtos.ReportGeneratorDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface ReportsServiceInterface extends BasicInterface {
    /**
     * Begins generation of a report.
     * 
     * This method will return instantly after the basic information
     * about the report has been saved.
     * 
     * @param reportGeneratorId the report generator's ID
     * @param input the generator's input object
     * @throws IllegalArgumentException if the class is not available or the
     * input is null or of an unexpected type
     * @throws SecurityViolationException if the caller does not have REPORTS_MANAGER role
     * @return 
     */
    public Integer generateReport(String reportGeneratorId, Object input) throws ValidationException, SecurityViolationException;
    
    /**
     * Converts the report to the given format.
     * 
     * @param id the report's ID
     * @param format the report's target format
     * @throws ValidationException if the ID is invalid or the format in not supported
     * @throws SecurityViolationException if the caller does not have REPORTS_MANAGER role
     */
    public void convertReport(Integer id, String format) throws ValidationException, SecurityViolationException;
    
    /**
     * Gets all reports.
     * 
     * @return the list of all reports
     * @throws SecurityViolationException if the caller does not have REPORTS_MANAGER role 
     */
    public List<ReportDTO> getAll() throws SecurityViolationException;
    
    /**
     * Gets the report's body in given format.
     * 
     * @param id the report's ID
     * @param format the format to return
     * @throws ValidationException if the ID is invalid or the format in not available
     * @throws SecurityViolationException if the caller does not have REPORTS_MANAGER role
     * @return the report's body
     */
    public byte[] getReportBody(Integer id, String format) throws ValidationException, SecurityViolationException;
    
    /**
     * Removes the report of the given format.
     * 
     * If the format is null, the report in all its formats
     * is removed.
     * 
     * @param id the report's ID
     * @throws NullPointerException if id is null
     * @throws ValidationException if the ID is invalid
     * @throws SecurityViolationException if the caller does not have REPORTS_MANAGER role
     */
    public void removeReport(Integer id) throws ValidationException, SecurityViolationException;
    
    /**
     * Returns all the report generator available.
     * 
     * @throws SecurityViolationException if the caller does not have REPORTS_MANAGER role
     * @return the list of all report generators
     */
    public List<ReportGeneratorDTO> getReportGenerators() throws SecurityViolationException;
}
