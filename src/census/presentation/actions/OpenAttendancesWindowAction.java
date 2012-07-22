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
import java.beans.Beans;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Danylo Vashchilenko
 */
public class OpenAttendancesWindowAction extends CensusAction {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    public OpenAttendancesWindowAction() {
        if (!Beans.isDesignTime()) {
            update(null, null);
        }
        
        setText(bundle.getString("Text.Attendances"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {

            PickDateDialog pickDateDialog = new PickDateDialog(getFrame());
            pickDateDialog.setVisible(true);

            if (pickDateDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw pickDateDialog.getException();
            }

            if (pickDateDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                return;
            }
            
            CensusFrame.getInstance().openAttendancesTabForDate(pickDateDialog.getDate());


        } catch(SecurityException ex) {
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
        } catch (RuntimeException ex) {
            Logger.getLogger(RegisterClientAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
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
