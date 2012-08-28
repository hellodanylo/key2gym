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
public class OkAction extends DialogAction {
    /**
     * Creates an OkAction.
     */
    public OkAction(AbstractDialog dialog) {     
        super(dialog);
        putValue(NAME, getString("Button.Ok"));
        putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/key2gym/presentation/resources/ok16.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractDialog dialog = getDialog();
        
        dialog.setResult(AbstractDialog.Result.OK);
        dialog.dispose();
    }
}
