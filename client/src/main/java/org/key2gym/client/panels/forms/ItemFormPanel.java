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
package org.key2gym.client.panels.forms;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Converter;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ItemDTO;
import org.key2gym.client.util.FormBindingListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemFormPanel extends FormPanel<ItemDTO> {

    /**
     * Creates new form ItemFormPanel
     */
    public ItemFormPanel() {
        strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");
        initComponents();
        buildForm();
    }

    /**
     * Initializes the components on this form.
     */
    private void initComponents() {
        barcodeTextField = new JTextField(30);
        priceTextField = new JTextField(30);
        titleTextField = new JTextField(30);
        quantityTextField = new JTextField(30);
    }

    /**
     * Builds this from by placing the components on it.
     */
    private void buildForm() {
        FormLayout layout = new FormLayout("right:default, 3dlu, default:grow", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, strings, this);

        builder.appendI15d("Label.Title", titleTextField);
        builder.nextLine();
        builder.appendI15d("Label.Quantity", quantityTextField);
        builder.nextLine();
        builder.appendI15d("Label.Price", priceTextField);
        builder.nextLine();
        builder.appendI15d("Label.Barcode", barcodeTextField);

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, KeyEvent.CTRL_DOWN_MASK), BarcodeFocusAction.class.getName());
        getActionMap().put(BarcodeFocusAction.class.getName(), new BarcodeFocusAction());

    }

    private class BarcodeFocusAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            barcodeTextField.requestFocusInWindow();
        }
    }

    /**
     * Sets the form's item.
     *
     * @param item the new item
     */
    public void setForm(ItemDTO item) {
        this.item = item;

        if (item == null) {
            titleTextField.setEnabled(false);
            barcodeTextField.setEnabled(false);
            quantityTextField.setEnabled(false);
            priceTextField.setEnabled(false);
        } else {
            titleTextField.setEnabled(true);
            barcodeTextField.setEnabled(true);
            quantityTextField.setEnabled(true);
            priceTextField.setEnabled(true);
        }

        if (bindingGroup == null) {
            formBindingListener = new FormBindingListener();
            bindingGroup = new BindingGroup();
            bindingGroup.addBindingListener(formBindingListener);

            /**
             * Title
             */
            Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    item, BeanProperty.create("title"), titleTextField, BeanProperty.create("text"), "title");
            binding.setSourceUnreadableValue("");
            binding.setSourceNullValue("");
            bindingGroup.addBinding(binding);

            /**
             * Barcode
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    item, BeanProperty.create("barcode"), barcodeTextField, BeanProperty.create("text"), "barcode");
            binding.setSourceUnreadableValue("");
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
                        throw new RuntimeException(new ValidationException(strings.getString("Message.BarcodeHasToBeNumber")));
                    }
                }
            });
            bindingGroup.addBinding(binding);

            /**
             * Quantity
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    item, BeanProperty.create("quantity"), quantityTextField, BeanProperty.create("text"), "quantity");
            binding.setSourceUnreadableValue("");
            binding.setSourceNullValue("");
            binding.setConverter(new Converter<Integer, String>() {

                @Override
                public String convertForward(Integer value) {
                    return value == null ? strings.getString("Messages.Infinite") : value.toString();
                }

                @Override
                public Integer convertReverse(String value) {
                    if (value == null) {
                        return null;
                    }
                    value = value.trim();
                    if (value.isEmpty()) {
                        return null;
                    }
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException(new ValidationException(strings.getString("Message.QuantityHasToBeNumber")));
                    }
                }
            });
            bindingGroup.addBinding(binding);

            /**
             * Price
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    item, BeanProperty.create("price"), priceTextField, BeanProperty.create("text"), "price");
            binding.setSourceUnreadableValue("");
            binding.setSourceNullValue("");
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
                        String string = strings.getString("Message.FieldIsNotFilledInCorrectly.withFieldName");
                        string = MessageFormat.format(string, strings.getString("Text.Price"));
                        throw new RuntimeException(new ValidationException(string));
                    }
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
                binding.setSourceObject(item);
                binding.bind();
            }
        }
    }

    @Override
    public ItemDTO getForm() {
        return item;
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
    private ItemDTO item;
    /*
     * Presentation
     */
    private ResourceBundle strings;
    private BindingGroup bindingGroup;
    private FormBindingListener formBindingListener;
    private JTextField barcodeTextField;
    private JTextField priceTextField;
    private JTextField quantityTextField;
    private JTextField titleTextField;
}
