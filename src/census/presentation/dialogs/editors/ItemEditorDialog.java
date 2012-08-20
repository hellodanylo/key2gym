/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.dialogs.editors;

import census.business.ItemsService;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.dto.ItemDTO;
import census.presentation.CensusFrame;
import census.presentation.dialogs.CensusDialog;
import census.presentation.forms.ItemForm;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import javax.swing.Action;
import javax.swing.JButton;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemEditorDialog extends CensusDialog {

    public ItemEditorDialog(ItemDTO item) {
        super(null, true);
        this.item = item;

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
        form.setItem(item);
    }

    /**
     * Builds the dialog by placing the components it.
     */
    private void buildDialog() {
        FormLayout layout = new FormLayout("4dlu, right:100dlu, left:100dlu, 4dlu",
                "4dlu, default, 4dlu, default, 4dlu");
        setLayout(layout);

        add(form, CC.xywh(2, 2, 2, 1));
        add(okButton, CC.xy(2, 4));
        add(cancelButton, CC.xy(3, 4));

        if (item.getId() == null) {
            setTitle(getString("Title.ItemDialog.new"));
        } else {
            setTitle(MessageFormat.format(getString("Title.ItemDialog.withTitle"), item.getTitle()));
        }
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
            if (item.getId() == null) {
                ItemsService.getInstance().addItem(item);
            } else {
                ItemsService.getInstance().updateItem(form.getItem());
            }
        } catch (SecurityException | ValidationException ex) {
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
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
    private ItemDTO item;
    /*
     * Presentation
     */
    private ItemForm form;
    private JButton okButton;
    private JButton cancelButton;
}
