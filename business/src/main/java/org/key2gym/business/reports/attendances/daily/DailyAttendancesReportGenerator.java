/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.business.reports.attendances.daily;

import javax.persistence.EntityManager;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.spi.report.ReportGenerator;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DailyAttendancesReportGenerator implements ReportGenerator {

    public String getTitle() {
        return "Attendances Daily";
    }

    public String getPrimaryFormat() {
        return "XML";
    }

    public byte[] generate(Object o, EntityManager em) throws ValidationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String formatTitle(Object o, EntityManager em) throws ValidationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getSecondaryFormats() {
        return new String[0];
    }

    public byte[] convert(byte[] bytes, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
