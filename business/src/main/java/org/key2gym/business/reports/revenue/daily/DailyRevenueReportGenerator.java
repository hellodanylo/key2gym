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
package org.key2gym.business.reports.revenue.daily;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.io.*;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.spi.report.ReportGenerator;
import org.key2gym.business.resources.ResourcesManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DailyRevenueReportGenerator implements ReportGenerator {

    /**
     * Generates the report.
     * 
     * The input for this generator is an array of two DateMidnight instances.
     * They represent the range of dates to generate the revenue for.
     * The first date has to be before or equal to the second date.
     * 
     * @param input the array of two dates
     * @param em the entity manager with full access to the database
     * @throws ValidationException if the input is invalid
     * @return the XML report 
     */
    public byte[] generate(Object input, EntityManager em) throws ValidationException {

        /*
         * Casts the input object.
         */
        DateMidnight[] range = (DateMidnight[]) input;

        /*
         * Validates the input.
         */
        if (range[0].compareTo(range[1]) > 1) {
            throw new ValidationException(ResourcesManager.getStrings().getString("Invalid.DateRange.BeginningAfterEnding"));
        }

        /*
         * The resulting report entity.
         */
        DailyRevenueReport report = new DailyRevenueReport();

        report.setTitle(formatTitle(input, em));
        report.setPeriodBegin(range[0].toDate());
        report.setPeriodEnd(range[1].toDate());


        DateMidnight date = range[0];
        Integer dayNumber = 1;

        /*
         * Loops through each day within the range.
         */
        while (date.compareTo(range[1]) <= 0) {
            Day day = new Day();
            day.setDate(date.toDate());
            day.setNumber(dayNumber);

            BigDecimal revenue = (BigDecimal) em.createNamedQuery("OrderEntity.sumPaymentsForDateRecorded")
                    .setParameter("dateRecorded", date.toDate())
                    .getSingleResult();
            if (revenue == null) {
                revenue = BigDecimal.ZERO;
            }
            day.setRevenue(revenue);

            report.getDays().add(day);

            date = date.plusDays(1);
            dayNumber++;
        }

        JAXBContext context;
        Marshaller marshaller;

        try {
            context = JAXBContext.newInstance(DailyRevenueReport.class);
            marshaller = context.createMarshaller();
        } catch (JAXBException ex) {
            throw new RuntimeException("Failed create a marshaller", ex);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            marshaller.marshal(report, stream);
        } catch (JAXBException ex) {
            throw new RuntimeException("Failed to marshall the report", ex);
        }

        return stream.toByteArray();
    }

    public String formatTitle(Object o, EntityManager em) throws ValidationException {
        return ResourcesManager.getStrings().getString("Report.DailyRevenue.Title");
    }

    public byte[] convertToSecondaryFormat(byte[] bytes, String string) {
	TransformerFactory factory = TransformerFactory.newInstance();

        Source xslt = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(this.getClass().getPackage().getName() + "xml2html.xslt"));
        Transformer transformer;
	try {
	    transformer = factory.newTransformer(xslt);
	} catch(TransformerConfigurationException ex) {
	    throw new RuntimeException("Can not create a transformer", ex);
	}

        Source xml = new StreamSource(new ByteArrayInputStream(bytes));
	ByteArrayOutputStream output = new ByteArrayOutputStream();

	try {
	    transformer.transform(xml, new StreamResult(output));
	} catch(TransformerException ex) {
	    throw new RuntimeException("Failed to transform the report", ex);
	}

	return output.toByteArray();
    }

    public String getTitle() {
        return ResourcesManager.getStrings().getString("Report.DailyRevenue.Title");
    }

    public String getPrimaryFormat() {
        return "xml";
    }

    public String[] getSecondaryFormats() {
        return new String[]{"html"};
    }
}
