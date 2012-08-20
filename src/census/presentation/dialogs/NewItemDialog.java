/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.dialogs;

import census.business.ItemsService;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.ItemDTO;
import census.presentation.CensusFrame;
import census.presentation.forms.ItemForm;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JButton;

/**
 *
 * @author Danylo Vashchilenko
 */
public class NewItemDialog extends CensusDialog {

    public NewItemDialog() {
        super(null, true);

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

        form = new ItemForm();
        form.setItem(new ItemDTO());
    }

    /**
     * Builds the dialog by placing the components on it.
     */
    private void buildDialog() {
        FormLayout layout = new FormLayout("4dlu, right:100dlu, left:100dlu, 4dlu",
                "4dlu, default:grow, 4dlu, default, 4dlu");
        setLayout(layout);

        add(form, CC.xywh(2, 2, 2, 1));
        add(okButton, CC.xy(2, 4));
        add(cancelButton, CC.xy(3, 4));

        setTitle(getString("Title.ItemDialog.new"));
        setLocationRelativeTo(null);
        pack();
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
            ItemsService.getInstance().addItem(form.getItem());
        } catch (SecurityException | ValidationException ex) {
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
            return;
        }

        super.onOkActionPerformed(evt);
    }
    /*
     * Presentation
     */
    private ItemForm form;
    private JButton okButton;
    private JButton cancelButton;
}
