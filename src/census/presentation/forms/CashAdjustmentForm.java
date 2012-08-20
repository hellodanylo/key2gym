/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.forms;

import census.business.api.ValidationException;
import census.business.dto.CashAdjustmentDTO;
import census.presentation.CensusFrame;
import census.presentation.util.CensusBindingListener;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.*;
import org.jdesktop.beansbinding.*;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CashAdjustmentForm extends JPanel {

    /**
     * Creates new form ItemForm
     */
    public CashAdjustmentForm() {
        strings = ResourceBundle.getBundle("census/presentation/resources/Strings");
        initComponents();
        buildForm();
    }

    /**
     * Initializes the components on this form.
     */
    private void initComponents() {
        dateTextField = new JTextField();
        dateTextField.setEnabled(false);

        newAdjustmentTextField = new JTextField();
        newAdjustmentTextField.setColumns(8);

        totalAdjustmentTextField = new JTextField();
        totalAdjustmentTextField.setEnabled(false);

        noteTextArea = new JTextArea(10, 20);
        noteTextAreaScrollPane = new JScrollPane(noteTextArea);

        addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addButtonActionPerformed(e);
            }
        });

        subtractButton = new JButton("-");
        subtractButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                subtractButtonActionPerformed(e);
            }
        });
        subtractButton.setPreferredSize(addButton.getPreferredSize());

    }

    /**
     * Builds this from by placing the components on it.
     */
    private void buildForm() {
        FormLayout layout = new FormLayout("r:p, 3dlu, p:g", "p, 3dlu, p, 3dlu, p, 3dlu, f:p:g");
        JLabel label;
        
        setLayout(layout);
        
        label = new JLabel(strings.getString("Label.Date"));
        add(label,                      CC.xy(1, 1));
        add(dateTextField,              CC.xy(3, 1));

        label = new JLabel(strings.getString("Label.Adjustment"));
        add(label,                      CC.xy(1, 3));
        add(totalAdjustmentTextField,   CC.xy(3, 3));

        JPanel adjustmentPanel = new JPanel();
        adjustmentPanel.setLayout(new FormLayout("p:g, p, p", "p"));
        adjustmentPanel.add(newAdjustmentTextField, CC.xy(1, 1));
        adjustmentPanel.add(addButton,              CC.xy(2, 1));
        adjustmentPanel.add(subtractButton,         CC.xy(3, 1));
        add(adjustmentPanel,            CC.xy(3, 5));

        label = new JLabel(strings.getString("Label.Note"));
        label.setVerticalAlignment(SwingConstants.TOP);
        add(label,                      CC.xy(1, 7));
        add(noteTextAreaScrollPane,     CC.xy(3, 7));
    }

    private void addButtonActionPerformed(ActionEvent evt) {
        BigDecimal augend;

        try {
            augend = new BigDecimal(newAdjustmentTextField.getText().trim());
        } catch (NumberFormatException ex) {
            String message = MessageFormat.format(strings.getString("Message.FieldIsNotFilledInCorrectly.withFieldName"), strings.getString("Text.Adjustmnet"));
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(new ValidationException(message));
            return;
        }

        cashAdjustment.setAmount(cashAdjustment.getAmount().add(augend));
        totalAdjustmentTextField.setText(cashAdjustment.getAmount().toPlainString());
        newAdjustmentTextField.setText("");
    }

    private void subtractButtonActionPerformed(ActionEvent evt) {
        BigDecimal subtrahend;

        try {
            subtrahend = new BigDecimal(newAdjustmentTextField.getText().trim());
        } catch (NumberFormatException ex) {
            String message = MessageFormat.format(strings.getString("Message.FieldIsNotFilledInCorrectly.withFieldName"), strings.getString("Text.Adjustmnet"));
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(new ValidationException(message));
            return;
        }

        cashAdjustment.setAmount(cashAdjustment.getAmount().subtract(subtrahend));
        totalAdjustmentTextField.setText(cashAdjustment.getAmount().toPlainString());
        newAdjustmentTextField.setText("");
    }

    /**
     * Sets the form's cash adjustment.
     *
     * @param cashAdjustment the new cash adjustment
     */
    public void setCashAdjustment(CashAdjustmentDTO cashAdjustment) {
        this.cashAdjustment = cashAdjustment;

        if (cashAdjustment == null) {
            throw new NullPointerException();
        }

        if (bindingGroup == null) {
            censusBindingListener = new CensusBindingListener();
            bindingGroup = new BindingGroup();
            bindingGroup.addBindingListener(censusBindingListener);

            /**
             * Date
             */
            Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    cashAdjustment, BeanProperty.create("date"), dateTextField, BeanProperty.create("text"), "date");
            binding.setSourceUnreadableValue("");
            binding.setSourceNullValue("");
            binding.setConverter(new Converter<DateMidnight, String>() {

                @Override
                public String convertForward(DateMidnight s) {
                    return MessageFormat.format("{0, date, long}", s.toDate());
                }

                @Override
                public DateMidnight convertReverse(String t) {
                    throw new UnsupportedOperationException("The date modification is not supported.");
                }
            });
            bindingGroup.addBinding(binding);

            /**
             * Cash adjustment
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    cashAdjustment, BeanProperty.create("amount"), totalAdjustmentTextField, BeanProperty.create("text"), "amount");
            binding.setSourceUnreadableValue("");
            binding.setSourceNullValue("");
            bindingGroup.addBinding(binding);

            /**
             * Note
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    cashAdjustment, BeanProperty.create("note"), noteTextArea, BeanProperty.create("text"), "note");
            binding.setSourceUnreadableValue("");
            binding.setSourceNullValue("");
            bindingGroup.addBinding(binding);

            bindingGroup.bind();
        } else {

            /*
             * Takes each binding and resets the source object.
             */
            for (Binding binding : bindingGroup.getBindings()) {
                binding.unbind();
                binding.setSourceObject(cashAdjustment);
                binding.bind();
            }
        }
    }

    public CashAdjustmentDTO getCashAdjustment() {
        return cashAdjustment;
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
    private CashAdjustmentDTO cashAdjustment;
    /*
     * Presentation
     */
    private ResourceBundle strings;
    private BindingGroup bindingGroup;
    private CensusBindingListener censusBindingListener;
    private JTextField dateTextField;
    private JTextField newAdjustmentTextField;
    private JTextArea noteTextArea;
    private JScrollPane noteTextAreaScrollPane;
    private JTextField totalAdjustmentTextField;
    private JButton addButton;
    private JButton subtractButton;
}