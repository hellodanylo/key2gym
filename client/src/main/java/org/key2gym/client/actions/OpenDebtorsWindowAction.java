package org.key2gym.client.actions;

import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.client.MainFrame;
import org.key2gym.client.panels.forms.DebtorsPanel;
import org.key2gym.client.resources.ResourcesManager;

import java.awt.event.ActionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 10/2/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenDebtorsWindowAction extends BasicAction {

    public OpenDebtorsWindowAction() {
        setText(getString("Text.Debtors"));
    }

    @Override
    protected void onActionPerformed(ActionEvent e) throws SecurityViolationException, BusinessException, ValidationException {
        MainFrame.getInstance().createTab(new DebtorsPanel(), ResourcesManager.getString("Text.Debtors"));
    }
}
