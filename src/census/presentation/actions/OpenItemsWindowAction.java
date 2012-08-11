/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.business.SessionsService;
import census.business.api.SecurityException;
import census.presentation.CensusFrame;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.PickDateDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OpenItemsWindowAction extends CensusAction {

    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public OpenItemsWindowAction() {
        if (!Beans.isDesignTime()) {
            update(null, null);
        }
        
        setAccelerationKey(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));
        setText(bundle.getString("Text.Items"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            CensusFrame.getInstance().openItemsTab();
        } catch (RuntimeException ex) {
            Logger.getLogger(this.getClass().getName()).error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    @Override
    public final void update(Observable o, Object arg) {
        if (o == null) {
            SessionsService.getInstance().addObserver(this);
        }
        Boolean open = SessionsService.getInstance().hasOpenSession();
        setEnabled(open);
    }
}
