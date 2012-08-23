/*
 * Copyright 2012 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package census.presentation.actions;

import census.business.OrdersService;
import census.business.SessionsService;
import census.business.StorageService;
import census.business.api.BusinessException;
import census.business.api.ValidationException;
import census.presentation.dialogs.AbstractDialog;
import census.presentation.dialogs.CheckInDialog;
import census.presentation.dialogs.EditOrderDialog;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;


/**
 *
 * @author Danylo Vashchilenko
 */
public class CheckInAction extends CensusAction implements Observer {
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");
    private Logger logger = Logger.getLogger(CheckInAction.class.getName());

    public CheckInAction() {
        if(!Beans.isDesignTime()) {
            update(null, null);
        }
        
        setText(bundle.getString("Text.Entry"));
        setIcon(new ImageIcon(getClass().getResource("/census/presentation/resources/open.png")));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            
            StorageService storageService = StorageService.getInstance();

            storageService.beginTransaction();

            /*
             * OpenAttendance
             */         
            CheckInDialog checkInDialog = new CheckInDialog(getFrame());  
            checkInDialog.setVisible(true);

            if (checkInDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                throw checkInDialog.getException();
            }

            if (checkInDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                storageService.rollbackTransaction();
                return;
            }

            /*
             * If requested, EditFinancialActivty
             */
            if (checkInDialog.isOrderRequested()) {

                Short orderId = null;
                EditOrderDialog editOrderDialog = new EditOrderDialog(getFrame());

                try {
                    if (checkInDialog.isAnonymous()) {
                        orderId = OrdersService.getInstance().findForAttendanceById(checkInDialog.getAttendanceId());
                    } else {
                        orderId = OrdersService.getInstance().findByClientIdAndDate(checkInDialog.getClientId(), new DateMidnight(), true);
                    }
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }

                editOrderDialog.setOrderId(orderId);
                editOrderDialog.setFullPaymentForced(false);
                
                editOrderDialog.setVisible(true);

                if (editOrderDialog.getResult().equals(AbstractDialog.RESULT_EXCEPTION)) {
                    throw editOrderDialog.getException();
                }

                if (editOrderDialog.getResult().equals(AbstractDialog.RESULT_CANCEL)) {
                    storageService.rollbackTransaction();
                    return;
                }
            }

            storageService.commitTransaction();

        } catch (BusinessException ex) {
            StorageService.getInstance().rollbackTransaction();
            JOptionPane.showMessageDialog(getFrame(), ex.getMessage(), "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        } catch (RuntimeException ex) {
            logger.error("RuntimeException", ex);
            JOptionPane.showMessageDialog(getFrame(), bundle.getString("Message.ProgramEncounteredError"), bundle.getString("Title.Error"), JOptionPane.ERROR_MESSAGE);
            if(StorageService.getInstance().isTransactionActive()) {
                StorageService.getInstance().rollbackTransaction();
            }
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
