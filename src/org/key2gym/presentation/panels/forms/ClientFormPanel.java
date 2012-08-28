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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import org.jdesktop.beansbinding.*;
import org.key2gym.business.SessionsService;
import org.key2gym.business.dto.ClientDTO;
import org.key2gym.presentation.highlighters.AttendancesBalanceHighlighter;
import org.key2gym.presentation.highlighters.ExpirationDateHighlighter;
import org.key2gym.presentation.highlighters.MoneyBalanceHighlighter;
import org.key2gym.presentation.util.*;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ClientFormPanel extends JPanel {

    public ClientFormPanel(List<Column> columnsList) {
        isPriviliged = SessionsService.getInstance().getPermissionsLevel().equals(SessionsService.PL_ALL);
        this.columnsList = columnsList;

        buildForm();

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, KeyEvent.CTRL_DOWN_MASK), CardFocusAction.class.getName());
        getActionMap().put(CardFocusAction.class.getName(), new CardFocusAction());
    }

    /**
     * Builds this from by placing the fields specified in columnsList.
     */
    private void buildForm() {
        FormLayout layout = new FormLayout("right:default, 3dlu, default:grow", "");
        ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, strings, this);
        builder.defaultRowSpec(new RowSpec(RowSpec.FILL, Sizes.DEFAULT, RowSpec.NO_GROW));

        bindingGroup = new BindingGroup();

        formBindingListener = new FormBindingListener();
        bindingGroup.addBindingListener(formBindingListener);

        for (Column column : columnsList) {
            Binding binding;

            if (column.equals(Column.ID)) {
                /*
                 * ID
                 */
                idTextField = new JTextField();
                idTextField.setEditable(false);
                idTextField.setEnabled(false);
                builder.appendI15d("Label.ID", idTextField);
                builder.nextLine();

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("id"), idTextField, BeanProperty.create("text"), "id");
                bindingGroup.addBinding(binding);

            } else if (column.equals(Column.FULL_NAME)) {
                /*
                 * Full name
                 */
                fullNameTextField = new JTextField();
                builder.appendI15d("Label.FullName", fullNameTextField);
                builder.nextLine();

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("fullName"), fullNameTextField, BeanProperty.create("text"), "fullName");
                bindingGroup.addBinding(binding);
            } else if (column.equals(Column.CARD)) {
                /*
                 * Card
                 */
                cardTextField = new JTextField();
                builder.appendI15d("Label.Card", cardTextField);
                builder.nextLine();

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("card"), cardTextField, BeanProperty.create("text"), "card");
                binding.setConverter(new IntegerToStringConverter("Card", true));
                bindingGroup.addBinding(binding);

            } else if (column.equals(Column.EXPIRATION_DATE)) {
                /*
                 * Expiration date
                 */
                expirationDateTextField = new JTextField();
                expirationDateTextField.setEditable(isPriviliged);
                new ExpirationDateHighlighter(expirationDateTextField);
                builder.appendI15d("Label.ExpirationDate", expirationDateTextField);
                builder.nextLine();

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("expirationDate"), expirationDateTextField, BeanProperty.create("text"), "expirationDate");
                binding.setConverter(new DateMidnightToStringConverter(strings.getString("Text.ExpirationDate"), "dd-MM-yyyy"));
                bindingGroup.addBinding(binding);
            } else if (column.equals(Column.ATTENDANCES_BALANCE)) {
                /*
                 * Attendances balance
                 */
                attendancesBalanceTextField = new JTextField();
                attendancesBalanceTextField.setEditable(isPriviliged);
                new AttendancesBalanceHighlighter(attendancesBalanceTextField);
                builder.appendI15d("Label.AttendancesBalance", attendancesBalanceTextField);
                builder.nextLine();

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("attendancesBalance"), attendancesBalanceTextField, BeanProperty.create("text"), "attendancesBalance");
                binding.setConverter(new ShortToStringConverter(strings.getString("Text.AttendancesBalance"), false));
                bindingGroup.addBinding(binding);

            } else if (column.equals(Column.MONEY_BALANCE)) {
                /*
                 * Money balance
                 */
                moneyBalanceTextField = new JTextField();
                moneyBalanceTextField.setEditable(isPriviliged);
                new MoneyBalanceHighlighter(moneyBalanceTextField);
                builder.appendI15d("Label.MoneyBalance", moneyBalanceTextField);
                builder.nextLine();

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("moneyBalance"), moneyBalanceTextField, BeanProperty.create("text"), "moneyBalance");
                binding.setConverter(new MoneyBigDecimalToStringConverter(strings.getString("Text.MoneyBalance")));
                bindingGroup.addBinding(binding);
            } else if (column.equals(Column.REGISTRATION_DATE)) {
                /*
                 * Registration date
                 */
                registrationDateTextField = new JTextField();
                registrationDateTextField.setEditable(isPriviliged);
                builder.appendI15d("Label.RegistrationDate", registrationDateTextField);
                builder.nextLine();

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("registrationDate"), registrationDateTextField, BeanProperty.create("text"), "registrationDate");
                binding.setConverter(new DateMidnightToStringConverter("Registration Date", "dd-MM-yyyy"));
                bindingGroup.addBinding(binding);
            } else if (column.equals(Column.NOTE)) {
                /*
                 * Note
                 */
                noteScrollPane = new JScrollPane();
                noteTextArea = new JTextArea();
                noteTextArea.setColumns(20);
                noteTextArea.setRows(5);
                noteScrollPane.setViewportView(noteTextArea);
                JLabel label = new JLabel(strings.getString("Label.Note"));
                label.setVerticalAlignment(SwingConstants.TOP);
                builder.append(label, noteScrollPane);

                binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, client,
                        BeanProperty.create("note"), noteTextArea, BeanProperty.create("text"), "note");
                bindingGroup.addBinding(binding);

            }
        }

        bindingGroup.bind();
    }

    /**
     * Sets the form's client.
     *
     * @param newClient the new client
     */
    public void setClient(ClientDTO newClient) {
        if (newClient == null) {
            newClient = new ClientDTO();
        }

        this.client = newClient;

        formBindingListener.getInvalidTargets().clear();

        /*
         * We take each binding and set the source object.
         */
        for (Binding binding : bindingGroup.getBindings()) {
            binding.unbind();
            binding.setSourceObject(client);
            binding.bind();
        }
    }

    /**
     * Sets whether the form can be edited by the user.
     *
     * @param canEdit if true, this form will do its best to show user that the
     * form is not editable.
     */
    public void setEditable(boolean canEdit) {
        /*
         * Takes each binding and applies the editable property, if it's a
         * component.
         */
        for (Binding binding : bindingGroup.getBindings()) {
            Object target = binding.getTargetObject();
            if (JTextComponent.class.isInstance(target)) {
                ((JTextComponent) binding.getTargetObject()).setEditable(canEdit);
            }
        }
    }

    /**
     * Sets whether the form can be edited by the user.
     *
     * @param enabled if true, this form will do its best to show user that the
     * form is not editable.
     */
    @Override
    public void setEnabled(boolean enabled) {
        /*
         * Takes each binding and applies the editable property, if it's a
         * component.
         */
        for (Binding binding : bindingGroup.getBindings()) {
            Object target = binding.getTargetObject();
            if (JTextComponent.class.isInstance(target)) {
                ((JTextComponent) binding.getTargetObject()).setEnabled(enabled);
            }
        }

        super.setEnabled(enabled);
    }

    /**
     * Tries to save the form to the current client.
     *
     * @return true, if the form is valid and has been saved
     */
    public boolean trySave() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
        return formBindingListener.getInvalidTargets().isEmpty();
    }

    private class CardFocusAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            cardTextField.requestFocusInWindow();
        }
    }

    public enum Column {

        ID,
        FULL_NAME,
        CARD,
        REGISTRATION_DATE,
        EXPIRATION_DATE,
        ATTENDANCES_BALANCE,
        MONEY_BALANCE,
        NOTE
    };

    /*
     * Business
     */
    private Boolean isPriviliged;
    private ClientDTO client;
    /*
     * Form
     */
    private List<Column> columnsList;
    /*
     * Presentation
     */
    private BindingGroup bindingGroup;
    private FormBindingListener formBindingListener;
    private JTextField attendancesBalanceTextField;
    private JTextField cardTextField;
    private JTextField expirationDateTextField;
    private JTextField fullNameTextField;
    private JTextField idTextField;
    private JTextField moneyBalanceTextField;
    private JScrollPane noteScrollPane;
    private JTextArea noteTextArea;
    private JTextField registrationDateTextField;
}
