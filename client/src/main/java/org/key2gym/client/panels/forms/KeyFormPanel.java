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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JTextField;
import org.jdesktop.beansbinding.*;
import org.key2gym.business.api.dtos.KeyDTO;
import org.key2gym.client.util.FormBindingListener;
import org.key2gym.client.util.IntegerToStringConverter;

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
