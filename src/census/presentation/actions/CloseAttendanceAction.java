/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.business.AttendancesService;
import census.business.OrdersService;
import census.business.SessionsService;
import census.business.StorageService;
import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.business.dto.AttendanceDTO;
import census.presentation.CensusFrame;
import census.presentation.dialogs.CensusDialog;
import census.presentation.dialogs.EditOrderDialog;
import census.presentation.dialogs.PickAttendanceDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.joda.time.DateMidnight;

/**
 *
 * @author daniel
 */
public class CloseAttendanceAction extends CensusAction implements Observer {
    
    private ResourceBundle bundle;
    public CloseAttendanceAction() {
        if(!Beans.isDesignTime()) {
            update(null, null);
        }
        
        bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");
        setText(bundle.getString("Text.Leaving"));
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/close.png")));

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        /*
         * Business
         */
        StorageService storageService = null;
        
        /*
         * Presentation
         */
        PickAttendanceDialog pickAttendanceDialog;

        try {
            storageService = StorageService.getInstance();
            storageService.beginTransaction();
            
            pickAttendanceDialog = new PickAttendanceDialog(getFrame());
            
            AttendanceDTO attendanceDTO = getFrame().getSelectedAttendance();
            if (attendanceDTO != null) {
                pickAttendanceDialog.setAttendanceId(attendanceDTO.getId());
                pickAttendanceDialog.setAttendanceLocked(false);
            }

            pickAttendanceDialog.setVisible(true);

            if (pickAttendanceDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                throw pickAttendanceDialog.getException();
            }

            if (pickAttendanceDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }
            
            Short attendanceId = pickAttendanceDialog.getAttendanceId();
            Short financialActivityId;
            Boolean isAnonymous;
            
            try {
                isAnonymous = AttendancesService.getInstance().isAnonymous(attendanceId);
                if(isAnonymous) {
                    financialActivityId = OrdersService.getInstance().findForAttendanceById(attendanceId);
                } else {
                    financialActivityId = OrdersService.getInstance().findByClientIdAndDate(AttendancesService.getInstance().getAttendanceById(attendanceId).getClientId(), new DateMidnight(), pickAttendanceDialog.isEditFinancialActivityDialogRequested());
                }
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            } catch (census.business.api.SecurityException ex) {
                throw new RuntimeException(ex);
            }
            
            
            if(financialActivityId != null) {
                           
                EditOrderDialog efaDialog = new EditOrderDialog(getFrame());
                efaDialog.setOrderId(financialActivityId);
                efaDialog.setFullPaymentForced(isAnonymous);
                efaDialog.setVisible(true);

                if (efaDialog.getResult().equals(CensusDialog.RESULT_EXCEPTION)) {
                    throw efaDialog.getException();
                }

                if (efaDialog.getResult().equals(CensusDialog.RESULT_CANCEL)) {
                    storageService.rollbackTransaction();
                    return;
                }
            }
            
            /*
             * Finally, closes the attendance.
             */
            try {
                AttendancesService.getInstance().closeAttendance(attendanceId);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }
            
            storageService.commitTransaction();

        } catch (BusinessException ex) {
            storageService.rollbackTransaction();
            CensusFrame.getGlobalCensusExceptionListenersStack().peek().processException(ex);
            return;
        } catch (RuntimeException ex) {
            Logger.getLogger(RegisterClientAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    @Override
    public final void update(Observable o, Object arg) {
        if(o == null) {
            SessionsService.getInstance().addObserver(this);
        }
        Boolean open = SessionsService.getInstance().hasOpenSession();
        setEnabled(open);
    }
}
