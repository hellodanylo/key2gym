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
package org.key2gym.business.services;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import javax.annotation.security.RolesAllowed;
import javax.persistence.NoResultException;

import org.joda.time.DateTime;
import org.key2gym.business.api.SecurityRoles;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.business.api.dtos.ReportGeneratorDTO;
import org.key2gym.business.api.services.ReportsService;
import org.key2gym.business.api.spi.report.ReportGenerator;
import org.key2gym.business.entities.Report;
import org.key2gym.business.entities.ReportBody;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Danylo Vashchilenko
 */
@Service("org.key2gym.business.api.services.ReportsService")
@RolesAllowed({ SecurityRoles.MANAGER })
public class ReportsServiceBean extends BasicBean implements ReportsService {

	@Override
	public Integer generateReport(String reportGeneratorId, Object input)
			throws ValidationException, SecurityViolationException {

		ReportGenerator generator;

		try {
			Class<ReportGenerator> generatorClass = (Class<ReportGenerator>) Thread
					.currentThread().getContextClassLoader()
					.loadClass(reportGeneratorId);
			generator = generatorClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate the generator",
					ex);
		}

		Report report = new Report();
		report.setPrimaryFormat(generator.getPrimaryFormat());
		report.setTimestampGenerated(new Date());
		report.setTitle(generator.formatTitle(input, em));
		report.setNote("");
		report.setReportGeneratorClass(generator.getClass().getName());

		em.persist(report);
		em.flush();

		ReportBody reportBody = new ReportBody();
		reportBody.setReportId(report.getId());
		reportBody.setFormat(generator.getPrimaryFormat());

		try {
			reportBody.setBody(generator.generate(input, em));
		} catch (ValidationException ex) {
			throw new RuntimeException("Failed to generate a report", ex);
		}

		em.persist(reportBody);

		return report.getId();
	}

	public void convertReport(Integer reportId, String format)
			throws ValidationException, SecurityViolationException {

		Report report = em.find(Report.class, reportId);
		ReportBody primaryBody = null;

		if (report == null) {
			throw new ValidationException(getString("Invalid.ID"));
		}

		boolean exists = false;

		try {
			em.createNamedQuery("ReportBody.findByReportIdAndFormat",
					ReportBody.class).setParameter("reportId", report.getId())
					.setParameter("format", format).getSingleResult();

			exists = true;
		} catch (NoResultException ex) {
		}

		if (exists) {
			throw new ValidationException(
					getString("Invalid.Report.Format.AlreadyConverted"));
		}

		primaryBody = em
				.createNamedQuery("ReportBody.findByReportIdAndFormat",
						ReportBody.class).setParameter("reportId", reportId)
				.setParameter("format", report.getPrimaryFormat())
				.getSingleResult();

		if (primaryBody == null) {
			throw new RuntimeException(
					"The report does not have a primary body!");
		}

		ReportBody reportBody = new ReportBody();
		reportBody.setReportId(report.getId());
		reportBody.setFormat(format);

		ReportGenerator generator;

		try {
			Class<ReportGenerator> generatorClass = (Class<ReportGenerator>) Thread
					.currentThread().getContextClassLoader()
					.loadClass(report.getReportGeneratorClass());
			generator = generatorClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate the generator",
					ex);
		}
		reportBody.setBody(generator.convert(primaryBody.getBody(), format));

		em.persist(reportBody);
	}

	public List<ReportDTO> getAll() throws SecurityViolationException {

		List<ReportDTO> result = new LinkedList<ReportDTO>();

		List<Report> reports = em.createNamedQuery("Report.findAll")
				.getResultList();

		for (Report report : reports) {
			result.add(convertToDTO(report));
		}

		return result;
	}

	public byte[] getReportBody(Integer reportId, String format)
			throws ValidationException, SecurityViolationException {

		if (reportId == null) {
			throw new NullPointerException("The reportId is null");
		}

		if (format == null) {
			throw new NullPointerException("The format is null");
		}

		ReportBody reportBody;

		try {
			reportBody = (ReportBody) em
					.createNamedQuery("ReportBody.findByReportIdAndFormat")
					.setParameter("reportId", reportId)
					.setParameter("format", format).getSingleResult();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get the report body for: "
					+ reportId + ", " + format);
		}

		return reportBody.getBody();
	}

	public void removeReport(Integer reportId) throws ValidationException,
			SecurityViolationException {

		em.createNamedQuery("ReportBody.removeByReportId")
				.setParameter("reportId", reportId).executeUpdate();

		em.createNamedQuery("Report.removeById").setParameter("id", reportId)
				.executeUpdate();
	}

	@Override
	public List<ReportGeneratorDTO> getReportGenerators()
			throws SecurityViolationException {
		LinkedList<ReportGeneratorDTO> generators = new LinkedList<ReportGeneratorDTO>();

		Iterator<ReportGenerator> it = ServiceLoader
				.load(ReportGenerator.class).iterator();

		while (it.hasNext()) {
			generators.add(convertToDTO(it.next()));
		}

		return generators;
	}

	protected ReportGeneratorDTO convertToDTO(ReportGenerator generator) {
		ReportGeneratorDTO generatorDTO = new ReportGeneratorDTO();
		generatorDTO.setTitle(generator.getTitle());

		List<String> formats = new LinkedList<String>();
		formats.add(generator.getPrimaryFormat());
		formats.addAll(Arrays.asList(generator.getSecondaryFormats()));
		generatorDTO.setFormats(formats);

		generatorDTO.setId(generator.getClass().getName());
		return generatorDTO;
	}

	protected ReportDTO convertToDTO(Report report) {
		ReportDTO reportDTO = new ReportDTO();

		reportDTO.setId(report.getId());
		reportDTO.setNote(report.getNote());
		reportDTO.setTitle(report.getTitle());

		reportDTO.setDateTimeGenerated(new DateTime(report
				.getTimestampGenerated()));

		List<String> formats = (List<String>) em
				.createNamedQuery("ReportBody.findFormatsByReportId",
						String.class).setParameter("reportId", report.getId())
				.getResultList();

		reportDTO.setFormats(new java.util.ArrayList(formats));

		ReportGenerator generator;

		try {
			Class<ReportGenerator> generatorClass = (Class<ReportGenerator>) Thread
					.currentThread().getContextClassLoader()
					.loadClass(report.getReportGeneratorClass());
			generator = generatorClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate the generator",
					ex);
		}

		reportDTO.setReportGenerator(convertToDTO(generator));

		return reportDTO;
	}
}
