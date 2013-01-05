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
package org.key2gym.client.panels.forms;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.util.ResourceBundle;
import javax.swing.JTextField;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.key2gym.client.report.spi.ReportInputSource;
import org.key2gym.client.resources.ResourcesManager;
import org.key2gym.client.util.DateTimeToStringConverter;
import org.key2gym.client.util.FormBindingListener;
import org.key2gym.client.util.BindableMutableInterval;
import org.joda.time.*;

/**
 *
 * @author Danylo Vashchilenko
 */
@ReportInputSource(supports = {
	"org.key2gym.business.reports.revenue.daily.DailyRevenueReportGenerator",
	"org.key2gym.business.reports.attendances.daily.DailyAttendancesReportGenerator",
	"org.key2gym.business.reports.revenue.monthly.MonthlyRevenueReportGenerator",
	"org.key2gym.business.reports.attendances.monthly.MonthlyAttendancesReportGenerator"
})
public class DateIntervalReportInputFormPanel extends ReportInputFormPanel<ReadableInterval> {

    /**
     * Creates new DateIntervalReportInputFormPanel
     */
    public DateIntervalReportInputFormPanel() {
        strings = ResourcesManager.getStrings();

        initComponents();
        buildForm();
        
        interval = new BindableMutableInterval(new DateMidnight(), new DateMidnight());

        formBindingListener = new FormBindingListener();
        bindingGroup = new BindingGroup();
        bindingGroup.addBindingListener(formBindingListener);

        /**
         * Interval start
         */
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                interval, BeanProperty.create("start"), intervalStartTextField, BeanProperty.create("text"), "start");
        binding.setConverter(new DateTimeToStringConverter(getString("Text.Beginning"), "dd-MM-yyyy"));
        bindingGroup.addBinding(binding);

        /**
         * Interval end
         */
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                interval, BeanProperty.create("end"), intervalEndTextField, BeanProperty.create("text"), "end");
        binding.setConverter(new DateTimeToStringConverter(getString("Text.Ending"), "dd-MM-yyyy"));
        bindingGroup.addBinding(binding);

        bindingGroup.bind();
    }

    /**
     * Initializes the components on this form.
     */
    private void initComponents() {
        intervalStartTextField = new JTextField(30);
        intervalEndTextField = new JTextField(30);
    }

    /**
     * Builds this form by placing the components on it.
     */
    private void buildForm() {
        FormLayout layout = new FormLayout("right:default, 3dlu, default:grow", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, strings, this);

        builder.appendI15d("Label.BeginningDate", intervalStartTextField);
        builder.nextLine();
        builder.appendI15d("Label.EndingDate", intervalEndTextField);
    }

    /**
     * Sets the form's interval.
     *
     * @param range the interval, can not be null
     */
    @Override
    public void setForm(ReadableInterval interval) {
        this.interval = new BindableMutableInterval(interval);

        for (Binding binding : bindingGroup.getBindings()) {
            binding.unbind();
            binding.setSourceObject(interval);
            binding.bind();
        }
    }

    @Override
    public ReadableInterval getForm() {
        return new Interval(interval);
    }

    /**
     * Tries to save the form to the current item.
     *
     * @return true, if the form is valid and has been saved
     */
    @Override
    public boolean trySave() {
	
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }

        return formBindingListener.getInvalidTargets().isEmpty();
    }
    /*
     * Business
     */
    private BindableMutableInterval interval;
    /*
     * Presentation
     */
    private ResourceBundle strings;
    private BindingGroup bindingGroup;
    private FormBindingListener formBindingListener;
    private JTextField intervalStartTextField;
    private JTextField intervalEndTextField;
}
