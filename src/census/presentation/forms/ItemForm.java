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
package census.presentation.forms;

import census.business.api.ValidationException;
import census.business.dto.ItemDTO;
import census.presentation.util.CensusBindingListener;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import org.jdesktop.beansbinding.*;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemForm extends JPanel {

    /**
     * Creates new form ItemForm
     */
    public ItemForm() {
        strings = ResourceBundle.getBundle("census/presentation/resources/Strings");
        initComponents();
        buildForm();
    }

    /**
     * Initializes the components on this form.
     */
    private void initComponents() {
        barcodeTextField = new javax.swing.JTextField();
        priceTextField = new javax.swing.JTextField();
        titleTextField = new javax.swing.JTextField();
        quantityTextField = new javax.swing.JTextField();
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
    }

    /**
     * Sets the form's item.
     *
     * @param item the new item
     */
    public void setItem(ItemDTO item) {
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
            censusBindingListener = new CensusBindingListener();
            bindingGroup = new BindingGroup();
            bindingGroup.addBindingListener(censusBindingListener);

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
            binding.setConverter(new Converter<Short, String>() {

                @Override
                public String convertForward(Short value) {
                    return value == null ? strings.getString("Messages.Infinite") : value.toString();
                }

                @Override
                public Short convertReverse(String value) {
                    if (value == null) {
                        return null;
                    }
                    value = value.trim();
                    if (value.isEmpty()) {
                        return null;
                    }
                    try {
                        return Short.parseShort(value);
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

    public ItemDTO getItem() {
        return item;
    }

    /**
     * Tries to save the form to the current item.
     *
     * @return true, if the form is valid and has been saved
     */
    public boolean trySave() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
        return censusBindingListener.getInvalidTargets().isEmpty();
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
    private CensusBindingListener censusBindingListener;
    private javax.swing.JTextField barcodeTextField;
    private javax.swing.JTextField priceTextField;
    private javax.swing.JTextField quantityTextField;
    private javax.swing.JTextField titleTextField;
}
