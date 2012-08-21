/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.dialogs.editors;

import census.business.CashService;
import census.business.SubscriptionsService;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.CashAdjustmentDTO;
import census.business.dto.SubscriptionDTO;
import census.presentation.MainFrame;
import census.presentation.dialogs.CensusDialog;
import census.presentation.forms.CashAdjustmentForm;
import census.presentation.forms.SubscriptionForm;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import javax.swing.JButton;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CashAdjustmentEditorDialog extends CensusDialog {

    public CashAdjustmentEditorDialog(CashAdjustmentDTO cashAdjustment) {
        super(null, true);

        this.cashAdjustment = cashAdjustment;

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

        form = new CashAdjustmentForm();
        form.setCashAdjustment(cashAdjustment);
    }

    /**
     * Builds the dialog by placing the components it.
     */
    private void buildDialog() {
        FormLayout layout = new FormLayout("4dlu, r:p:g, l:p:g, 4dlu",
                "4dlu, f:p:g, 4dlu, p, 4dlu");
        setLayout(layout);

        add(form, CC.xywh(2, 2, 2, 1));
        add(okButton, CC.xy(2, 4));
        add(cancelButton, CC.xy(3, 4));

        setTitle(MessageFormat.format(getString("Title.CashAdjustment"), cashAdjustment.getDate().toDate()));
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(null);
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
            CashService.getInstance().recordCashAdjustment(cashAdjustment);
        } catch (SecurityException | ValidationException ex) {
            MainFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
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
    private CashAdjustmentDTO cashAdjustment;
    /*
     * Presentation
     */
    private CashAdjustmentForm form;
    private JButton okButton;
    private JButton cancelButton;
}
