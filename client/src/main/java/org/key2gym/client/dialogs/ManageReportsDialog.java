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
package org.key2gym.client.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.UserException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.business.api.dtos.ReportGeneratorDTO;
import org.key2gym.business.api.services.ReportsService;
import org.key2gym.client.ContextManager;
import org.key2gym.client.MainFrame;
import org.key2gym.client.UserExceptionHandler;
import org.key2gym.client.panels.forms.ReportInputFormPanel;
import org.key2gym.client.report.spi.ReportHandler;
import org.key2gym.client.report.spi.ReportInputSource;
import org.key2gym.client.util.ReportsTableModel;
import org.key2gym.client.util.ReportsTableModel.Column;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * @author Danylo Vashchilenko
 */
public class ManageReportsDialog extends AbstractDialog {

	/**
	 * Creates new form ItemsDialog
	 */
	public ManageReportsDialog(JFrame parent) throws SecurityViolationException {
		super(parent, true);

		service = ContextManager.lookup(ReportsService.class);

		initComponents();
		buildDialog();
		loadReports();
	}

	private void initComponents() {

		/*
		 * Reports table
		 */
		reportsScrollPane = new JScrollPane();
		reportsTable = new JTable();
		Column[] columns = new Column[] { Column.ID, Column.TITLE,
				Column.DATETIME_GENERATED, Column.NOTE };
		reportsTableModel = new ReportsTableModel(columns);
		reportsTable.setModel(reportsTableModel);
		reportsScrollPane.setViewportView(reportsTable);

		/*
		 * Add button
		 */
		addButton = new JButton();
		addButton.setIcon(new ImageIcon(getClass().getResource(
				"/org/key2gym/client/resources/plus32.png"))); // NOI18N
		addButton.setText(getString("Button.Add")); // NOI18N
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		/*
		 * Edit button
		 */
		editButton = new JButton();
		editButton.setIcon(new ImageIcon(getClass().getResource(
				"/org/key2gym/client/resources/edit32.png"))); // NOI18N
		editButton.setText(getString("Button.Edit")); // NOI18N
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				editButtonActionPerformed(evt);
			}
		});

		/*
		 * Open button
		 */
		openButton = new JButton();
		openButton.setIcon(new ImageIcon(getClass().getResource(
				"/org/key2gym/client/resources/document32.png"))); // NOI18N
		openButton.setText(getString("Button.Open")); // NOI18N
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				openButtonActionPerformed(evt);
			}
		});

		/*
		 * Save button
		 */
		saveButton = new JButton();
		saveButton.setIcon(new ImageIcon(getClass().getResource(
				"/org/key2gym/client/resources/save32.png"))); // NOI18N
		saveButton.setText(getString("Button.Save")); // NOI18N
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});

		/*
		 * Remove button
		 */
		removeButton = new JButton();
		removeButton.setIcon(new ImageIcon(getClass().getResource(
				"/org/key2gym/client/resources/remove32.png"))); // NOI18N
		removeButton.setText(getString("Button.Remove")); // NOI18N
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				removeButtonActionPerformed(evt);
			}
		});

		/*
		 * Listens to the table.
		 */
		reportsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						onItemsTableSelectionChanged();
					}
				});
		onItemsTableSelectionChanged();

		/*
		 * Close button
		 */
		closeButton = new JButton();
		closeButton.setAction(getCloseAction());
		getRootPane().setDefaultButton(closeButton);
	}

	private void buildDialog() {
		FormLayout layout = new FormLayout(
				"4dlu, [400dlu, p]:g, 4dlu, p, 4dlu",
				"4dlu, f:[200dlu, p]:g, 4dlu");
		setLayout(layout);

		add(reportsScrollPane, CC.xy(2, 2));

		JPanel buttonsPanel = new JPanel();
		{
			buttonsPanel.setLayout(new FormLayout("d",
					"b:d:g, c:d, c:d, c:d, t:d:g, d"));
			buttonsPanel.add(addButton, CC.xy(1, 1));
			buttonsPanel.add(editButton, CC.xy(1, 2));
			buttonsPanel.add(openButton, CC.xy(1, 3));
			buttonsPanel.add(saveButton, CC.xy(1, 4));
			buttonsPanel.add(removeButton, CC.xy(1, 5));
			buttonsPanel.add(closeButton, CC.xy(1, 6));
		}
		add(buttonsPanel, CC.xy(4, 2));

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(getString("Title.ManageReports")); // NOI18N
		pack();
		setMinimumSize(getPreferredSize());
		setResizable(true);
		setLocationRelativeTo(getParent());
	}

	private void addButtonActionPerformed(ActionEvent evt) {

		/*
		 * Prompts the user for the report type.
		 */
		PickReportTypeDialog pickReportTypeDialog;
		try {
			pickReportTypeDialog = new PickReportTypeDialog(this);
		} catch (SecurityViolationException ex) {
			UserExceptionHandler.getInstance().processException(ex);
			return;
		}
		pickReportTypeDialog.setVisible(true);

		if (pickReportTypeDialog.getResult().equals(
				AbstractDialog.Result.CANCEL)) {
			return;
		}

		ReportGeneratorDTO generator = pickReportTypeDialog
				.getReportGenerator();
		Iterator<ReportInputFormPanel> iterator = ServiceLoader.load(
				ReportInputFormPanel.class).iterator();
		ReportInputFormPanel formPanel = null;

		/*
		 * Looks for the FormPanel supporting the selected report type.
		 */
		while (iterator.hasNext()) {
			ReportInputFormPanel candidate = iterator.next();
			ReportInputSource annotation = candidate.getClass().getAnnotation(
					ReportInputSource.class);

			/*
			 * If the annotation is not present, skips the form panel.
			 */
			if (annotation == null) {
				Logger.getLogger(ManageReportsDialog.class).error(
						candidate.getClass()
								+ " is missing the required annotation!");
				continue;
			}

			Logger.getLogger(ManageReportsDialog.class).debug(
					"Trying " + candidate.getClass().getName());

			/*
			 * If the form panel does not support the required report generator,
			 * skips it.
			 */
			boolean supported = false;
			for (String generatorId : annotation.supports()) {

				Logger.getLogger(ManageReportsDialog.class).debug(
						candidate.getClass().getName() + " supports "
								+ generatorId);

				if (generator.getId().equals(generatorId)) {
					supported = true;
					break;
				}
			}

			if (!supported) {
				Logger.getLogger(ManageReportsDialog.class).warn(
						"No ReportInputFormPanel supports the generator: "
								+ generator.getClass().getName());
				continue;
			}

			formPanel = candidate;
		}

		if (formPanel == null) {
			UserExceptionHandler.getInstance().processException(
					new ValidationException(
							getString("Message.ReportsTypeNotSupported")));
			return;
		}

		FormDialog formDialog = new FormDialog(this, formPanel);
		formDialog.setTitle(getString("Title.EnterInputData"));

		List<Action> actions = new LinkedList<>();
		actions.add(new OkAction(formDialog));
		actions.add(new CancelAction(formDialog));
		formDialog.setActions(actions);

		while (true) {
			formDialog.setVisible(true);

			if (formDialog.getResult().equals(Result.CANCEL)) {
				return;
			}

			if (!formDialog.getFormPanel().trySave()) {
				continue;
			}

			try {
				ContextManager.lookup(ReportsService.class).generateReport(
						generator.getId(), formDialog.getFormPanel().getForm());
			} catch (UserException ex) {
				UserExceptionHandler.getInstance().processException(ex);
			}

			break;
		}

		loadReports();
	}

	private void editButtonActionPerformed(ActionEvent evt) {
	}

	private void openButtonActionPerformed(ActionEvent evt) {
		ReportHandler handler = null;
		ReportDTO report = reports.get(reportsTable.getSelectedRow());

		PickReportFormatDialog reportFormatDialog = new PickReportFormatDialog(
				this, report);
		reportFormatDialog.setVisible(true);

		if (reportFormatDialog.getResult().equals(Result.CANCEL)) {
			return;
		}

		String reportFormat = reportFormatDialog.getReportFormat();

		/*
		 * If the required format is not available, converts the report to it
		 * right now.
		 */
		if (!report.getFormats().contains(reportFormat)) {
			try {
				service.convertReport(report.getId(), reportFormat);
			} catch (ValidationException ex) {
				throw new RuntimeException(
						"The report's id should have been valid", ex);
			} catch (SecurityViolationException ex) {
				UserExceptionHandler.getInstance().processException(ex);
				return;
			}
		}

		byte[] reportBody;
		try {
			reportBody = service.getReportBody(report.getId(), reportFormat);
		} catch (ValidationException | SecurityViolationException ex) {
			UserExceptionHandler.getInstance().processException(ex);
			return;
		}

		PickReportHandlerDialog reportHandlerDialog;
		try {
			reportHandlerDialog = new PickReportHandlerDialog(this,
					reportFormat);
		} catch (UserException ex) {
			UserExceptionHandler.getInstance().processException(ex);
			return;
		}
		reportHandlerDialog.setVisible(true);

		if (reportHandlerDialog.getResult().equals(Result.CANCEL)) {
			return;
		}

		reportHandlerDialog.getReportHandler().handle(report, reportBody,
				reportFormat);

		setResult(Result.CLOSE);
		dispose();
	}

	private void saveButtonActionPerformed(ActionEvent evt) {
		ReportDTO report = reports.get(reportsTable.getSelectedRow());

		PickReportFormatDialog reportFormatDialog = new PickReportFormatDialog(
				this, report);
		reportFormatDialog.setVisible(true);

		if (reportFormatDialog.getResult().equals(Result.CANCEL)) {
			return;
		}

		String reportFormat = reportFormatDialog.getReportFormat();

		/*
		 * If the required format is not available, converts the report to it
		 * right now.
		 */
		if (!report.getFormats().contains(reportFormat)) {
			try {
				service.convertReport(report.getId(), reportFormat);
				report.getFormats().add(reportFormat);
			} catch (ValidationException ex) {
				throw new RuntimeException("Failed to convert the report", ex);
			} catch (SecurityViolationException ex) {
				UserExceptionHandler.getInstance().processException(ex);
				return;
			}
		}

		byte[] reportBody;
		try {
			reportBody = service.getReportBody(report.getId(), reportFormat);
		} catch (ValidationException | SecurityViolationException ex) {
			UserExceptionHandler.getInstance().processException(ex);
			return;
		}

		java.io.File file = null;

		/* Writes the report to file. */
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new java.io.File(System
					.getProperty("user.home")
					+ "/key2gym-report-"
					+ report.getId() + "." + reportFormat));
			int returnVal = chooser.showSaveDialog(MainFrame.getInstance());

			if (returnVal == JFileChooser.CANCEL_OPTION) {
				return;
			}

			file = chooser.getSelectedFile();

			java.io.OutputStream stream = new java.io.FileOutputStream(file);
			stream.write(reportBody);
			stream.close();
		} catch (java.io.IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void removeButtonActionPerformed(ActionEvent evt) {
		if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this,
				getString("Message.ConfirmRemoval"),
				getString("Title.Confirmation"), JOptionPane.YES_NO_OPTION)) {
			return;
		}

		for (int index : reportsTable.getSelectedRows()) {
			try {
				ReportDTO report = reports.get(index);
				service.removeReport(report.getId());
			} catch (ValidationException | SecurityViolationException ex) {
				UserExceptionHandler.getInstance().processException(ex);
			}
		}

		loadReports();
	}

	private void loadReports() {
		try {
			reports = ContextManager.lookup(ReportsService.class).getAll();
		} catch (SecurityViolationException ex) {
			UserExceptionHandler.getInstance().processException(ex);
			return;
		}
		reportsTableModel.setReports(reports);
	}

	private void onItemsTableSelectionChanged() {
		if (reportsTable.getSelectedRowCount() == 0) {
			removeButton.setEnabled(false);
			editButton.setEnabled(false);
			openButton.setEnabled(false);
			saveButton.setEnabled(false);
		} else if (reportsTable.getSelectedRowCount() == 1) {
			editButton.setEnabled(true);
			removeButton.setEnabled(true);
			openButton.setEnabled(true);
			saveButton.setEnabled(true);
		} else {
			removeButton.setEnabled(true);
			editButton.setEnabled(false);
			openButton.setEnabled(false);
			saveButton.setEnabled(false);
		}
	}

	/*
	 * Business
	 */
	private List<ReportDTO> reports;
	private ReportsService service;

	/*
	 * Presentation
	 */
	private ReportsTableModel reportsTableModel;
	private JButton addButton;
	private JButton editButton;
	private JButton openButton;
	private JButton saveButton;
	private JButton closeButton;
	private JButton removeButton;
	private JScrollPane reportsScrollPane;
	private JTable reportsTable;
}
