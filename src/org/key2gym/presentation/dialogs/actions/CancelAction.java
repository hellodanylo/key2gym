/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.presentation.dialogs.actions;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import org.key2gym.presentation.dialogs.AbstractDialog;

/**
 *
 * @author Danylo Vashchilenko
 */
public class CancelAction extends DialogAction {
    /**
     * Creates a CancelAction.
     */
    public CancelAction(AbstractDialog dialog) {
        super(dialog);
        putValue(NAME, getString("Button.Cancel"));
        putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/cancel16.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractDialog dialog = getDialog();
        
        dialog.setResult(AbstractDialog.Result.CANCEL);
        dialog.dispose();
    }
}