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
package org.key2gym.client.util;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.key2gym.business.api.ValidationException;
import org.key2gym.client.UserExceptionHandler;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FormBindingListener implements BindingListener {

    private Set<Object> invalidTargets;
    private ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/client/resources/Strings");

    public FormBindingListener() {
        this.invalidTargets = new HashSet<>();
    }

    public Set<Object> getInvalidTargets() {
        return invalidTargets;
    }

    @Override
    public void bindingBecameBound(Binding binding) {
    }

    @Override
    public void bindingBecameUnbound(Binding binding) {
    }

    @Override
    public void syncFailed(Binding binding, SyncFailure failure) {
        if (failure.getType().equals(Binding.SyncFailureType.CONVERSION_FAILED)) {
            if (failure.getConversionException() instanceof UnsupportedOperationException) {
                return;
            }
            invalidTargets.add(binding.getTargetObject());
            UserExceptionHandler.getInstance().processException((ValidationException) failure.getConversionException().getCause());
        } else if (failure.getType().equals(Binding.SyncFailureType.VALIDATION_FAILED)) {
            invalidTargets.add(binding.getTargetObject());
            
            String message = MessageFormat.format(strings.getString("Message.FieldIsNotFilledInCorrectly.withFieldName"),
                    binding.getName());
            UserExceptionHandler.getInstance().processException(new ValidationException(message));
        }
    }

    @Override
    public void synced(Binding binding) {
        invalidTargets.remove(binding.getTargetObject());
    }

    @Override
    public void sourceChanged(Binding binding, PropertyStateEvent event) {
    }

    @Override
    public void targetChanged(Binding binding, PropertyStateEvent event) {
    }
}