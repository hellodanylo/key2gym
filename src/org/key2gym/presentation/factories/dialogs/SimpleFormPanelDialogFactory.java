/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.presentation.factories.dialogs;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.key2gym.business.ItemsService;
import org.key2gym.business.api.SecurityException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.dto.ItemDTO;
import org.key2gym.presentation.dialogs.AbstractDialog;
import org.key2gym.presentation.dialogs.FormDialog;
import org.key2gym.presentation.dialogs.actions.CancelAction;
import org.key2gym.presentation.dialogs.actions.OkAction;
import org.key2gym.presentation.panels.forms.ItemFormPanel;
import org.key2gym.presentation.util.UserExceptionHandler;

/**
 *
 * @author Danylo Vashchilenko
 */
public class SimpleFormPanelDialogFactory {
    
    public static FormDialog createItemDialog(Window parent, final ItemDTO item) {
        final ItemFormPanel formPanel = new ItemFormPanel();
        formPanel.setForm(item);
        
        final FormDialog dialog = new FormDialog(parent, formPanel);
        
        List<Action> actions = new LinkedList<>();
        
        actions.add(new OkAction(dialog) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!formPanel.trySave()) {
                    return;
                }
                try {
                    if (formPanel.getForm().getId() == null) {
                        ItemsService.getInstance().addItem(item);
                    } else {
                        
                        ItemsService.getInstance().updateItem(item);
                    }
                } catch (ValidationException ex) {
                    UserExceptionHandler.getInstance().processException(ex);
                    return;
                } catch (SecurityException ex) {
                    dialog.setResult(AbstractDialog.Result.EXCEPTION);
                    dialog.setException(new RuntimeException(ex));
                    return;
                }
                
                super.actionPerformed(e);
            }
        });
        
        actions.add(new CancelAction(dialog));
        
        dialog.setActions(actions);
        
        if (item.getId() == null) {
            dialog.setTitle(strings.getString("Title.ItemDialog.new"));
        } else {
            dialog.setTitle(MessageFormat.format(strings.getString("Title.ItemDialog.withTitle"), item.getTitle()));
        }
        
        return dialog;
    }
    
    private static ResourceBundle strings = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings");
}
