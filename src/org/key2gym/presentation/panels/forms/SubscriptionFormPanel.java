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
package org.key2gym.presentation.panels.forms;

import org.key2gym.business.TimeSplitsService;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.SubscriptionDTO;
import org.key2gym.business.dto.TimeSplitDTO;
import org.key2gym.presentation.util.FormBindingListener;
import org.key2gym.presentation.renderers.TimeSplitListCellRenderer;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import org.jdesktop.beansbinding.*;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SubscriptionFormPanel extends FormPanel<SubscriptionDTO> {

    /**
     * Creates new form SubscriptionFormPanel
     */
    public SubscriptionFormPanel() {

        timeSplits = TimeSplitsService.getInstance().getAll();
        if (timeSplits.isEmpty()) {
            throw new RuntimeException("There is not any time splits found.");
        }

        initComponents();
        buildForm();
    }

    /**
     * Initializes the components.
     */
    private void initComponents() {

        subscription = new SubscriptionDTO();
        titleTextField = new JTextField();
        priceTextField = new JTextField();
        barcodeTextField = new JTextField();

        /*
         * Units
         */
        unitsSpinner = new JSpinner();
        unitsSpinner.setModel(new SpinnerNumberModel(Short.valueOf((short) 0), Short.valueOf((short) 0), Short.valueOf((short) 999), Short.valueOf((short) 1)));

        /*
         * Days
         */
        daysSpinner = new JSpinner();
        daysSpinner.setModel(new SpinnerNumberModel(Short.valueOf((short) 0), Short.valueOf((short) 0), Short.valueOf((short) 999), Short.valueOf((short) 1)));

        /*
         * Months
         */
        monthsSpinner = new JSpinner();
        monthsSpinner.setModel(new SpinnerNumberModel(Short.valueOf((short) 0), Short.valueOf((short) 0), Short.valueOf((short) 999), Short.valueOf((short) 1)));

        /*
         * Years
         */
        yearsSpinner = new JSpinner();
        yearsSpinner.setModel(new SpinnerNumberModel(Short.valueOf((short) 0), Short.valueOf((short) 0), Short.valueOf((short) 999), Short.valueOf((short) 1)));

        /*
         * Time split
         */
        timeSplitComboBox = new JComboBox();
        timeSplitComboBox.setModel(new DefaultComboBoxModel(timeSplits.toArray()));
        timeSplitComboBox.setRenderer(new TimeSplitListCellRenderer());
    }

    /**
     * Builds the form by placing the components on it.
     */
    private void buildForm() {
        FormLayout layout = new FormLayout("right:default, 3dlu, default:grow", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, strings, this);

        builder.appendI15d("Label.Title", titleTextField);
        builder.nextLine();

        builder.appendI15d("Label.Price", priceTextField);
        builder.nextLine();

        builder.appendI15d("Label.Units", unitsSpinner);
        builder.nextLine();

        builder.appendI15d("Label.TimeSplit", timeSplitComboBox);
        builder.nextLine();

        builder.appendI15d("Label.Days", daysSpinner);
        builder.nextLine();

        builder.appendI15d("Label.Months", monthsSpinner);
        builder.nextLine();

        builder.appendI15d("Label.Years", yearsSpinner);
        builder.nextLine();

        builder.appendI15d("Label.Barcode", barcodeTextField);
        builder.nextLine();

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, KeyEvent.CTRL_DOWN_MASK), BarcodeFocusAction.class.getName());
        getActionMap().put(BarcodeFocusAction.class.getName(), new BarcodeFocusAction());

    }

    @Override
    public SubscriptionDTO getForm() {
        return subscription;
    }

    private class BarcodeFocusAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            barcodeTextField.requestFocusInWindow();
        }
    }

    /**
     * Sets the current subscription.
     *
     * @param subscription the new subscription
     */
    @Override
    public void setForm(SubscriptionDTO subscription) {
        this.subscription = subscription;

        if (subscription == null) {
            titleTextField.setEnabled(false);
            barcodeTextField.setEnabled(false);
            priceTextField.setEnabled(false);
            unitsSpinner.setEnabled(false);
            daysSpinner.setEnabled(false);
            monthsSpinner.setEnabled(false);
            yearsSpinner.setEnabled(false);
            timeSplitComboBox.setEnabled(false);
        } else {
            titleTextField.setEnabled(true);
            barcodeTextField.setEnabled(true);
            priceTextField.setEnabled(true);
            unitsSpinner.setEnabled(true);
            daysSpinner.setEnabled(true);
            monthsSpinner.setEnabled(true);
            yearsSpinner.setEnabled(true);
            timeSplitComboBox.setEnabled(true);
        }

        if (bindingGroup == null) {
            formBindingListener = new FormBindingListener();
            bindingGroup = new BindingGroup();
            bindingGroup.addBindingListener(formBindingListener);

            /**
             * Title
             */
            Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("title"), titleTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "title");
            binding.setSourceNullValue("");
            bindingGroup.addBinding(binding);

            /**
             * Barcode
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("barcode"), barcodeTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "barcode");
            binding.setSourceNullValue("");
            binding.setConverter(new Converter<Long, String>() {

                @Override
                public String convertForward(Long value) {
                    return value == null ? "" : value.toString();
                }

                @Override
                public Long convertReverse(String value) {
                    if (value == null) {
                        return null;
                    }
                    value = value.trim();
                    if (value.isEmpty()) {
                        return null;
                    }
                    try {
                        return Long.parseLong(value);
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException(new ValidationException(
                                strings.getString("Message.BarcodeHasToBeNumbe")));
                    }
                }
            });
            bindingGroup.addBinding(binding);

            /**
             * Price
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("price"), priceTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "price");
            binding.setSourceNullValue("");
            binding.setSourceUnreadableValue("");
            binding.setConverter(new Converter<BigDecimal, String>() {

                @Override
                public String convertForward(BigDecimal value) {
                    return value.setScale(2).toPlainString();
                }

                @Override
                public BigDecimal convertReverse(String value) {
                    value = value.trim();
                    try {
                        return new BigDecimal(value);
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException(new ValidationException(
                                MessageFormat.format(
                                strings.getString("Message.FieldIsNotFilledInCorrectly.withFieldName"),
                                strings.getString("Text.Price"))));
                    }
                }
            });
            bindingGroup.addBinding(binding);

            /**
             * Units
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("units"), unitsSpinner, BeanProperty.create("value"), "units");
            binding.setSourceNullValue((short) 0);
            binding.setSourceUnreadableValue((short) 0);
            bindingGroup.addBinding(binding);

            /**
             * Days
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("termDays"), daysSpinner, BeanProperty.create("value"), "termDays");
            binding.setSourceNullValue((short) 0);
            binding.setSourceUnreadableValue((short) 0);
            bindingGroup.addBinding(binding);

            /**
             * Months
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("termMonths"), monthsSpinner, BeanProperty.create("value"), "termMonths");
            binding.setSourceNullValue((short) 0);
            binding.setSourceUnreadableValue((short) 0);
            bindingGroup.addBinding(binding);

            /**
             * Years
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("termYears"), yearsSpinner, BeanProperty.create("value"), "termYears");
            binding.setSourceNullValue((short) 0);
            binding.setSourceUnreadableValue((short) 0);
            bindingGroup.addBinding(binding);

            /**
             * Time split
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("timeSplitId"), timeSplitComboBox, BeanProperty.create("selectedItem"), "timeSplit");
            binding.setSourceNullValue(timeSplits.get(0));
            binding.setSourceUnreadableValue(timeSplits.get(0));
            binding.setConverter(new Converter<Integer, TimeSplitDTO>() {

                @Override
                public TimeSplitDTO convertForward(Integer value) {
                    for (TimeSplitDTO timeRange : timeSplits) {
                        if (timeRange.getId().equals(value)) {
                            return timeRange;
                        }
                    }
                    return null;
                }

                @Override
                public Integer convertReverse(TimeSplitDTO value) {
                    return value.getId();
                }
            });
            bindingGroup.addBinding(binding);

            bindingGroup.bind();
        } else {

            /*
             * Takes each binding and resets the source object.
             */
            for (Binding binding : bindingGroup.getBindings()) {
                binding.unbind();
                binding.setSourceObject(subscription);
                binding.bind();
            }
        }
    }

    /**
     * Gets the current subscription.
     *
     * @return the current subscription
     */
    public SubscriptionDTO getSubscription() {
        return subscription;
    }

    /**
     * Tries to save the form to the current subscription.
     *
     * @return true, if the form is valid and has been saved
     */
    public boolean trySave() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
        return formBindingListener.getInvalidTargets().isEmpty();
    }
    /*
     * Business
     */
    private SubscriptionDTO subscription;
    private List<TimeSplitDTO> timeSplits;
    /*
     * Presentation
     */
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
    private BindingGroup bindingGroup;
    private FormBindingListener formBindingListener;
    private JTextField barcodeTextField;
    private JSpinner daysSpinner;
    private JSpinner monthsSpinner;
    private JTextField priceTextField;
    private JComboBox timeSplitComboBox;
    private JTextField titleTextField;
    private JSpinner unitsSpinner;
    private JSpinner yearsSpinner;
}
