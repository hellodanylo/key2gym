/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.presentation.panels.forms;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.jdesktop.beansbinding.*;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.ItemDTO;
import org.key2gym.business.dto.KeyDTO;
import org.key2gym.presentation.util.FormBindingListener;
import org.key2gym.presentation.util.IntegerToStringConverter;

/**
 *
 * @author Danylo Vashchilenko
 */
public class KeyFormPanel extends FormPanel<KeyDTO> {
  
    /**
     * Creates new KeyFormPanel
     */
    public KeyFormPanel() {
        buildForm();
    }

    /**
     * Builds this from by placing the components on it.
     */
    private void buildForm() {
        FormLayout layout = new FormLayout("right:default, 3dlu, default:grow", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, getStrings(), this);
        
        idTextField = new JTextField(30);
        idTextField.setEnabled(false);
        titleTextField = new JTextField(30);

        builder.appendI15d("Label.ID", idTextField);
        builder.nextLine();
        builder.appendI15d("Label.Title", titleTextField);

    }

    /**
     * Sets the form's item.
     *
     * @param item the new item
     */
    @Override
    public void setForm(KeyDTO key) {
        this.key = key;

        if (bindingGroup == null) {
            formBindingListener = new FormBindingListener();
            bindingGroup = new BindingGroup();
            bindingGroup.addBindingListener(formBindingListener);
            
            
            /**
             * ID
             */
            Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    key, BeanProperty.create("id"), idTextField, BeanProperty.create("text"), "id");
            binding.setConverter(new IntegerToStringConverter(getString("Text.ID"), true));
            bindingGroup.addBinding(binding);

            /**
             * Title
             */
            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE,
                    key, BeanProperty.create("title"), titleTextField, BeanProperty.create("text"), "title");
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
                binding.setSourceObject(key);
                binding.bind();
            }
        }
    }

    @Override
    public KeyDTO getForm() {
        return key;
    }

    /**
     * Tries to save the form to the current key.
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
    private KeyDTO key;
    /*
     * Presentation
     */
    private BindingGroup bindingGroup;
    private FormBindingListener formBindingListener;
    private JTextField idTextField;
    private JTextField titleTextField;

}
