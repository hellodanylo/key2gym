/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.blocks;

import census.business.SessionsService;
import census.business.dto.ClientDTO;
import census.presentation.util.CardIntegerToStringConverter;
import census.presentation.util.CensusBindingListener;
import census.presentation.util.DateMidnightToStringConverter;
import census.presentation.util.MoneyBigDecimalToStringConverter;
import java.beans.Beans;
import java.util.ResourceBundle;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.Binding.SyncFailure;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ClientPanel extends javax.swing.JPanel {

    /*
     * Presentation
     */
    private BindingGroup bindingGroup;
    private CensusBindingListener censusBindingListener;
    private ResourceBundle bundle;

    /*
     * Business
     */
    private Boolean isPriviliged;
    private ClientDTO client;

    /**
     * Creates new form ClientPanel.
     */
    public ClientPanel() {
        if(!Beans.isDesignTime()) {
            isPriviliged = SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL);
            bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");
        } else {
            isPriviliged = true;
            bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");
        }
        
        initComponents();
    }

    public void setClient(ClientDTO newClient) {
        this.client = newClient;

        if (bindingGroup == null) {
            censusBindingListener = new CensusBindingListener();

            bindingGroup = new BindingGroup();

            Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("id"), idTextField, BeanProperty.create("text"), "id");
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("fullName"), fullNameTextField, BeanProperty.create("text"), "fullName");
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("card"), cardTextField, BeanProperty.create("text"), "card");
            binding.setConverter(new CardIntegerToStringConverter("Card"));
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("note"), noteTextArea, BeanProperty.create("text"), "note");
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("registrationDate"), registrationDateField, BeanProperty.create("text"), "registrationDate");
            binding.setConverter(new DateMidnightToStringConverter("Registration Date", "dd-MM-yyyy"));
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("expirationDate"), expirationDateField, BeanProperty.create("text"), "expirationDate");
            binding.setConverter(new DateMidnightToStringConverter("Expiration Date", "dd-MM-yyyy"));
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("attendancesBalance"), attendancesBalanceField, BeanProperty.create("value"), "attendancesBalance");
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                    BeanProperty.create("moneyBalance"), moneyBalanceField, BeanProperty.create("text"), "moneyBalance");
            binding.setConverter(new MoneyBigDecimalToStringConverter("Money Balance"));
            bindingGroup.addBinding(binding);
            
            bindingGroup.addBindingListener(censusBindingListener);

            bindingGroup.bind();
        } else {
            
            censusBindingListener.getInvalidTargets().clear();

            /*
             * We take each binding and set the source object.
             */
            for (Binding binding : bindingGroup.getBindings()) {
                binding.unbind();
                binding.setSourceObject(client);
                binding.bind();
            }
        }
    }

    private void save() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
    }
    
    public boolean isFormValid() {
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

        fullNameLabel = new javax.swing.JLabel();
        cardLabel = new javax.swing.JLabel();
        noteLabel = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        fullNameTextField = new javax.swing.JTextField();
        cardTextField = new javax.swing.JTextField();
        noteScrollPane = new javax.swing.JScrollPane();
        noteTextArea = new javax.swing.JTextArea();
        idTextField = new javax.swing.JTextField();
        registrationDateLabel = new javax.swing.JLabel();
        registrationDateField = new javax.swing.JTextField();
        expirationDateLabel = new javax.swing.JLabel();
        expirationDateField = new javax.swing.JTextField();
        attendancesBalanceLabel = new javax.swing.JLabel();
        attendancesBalanceField = new javax.swing.JSpinner();
        moneyBalanceLabel = new javax.swing.JLabel();
        moneyBalanceField = new javax.swing.JTextField();

        fullNameLabel.setText(bundle.getString("Label.FullName")); // NOI18N

        cardLabel.setText(bundle.getString("Label.Card")); // NOI18N

        noteLabel.setText(bundle.getString("Label.Note")); // NOI18N

        idLabel.setText(bundle.getString("Label.ID")); // NOI18N

        fullNameTextField.setName("null");

        cardTextField.setName("null");

        noteScrollPane.setName("null");

        noteTextArea.setColumns(20);
        noteTextArea.setRows(5);
        noteTextArea.setName("null");
        noteScrollPane.setViewportView(noteTextArea);

        idTextField.setEditable(false);
        idTextField.setEnabled(false);
        idTextField.setVerifyInputWhenFocusTarget(false);

        registrationDateLabel.setText(bundle.getString("Label.RegistrationDate")); // NOI18N

        registrationDateField.setEnabled(isPriviliged);
        registrationDateField.setName("null");

        expirationDateLabel.setText(bundle.getString("Label.ExpirationDate")); // NOI18N

        expirationDateField.setEnabled(isPriviliged);
        expirationDateField.setName("expirationDate");

        attendancesBalanceLabel.setText(bundle.getString("Label.AttendancesBalance")); // NOI18N

        attendancesBalanceField.setModel(new javax.swing.SpinnerNumberModel(Short.valueOf((short)0), Short.valueOf((short)0), Short.valueOf((short)999), Short.valueOf((short)1)));
        attendancesBalanceField.setEnabled(isPriviliged);
        attendancesBalanceField.setName("attendancesBalance");

        moneyBalanceLabel.setText(bundle.getString("Label.MoneyBalance")); // NOI18N

        moneyBalanceField.setEnabled(isPriviliged);
        moneyBalanceField.setName("null");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attendancesBalanceLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(idLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(noteLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cardLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(moneyBalanceLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(expirationDateLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(registrationDateLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fullNameLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(moneyBalanceField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cardTextField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fullNameTextField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(idTextField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(registrationDateField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(expirationDateField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attendancesBalanceField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noteScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idLabel)
                    .addComponent(idTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fullNameLabel)
                    .addComponent(fullNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cardLabel)
                    .addComponent(cardTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registrationDateLabel)
                    .addComponent(registrationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moneyBalanceLabel)
                    .addComponent(moneyBalanceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attendancesBalanceLabel)
                    .addComponent(attendancesBalanceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expirationDateLabel)
                    .addComponent(expirationDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noteLabel)
                    .addComponent(noteScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner attendancesBalanceField;
    private javax.swing.JLabel attendancesBalanceLabel;
    private javax.swing.JLabel cardLabel;
    private javax.swing.JTextField cardTextField;
    private javax.swing.JTextField expirationDateField;
    private javax.swing.JLabel expirationDateLabel;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JTextField fullNameTextField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idTextField;
    private javax.swing.JTextField moneyBalanceField;
    private javax.swing.JLabel moneyBalanceLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JScrollPane noteScrollPane;
    private javax.swing.JTextArea noteTextArea;
    private javax.swing.JTextField registrationDateField;
    private javax.swing.JLabel registrationDateLabel;
    // End of variables declaration//GEN-END:variables
}
