/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.dialogs.editors;

import org.key2gym.business.SubscriptionsService;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.SubscriptionDTO;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.forms.SubscriptionForm;
import org.key2gym.presentation.util.UserExceptionHandler;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SubscriptionEditorDialog extends AbstractDialog {

    public SubscriptionEditorDialog(SubscriptionDTO subscription) {
        super(null, true);

        this.subscription = subscription;

        initComponents();
        buildDialog();
    }

    /**
     * Initializes the dialog's components.
     */
    private void initComponents() {
        okButton = new JButton(getOkAction());
        cancelButton = new JButton(getCancelAction());
        okButton.setPreferredSize(cancelButton.getPreferredSize());

        form = new SubscriptionForm();
        form.setSubscription(subscription);
    }

    /**
     * Builds the dialog by placing the components it.
     */
    private void buildDialog() {
        FormLayout layout = new FormLayout("4dlu, [200dlu, p]:g, 4dlu",
                "4dlu, p, 4dlu, p, 4dlu");
        setLayout(layout);

        add(form, CC.xy(2, 2));

        JPanel buttonsPanel = new JPanel();
        {
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            buttonsPanel.add(okButton);
            buttonsPanel.add(cancelButton);
        }
        add(buttonsPanel, CC.xy(2, 4));

        if (subscription.getId() == null) {
            setTitle(getString("Title.SubscriptionEditorDialog.new"));
        } else {
            setTitle(MessageFormat.format(getString("Title.SubscriptionEditorDialog.withTitle"), subscription.getTitle()));
        }
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    /**
     * Called when the OK action has been performed.
     *
     * @param evt the action event
     */
    @Override
    protected void onOkActionPerformed(ActionEvent evt) {
        if (!form.trySave()) {
            return;
        }

        try {
            if (subscription.getId() == null) {
                SubscriptionsService.getInstance().addSubscription(subscription);
            } else {
                SubscriptionsService.getInstance().updateSubscription(subscription);
            }
        } catch (SecurityException | ValidationException ex) {
            UserExceptionHandler.getInstance().processException(ex);
            return;
        } catch (RuntimeException ex) {
            setResult(RESULT_EXCEPTION);
            setException(ex);
            dispose();
            return;
        }

        super.onOkActionPerformed(evt);
    }
    /*
     * Business
     */
    private SubscriptionDTO subscription;
    /*
     * Presentation
     */
    private SubscriptionForm form;
    private JButton okButton;
    private JButton cancelButton;
}
