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
package org.key2gym.business;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;
import org.joda.time.DateTime;
import org.key2gym.business.api.*;
import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.business.api.dtos.ReportGeneratorDTO;
import org.key2gym.business.api.remote.ReportsServiceRemote;
import org.key2gym.business.api.spi.report.ReportGenerator;
import org.key2gym.persistence.Report;
import org.key2gym.persistence.ReportBody;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(ReportsServiceRemote.class)
@DeclareRoles({SecurityRoles.REPORTS_MANAGER})
@TransactionManagement(TransactionManagementType.BEAN)
public class ReportsServiceBean extends BasicBean implements ReportsServiceRemote {

    @Override
    public Integer generateReport(String reportGeneratorId, final Object input) throws ValidationException, SecurityViolationException {

	if(!callerHasRole(SecurityRoles.REPORTS_MANAGER)) {
	    throw new SecurityViolationException(getString("Security.Operation.Denied"));
	}

        final ReportGenerator generator;

        try {
            Class<ReportGenerator> generatorClass = (Class<ReportGenerator>) Thread.currentThread().getContextClassLoader().loadClass(reportGeneratorId);
            generator = generatorClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to instantiate the generator", ex);
        }

	Report report = new Report();
        report.setPrimaryFormat(generator.getPrimaryFormat());
        report.setTimestampGenerated(new Date());
        report.setTitle(generator.formatTitle(input, getEntityManager()));
        report.setNote("");
        report.setReportGeneratorClass(generator.getClass().getName());

        try {
            transaction.begin();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to begin a transaction", ex);
        }

        try {
            getEntityManager().persist(report);
        } catch(Exception ex) {      
            try {
                transaction.rollback();
            } catch (Exception anotherException) {
                throw new RuntimeException("Failed to rollback the transaction", anotherException);
            }
            throw new RuntimeException("Failed to create a report", ex);
        }

        try {
            transaction.commit();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to commit the transaction", ex);
        }

        final Integer reportId = report.getId();

        new Thread() {
            @Override
            public void run() {
                try {
                    transaction.begin();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to begin a transaction", ex);
                }
                
                ReportBody reportBody = new ReportBody();
		reportBody.setReportId(reportId);
		reportBody.setFormat(generator.getPrimaryFormat());
                
                try {
                    reportBody.setBody(generator.generate(input, getEntityManager()));
                } catch (ValidationException ex) {
                    throw new RuntimeException("Failed  to generate a report", ex);
                }
                
                getEntityManager().persist(reportBody);

                try {
                    transaction.commit();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to commit a transaction", ex);
                }
            }
        }.start();

        return reportId;
    }

    public void convertReport(Integer intgr, String string) throws ValidationException, SecurityViolationException {
	if(!callerHasRole(SecurityRoles.REPORTS_MANAGER)) {
	    throw new SecurityViolationException(getString("Security.Operation.Denied"));
	}

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<ReportDTO> getAll() throws SecurityViolationException {
        
	if(!callerHasRole(SecurityRoles.REPORTS_MANAGER)) {
	    throw new SecurityViolationException(getString("Security.Access.Denied"));
	}

	List<ReportDTO> result = new LinkedList<ReportDTO>();
        
        List<Report> reports = getEntityManager().createNamedQuery("Report.findAll")
                .getResultList();
        
        for(Report report : reports) {
            ReportDTO reportDTO = new ReportDTO();
            
            reportDTO.setDateTimeGenerated(new DateTime(report.getTimestampGenerated()));

	    List<String> formats = (List<String>)getEntityManager()
		.createNamedQuery("ReportBody.findFormatsByReportId")
		.setParameter("reportId", report.getId())
		.getResultList();
            reportDTO.setFormats((String[])formats.toArray());

            reportDTO.setId(report.getId());
            reportDTO.setNote(report.getNote());
            reportDTO.setTitle(report.getTitle());
            reportDTO.setGeneratorId(report.getReportGeneratorClass());
            
            result.add(reportDTO);
        }
        
        return result;
    }

    public byte[] getReportBody(Integer reportId, String format) throws ValidationException, SecurityViolationException {
	if(!callerHasRole(SecurityRoles.REPORTS_MANAGER)) {
	    throw new SecurityViolationException(getString("Security.Access.Denied"));
	}
	
	if(reportId == null) {
	    throw new NullPointerException("The reportId is null");
	}

	if(format == null) {
	    throw new NullPointerException("The format is null");
	}

	ReportBody reportBody;
	
	try {
	    reportBody = (ReportBody) getEntityManager()
		.createNamedQuery("ReportBody.findByReportIdAndFormat")
		.setParameter("reportId", reportId)
		.setParameter("format", format)
		.getSingleResult();
	} catch(Exception ex) {
	    throw new RuntimeException("Failed to get the report body for: " + reportId + ", " + format);
	}

	return reportBody.getBody();	
    }

    public void removeReport(Integer intgr, String string) throws ValidationException, SecurityViolationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ReportGeneratorDTO> getReportGenerators() throws SecurityViolationException {
        if (generators == null) {
            generators = new LinkedList<ReportGeneratorDTO>();

            Iterator<ReportGenerator> it = ServiceLoader.load(ReportGenerator.class).iterator();

            while (it.hasNext()) {
                ReportGenerator generator = it.next();
                ReportGeneratorDTO generatorDTO = new ReportGeneratorDTO();
                generatorDTO.setTitle(generator.getTitle());
                generatorDTO.setPrimaryFormat(generator.getPrimaryFormat());
                generatorDTO.setSecondaryFormats(generator.getSecondaryFormats());
                generatorDTO.setId(generator.getClass().getName());
                
                generators.add(generatorDTO);
            }
        }

        return generators;
    }
    private static List<ReportGeneratorDTO> generators;
    @Resource
    private UserTransaction transaction;
}
