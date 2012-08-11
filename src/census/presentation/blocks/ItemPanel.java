/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.blocks;

import census.business.api.ValidationException;
import census.business.dto.ItemDTO;
import census.presentation.util.CensusBindingListener;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.jdesktop.beansbinding.*;

/**
 *
 * @author daniel
 */
public class ItemPanel extends javax.swing.JPanel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    private BindingGroup bindingGroup;
    private CensusBindingListener censusBindingListener;
    private ItemDTO item;

    /**
     * Creates new form ItemPanel
     */
    public ItemPanel() {
        initComponents();
    }

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
                        throw new RuntimeException(new ValidationException(bundle.getString("Message.BarcodeHasToBeNumber")));
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
                    return value == null ? bundle.getString("Messages.Infinite") : value.toString();
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
                        throw new RuntimeException(new ValidationException(bundle.getString("Message.QuantityHasToBeNumber")));
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
                    } catch(NumberFormatException ex) {
                        String string = bundle.getString("Message.FieldIsNotFilledInCorrectly.withFieldName");
                        string = MessageFormat.format(string, bundle.getString("Text.Price"));
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

    /**
     * Saves all fields to the item.
     */
    public void save() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
    }

    public ItemDTO getItem() {
        return item;
    }

    public Boolean isFormValid() {
        save();
        return censusBindingListener.getInvalidTargets().isEmpty();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        barcodeTextField = new javax.swing.JTextField();
        barcodeLabel = new javax.swing.JLabel();
        priceTextField = new javax.swing.JTextField();
        priceLabel = new javax.swing.JLabel();
        titleTextField = new javax.swing.JTextField();
        titleLabel = new javax.swing.JLabel();
        quantityTextField = new javax.swing.JTextField();
        quantityLabel = new javax.swing.JLabel();

        barcodeTextField.setName("barcode");

        barcodeLabel.setText(bundle.getString("Label.Barcode")); // NOI18N

        priceTextField.setName("price");

        priceLabel.setText(bundle.getString("Label.Price")); // NOI18N

        titleTextField.setName("title");

        titleLabel.setText(bundle.getString("Label.Title")); // NOI18N

        quantityTextField.setName("quantity");

        quantityLabel.setText(bundle.getString("Label.Quantity")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barcodeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(priceLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(quantityLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(barcodeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(priceTextField)
                    .addComponent(quantityTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel)
                    .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quantityLabel)
                    .addComponent(quantityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(priceLabel)
                    .addComponent(priceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(barcodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(barcodeLabel))
                .addContainerGap(22, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel barcodeLabel;
    private javax.swing.JTextField barcodeTextField;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JTextField priceTextField;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JTextField quantityTextField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField titleTextField;
    // End of variables declaration//GEN-END:variables
}
