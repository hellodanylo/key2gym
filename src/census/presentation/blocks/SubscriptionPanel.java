/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.blocks;

import census.business.TimeRangesService;
import census.business.api.ValidationException;
import census.business.dto.SubscriptionDTO;
import census.business.dto.TimeRangeDTO;
import census.presentation.util.CensusBindingListener;
import census.presentation.util.TimeRangeListCellRenderer;
import java.beans.Beans;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import org.jdesktop.beansbinding.*;

/**
 *
 * @author daniel
 */
public class SubscriptionPanel extends javax.swing.JPanel {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    private BindingGroup bindingGroup;
    private CensusBindingListener censusBindingListener;
    private List<TimeRangeDTO> timeRanges;
    
    /**
     * Creates new form SubscriptionPanel
     */
    public SubscriptionPanel() {
        if(Beans.isDesignTime()) {
            timeRanges = new LinkedList<>();
        } else {
            timeRanges = TimeRangesService.getInstance().getAllTimeRanges();
            if(timeRanges.isEmpty()) {
                throw new RuntimeException("There is not any time ranges found.");
            }
        }
        
        initComponents();

    }
    
    public void setSubscription(SubscriptionDTO subscription) {
        this.subscription = subscription;
        
        if(subscription == null) {
            titleTextField.setEnabled(false);
            barcodeTextField.setEnabled(false);
            priceTextField.setEnabled(false);
            unitsSpinner.setEnabled(false);
            daysSpinner.setEnabled(false);
            monthsSpinner.setEnabled(false);
            yearsSpinner.setEnabled(false);
            timeRangeComboBox.setEnabled(false);
        } else {
            titleTextField.setEnabled(true);
            barcodeTextField.setEnabled(true);
            priceTextField.setEnabled(true);
            unitsSpinner.setEnabled(true);
            daysSpinner.setEnabled(true);
            monthsSpinner.setEnabled(true);
            yearsSpinner.setEnabled(true);
            timeRangeComboBox.setEnabled(true);         
        }
    
        if (bindingGroup == null) {
            censusBindingListener = new CensusBindingListener();
            bindingGroup = new BindingGroup();
            bindingGroup.addBindingListener(censusBindingListener);

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
                        throw new RuntimeException(new ValidationException("The barcode has to be a number."));
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
                        throw new RuntimeException(new ValidationException("The price field is not filled in correctly."));
                    }
                }
            });          
            bindingGroup.addBinding(binding);
            
            /**
             * Units
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("units"), unitsSpinner, BeanProperty.create("value"), "units");
            binding.setSourceNullValue((short)0);
            binding.setSourceUnreadableValue((short)0);
            bindingGroup.addBinding(binding);
            
            /**
             * Days
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("termDays"), daysSpinner, BeanProperty.create("value"), "termDays");
            binding.setSourceNullValue((short)0);
            binding.setSourceUnreadableValue((short)0);
            bindingGroup.addBinding(binding);
            
            /**
             * Months
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("termMonths"), monthsSpinner, BeanProperty.create("value"), "termMonths");
            binding.setSourceNullValue((short)0);
            binding.setSourceUnreadableValue((short)0);
            bindingGroup.addBinding(binding);
            
            /**
             * Years
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("termYears"), yearsSpinner, BeanProperty.create("value"), "termYears");
            binding.setSourceNullValue((short)0);
            binding.setSourceUnreadableValue((short)0);
            bindingGroup.addBinding(binding);
            
            /**
             * Time range
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    subscription, BeanProperty.create("timeRangeId"), timeRangeComboBox, BeanProperty.create("selectedItem"), "timeRange");
            binding.setSourceNullValue(timeRanges.get(0));
            binding.setSourceUnreadableValue(timeRanges.get(0));
            binding.setConverter(new Converter<Short, TimeRangeDTO>() {

                @Override
                public TimeRangeDTO convertForward(Short value) {
                    for(TimeRangeDTO timeRange : timeRanges) {
                        if(timeRange.getId().equals(value)) {
                            return timeRange;
                        }
                    }
                    return null;
                }

                @Override
                public Short convertReverse(TimeRangeDTO value) {
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
     * Saves all fields to the subscription.
     */
    public void save() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
    }
    
    public SubscriptionDTO getSubscription() {
        return subscription;
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

        subscription = new census.business.dto.SubscriptionDTO();
        titleLabel = new javax.swing.JLabel();
        titleTextField = new javax.swing.JTextField();
        priceLabel = new javax.swing.JLabel();
        priceTextField = new javax.swing.JTextField();
        barcodeLabel = new javax.swing.JLabel();
        barcodeTextField = new javax.swing.JTextField();
        unitsLabel = new javax.swing.JLabel();
        unitsSpinner = new javax.swing.JSpinner();
        daysLabel = new javax.swing.JLabel();
        daysSpinner = new javax.swing.JSpinner();
        monthsLabel = new javax.swing.JLabel();
        monthsSpinner = new javax.swing.JSpinner();
        yearsLabel = new javax.swing.JLabel();
        yearsSpinner = new javax.swing.JSpinner();
        timeRangeLabel = new javax.swing.JLabel();
        timeRangeComboBox = new javax.swing.JComboBox();

        titleLabel.setText(bundle.getString("Label.Title")); // NOI18N

        priceLabel.setText(bundle.getString("Label.Price")); // NOI18N

        barcodeLabel.setText(bundle.getString("Label.Barcode")); // NOI18N

        unitsLabel.setText(bundle.getString("Label.Units")); // NOI18N

        unitsSpinner.setModel(new javax.swing.SpinnerNumberModel(Short.valueOf((short)0), Short.valueOf((short)0), Short.valueOf((short)999), Short.valueOf((short)1)));

        daysLabel.setText(bundle.getString("Label.Days")); // NOI18N

        daysSpinner.setModel(new javax.swing.SpinnerNumberModel(Short.valueOf((short)0), Short.valueOf((short)0), Short.valueOf((short)999), Short.valueOf((short)1)));

        monthsLabel.setText(bundle.getString("Label.Months")); // NOI18N

        monthsSpinner.setModel(new javax.swing.SpinnerNumberModel(Short.valueOf((short)0), Short.valueOf((short)0), Short.valueOf((short)999), Short.valueOf((short)1)));

        yearsLabel.setText(bundle.getString("Label.Years")); // NOI18N

        yearsSpinner.setModel(new javax.swing.SpinnerNumberModel(Short.valueOf((short)0), Short.valueOf((short)0), Short.valueOf((short)999), Short.valueOf((short)1)));

        timeRangeLabel.setText(bundle.getString("Label.TimeRange")); // NOI18N

        timeRangeComboBox.setModel(new DefaultComboBoxModel(timeRanges.toArray()));
        timeRangeComboBox.setRenderer(new TimeRangeListCellRenderer());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barcodeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(yearsLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(monthsLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(daysLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(timeRangeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(unitsLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(priceLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(daysSpinner)
                    .addComponent(barcodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(monthsSpinner)
                    .addComponent(yearsSpinner)
                    .addComponent(titleTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(unitsSpinner)
                    .addComponent(priceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(timeRangeComboBox, 0, 1, Short.MAX_VALUE))
                .addGap(18, 18, 18))
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
                    .addComponent(priceLabel)
                    .addComponent(priceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unitsLabel)
                    .addComponent(unitsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeRangeLabel)
                    .addComponent(timeRangeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(daysLabel)
                    .addComponent(daysSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(monthsLabel)
                    .addComponent(monthsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yearsLabel)
                    .addComponent(yearsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(barcodeLabel)
                    .addComponent(barcodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel barcodeLabel;
    private javax.swing.JTextField barcodeTextField;
    private javax.swing.JLabel daysLabel;
    private javax.swing.JSpinner daysSpinner;
    private javax.swing.JLabel monthsLabel;
    private javax.swing.JSpinner monthsSpinner;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JTextField priceTextField;
    private census.business.dto.SubscriptionDTO subscription;
    private javax.swing.JComboBox timeRangeComboBox;
    private javax.swing.JLabel timeRangeLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField titleTextField;
    private javax.swing.JLabel unitsLabel;
    private javax.swing.JSpinner unitsSpinner;
    private javax.swing.JLabel yearsLabel;
    private javax.swing.JSpinner yearsSpinner;
    // End of variables declaration//GEN-END:variables
}
