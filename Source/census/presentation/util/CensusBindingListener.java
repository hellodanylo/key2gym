/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.api.ValidationException;
import census.presentation.CensusFrame;
import java.util.HashSet;
import java.util.Set;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;

/**
 *
 * @author daniel
 */
public class CensusBindingListener implements BindingListener {

    private Set<Object> invalidTargets;

    public CensusBindingListener() {
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
            invalidTargets.add(binding.getTargetObject());
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException((ValidationException) failure.getConversionException().getCause());
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
